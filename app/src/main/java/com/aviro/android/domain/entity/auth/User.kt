package com.aviro.android.domain.entity.auth

data class User(
    val isMember : Boolean,
    val nickname : String,
    val gender : String?,
    val birthday : String?
)
