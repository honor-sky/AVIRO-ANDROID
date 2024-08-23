package com.aviro.android.presentation.home.ui.map

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.aviro.android.databinding.PromotionItemBinding
import com.aviro.android.presentation.entity.NoticePopUpEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners


class NoticePopUpAdapter(private val gotoUrl: (String) -> Unit,
                         private val gotoApp: (NoticePopUpEvent) -> Unit) : BaseAdapter() {

    // 이미지, 버튼색, 메서드
    private lateinit var imageList : List<String>
    private lateinit var eventList : List<String>
    private lateinit var urlList : List<String>
    private lateinit var colorList : List<String>

    override fun getCount(): Int {
        return imageList.size
    }

    override fun getItem(p0: Int): String {
       return imageList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {

        val layoutInflater = LayoutInflater.from(parent!!.context)
        val binding = PromotionItemBinding.inflate(layoutInflater, parent, false)


        Glide.with(parent.context)
            .load(imageList[position])
            .transform(CenterCrop(), GranularRoundedCorners(30f, 30f, 0f, 0f)) // 상단 모서리 둥글게
            .into(binding.popupImg)

        val shapeDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(ContextCompat.getColor(parent.context, colorList[position]!!.toInt())) // 매번 바뀔 수 있음
            cornerRadii = floatArrayOf(
                0f, 0f,
                0f, 0f,
                30f, 30f, // Bottom right radius in px
                30f, 30f // Bottom left radius in px
            )
        }

        binding.popupBtn.background = shapeDrawable

        binding.popupBtn.setOnClickListener {
            if(eventList[position] == "") {
                gotoUrl(urlList[position])
            } else {
                // event에 따라 내부 함수 호출
                val event = NoticePopUpEvent.fromEvent(eventList[position])
                gotoApp(event ?: NoticePopUpEvent.UNKNOWN)
            }

        }


        return binding.root
    }

    fun setImgData(imageList : List<String>) {
        imageList.let {
            this.imageList = imageList
        }
    }

    fun setEventData(eventList : List<String>) {
        eventList.let {
            this.eventList = imageList
        }
    }

    fun setUrlData(urlList : List<String>) {
        urlList.let {
            this.urlList = imageList
        }
    }

    fun setColorData(colorList : List<String>) {
        colorList.let {
            this.colorList = imageList
        }
    }



}