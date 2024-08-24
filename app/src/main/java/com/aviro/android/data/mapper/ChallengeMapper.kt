package com.aviro.android.data.mapper


import com.aviro.android.data.model.challenge.*
import com.aviro.android.domain.entity.challenge.*
import com.aviro.android.domain.entity.member.MyRestaurant


fun ChallengeInfoResponse.toChallengeInfo() : ChallengeInfo {
    return ChallengeInfo(
        period = this.period,
        title = this.title
    )
}

fun ChallengeCommentResponse.toChallengeComment() : ChallengeComment {
    return ChallengeComment(
        comment = this.comment
    )
}


fun List<NoticePopUpResponse>.toChallengePopUp() : List<NoticePopUp> {
    val noticePopupList = this.map {
        NoticePopUp(
            title = it.title,
            image_url = it.image_url,
            url = it.url,
            event = it.event,
            button_color = it.button_color,
            order = it.order
        )
    }.toMutableList()

    return noticePopupList

}
