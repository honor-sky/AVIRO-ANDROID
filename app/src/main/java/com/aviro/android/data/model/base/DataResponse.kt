package com.aviro.android.data.model.base

data class DataResponse<T>(

    val statusCode : Int,
    val message : String?,
    val data: T?

)