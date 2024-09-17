package com.aviro.android.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.aviro.android.BuildConfig
import com.aviro.android.R
import com.aviro.android.databinding.ActivitySplashBinding
import com.aviro.android.domain.entity.base.MappingResult
import com.aviro.android.presentation.BaseActivity
import com.aviro.android.presentation.guide.Guide
import com.aviro.android.presentation.home.Home
import com.aviro.android.presentation.sign.Sign
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class Splash : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewmodel: SplashViewModel by viewModels()

    val MY_REQEUST_CODE = 1000
    lateinit var appUpdateManager : AppUpdateManager
    //var diff : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)



        viewmodel.isSignIn.observe(this, Observer { isSignIn ->
            if (isSignIn == true) {
                // 어떤 로그인인지 확인
                startActivity(Intent(this@Splash, Home::class.java))
                finish()
            } else {
                startActivity(Intent(this@Splash, Sign::class.java))
                finish()
            }
        })

        //goToHomeOrGuideOrSignWithDelay()
        updateApp()

    }

    fun goToHomeOrGuideOrSignWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
        }, 5000)

        if (isFirstStartApp()) { // 앱 최초 실행인지 확인 필
            startActivity(Intent(this, Guide::class.java))
            finish()
        } else {
            runBlocking {
                val job = async(Dispatchers.IO) {
                    /*
                    // 구글 로그인 있는지 확인
                    val account = GoogleSignIn.getLastSignedInAccount(this@Splash)
                    if(account == null) {
                        // 다른 자동 로그인 확인
                        viewmodel.isSignIn()
                    } else {
                        // userId 있는지 확인
                        // 구글 자동로그인
                        viewmodel.isSignIn() // Google 로그인인데 userID 없으면 X ->
                    }
                     */
                    viewmodel.isSignIn()

                }
                job.await() // job이 완료될 때까지 대기
            }
        }
    }



    fun isFirstStartApp() : Boolean {
        val prefs = this.getSharedPreferences("aviro", MODE_PRIVATE)
        val firstRun = prefs.getBoolean("firstRun", true) // 처음엔 default 값 출력

        if (firstRun) {
            // 처음 실행될 때의 작업 수행
            // "처음 실행 여부"를 false로 변경
            prefs.edit().putBoolean("firstRun", false).apply()
        }

        return firstRun
    }



    fun updateApp() {

        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo


        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // 업데이트가 있는 경우

                val nowVersionCode = BuildConfig.VERSION_CODE
                val newVersionCode = appUpdateInfo.availableVersionCode()
                Log.d("updateApp","nowVersionCode : ${nowVersionCode}, newVersionCode : ${newVersionCode}")

                /* 업데이트 규칙 */
                val diff = newVersionCode - nowVersionCode
                if(diff >= 5) {
                    // 즉시 (+10 이상)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQEUST_CODE
                    )
                    Log.d("SplashSignIn","무한대기")
                } else {
                    goToHomeOrGuideOrSignWithDelay()
                }

            } else {
                // 업데이트가 없는 경우
                Log.d("updateApp","업데이트 없음")
                goToHomeOrGuideOrSignWithDelay()
            }
        }


        Log.d("updateApp","리스너 없음")

        goToHomeOrGuideOrSignWithDelay()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == MY_REQEUST_CODE) {
            if (resultCode != RESULT_OK) {
                // 업데이트 실패
                // 다시 업데이트 창 띄움
                Log.d("SplashSignIn","업데이트 실패")
                updateApp()
            } else {
                //goToHomeOrGuideOrSignWithDelay()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener {
                // 업데이트가 진행중이었음
                if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    updateApp()
                }
            }
    }
}
