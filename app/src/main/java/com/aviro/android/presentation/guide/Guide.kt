package com.aviro.android.presentation.guide

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.aviro.android.databinding.ActivityGuideBinding
import com.aviro.android.presentation.BaseActivity


class Guide : BaseActivity() {

    private lateinit var binding: ActivityGuideBinding
    lateinit var viewmodel: GuideViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = GuidePagerAdapter(this)

        viewmodel = GuideViewModel(adapter)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this


    }

    override fun attachBaseContext(newBase: Context) {
        val configuration = android.content.res.Configuration(newBase.resources.configuration)
        configuration.fontScale = 1.0f // 시스템 글꼴 크기 설정 무시
        applyOverrideConfiguration(configuration)
        super.attachBaseContext(newBase)
    }


}
