package com.aviro.android.presentation.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.aviro.android.common.AmplitudeUtils
import com.aviro.android.databinding.PreSearchedWordBinding
import com.aviro.android.presentation.entity.SearchType

class PreSearchAdapter(private val searchViewModel : SearchViewModel, private val searchBar : EditText): RecyclerView.Adapter<PreSearchAdapter.PreSearchViewHolder>() {

    var preSearchedList: MutableList<String>? = null
    //var preSearchedList: MutableMap<String, Int>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreSearchViewHolder {
        return PreSearchViewHolder.from(parent, searchViewModel, searchBar)
    }

    override fun onBindViewHolder(holder: PreSearchViewHolder, position: Int) {
        if (preSearchedList != null) {
            holder.bind(preSearchedList!![position])
        }
    }

    override fun getItemCount() = preSearchedList?.size ?: 0


    // 뷰홀더 클래스
    class PreSearchViewHolder private constructor(
        val binding: PreSearchedWordBinding,
        val searchViewModel: SearchViewModel,
        val searchBar : EditText
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.word.text = item
            binding.viewmodel = searchViewModel

            binding.cancelBtn.setOnClickListener {
                searchViewModel.removePreSearchedWord(item)
            }
            binding.word.setOnClickListener {
                // 이전 검색어 클릭
                searchViewModel._searchType.value = SearchType.PRE.typeName

                searchBar.setText(item)
                searchViewModel._keyword.value = item
                searchViewModel.initList()
                searchViewModel.onEditTextFocusChanged(searchBar, true)

            }
        }

        companion object {
            fun from(parent: ViewGroup, searchViewModel: SearchViewModel, searchBar : EditText): PreSearchViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PreSearchedWordBinding.inflate(layoutInflater, parent, false)

                return PreSearchViewHolder(binding, searchViewModel, searchBar)
            }
        }
    }



}


