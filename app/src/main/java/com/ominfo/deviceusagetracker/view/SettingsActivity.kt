package com.ominfo.deviceusagetracker.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.ominfo.deviceusagetracker.R
import com.ominfo.deviceusagetracker.data.db.AppDatabase
import com.ominfo.deviceusagetracker.data.db.CategoryPolicyEntity
import com.ominfo.deviceusagetracker.databinding.ActivitySettingsBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/*
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.get(this)

        binding.btnSave.setOnClickListener {

            val socialLimit =
                binding.etSocial.text.toString().toIntOrNull() ?: 60

            if (socialLimit < 5) {
                Toast.makeText(
                    this,
                    "Minimum limit is 5 minutes",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                database.policyDao().insert(
                    CategoryPolicyEntity(
                        category = "Social",
                        dailyLimitMinutes = socialLimit
                    )
                )

                Toast.makeText(
                    this@SettingsActivity,
                    "Limit Saved Successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }
    }
}*/


/*
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var database: AppDatabase
    private lateinit var categoryAdapter: ArrayAdapter<String>

    // Get categories from existing policies or define them
    private val categories = listOf("Social", "Entertainment", "Others")
    private var selectedCategory: String = categories[0]
    private var currentPolicies: List<CategoryPolicyEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.get(this)

        setupSpinner()
        loadExistingPolicies()

        binding.btnSave.setOnClickListener {
            saveLimit()
        }
    }

    private fun setupSpinner() {
        categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                loadCategoryLimit()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadCategoryLimit() {
        lifecycleScope.launch {
            // Get current limit for selected category
            val policies = database.policyDao().getPolicies().firstOrNull()
            val policy = policies?.find { it.category == selectedCategory }

            binding.etLimit.setText(policy?.dailyLimitMinutes?.toString() ?: "")

            binding.tvCurrentLimit.text = if (policy != null) {
                "Current limit: ${policy.dailyLimitMinutes} minutes"
            } else {
                "Current limit: Not set (default: 60 minutes)"
            }
        }
    }

    private fun loadExistingPolicies() {
        lifecycleScope.launch {
            database.policyDao().getPolicies().collect { policies ->
                currentPolicies = policies
            }
        }
    }

    private fun saveLimit() {
        val limitInput = binding.etLimit.text.toString()

        if (limitInput.isEmpty()) {
            Toast.makeText(this, "Please enter a limit", Toast.LENGTH_SHORT).show()
            return
        }

        val limit = limitInput.toIntOrNull()

        if (limit == null) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        if (limit < 5) {
            Toast.makeText(this, "Minimum limit is 5 minutes", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            database.policyDao().insert(
                CategoryPolicyEntity(
                    category = selectedCategory,
                    dailyLimitMinutes = limit
                )
            )

            Toast.makeText(
                this@SettingsActivity,
                "✓ Limit saved for $selectedCategory",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}*/

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var database: AppDatabase
    private lateinit var categoryAdapter: ArrayAdapter<String>

    // List of categories (can be expanded later)
    private val categories = listOf("Social", "Entertainment", "Others")
    private var selectedCategory: String = categories[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.get(this)

        setupWindowInsets()

        setupSpinner()
        loadCategoriesWithLimits()

        binding.btnSave.setOnClickListener {
            saveLimit()
        }
    }

    private fun setupWindowInsets() {
        // Make the activity edge-to-edge
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        // Apply insets to the root layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // Apply padding to the ScrollView to account for system bars
            binding.scrollView.updatePadding(
                left = systemBars.left,
                top = statusBars.top,
                right = systemBars.right,
                bottom = navigationBars.bottom
            )

            insets
        }
    }

    private fun setupSpinner() {
        // Create custom adapter with black text and white background for dropdown
        categoryAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as? TextView)?.apply {
                    setTextColor(Color.BLACK)
                    setBackgroundColor(Color.WHITE)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                // Use the convertView if available, otherwise inflate new view
                val itemView = convertView ?: layoutInflater.inflate(
                    R.layout.spinner_dropdown_item,
                    parent,
                    false
                )

                // Safely set the text with null check
                (itemView as? TextView)?.apply {
                    text = getItem(position) ?: ""
                    setTextColor(Color.BLACK)
                    setBackgroundColor(Color.WHITE)
                }

                return itemView
            }
        }

        binding.spinnerCategory.adapter = categoryAdapter

        // Handle spinner selection
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                loadCategoryLimit()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun loadCategoriesWithLimits() {
        lifecycleScope.launch {
            // Collect flow to get all policies
            database.policyDao().getPolicies().collect { policies ->
                // This will update the display when any policy changes
                if (::binding.isInitialized) {
                    loadCategoryLimit()
                }
            }
        }
    }

    private fun loadCategoryLimit() {
        lifecycleScope.launch {
            // Get current limit for selected category
            val policies = database.policyDao().getPolicies().firstOrNull()
            val policy = policies?.find { it.category == selectedCategory }

            // Set the current limit in the display
            binding.tvCurrentLimit.text = if (policy != null) {
                "Current limit: ${policy.dailyLimitMinutes} minutes"
            } else {
                "Current limit: Not set (default: 60 minutes)"
            }

            // Optional: Pre-fill the edit text with current value
            binding.etLimit.setText(policy?.dailyLimitMinutes?.toString() ?: "")
        }
    }

    private fun saveLimit() {
        val limitInput = binding.etLimit.text.toString()

        if (limitInput.isEmpty()) {
            Toast.makeText(this, "Please enter a limit", Toast.LENGTH_SHORT).show()
            return
        }

        val limit = limitInput.toIntOrNull()

        if (limit == null) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        if (limit < 5) {
            Toast.makeText(
                this,
                "Minimum limit is 5 minutes",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            // Save the policy for selected category
            database.policyDao().insert(
                CategoryPolicyEntity(
                    category = selectedCategory,
                    dailyLimitMinutes = limit
                )
            )

            Toast.makeText(
                this@SettingsActivity,
                "✓ Limit saved for $selectedCategory",
                Toast.LENGTH_SHORT
            ).show()

            // Update the display with new limit
            loadCategoryLimit()

            // Set result to refresh dashboard
            setResult(RESULT_OK)
        }
    }
}