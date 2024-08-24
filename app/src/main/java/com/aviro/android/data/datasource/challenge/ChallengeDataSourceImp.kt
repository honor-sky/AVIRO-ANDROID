package com.aviro.android.data.datasource.challenge

import com.aviro.android.data.api.ChallengeService
import com.aviro.android.data.model.base.DataListResponse
import com.aviro.android.data.model.base.DataResponse
import com.aviro.android.data.model.challenge.ChallengeCommentResponse
import com.aviro.android.data.model.challenge.ChallengeInfoResponse
import com.aviro.android.data.model.challenge.NoticePopUpResponse
import javax.inject.Inject
import javax.inject.Named

class ChallengeDataSourceImp @Inject constructor(
    @Named("ChallengeServiceBase") private val challengeServiceBase : ChallengeService,
    @Named("ChallengeServiceSupport") private val challengeServiceSupport : ChallengeService,
) : ChallengeDataSource {

    // 챌린지 기간, 이름
    override suspend fun getChallengeInfo() : Result<DataResponse<ChallengeInfoResponse>> {
        return challengeServiceBase.getChallengeInfo()
    }
    override suspend fun getChallengeComment() : Result<DataResponse<ChallengeCommentResponse>> {
        return challengeServiceBase.getChallengeComment()
    }
    override suspend fun getChallengePopUp() : Result<DataListResponse<NoticePopUpResponse>> {
        return challengeServiceSupport.getChallengePopUp()
    }

}