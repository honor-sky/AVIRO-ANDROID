package com.aviro.android.presentation

import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {


    override fun onAttach(context: Context) {
        super.onAttach(context.applyOverrideConfiguration())
    }


    private fun Context.applyOverrideConfiguration(): Context {
        val configuration = Configuration(resources.configuration)
        configuration.fontScale = 1.0f // 시스템 글꼴 크기 설정 무시
        return createConfigurationContext(configuration)
    }
}