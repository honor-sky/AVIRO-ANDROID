package com.aviro.android.data.model.base

data class DataListResponse<T>(

val statusCode : Int,
val message : String?,
val data: List<T>?


)
