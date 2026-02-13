package com.ominfo.deviceusagetracker.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.ominfo.deviceusagetracker.R
import com.ominfo.deviceusagetracker.config.RemoteConfigManager

object AdsManager {
    private const val TAG = "AdsManager"

    // Test Ad Unit IDs
    private const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val ADMOB_NATIVE_ID = "ca-app-pub-3940256099942544/2247696110"

    // Ad instances
    private var admobInterstitial: InterstitialAd? = null
    private var admobNativeAd: NativeAd? = null
    private var interstitialCounter = 0

    // Callbacks
    interface InterstitialLoadCallback {
        fun onAdLoaded()
        fun onAdFailedToLoad(error: String)
    }

    interface NativeAdLoadCallback {
        fun onAdLoaded(adView: NativeAdView)
        fun onAdFailedToLoad(error: String)
    }

    /** Initialize Ads */
    fun initialize(context: Context) {
//        MobileAds.initialize(context) {}
        Log.d(TAG, "✅ AdsManager initialized")
    }

    // ============== INTERSTITIAL ADS ==============
    fun loadInterstitial(context: Context, callback: InterstitialLoadCallback? = null) {
        if (!RemoteConfigManager.adsEnabled()) {
            callback?.onAdFailedToLoad("Ads disabled by Remote Config")
            return
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, ADMOB_INTERSTITIAL_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    admobInterstitial = interstitialAd
                    Log.d(TAG, "✅ Interstitial loaded successfully")

                    // Setup dismiss callback
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "❌ Interstitial dismissed")
                            admobInterstitial = null
                            loadInterstitial(context) // Preload next
                        }
                        override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                            Log.e(TAG, "❌ Interstitial failed to show: ${error.message}")
                            admobInterstitial = null
                        }
                    }
                    callback?.onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "❌ Interstitial failed: ${error.message}")
                    callback?.onAdFailedToLoad(error.message)
                }
            })
    }

    fun showInterstitial(activity: Activity): Boolean {
        admobInterstitial?.let { ad ->
            ad.show(activity)
            return true
        } ?: run {
            Log.w(TAG, "No interstitial available")
            return false
        }
    }

    /** Counter-based interstitial (every N clicks) */
    fun incrementAndShowInterstitial(activity: Activity): Boolean {
        if (!RemoteConfigManager.adsEnabled() || !RemoteConfigManager.homeInterstitialEnabled()) {
            return false
        }

        interstitialCounter++
        Log.d(TAG, "Interstitial counter: $interstitialCounter")

        val counterLimit = RemoteConfigManager.homeInterstitialCounter()
        if (interstitialCounter >= counterLimit) {
            val shown = showInterstitial(activity)
            if (shown) {
                interstitialCounter = 0
                loadInterstitial(activity.applicationContext)
            }
            return shown
        }
        return false
    }

    // ============== NATIVE ADS ==============
    fun loadNativeAd(context: Context, callback: NativeAdLoadCallback) {
        if (!RemoteConfigManager.adsEnabled() || !RemoteConfigManager.nativeEnabled()) {
            callback.onAdFailedToLoad("Native ads disabled")
            return
        }

        val adLoader = AdLoader.Builder(context, ADMOB_NATIVE_ID)
            .forNativeAd { nativeAd: NativeAd ->
                Log.d(TAG, "✅ Native ad loaded successfully")
                admobNativeAd = nativeAd

                // Inflate YOUR perfect layout
                val nativeAdView = LayoutInflater.from(context)
                    .inflate(R.layout.native_ad_layout, null) as NativeAdView

                populateNativeAdView(nativeAd, nativeAdView)
                callback.onAdLoaded(nativeAdView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "❌ Native ad failed: ${error.message}")
                    callback.onAdFailedToLoad(error.message)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /** Populate YOUR native_ad_layout.xml perfectly */
    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Map all views from YOUR layout ✅
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // Populate headline & body (ALWAYS available)
        (adView.headlineView as? TextView)?.apply {
            text = nativeAd.headline
            visibility = android.view.View.VISIBLE
        }
        (adView.bodyView as? TextView)?.apply {
            text = nativeAd.body
            visibility = android.view.View.VISIBLE
        }

        // Call to action button
        (adView.callToActionView as? Button)?.apply {
            text = nativeAd.callToAction
            visibility = android.view.View.VISIBLE
        }

        // Icon (if available)
        nativeAd.icon?.let { icon ->
            (adView.iconView as? ImageView)?.apply {
                setImageDrawable(icon.drawable)
                visibility = android.view.View.VISIBLE
            }
        }

        // Optional fields (show only if data exists)
        nativeAd.price?.let { price ->
            (adView.priceView as? TextView)?.apply {
                text = price
                visibility = android.view.View.VISIBLE
            }
        }

        nativeAd.store?.let { store ->
            (adView.storeView as? TextView)?.apply {
                text = store
                visibility = android.view.View.VISIBLE
            }
        }

        nativeAd.advertiser?.let { advertiser ->
            (adView.advertiserView as? TextView)?.apply {
                text = advertiser
                visibility = android.view.View.VISIBLE
            }
        }

        nativeAd.starRating?.let { rating ->
            (adView.starRatingView as? RatingBar)?.apply {
                this.rating = rating.toFloat()
                visibility = android.view.View.VISIBLE
            }
        }

        // CRITICAL: Bind native ad to view
        adView.setNativeAd(nativeAd)
        Log.d(TAG, "✅ Native ad view populated")
    }

    // ============== BANNER ADS ==============
    fun loadBanner(adView: AdView) {
        if (!RemoteConfigManager.adsEnabled() || !RemoteConfigManager.bannerEnabled()) {
            adView.visibility = android.view.View.GONE
            return
        }

        adView.visibility = android.view.View.VISIBLE
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        Log.d(TAG, "📱 Banner ad loading...")
    }

    // ============== CLEANUP ==============
    fun destroy() {
        admobInterstitial = null
        admobNativeAd?.destroy()
        admobNativeAd = null
        Log.d(TAG, "🧹 AdsManager destroyed")
    }
}
