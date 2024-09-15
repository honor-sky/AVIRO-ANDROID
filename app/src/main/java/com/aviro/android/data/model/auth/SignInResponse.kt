package com.aviro.android.data.model.auth

data class SignInResponse(
    val userId : String,
    val userName : String?,
    val userEmail : String,
    val nickname : String,
    val gender : String,
    val birthday : String,
    val marketingAgree : Int,
    )