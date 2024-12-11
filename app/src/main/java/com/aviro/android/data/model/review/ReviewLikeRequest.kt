package com.aviro.android.data.model.review


data class ReviewLikeRequest(
    val placeId : String,
    val commentId : String,
    val userId : String
)
