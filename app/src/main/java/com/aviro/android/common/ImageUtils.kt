package com.aviro.android.common

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.LruCache
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object ImageUtils {

    // 이미지 메모리 캐시 할당
    private val imageMaxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val imageCacheSize = imageMaxMemory / 8
    private val imageMemoryCache = LruCache<String, Bitmap>(imageCacheSize) // <uri, bitmap>


    // 캐시에 이미지 넣기
    fun putImageToCache(key: String, bitmap: Bitmap) {
        imageMemoryCache.put(key, bitmap)
    }

    // 캐시에서 이미지 가져오기
    fun getImageFromCache(key: String): Bitmap? {
        return imageMemoryCache.get(key)
    }

    // 캐시 비우기
    fun clearImageCache() {
        //imageMemoryCache.re
    }

    suspend fun externalImageDownload(uri : String) : Bitmap? {
        // 외부 이미지 다운로드
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(uri)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

    fun internalImageDownload(uri : String, contentResolver : ContentResolver) : Bitmap? {
        // 내부 이미지 다운로드
        var bitmap : Bitmap? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri.toUri())
                bitmap = ImageDecoder.decodeBitmap(source)
                putImageToCache(uri, bitmap)
                Log.d("bitmap","$bitmap")
                return bitmap
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri.toUri())
                putImageToCache(uri, bitmap)
                Log.d("bitmap","$bitmap")
                return bitmap
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return bitmap
    }




}