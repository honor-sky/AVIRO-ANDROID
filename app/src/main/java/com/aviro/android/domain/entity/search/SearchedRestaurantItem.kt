package com.aviro.android.domain.entity.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable



@Parcelize
data class SearchedRestaurantItem(
    var index : Int,
    var placeId : String?,
    val placeName : String,
    val addressName : String,
    val roadAddressName : String,
    val phone : String?,
    var distance : String,
    val x : String,
    val y : String,
    val veganType :VeganOptions
    ): Parcelable

@Parcelize
data class VeganOptions(
    var category : String?,
    var allVegan: Boolean,
    var someMenuVegan: Boolean,
    var ifRequestVegan: Boolean
): Parcelable

