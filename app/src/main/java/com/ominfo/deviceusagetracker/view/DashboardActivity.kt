package com.ominfo.deviceusagetracker.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ominfo.deviceusagetracker.config.RemoteConfigManager
import com.ominfo.deviceusagetracker.databinding.ActivityDashboardBinding
import com.ominfo.deviceusagetracker.model.CategoryDashboardModel
import com.ominfo.deviceusagetracker.utils.AdsManager
import com.ominfo.deviceusagetracker.utils.NotificationHelper
import com.ominfo.deviceusagetracker.utils.UsageWorkManager
import com.ominfo.deviceusagetracker.view.adapter.CategoryDashboardAdapter
import com.ominfo.deviceusagetracker.viewmodel.DashboardViewModel

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var adapter: CategoryDashboardAdapter

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.loadUsage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupViews()
        setupViewModel()
        setupObservers()
        setupAds()
        NotificationHelper.createNotificationChannel(this)
        viewModel.loadUsage()
    }

    private fun setupWindowInsets() {
        // Make the activity edge-to-edge
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        // Apply insets to the main coordinator layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // Apply padding to the main content
            view.updatePadding(
                left = systemBars.left,
                right = systemBars.right
            )

            // Apply padding to AppBarLayout to account for status bar
            binding.appBarLayout.updatePadding(
                top = statusBars.top
            )

            // Apply padding to bottom content for navigation bar
            binding.mainContent.updatePadding(
                bottom = navigationBars.bottom
            )

            insets
        }
    }

    private fun setupViews() {
        // Set support action bar with the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        adapter = CategoryDashboardAdapter(
            categories = emptyList(),
            onItemClick = { category ->
                navigateToCategoryDetail(category)
            },
            onManageLimitsClick = { category ->
                navigateToManageLimits(category)
            }
        )

        binding.recyclerCategories.layoutManager = LinearLayoutManager(this)
        binding.recyclerCategories.adapter = adapter

        binding.btnSettings.setOnClickListener {
            settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
        }

        binding.btnRetry.setOnClickListener {
            viewModel.loadUsage()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[DashboardViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.dashboardData.observe(this) { dashboardList ->
            if (dashboardList.isEmpty()) {
                showEmptyState()
            } else {
                showContent(dashboardList)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                showError(error)
            }
        }

        // Observe blocked category to launch BlockActivity
        viewModel.blockedCategory.observe(this) { category ->
            if (category != null) {
                launchBlockActivity(category)
            }
        }
    }

    private fun launchBlockActivity(category: String) {
        val intent = Intent(this, BlockActivity::class.java).apply {
            putExtra("category", category)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun setupAds() {
        // Banner ad
        if (RemoteConfigManager.bannerEnabled()) {
            AdsManager.loadBanner(binding.bannerAd)

            ViewCompat.setOnApplyWindowInsetsListener(binding.bannerAd) { view, insets ->
                val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                view.updatePadding(bottom = navigationBars.bottom)
                insets
            }
        } else {
            binding.bannerAd.visibility = View.GONE
        }

        // Native ad
        if (RemoteConfigManager.nativeEnabled()) {
            AdsManager.loadNativeAd(this, object : AdsManager.NativeAdLoadCallback {
                override fun onAdLoaded(adView: com.google.android.gms.ads.nativead.NativeAdView) {
                    binding.nativeAdContainer.removeAllViews()
                    binding.nativeAdContainer.addView(adView)
                    binding.nativeAdContainer.visibility = View.VISIBLE

                    ViewCompat.setOnApplyWindowInsetsListener(binding.nativeAdContainer) { view, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        view.updatePadding(
                            left = systemBars.left,
                            right = systemBars.right
                        )
                        insets
                    }
                }

                override fun onAdFailedToLoad(error: String) {
                    binding.nativeAdContainer.visibility = View.GONE
                }
            })
        } else {
            binding.nativeAdContainer.visibility = View.GONE
        }
    }

    private fun showContent(dashboardList: List<CategoryDashboardModel>) {
        binding.tvEmpty.visibility = View.GONE
        binding.recyclerCategories.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        adapter.updateList(dashboardList)
    }

    private fun showEmptyState() {
        binding.tvEmpty.visibility = View.VISIBLE
        binding.recyclerCategories.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE

        ViewCompat.setOnApplyWindowInsetsListener(binding.tvEmpty) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }

    private fun showError(message: String) {
        binding.tvEmpty.visibility = View.GONE
        binding.recyclerCategories.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.tvError.text = message

        ViewCompat.setOnApplyWindowInsetsListener(binding.errorLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }
    }

    private fun navigateToCategoryDetail(category: CategoryDashboardModel) {
        val intent = Intent(this, CategoryDetailActivity::class.java).apply {
            putExtra("category", category.category)
            putExtra("used_minutes", category.usedMinutes)
            putExtra("limit_minutes", category.limitMinutes)
        }
        startActivity(intent)
    }

    private fun navigateToManageLimits(category: CategoryDashboardModel) {
        val intent = Intent(this, SettingsActivity::class.java).apply {
            putExtra("category", category.category)
            putExtra("limit_minutes", category.limitMinutes)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        AdsManager.destroy()
    }

    override fun onResume() {
        super.onResume()
        // Trigger WorkManager to track usage when app resumes
        UsageWorkManager.triggerImmediateTracking(this)
    }
}