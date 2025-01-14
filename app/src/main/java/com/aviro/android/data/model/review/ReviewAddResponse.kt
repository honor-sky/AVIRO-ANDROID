package com.aviro.android.data.model.review

data class ReviewAddResponse(
    //val statusCode : Int,
    //val message : String,
    val levelUp : Boolean,
    val userLevel : Int,
    val added_comment_num : Int,
    val isFirst : Boolean
)
