package com.aviro.android.data.datasource.member

import com.aviro.android.data.model.auth.SignInResponse
import com.aviro.android.data.model.base.BaseResponse
import com.aviro.android.data.model.base.DataResponse
import com.aviro.android.data.model.member.*
import com.aviro.android.data.model.member.BookmarkAddResponse

interface MemberDataSource {

    suspend fun checkNickname(nickname : NicknameCheckRequest) :  Result<DataResponse<NicknameCheckResponse>>
    suspend fun creatMember(newMember : SignUpRequest) : Result<DataResponse<SignInResponse>> // Result<Any>
    suspend fun getActivityCount(request : String) : Result<DataResponse<MemberHistoryResponse>>
    suspend fun getChallengeLevel(request : String) : Result<DataResponse<MemberLevelResponse>>
    suspend fun deleteMember(request : MemberWithdrawRequest) : Result<BaseResponse>
    suspend fun updateNickname(request : NicknameUpdateRequest) : Result<BaseResponse>
    suspend fun addBookmark(request : AddBookmarkRequest) : Result<BookmarkAddResponse>
    suspend fun deleteBookmark(place_id : String, user_id : String) : Result<BaseResponse>
    suspend fun getMyRestaurantList(user_id : String) : Result<DataResponse<MyRestaurantListResponse>>
    suspend fun getMyCommentList(user_id : String) : Result<DataResponse<MyCommentListResponse>>
    suspend fun getMyBookmarkList(user_id : String) : Result<DataResponse<MyBookmarkListResponse>>

    suspend fun deleteReivew(commentId : String, userId: String) : Result<BaseResponse>
    suspend fun updateReview(request : UpdateReviewRequest) : Result<BaseResponse>

}