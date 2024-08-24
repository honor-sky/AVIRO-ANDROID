package com.aviro.android.presentation.home.ui.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aviro.android.R
import com.aviro.android.databinding.PromotionItemBinding
import com.aviro.android.domain.entity.challenge.NoticePopUp
import com.aviro.android.presentation.entity.NoticePopUpEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners



class NoticePopUpAdapter(private val gotoUrl: (String) -> Unit,
                         private val gotoApp: (NoticePopUpEvent) -> Unit) : RecyclerView.Adapter<NoticePopUpAdapter.NoticePopUpViewHolder>() {

    // 이미지, 버튼색, 메서드
    private  var noticeDataList : List<NoticePopUp>? = null


    fun setData(noticeDataList : List<NoticePopUp>) {
        noticeDataList.let {
            this.noticeDataList = noticeDataList
        }
    }

    inner class NoticePopUpViewHolder (val binding: PromotionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceType")
        fun bind(item: NoticePopUp) {

            Glide.with(binding.popupImg.context)
                .load(item.image_url)
                .transform(CenterCrop(), GranularRoundedCorners(30f, 30f, 0f, 0f)) // 상단 모서리 둥글게
                .into(binding.popupImg)

            val shapeDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                //setColor(ContextCompat.getColor(binding.popupBtn.context, colorCode)) // 매번 바뀔 수 있음
                setColor(Color.parseColor(item.button_color))
                cornerRadii = floatArrayOf(
                    0f, 0f,
                    0f, 0f,
                    30f, 30f,
                    30f, 30f
                )
            }
            binding.popupBtn.background = shapeDrawable


            binding.popupBtn.setOnClickListener {
                item.url?.let {
                    gotoUrl(item.url)
                } ?: run {
                    // event에 따라 내부 함수 호출
                    val event = NoticePopUpEvent.fromEvent(item.event ?: "")
                    gotoApp(event ?: NoticePopUpEvent.UNKNOWN)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticePopUpViewHolder {
        return NoticePopUpViewHolder(PromotionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NoticePopUpViewHolder, position: Int) {
        if (noticeDataList != null) {
            holder.bind(noticeDataList!![position])
        }
    }

    override fun getItemCount() = noticeDataList?.size ?: 0


}

/*
class NoticePopUpAdapter(private val gotoUrl: (String) -> Unit,
                         private val gotoApp: (NoticePopUpEvent) -> Unit) : BaseAdapter() {



    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {

        val layoutInflater = LayoutInflater.from(parent!!.context)
        val binding = PromotionItemBinding.inflate(layoutInflater, parent, false)


        Glide.with(parent.context)
            .load(imageList?.get(position))
            .transform(CenterCrop(), GranularRoundedCorners(30f, 30f, 0f, 0f)) // 상단 모서리 둥글게
            .into(binding.popupImg)

        val shapeDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            //val colorCode = colorList?.get(position)!!.toInt()
            setColor(ContextCompat.getColor(parent.context, R.color.Green)) // 매번 바뀔 수 있음
            cornerRadii = floatArrayOf(
                0f, 0f,
                0f, 0f,
                30f, 30f, // Bottom right radius in px
                30f, 30f // Bottom left radius in px
            )
        }

        binding.popupBtn.background = shapeDrawable

        binding.popupBtn.setOnClickListener {
            if(eventList?.get(position) ?: "" == "") {
                gotoUrl(urlList?.get(position) ?: "")
            } else {
                // event에 따라 내부 함수 호출
                val event = NoticePopUpEvent.fromEvent(eventList?.get(position) ?: "")
                gotoApp(event ?: NoticePopUpEvent.UNKNOWN)
            }

        }


        return binding.root
    }

}

 */