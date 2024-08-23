package com.aviro.android.presentation.entity

enum class NoticePopUpEvent(val event : String) {
    MTCH("MTCH"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromEvent(event: String): NoticePopUpEvent? {
            return values().find { it.event == event }
        }
    }
}

