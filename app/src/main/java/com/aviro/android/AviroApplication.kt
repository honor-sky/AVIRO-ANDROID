package com.aviro.android

import android.app.Application
import android.content.Context
import android.util.Log
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.DefaultTrackingOptions
import com.aviro.android.common.AmplitudeUtils
import com.aviro.android.presentation.sign.GoogleSignInManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class AviroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Naver SDK 초기화
        val naverClientId = com.aviro.android.BuildConfig.NAVER_LOGIN_CLIENT_ID
        val naverClientSecret =  com.aviro.android.BuildConfig.NAVER_LOGIN_CLIENT_SECRET
        val naverClientName =  "NAVER_LOGIN_SERVICE"

        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)

        // Kakao SDK 초기화
        KakaoSdk.init(this, com.aviro.android.BuildConfig.KAKAO_SDK_KEY)

        // 구글 초기화
        GoogleSignInManager.initialize(this, BuildConfig.GOOGLE_CLIENT_ID)

        // Amplitude 변수 초기화
        if (!BuildConfig.DEBUG) {
            val amplitude = Amplitude(
                Configuration(
                    apiKey = BuildConfig.AMPLITUDE_API_KEY,
                    context = applicationContext,
                    defaultTracking = DefaultTrackingOptions(
                        appLifecycles = false,
                        sessions = false,
                        deepLinks = false,
                        screenViews = false
                    )
                )
            )

            AmplitudeUtils.init(amplitude)

        }

    }


}