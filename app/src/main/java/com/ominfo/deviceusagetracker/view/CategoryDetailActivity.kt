package com.ominfo.deviceusagetracker.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ominfo.deviceusagetracker.databinding.ActivityCategoryDetailBinding
import com.ominfo.deviceusagetracker.view.adapter.AppUsageAdapter
import com.ominfo.deviceusagetracker.viewmodel.DashboardViewModel

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var adapter: AppUsageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("category") ?: "Unknown"
        val usedMinutes = intent.getLongExtra("used_minutes", 0)
        val limitMinutes = intent.getIntExtra("limit_minutes", 60)

        setupWindowInsets()

        setupViews(category, usedMinutes, limitMinutes)
        setupViewModel(category)
    }

    private fun setupViews(category: String, usedMinutes: Long, limitMinutes: Int) {
        // Set toolbar as action bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = "$category Apps"

        // Set category title
//        binding.tvCategoryTitle.text = "$category Apps"

        // Setup RecyclerView
        adapter = AppUsageAdapter()
        binding.recyclerApps.layoutManager = LinearLayoutManager(this)
        binding.recyclerApps.adapter = adapter

        // Back button click
        binding.btnBack.setOnClickListener {
            finish()
        }
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupViewModel(category: String) {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[DashboardViewModel::class.java]

        viewModel.usage.observe(this) { usageList ->
            val categoryApps = usageList
                .filter { it.category == category }
                .sortedByDescending { it.usageMinutes }

            if (categoryApps.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.recyclerApps.visibility = View.GONE
                binding.tvEmpty.text = "No apps found in $category category"
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.recyclerApps.visibility = View.VISIBLE
                adapter.submit(categoryApps)
            }
        }
    }
}