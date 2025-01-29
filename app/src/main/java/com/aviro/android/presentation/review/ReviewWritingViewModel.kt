package com.aviro.android.presentation.review

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aviro.android.domain.usecase.challenge.GetChallengeInfo
import com.aviro.android.domain.usecase.member.UpdateMyReviewUseCase
import com.aviro.android.domain.usecase.retaurant.CreateRestaurantReviewUseCase
import com.aviro.android.presentation.entity.GalleryPhotoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ReviewWritingViewModel @Inject constructor (
    private val getChallengeInfo : GetChallengeInfo,
    private val createRestaurantReviewUseCase : CreateRestaurantReviewUseCase,
    private val updateMyReviewUseCase : UpdateMyReviewUseCase
) : ViewModel() {

    // 가게 정보
    private val _restaurantInfo = MutableStateFlow<Int>(0)
    val restaurantInfo: StateFlow<Int> = _restaurantInfo.asStateFlow()

    // 별점
    private val _ratingAmount = MutableStateFlow<Int>(0)
    val ratingAmount: StateFlow<Int> = _ratingAmount.asStateFlow()

    // 갤러리에서 선택된 이미지만
    private val _selectedPhotoList = MutableStateFlow<List<GalleryPhotoItem>>(emptyList())
    val selectedPhotoList: StateFlow<List<GalleryPhotoItem>> = _selectedPhotoList.asStateFlow()

    // 리뷰글
    private val _reviewContent = MutableStateFlow<String>("")
    val reviewContent: StateFlow<String> = _reviewContent.asStateFlow()


    // Review 리스트 (수정 가능하도록)


    // 에러 메세지


    // 이미지 가져오기
    // 앨범 리스트 가져오기

    // 선택된 이미지 처리 (선택 및 해제)
    fun toggleSelectedPhoto(photoItem : GalleryPhotoItem) {
        _selectedPhotoList.value = if (_selectedPhotoList.value.contains(photoItem)) {
            _selectedPhotoList.value - photoItem // 선택 해제
        } else {
            _selectedPhotoList.value + photoItem // 선택 추가
        }
    }


    // 해당 식당의 메뉴 정보를 받아와 태그로 생성
    fun getMenus() {

    }

    fun setRestaurantInfo() {

    }



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