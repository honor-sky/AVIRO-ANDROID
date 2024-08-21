package com.aviro.android.presentation.search

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.aviro.android.common.AmplitudeUtils
import com.aviro.android.databinding.ActivitySearchBinding
import com.aviro.android.domain.entity.search.SearchedRestaurantItem
import com.aviro.android.presentation.BaseActivity
import com.aviro.android.presentation.aviro_dialog.SortingAccDisDialog
import com.aviro.android.presentation.aviro_dialog.SortingLocationDialog
import com.aviro.android.presentation.entity.SortingLocEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Search : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val viewmodel: SearchViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this

        setLocation()
        setAdapter()
        initObserver()
        initListener()

    }

    fun setLocation()
    {
        val X = intent.getDoubleExtra("NaverMapOfX", 0.0)
        val Y = intent.getDoubleExtra("NaverMapOfY", 0.0)

        viewmodel.centerOfMapX = X
        viewmodel.centerOfMapY = Y

        // 기본 정렬 기준
        if(viewmodel.getCurrentGPSLoc() != null){
            val location = viewmodel.getCurrentGPSLoc()
            val SortingLoc = SortingLocEntity(location!!.longitude.toString(),location.latitude.toString(), "accuracy")
            viewmodel._SrotingLocation.value = SortingLoc
        } else {
            val SortingLoc = SortingLocEntity(X.toString(), Y.toString(), "accuracy")
            viewmodel._SrotingLocation.value = SortingLoc
        }
    }

    fun setAdapter()
    {
        binding.PreSearchRecyclerview.adapter = PreSearchAdapter(viewmodel, binding.EditTextSearchBar)
        binding.searchRecyclerview.adapter = SearchAdapter(viewmodel)
    }
    fun initObserver() {

        viewmodel.preSearchedItems.observe(this, ) {
            (binding.PreSearchRecyclerview.adapter as PreSearchAdapter).preSearchedList =
                it?.toList()?.toMutableList()
            (binding.PreSearchRecyclerview.adapter as PreSearchAdapter).notifyDataSetChanged()
        }

        binding.EditTextSearchBar.addTextChangedListener {
            viewmodel._keyword.value = it.toString()
            // 새로운 입력값 생길때마다 새로운 코루틴 생성
            lifecycleScope.launch {// main 스레드 -> IO 스레드 + 매번 생성 X 하게 만들면 좋을 듯

                /* 새로운 검색어가 들어오면 0.3초 후에 가장 최신의 검색어로 검색이 요청됨
                 * '이디야'를 입력해도 ㅇ, ㅣ, ㄷ, ㅣ, ㅇ, ㅑ 를 입려할 때마다 새로운 검색 코루틴이 생기지만 0.3초 후에 검색이 요청되므로
                 * 모두 '이디야' 로 검색 요청됨
                 * 그런데 여러개의 코루틴이 0.3초 간격을 두고 실행되는데 경우에 따라 아직 이전 코루틴이 끝나기 전에 다른 코루틴이 실행되는 경우
                 * 이전 코루틴은 취소되므로 initList()가 취소됨 */

                viewmodel._keyword
                    .debounce(300) // 0.3초 동안 입력값이 없는 경우만 방출
                    .collect {
                        viewmodel.initList()
                    }
            }
        }

        // 새로은 검색결과
        viewmodel.searchList.observe(this) {
            if (viewmodel.isNewKeyword == true) {
                binding.searchRecyclerview.removeAllViews() // 기존 검색 리스트 삭제
                (binding.searchRecyclerview.adapter as SearchAdapter).searchedList = it as MutableList<SearchedRestaurantItem>?

            }
            else {
                val currentPosition = (binding.searchRecyclerview.adapter as SearchAdapter).itemCount // 현재 아이템 총 갯수
                // 리사이클러 어댑터의 아이텝 리스트에 추가
                (binding.searchRecyclerview.adapter as SearchAdapter).searchedList!!.addAll(it.slice(currentPosition..it.size-1))
                (binding.searchRecyclerview.adapter as SearchAdapter).notifyItemRangeInserted(
                    currentPosition, // 새로 삽입될 포지션
                    viewmodel.searchListSize  // 삽입된 아이템의 개수
                )
            }
        }


        viewmodel.selectedSearchedItem.observe(this) {
            AmplitudeUtils.placeSearch(it.placeName)  // 앱플리튜드 연결
            viewmodel.storeSearchedWord(it.placeName) // 검색한 가게

            intent.putExtra("search_item", it)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    fun initListener() {

        // 검색 위치 설정
        binding.locationSortBtn.setOnClickListener {
            SortingLocationDialog(this,  viewmodel._SortLocText.value!!, { viewmodel.sortCenterOfMapLoc() }, { viewmodel.sortCenterOfMyLoc() } ).show()
           /* AviroDialogUtils.createTwoChoiceDialog(this, "검색 위치 설정",
                "지도 중심", "내 위치 중심",
                { viewmodel.sortCenterOfMapLoc() }, { viewmodel.sortCenterOfMyLoc() }).show()
           */
        }

        //  정렬 기준 변경
        binding.accuracySortBtn.setOnClickListener {
            SortingAccDisDialog(this,  viewmodel._SortAccText.value!!, { viewmodel.sortAccuracy() }, { viewmodel.sortDistance() } ).show()
            /*
            AviroDialogUtils.createTwoChoiceDialog(this, "정렬 기준",
                "정확도순", "거리순",
                { viewmodel.sortAccuracy() }, { viewmodel.sortDistance() }).show()
            */
        }


        binding.searchbarCancleBtn.setOnClickListener {
            binding.EditTextSearchBar.text = null
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        // 스크롤 발생할 때 호출
        binding.searchRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 다음페이지 존재 여부 확인
                if(viewmodel.isEnd == true) return

                // 아직 로딩중인지 확인
                if (viewmodel._isProgress.value == false) { // 아직 로딩중이면 호출 x
                    // 스크롤이 끝에 도달했는지 확인
                    if (!binding.searchRecyclerview.canScrollVertically(1)) { // 더이상 하단으로 내려갈 수 없음
                        viewmodel.currentPage++
                        viewmodel.nextList()
                    }
                }

            }
        })

    }
}





