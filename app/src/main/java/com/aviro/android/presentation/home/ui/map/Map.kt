package com.aviro.android.presentation.home.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.UiThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aviro.android.R
import com.aviro.android.common.AmplitudeUtils
import com.aviro.android.common.DistanceCalculator
import com.aviro.android.databinding.FragmentMapBinding
import com.aviro.android.domain.entity.search.SearchedRestaurantItem
import com.aviro.android.presentation.BaseFragment
import com.aviro.android.presentation.aviro_dialog.AviroDialogUtils
import com.aviro.android.presentation.aviro_dialog.NoticePopUp
import com.aviro.android.presentation.bottomsheet.*
import com.aviro.android.presentation.home.Home
import com.aviro.android.presentation.home.HomeViewModel
import com.aviro.android.presentation.search.Search
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Map : BaseFragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: MapViewModel by viewModels()
    private val bottomSheetViewmodel: BottomSheetViewModel by viewModels()
    private val homeViewmodel: HomeViewModel by activityViewModels()

    private lateinit var locationSource: FusedLocationSource

    private lateinit var mapFragment : MapFragment

    private var naver_map : NaverMap? = null
    // 현재 네이버 맵의 중심점
    var NaverMapOfX : Double? = null
    var NaverMapOfY : Double? = null

    private lateinit var gestureDetector: GestureDetector
    var bottomSheetSate = 0
    private lateinit var persistenetBottomSheet : BottomSheetBehavior<LinearLayout> //= BottomSheetBehavior.from(bottomSheet)

    val permission_list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    //private lateinit var fragList : Array<Fragment>


    val frag1 = BottomSheetHome({count -> setBottomSheetTabRevieAmount(count)})
    val frag2 = BottomSheetMenu()
    val frag3 = BottomSheetReview({count -> setBottomSheetTabRevieAmount(count)})
    val fragList = arrayOf(frag1, frag2, frag3)



    private var promotionPopUp : NoticePopUp? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //checkOnOffGPS()

        val fm = childFragmentManager
        mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)

        bottomSheetViewmodel.getNickname()


        frag1.setViewModel(bottomSheetViewmodel, viewmodel, homeViewmodel)
        frag2.setViewModel(bottomSheetViewmodel, viewmodel)
        frag3.setViewModel(bottomSheetViewmodel, viewmodel, homeViewmodel)


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.mapFragment.setViewModel(viewmodel)


        // 현재 화면 높이 가져오기
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        // 맵화면 높이 조정
        val layoutParams = binding.mapFragment.layoutParams
        layoutParams.height = screenHeight - 100
        binding.mapFragment.layoutParams = layoutParams

        initListener()
        initObserver()

       if(isTutorial()) {
           // 튜토리얼 시작
           runTutorial()

       } else{
           viewmodel.getPopInfo()
       }


        // 바텀시트 뷰페이저 설정
        setupViewPager(binding.bottomSheet.fragmentBottomsheetStep2.bottomsheetViewPager, binding.bottomSheet.fragmentBottomsheetStep2.tabLayout)


        // 맵화면 보여지면 동시에 바텀시트도 생성

        persistenetBottomSheet = BottomSheetBehavior.from(binding.bottomSheetLayout)
        persistenetBottomSheet.state = STATE_HIDDEN
        // 맵화면에 바텀시트 동작 달음

        binding.mapFragment.setBottomSheetBehavior(persistenetBottomSheet)
        binding.bottomSheet.fragmentBottomsheetStep2.bottomsheetViewPagerContainer.setViewModel(viewmodel)
        binding.bottomSheet.fragmentBottomsheetStep2.bottomsheetViewPagerContainer.setBottomSheetBehavior(persistenetBottomSheet)


        // 바텀시트 제스쳐 감지 리스너 달기
        gestureDetector = GestureDetector(context, GestureListener())

        binding.bottomSheetLayout.setOnTouchListener { _, event ->
            // 뷰그룹의 onTouchEvent() 호출 (즉, intercpt 에서 true한 효과와 동일)
            gestureDetector.onTouchEvent(event)
        }


        binding.bottomSheet.mapViewmodel = viewmodel
        binding.bottomSheet.bottomViewmodel = bottomSheetViewmodel
        binding.bottomSheet.fragmentBottomsheetStep2.viewmodel = bottomSheetViewmodel

        return root
    }


    @SuppressLint("ClickableViewAccessibility")
    fun initListener() {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewmodel._selectedMarker.value?.let {
                    if(viewmodel.isFavorite.value == true) {
                        viewmodel.cancelSelectedBookmarkMarker(it)
                    } else {
                        viewmodel.cancelSelectedMarker(it)
                    }

                    persistenetBottomSheet.state = STATE_HIDDEN
                    viewmodel._bottomSheetState.value = 0
                    bottomSheetSate = 0
                    viewmodel._isShowBottomSheetTab.value = false

                }
            }
        })


        binding.actionDownFloatingButton.setOnClickListener {
            persistenetBottomSheet.state = STATE_COLLAPSED

            viewmodel._bottomSheetState.value = 1
            bottomSheetSate = 1
            binding.bottomSheet.typeTextViewStep1.text = viewmodel.selectedMarker.value!!.category + " ‧ " +getVeganTypeColor2Text(viewmodel.selectedMarker.value!!.veganTypeColor)
        }


        binding.locationFloatingButton.setOnClickListener {
            checkOnOffGPS()
        }

        binding.bottomSheet.backBtn.setOnClickListener {
            viewmodel._isShowBottomSheetTab.value = true
            persistenetBottomSheet.state = STATE_COLLAPSED
            viewmodel._bottomSheetState.value = 1
            bottomSheetSate = 1
        }

        binding.searchBar.setOnClickListener {
            // 선택되어 있는 마커 취소
            viewmodel.cancelSelectedMarker(viewmodel._selectedMarker.value)

            val searchIntent = Intent(requireContext(), Search::class.java)
            val centerPostion = naver_map!!.cameraPosition
            val centerLatLng = centerPostion.target

            searchIntent.putExtra("NaverMapOfX", centerLatLng.longitude)
            searchIntent.putExtra("NaverMapOfY",  centerLatLng.latitude)

            viewmodel._bottomSheetState.value = 0
            bottomSheetSate = 0
            persistenetBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            viewmodel._isShowBottomSheetTab.value = false

            startActivityForResult(searchIntent, getString(R.string.SEARCH_RESULT_OK).toInt())
        }

        binding.bottomSheet.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"

            val blogUrlAOS = "https://play.google.com/store/apps/details?id=com.aviro.android"
            val blogUrlIOS = "https://apps.apple.com/app/6449352804"
            val content = "[어비로]\n${bottomSheetViewmodel.restaurantSummary.value!!.title}\n" +
                    "${bottomSheetViewmodel.restaurantSummary.value!!.address}\n\n" +
                    "[플레이스토어]\n${blogUrlAOS}\n\n" +
                    "[앱스토어]\n${blogUrlIOS}"

            intent.putExtra(Intent.EXTRA_TEXT,"$content")

            val chooserTitle = "[어비로]"
            startActivity(Intent.createChooser(intent, chooserTitle))

        }

        // 즐겨찾기 추가
        binding.bottomSheet.likeBtn.setOnClickListener {
            bottomSheetViewmodel.updateBookmark()
             }

        // 필터링 기능
        binding.filterDish.setOnClickListener {

            if(viewmodel.categoryFilter.value?.get(0) == false) {
                // 필터 적용
                viewmodel.markerList.value?.forEach { mapMarker ->
                    if(mapMarker.category == "식당") {
                        mapMarker.marker.isVisible = true
                    } else {
                        // 필터링 적용된 경우 (true) / 적용안 된 경우 (false)
                        viewmodel.getCategoryFilterBool(mapMarker.category)?.let {
                            if(!it) { mapMarker.marker.isVisible = false }
                        }
                    }
                }

                viewmodel.updateCategoryFilter(0, true)

            } else {
                viewmodel.markerList.value?.map {
                    if(it.category == "식당") {
                        it.marker.isVisible = false
                    }
                }

                viewmodel.updateCategoryFilter(0, false)
            }

            binding.filterDish.startAnimation(getShowingAlphaAnimation())

        }

        binding.filterCafe.setOnClickListener {
            if(viewmodel.categoryFilter.value?.get(1) == false) {
                // 필터 적용
                viewmodel.markerList.value?.forEach { mapMarker ->
                    if (mapMarker.category == "카페") {
                        mapMarker.marker.isVisible = true
                    } else {
                        // 식당 아닌 경우
                        // 필터링 적용된 경우 (true) / 적용안 된 경우 (false)
                        viewmodel.getCategoryFilterBool(mapMarker.category)?.let {
                            if (!it) {
                                mapMarker.marker.isVisible = false
                            }
                        }
                    }
                }
                binding.filterCafe.startAnimation(getShowingAlphaAnimation())
                viewmodel.updateCategoryFilter(1, true)

            } else {
                viewmodel.markerList.value?.map {
                    if(it.category == "카페") {
                        it.marker.isVisible = false
                    }
                }
                viewmodel.updateCategoryFilter(1, false)
            }
            //binding.filterCancelBtn.visibility = View.VISIBLE

        }

        binding.filterBakery.setOnClickListener {
            if(viewmodel.categoryFilter.value?.get(2) == false) {
                // 필터 적용
                viewmodel.markerList.value?.forEach { mapMarker ->
                    if (mapMarker.category == "빵집") {
                        mapMarker.marker.isVisible = true
                    } else {
                        // 식당 아닌 경우
                        // 필터링 적용된 경우 (true) / 적용안 된 경우 (false)
                        viewmodel.getCategoryFilterBool(mapMarker.category)?.let {
                            if (!it) {
                                mapMarker.marker.isVisible = false
                            }
                        }
                    }
                }

                viewmodel.updateCategoryFilter(2, true)

            } else {
                viewmodel.markerList.value?.map {
                    if(it.category == "빵집") {
                        it.marker.isVisible = false
                    }
                }
                viewmodel.updateCategoryFilter(2, false)
            }
            binding.filterBakery.startAnimation(getShowingAlphaAnimation())

        }

        binding.filterBar.setOnClickListener {
            if(viewmodel.categoryFilter.value?.get(3) == false) {
                // 필터 적용
                viewmodel.markerList.value?.forEach { mapMarker ->
                    if (mapMarker.category == "술집") {
                        mapMarker.marker.isVisible = true
                    } else {
                        // 식당 아닌 경우
                        // 필터링 적용된 경우 (true) / 적용안 된 경우 (false)
                        viewmodel.getCategoryFilterBool(mapMarker.category)?.let {
                            if (!it) {
                                mapMarker.marker.isVisible = false
                            }
                        }
                    }
                }
                viewmodel.updateCategoryFilter(3, true)

            } else {
                viewmodel.markerList.value?.map {
                    if(it.category == "술집") {
                        it.marker.isVisible = false
                    }
                }
                viewmodel.updateCategoryFilter(3, false)
            }
            binding.filterBar.startAnimation(getShowingAlphaAnimation())
        }

        // 필터링 취소 기능 (모든 필터링 해제) -> 모든 마커에 다 달아줌
        binding.filterCancelBtn.setOnClickListener {
            // false인 겻들 다 표시해주기
            binding.filterCancelBtn.startAnimation(getHidingAlphaAnimation())
            binding.filteringContainer.startAnimation(getLeftTranslateAnimation())
            viewmodel.updateCategoryFilter(0, false)
            viewmodel.updateCategoryFilter(1, false)
            viewmodel.updateCategoryFilter(2, false)
            viewmodel.updateCategoryFilter(3, false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initObserver() {
        viewmodel.categoryFilter.observe(viewLifecycleOwner) {
            var trueCount = 0
            it.forEach { value ->
                if(value) {
                    trueCount += 1
                }
            }
            if(trueCount == 1) {
                // 필터 하나 클릭했을때 한번만
                binding.filterCancelBtn.startAnimation(getRightTranslateAnimation())
                binding.filteringContainer.startAnimation(getRightTranslateAnimation())
            }

            if(!(it[0] || it[1] || it[2] || it[3])) {
                // 모든 마커 보이게
                viewmodel.markerList.value?.map {
                    it.marker.isVisible = true
                }
            }
        }



        viewmodel._selectedMarker.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            // 검색바 텍스트 설정
            if(it == null){
                binding.searchbarTextView.text = "어디로 이동할까요?"
                binding.searchbarTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray3))

            } else {

                Log.d("selectedMarker","감지")

                bottomSheetViewmodel._selectedMarker.value = it

                setLocation(it.y, it.x)
                val distanceToUser = setDistance(it.x, it.y)
                binding.bottomSheet.distanceTextViewStep1.text = distanceToUser
                binding.bottomSheet.distanceTextViewStep2.text = distanceToUser

                viewmodel.getRestaurantSummary(it.placeId)
                bottomSheetViewmodel.getRestaurantInfo(it.placeId)
                bottomSheetViewmodel.getRestaurantTimetable(it.placeId)
                bottomSheetViewmodel.getRestaurantMenu(it.placeId)
                bottomSheetViewmodel.getRestaurantReview(it.placeId)

                // 바텀시트 UP
                viewmodel._isShowBottomSheetTab.value = true
                persistenetBottomSheet.state = STATE_COLLAPSED
                viewmodel._bottomSheetState.value = 1
                // 바텀시트 정보
                binding.bottomSheet.typeTextViewStep1.text = viewmodel.selectedMarker.value!!.category + " ‧ " +getVeganTypeColor2Text(viewmodel.selectedMarker.value!!.veganTypeColor)

                when(homeViewmodel.currentNavigation.value) {
                    HomeViewModel.WhereToGo.BOOKMARK -> {
                        // 북마크 목록에서 가게 화면 첫 진입 트래킹
                        AmplitudeUtils.placePresentFirst(viewmodel.selectedMarker.value!!.placeId, viewmodel.selectedMarker.value!!.title, viewmodel.selectedMarker.value!!.category,
                            "bookmark list in challenge tab", viewmodel.categoryFilter.value!!.get(0), viewmodel.categoryFilter.value!!.get(1), viewmodel.categoryFilter.value!!.get(2), viewmodel.categoryFilter.value!!.get(3))
                        homeViewmodel._currentNavigation.value =  HomeViewModel.WhereToGo.MAP
                    }
                    HomeViewModel.WhereToGo.REVIEW -> {
                        // 내가 쓴 리뷰 목록에서 가게 화면 첫 진입 트래킹
                        AmplitudeUtils.placePresentFirst(viewmodel.selectedMarker.value!!.placeId, viewmodel.selectedMarker.value!!.title, viewmodel.selectedMarker.value!!.category,
                            "registered review list in challenge tab", viewmodel.categoryFilter.value!!.get(0), viewmodel.categoryFilter.value!!.get(1), viewmodel.categoryFilter.value!!.get(2), viewmodel.categoryFilter.value!!.get(3))
                        homeViewmodel._currentNavigation.value =  HomeViewModel.WhereToGo.MAP
                    }
                    HomeViewModel.WhereToGo.RESTAURANT -> {
                        // 내가 등록한 가게 목록에서 가게 화면 첫 진입 트래킹
                        AmplitudeUtils.placePresentFirst(viewmodel.selectedMarker.value!!.placeId, viewmodel.selectedMarker.value!!.title, viewmodel.selectedMarker.value!!.category,
                            "registered place list in challenge tab", viewmodel.categoryFilter.value!!.get(0), viewmodel.categoryFilter.value!!.get(1), viewmodel.categoryFilter.value!!.get(2), viewmodel.categoryFilter.value!!.get(3))
                        homeViewmodel._currentNavigation.value = HomeViewModel.WhereToGo.MAP
                    }

                    HomeViewModel.WhereToGo.REGISTER -> {}
                    HomeViewModel.WhereToGo.MYPAGE -> {}

                    HomeViewModel.WhereToGo.MAP -> {
                        AmplitudeUtils.placePresentFirst(viewmodel.selectedMarker.value!!.placeId, viewmodel.selectedMarker.value!!.title, viewmodel.selectedMarker.value!!.category,
                            "marker", viewmodel.categoryFilter.value!!.get(0), viewmodel.categoryFilter.value!!.get(1), viewmodel.categoryFilter.value!!.get(2), viewmodel.categoryFilter.value!!.get(3))

                    }

                    null -> {}
                }
            }
        })

        viewmodel._restaurantSummary.observe(viewLifecycleOwner) {

            bottomSheetViewmodel._isLike.value = it.bookmark

            TabLayoutMediator(binding.bottomSheet.fragmentBottomsheetStep2.tabLayout, binding.bottomSheet.fragmentBottomsheetStep2.bottomsheetViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = "홈"
                    1 -> tab.text = "메뉴"
                    2 -> tab.text = "후기 (${it.commentCount})"
                }
            }.attach()

            binding.searchbarTextView.text = it.title
            binding.searchbarTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray0))
            binding.bottomSheet.nameTextView.text = it.title
            bottomSheetViewmodel._restaurantSummary.value = it

        }

        viewmodel.errorLiveData.observe(viewLifecycleOwner) {
            it?.let { errorMsg ->
                AviroDialogUtils.createOneDialog(
                    requireContext(),
                    "Error",
                    "${it}",
                    "확인"
                ).show()
            }
        }

        viewmodel.diallogLiveData.observe(viewLifecycleOwner) {

            persistenetBottomSheet.state = STATE_COLLAPSED
            viewmodel._bottomSheetState.value = 1
            bottomSheetSate = 1

            AviroDialogUtils.createOneDialog(requireContext(),
            "신고가 완료되었어요",
            "3건 이상의 신고가 들어오면\n" +
                    "가게는 자동으로 삭제돼요.",
                "확인").show()
        }

        viewmodel.noticeData.observe(viewLifecycleOwner) {
            it?.let {
                checkPromotion()
            }
        }
    }

    fun setBottomSheetTabRevieAmount(newReviewAmount : Int) {
        binding.bottomSheet.fragmentBottomsheetStep2.tabLayout.getTabAt(2)!!.text = "후기 (${newReviewAmount})"
    }


    // 지도만 준비되면 퍼미션과 상관없이 동작
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        naver_map = naverMap
        NaverMapOfX = naver_map!!.cameraPosition.target.longitude
        NaverMapOfY = naver_map!!.cameraPosition.target.latitude

        naver_map!!.setOnMapClickListener { pointF, latLng ->
            // 다른 마커 터치
            if(viewmodel.isShowBottomSheetTab.value == false) {
                // 북마크 상태인지
                if(viewmodel.isFavorite.value == true) {
                    viewmodel.cancelSelectedBookmarkMarker(viewmodel._selectedMarker.value)
                } else {
                    viewmodel.cancelSelectedMarker(viewmodel._selectedMarker.value)
                }

                val bottomSheet = binding.bottomSheetLayout
                val persistenetBottomSheet = BottomSheetBehavior.from(bottomSheet)
                persistenetBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN


                viewmodel._selectedMarker.value = null
                viewmodel._bottomSheetState.value = 0
            }
        }


        naverMap.uiSettings.isScaleBarEnabled = false
        naverMap.uiSettings.isCompassEnabled = false
        naverMap.uiSettings.isZoomControlEnabled = false
        naverMap.uiSettings.isTiltGesturesEnabled = false

        // GPS 체크 및 퍼미션 체크 (한번허용, 항상 허용, 거부)
        checkOnOffGPS()
        // 위치 추적 기능 사용 여부 확인 (퍼미션 체크 이루어짐)
        locationSource = FusedLocationSource(this, getString(R.string.FUSED_LOCATION_CODE).toInt())

        // 맵 객체가 다시 생길 때 화면에 마커를 모두 다시 그림
        viewmodel.updateMap(naverMap, true)
        viewmodel._isFirst.value = false  // onResume에서 다시 그려짐 방지

    }

    override fun onResume() {
        super.onResume()

        checkIsFromChallenge()

        if(viewmodel.isFirst.value == false && naver_map != null) {
            viewmodel.updateMap(naver_map!!, false)
            viewmodel._isFirst.value = false
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewmodel.removeBookmarkList()
        _binding = null
        naver_map = null
    }

    fun isTutorial() : Boolean {
        val prefs = requireContext().getSharedPreferences("aviro", Context.MODE_PRIVATE)
        val isMapTutorial = prefs.getBoolean("showMapTutorial", true)

        if (isMapTutorial) {
            // 처음 실행될 때의 작업 수행
            // "처음 실행 여부"를 false로 변경
            prefs.edit().putBoolean("showMapTutorial", false).apply()
        }

        return isMapTutorial
    }

    fun checkPromotion() {
       // 띄울 광고 있는지
            if(promotionPopUp == null) {
                promotionPopUp = NoticePopUp(requireContext(),  viewmodel.noticeData.value!!, homeViewmodel)
                //promotionPopUp!!.setData(viewmodel.noticeData.value!!)
            } else {
                // 데이터 셋팅 다시
                promotionPopUp!!.setData(viewmodel.noticeData.value!!)
            }

            // 광고 팝업 (24시간인지 아닌지 확인)
            val prefs = requireContext().getSharedPreferences("promotion_24hours", Context.MODE_PRIVATE)
            // 현재시간
            val lastTime = prefs.getLong("isShow", -1)

            if(lastTime.toInt() == -1) {
                // 보여줘도 됨
                promotionPopUp!!.show()
            }
            val currentTime = System.currentTimeMillis()
            val twentyFourHoursInMillis = 24 * 60 * 60 * 1000

            if (currentTime - lastTime >= twentyFourHoursInMillis) {
                // 24시간 경과 -> 보여줘도 됨
                prefs.edit().putLong("isShow", -1).apply()
                promotionPopUp!!.show()
            }

    }

    fun runTutorial() {

            // 튜토리얼 시작
            val parentActivity = activity as Home
            parentActivity.findViewById<ConstraintLayout>(R.id.home_container).isEnabled = false

            val tutorial1 = parentActivity.findViewById<FrameLayout>(R.id.tutoral1)
            tutorial1.visibility = View.VISIBLE
            tutorial1.isEnabled = true

            tutorial1.setOnClickListener {
                tutorial1.visibility = View.GONE
                tutorial1.isEnabled = false

                // 튜토리얼2 시작
                binding.filterDish.performClick()
                binding.filterCafe.performClick()

                val tutoral2 = parentActivity.findViewById<LinearLayout>(R.id.tutoral2)
                tutoral2.visibility = View.VISIBLE
                tutoral2.isEnabled = true

                tutoral2.setOnClickListener {
                    binding.filterCancelBtn.performClick()

                    tutoral2.visibility = View.GONE
                    tutoral2.isEnabled = false
                    parentActivity.findViewById<ConstraintLayout>(R.id.home_container).isEnabled = true
                    viewmodel.getPopInfo()
                }
            }


    }

    /*
    fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // 권한이 이미 있는 경우에 대한 처리
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION_REQUEST_CODE
            )

            } else {

            // 권한이 없을 경우 권한 요청
            AviroDialogUtils.createOneDialog()
            Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )


            }
    }

     */

    // GPS가 켜져있는지 확인
    private fun checkOnOffGPS(): Boolean {
        val locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // gps 켜져 있음
            // 위치 추적을 위한 위치 정보 권한 확인
            requestPermissions(permission_list, getString(R.string.PERMISSIONS_REQUEST_CODE).toInt())
        } else {
            settingGPS()
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun settingGPS() {

        AviroDialogUtils.createTwoDialog(requireContext(),
            "GPS 비활성화",
            "GPS를 켜져 있어야 비건맵 서비스를 이용할 수 있습니다.",
            "취소",
            "설정하기",
            {
                val callGPSSettingIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, getString(R.string.GPS_ENABLE_REQUEST_CODE).toInt())
                // GPS 설정하러 설정 화면 넘어감
            }).show()

    }

    fun setLocation(x : Double, y:Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(y, x))
            .animate(CameraAnimation.Linear,300) // 애니메이션 -> 거리에 따라 다른 애니메이션 적용해보기
        naver_map!!.moveCamera(cameraUpdate)
    }

    fun setDistance(place_lat : Double, place_lng : Double) : String {
        val manager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // GPS사용 가능
            // 위치 기준
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // GPS가 Null 반환시(최근 갱신된 위치 반환), 네트워크 위치 사용
                val user_location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                // onLocationChanged() 콜백으로 네트워크위치 사용시 보강 가능
                return  DistanceCalculator.distanceMyLocation(place_lat, place_lng, user_location!!.latitude, user_location.longitude)

            }
        }

        return DistanceCalculator.distanceMyLocation(place_lat, place_lng, NaverMapOfY!!, NaverMapOfX!!)

    }



    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        // 반응성 조절
        private val SWIPE_DISTANCE_THRESHOLD = 200
        private val SWIPE_VELOCITY_THRESHOLD = 200

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            val distanceX = e2.x - e1!!.x
            val distanceY = e2.y - e1.y
            if (Math.abs(distanceX) < Math.abs(distanceY)
                && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD
                && Math.abs(distanceY) > SWIPE_VELOCITY_THRESHOLD
            ) {
                val bottomSheet = binding.bottomSheetLayout
                val persistenetBottomSheet = BottomSheetBehavior.from(bottomSheet)

                if (distanceY < 0) {
                    if (viewmodel._bottomSheetState.value == 1) { //step == 1 persistenetBottomSheet.state == STATE_COLLAPSED
                        viewmodel._isShowBottomSheetTab.value = true

                        persistenetBottomSheet.state = STATE_HALF_EXPANDED
                        viewmodel._bottomSheetState.value = 2
                        bottomSheetSate = 2
                        binding.bottomSheet.typeTextViewStep2.text = getVeganTypeColor2Text(viewmodel.selectedMarker.value!!.veganTypeColor)

                        // 바텀시트 절반 진입 트래킹
                        AmplitudeUtils.placePresentHalf(viewmodel.selectedMarker.value!!.placeId, viewmodel.selectedMarker.value!!.title, viewmodel.selectedMarker.value!!.category)

                    } else if (viewmodel._bottomSheetState.value == 2) {
                        persistenetBottomSheet.state = STATE_EXPANDED

                        viewmodel._bottomSheetState.value = 3
                        bottomSheetSate = 3

                    }
                } else if(distanceY > 0) {
                    if (viewmodel._bottomSheetState.value == 2) {

                        viewmodel._isShowBottomSheetTab.value = true
                        persistenetBottomSheet.state = STATE_COLLAPSED

                        //viewmodel._BottomSheetStep1.value = true
                        viewmodel._bottomSheetState.value = 1
                        bottomSheetSate = 1
                        binding.bottomSheet.typeTextViewStep1.text = viewmodel.selectedMarker.value!!.category + " ‧ " + getVeganTypeColor2Text(viewmodel.selectedMarker.value!!.veganTypeColor)

                    }

                }
                return true
            }
            return false
        }
    }



    // GPS 권한 설정 콜백
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            getString(R.string.GPS_ENABLE_REQUEST_CODE).toInt() -> {
                //사용자가 GPS 활성 시켰는지 검사
                requestPermissions(
                    permission_list,
                    getString(R.string.PERMISSIONS_REQUEST_CODE).toInt()
                )
            }

            getString(R.string.SEARCH_RESULT_OK).toInt() -> {
                if (data != null) {
                    val serahed_item =
                        data.getParcelableExtra<SearchedRestaurantItem>("search_item")

                    // 검색한 위치로 이동
                    setLocation(serahed_item!!.x.toDouble(), serahed_item.y.toDouble())

                    // 검색바 이름 변경
                    binding.searchbarTextView.text = serahed_item.placeName
                    binding.searchbarTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray0))

                    if (serahed_item.placeId != null) {
                        // 검색한 가게가 마커
                        val markerList = viewmodel._markerList.value!!
                        val searchMarker = markerList.filter { it.placeId == serahed_item.placeId }.first()
                        //viewmodel._selectedMarker.value = searchMarker
                        searchMarker.marker.performClick()

                        // 검색한 가게 첫 진입 트래킹
                        AmplitudeUtils.placePresentFirst(serahed_item.placeId!!, serahed_item.placeName, serahed_item.veganType.category!!,
                            "search", viewmodel.categoryFilter.value!!.get(0), viewmodel.categoryFilter.value!!.get(1), viewmodel.categoryFilter.value!!.get(2), viewmodel.categoryFilter.value!!.get(3))

                    }

                }
            }
        }
    }

        // 위치 권한 설정 콜백
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            when (requestCode) {
                // 위치 추적 기능 사용
                getString(R.string.FUSED_LOCATION_CODE).toInt() -> {
                    // 위치 퍼미션 허용 안 함
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                        //퍼미션 요청
                        requestPermissions(
                            permission_list,
                            getString(R.string.PERMISSIONS_REQUEST_CODE).toInt()
                        )
                    } else {
                        // 위치 추적 설정
                        naver_map!!.locationSource = locationSource
                        naver_map!!.locationTrackingMode = LocationTrackingMode.Follow
                    }

                }
                // 위치 퍼미션 사용
                getString(R.string.PERMISSIONS_REQUEST_CODE).toInt() -> {
                    Log.d("위치 퍼미션 콜백", "위치 퍼미션 콜백")
                    var check = true
                    for (isGranted in grantResults) {
                        if (isGranted == PackageManager.PERMISSION_DENIED) {
                            check = false
                            // 권한 거절시 Dialog로 다시 물어봄
                            MaterialAlertDialogBuilder(
                                ContextThemeWrapper(requireContext(), R.style.Base_Theme_AVIRO)
                            )
                                .setTitle("위치 정보 액세스 권한 설정")
                                .setMessage("위치정보 이용에 대한 액세스 권한이 없어요\n앱 설정으로 가서 액세스 권한을 수정할 수 있어요. 이동하시겠어요?")
                                .setNegativeButton("취소") { dialog, which ->
                                    // 권한 설정 거부
                                    // 위치 추적 없이 구동
                                    dialog.cancel()

                                }
                                .setPositiveButton("설정하기") { dialog, which ->
                                    // 권한 설정 화면 이동
                                    val callLocPermissonSettingIntent =
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", "com.aviro.android", null)
                                        )
                                    callLocPermissonSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(callLocPermissonSettingIntent)
                                    dialog.cancel()
                                }
                                .show()
                        }
                    }
                    if (check) {
                        naver_map!!.locationSource = locationSource
                        naver_map!!.locationTrackingMode = LocationTrackingMode.Follow
                    }
                }
            }
        }

    fun getShowingAlphaAnimation() : AlphaAnimation {
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 300

        return alphaAnimation
    }

    fun getRightTranslateAnimation() : TranslateAnimation {
        val animation = TranslateAnimation(0f, 5f, 0f, 0f)
        animation.duration = 300
        animation.fillAfter = true

        return animation
    }

    fun getHidingAlphaAnimation() : AlphaAnimation {
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 300

        return alphaAnimation
    }

    fun getLeftTranslateAnimation() : TranslateAnimation {
        val animation = TranslateAnimation(5f, 0f, 0f, 0f)
        animation.duration = 300
        animation.fillAfter = true

        return animation
    }


        private fun setupViewPager(viewPager: ViewPager2, tabLayout: TabLayout) {
            val adapter = object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int {
                    return fragList.size
                }

                override fun createFragment(position: Int): Fragment {
                    return fragList[position]
                }
            }
            viewPager.adapter = adapter


        }

        fun checkIsFromChallenge() {
            if (homeViewmodel.isNavigation.value == true) {
                when (homeViewmodel.currentNavigation.value) {
                    HomeViewModel.WhereToGo.BOOKMARK -> {
                        homeViewmodel.restaurantData.value?.let { myRestaurant ->
                          val marekrItem =
                                viewmodel._markerList.value?.filter { it.placeId == homeViewmodel.restaurantData.value!!.placeId }
                                    ?.firstOrNull()

                            // 마커 선택한 것으로
                            marekrItem?.let {
                                marekrItem.marker.performClick()
                                //viewmodel._selectedMarker.value = marekrItem
                            }
                        }

                    }

                    HomeViewModel.WhereToGo.RESTAURANT -> {
                        if(isTutorial()) {
                            runTutorial()
                        }
                        homeViewmodel.restaurantData.value?.let { myRestaurant ->
                           val marekrItem =
                                viewmodel._markerList.value?.filter { it.placeId == homeViewmodel.restaurantData.value!!.placeId }
                                    ?.firstOrNull()

                            // 마커 선택한 것으로
                            marekrItem?.let {
                                marekrItem.marker.performClick()
                                //viewmodel._selectedMarker.value = marekrItem
                            }
                        }
                    }

                    HomeViewModel.WhereToGo.REVIEW -> {
                        homeViewmodel.reviewData.value?.let {
                            val marekrItem =
                                viewmodel._markerList.value?.filter { it.placeId == homeViewmodel.reviewData.value!!.placeId }
                                    ?.firstOrNull()

                            // 마커 선택한 것으로
                            marekrItem?.let {
                                marekrItem.marker.performClick()
                                //viewmodel._selectedMarker.value = marekrItem

                                // 3단계
                                binding.viewmodel = viewmodel
                                persistenetBottomSheet.state = STATE_EXPANDED

                                viewmodel._bottomSheetState.value = 3
                                bottomSheetSate = 3
                                //bottomSheetViewmodel._bottomSheetSate.value = 3

                                // 리뷰 화면
                                binding.bottomSheet.fragmentBottomsheetStep2.bottomsheetViewPager.setCurrentItem(2,false)

                            }
                        }


                    }
                    else -> {}
                }
                homeViewmodel._isNavigation.value = false
            }

        }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("글자 크기 변경 에러","onDestroy:Map")
        viewmodel.cancelSelectedMarker(viewmodel._selectedMarker.value)

    }




}



