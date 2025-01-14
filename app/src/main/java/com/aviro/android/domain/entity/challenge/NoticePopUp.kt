package com.aviro.android.domain.entity.challenge

data class NoticePopUp(
    val title : String,
    val image_url : String,
    val url : String?,
    val event : String?,
    val button_color :String ,
    val order: Int // 순서
)
