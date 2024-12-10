package com.aviro.android.presentation.review

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.aviro.android.domain.usecase.challenge.GetChallengeInfo
import com.aviro.android.domain.usecase.member.UpdateMyReviewUseCase
import com.aviro.android.domain.usecase.retaurant.CreateRestaurantReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewWritingViewModel @Inject constructor (
    private val getChallengeInfo : GetChallengeInfo,
    private val createRestaurantReviewUseCase : CreateRestaurantReviewUseCase,
    private val updateMyReviewUseCase : UpdateMyReviewUseCase
) : ViewModel() {

    // 가게 정보

    // 별점

    // 갤러리에서 선택된 이미지만

    // Review 리스트 (수정 가능하도록)

    // 리뷰글

    // 에러 메세지



}

/*
data class Review (
    val image : Bitmap,
    val menuTag : String,
    val mediaType : Int, // 1: 사진, 2: 동영상
    val order : Int // 순서
)
 */

data class GalleryImage (
    val image : Uri,
    val mediaType : Int = 1, // 1: 사진, 2: 동영상 // 지금은 사진만 가능
    val order : Int // 순서
)