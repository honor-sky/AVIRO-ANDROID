package com.aviro.android.domain.entity.auth

data class Sign(
    val userId : String,
    val userName : String?,
    val userEmail : String,
    val nickname : String,
    val gender : String?,
    val birthday : String?
)
