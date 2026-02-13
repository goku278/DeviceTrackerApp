package com.ominfo.deviceusagetracker.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfigManager {

    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    fun init() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour in production
        }

        remoteConfig.setConfigSettingsAsync(configSettings)

        // Default values
        remoteConfig.setDefaultsAsync(
            mapOf(
                "ads_global_enable" to true,
                "splash_ad_type" to "interstitial",
                "home_interstitial_enable" to true,
                "home_interstitial_counter" to 3,
                "banner_enable" to true,
                "native_enable" to true,
                "rewarded_enable" to false,
                "ad_priority" to "admob_first" // admob_first or facebook_first
            )
        )

        remoteConfig.fetchAndActivate()
    }

    // Ads Control
    fun adsEnabled(): Boolean = remoteConfig.getBoolean("ads_global_enable")

    // Splash Ad
    fun splashAdType(): String = remoteConfig.getString("splash_ad_type") // "interstitial" or "app_open"

    // Home Interstitial
    fun homeInterstitialEnabled(): Boolean = remoteConfig.getBoolean("home_interstitial_enable")
    fun homeInterstitialCounter(): Int = remoteConfig.getLong("home_interstitial_counter").toInt()

    // Banner Ad
    fun bannerEnabled(): Boolean = remoteConfig.getBoolean("banner_enable")

    // Native Ad
    fun nativeEnabled(): Boolean = remoteConfig.getBoolean("native_enable")

    // Rewarded Ad
    fun rewardedEnabled(): Boolean = remoteConfig.getBoolean("rewarded_enable")

    // Ad Priority
    fun adPriority(): String = remoteConfig.getString("ad_priority") // "admob_first" or "facebook_first"
}
