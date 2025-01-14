package com.aviro.android.presentation

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.amplitude.android.Configuration

open class BaseActivity() : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    private fun Context.applyOverrideConfiguration(): Context {
        val configuration = android.content.res.Configuration(resources.configuration)
        configuration.fontScale = 1.0f // 시스템 글꼴 크기 설정 무시
        configuration.uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO
        return createConfigurationContext(configuration)
    }


}