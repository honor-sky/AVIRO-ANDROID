package com.aviro.android.presentation.aviro_dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aviro.android.common.AmplitudeUtils
import com.aviro.android.databinding.PromotionPopupBinding
import com.aviro.android.domain.entity.challenge.NoticePopUp
import com.aviro.android.presentation.entity.NoticePopUpEvent
import com.aviro.android.presentation.home.HomeViewModel
import com.aviro.android.presentation.home.ui.map.NoticePopUpAdapter

class NoticePopUp(context : Context, val data : List<NoticePopUp>, val viewmodel : HomeViewModel) : Dialog(context) {

    private lateinit var binding: PromotionPopupBinding
    val prefs = context.getSharedPreferences("promotion_24hours", Context.MODE_PRIVATE)

    private var adapter: NoticePopUpAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PromotionPopupBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        val widthInDp = 311
        val density = context.resources.displayMetrics.density
        val widthInPixels = (widthInDp * density).toInt()
        window?.setLayout(widthInPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        setCanceledOnTouchOutside(false)
        setCancelable(true)

        initAdapter()
        setData(data)


        binding.hoursCancelBtn.setOnClickListener {
            prefs.edit().putLong("isShow", System.currentTimeMillis()).apply()
            dismiss()

            AmplitudeUtils.didTappedNoMoreShowWelcome()
        }

        binding.cancelBtn.setOnClickListener {
            prefs.edit().putLong("isShow", -1).apply()
            dismiss()

            AmplitudeUtils.didTappedCloseWelcome()
        }
    }

    fun initAdapter() {
        if(adapter == null) {
            adapter = NoticePopUpAdapter(gotoUrl = { url -> gotoUrl(url)}, gotoApp = { event -> gotoApp(event)})
            binding.promotionListView.adapter = adapter
        }
    }

    fun setData(noticeData : List<NoticePopUp>) {
        //Log.d("noticeData","${noticeData}")
        initAdapter()
        adapter!!.setData(noticeData)
            //listOf(NoticePopUp(title="챌린지", image_url="https://aviro-images.s3.ap-northeast-2.amazonaws.com/challenge_image.png", url=null, event="MTCH", button_color="GREEN", order=0),
            //            NoticePopUp(title="챌린지", image_url="https://aviro-images.s3.ap-northeast-2.amazonaws.com/challenge_image.png", url=null, event="MTCH", button_color="GREEN", order=0))
    }

    // 외부 이동 이벤트
    fun gotoUrl(url : String) {
        val url_intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(url_intent)
        dismiss()
    }


    // 앱 내부 이벤트
    fun gotoApp(event : NoticePopUpEvent) {

        when(event) {
            NoticePopUpEvent.MTCH -> {
                viewmodel._currentNavigation.value = HomeViewModel.WhereToGo.MYPAGE
                viewmodel._isNavigation.value = true

                AmplitudeUtils.challengePresent()
                AmplitudeUtils.didTappedCheckWelcome()
            }

            NoticePopUpEvent.UNKNOWN -> {

            }
        }
        dismiss()

    }



}