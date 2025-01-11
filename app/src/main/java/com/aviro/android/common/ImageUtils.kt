package com.aviro.android.common

import android.graphics.Bitmap
import android.util.LruCache
import com.aviro.android.data.model.marker.MarkerDAO

object ImageCacheUtils {

    // 이미지 메모리 캐시 할당
    private val imageMaxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val imageCacheSize = imageMaxMemory / 8
    private val imageMemoryCache = LruCache<String, Bitmap>(imageCacheSize) // <uri, bitmap>

    fun put(key: String, bitmap: Bitmap) {
        imageMemoryCache.put(key, bitmap)
    }

    fun get(key: String): Bitmap? {
        return imageMemoryCache.get(key)
    }

}