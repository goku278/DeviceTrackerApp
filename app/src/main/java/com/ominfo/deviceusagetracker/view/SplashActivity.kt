package com.ominfo.deviceusagetracker.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ominfo.deviceusagetracker.config.RemoteConfigManager
import com.ominfo.deviceusagetracker.databinding.ActivitySplashBinding
import com.ominfo.deviceusagetracker.utils.AdsManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RemoteConfigManager.init()

        if (RemoteConfigManager.splashAdType() == "interstitial") {
            AdsManager.loadInterstitial(this)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, PermissionActivity::class.java))
            finish()
        }, 2000)
    }
}
