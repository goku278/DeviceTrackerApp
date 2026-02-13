package com.ominfo.deviceusagetracker.view

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.ominfo.deviceusagetracker.databinding.ActivityBlockBinding

class BlockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockBinding
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("category") ?: "Unknown"

        // Disable back button - now with proper import
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing - block back press
            }
        })

        // Set the category in the UI
        binding.tvCategory.text = category
        binding.tvMessage.text = "You've reached your daily limit for $category apps"

        startCountdown()
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(30000, 1000) { // 30 seconds
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvCountdown.text = "App will unlock in $seconds seconds"
            }

            override fun onFinish() {
                finish()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}