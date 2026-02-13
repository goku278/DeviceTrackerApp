package com.ominfo.deviceusagetracker.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ominfo.deviceusagetracker.R
import com.ominfo.deviceusagetracker.databinding.ItemCategoryBinding
import com.ominfo.deviceusagetracker.model.CategoryDashboardModel

/*class CategoryDashboardAdapter :
    RecyclerView.Adapter<CategoryDashboardAdapter.VH>() {

    private val list = mutableListOf<CategoryDashboardModel>()

    fun submit(newList: List<CategoryDashboardModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = list[position]

        holder.binding.tvCategory.text = item.category
        holder.binding.tvUsed.text =
            "Used: ${item.usedMinutes} min"
        holder.binding.tvRemaining.text =
            "Remaining: ${item.remaining} min"

        holder.binding.progress.max = item.limitMinutes
        holder.binding.progress.progress = item.usedMinutes.toInt()
    }
}*/


import android.widget.Button
import android.widget.ProgressBar


/*
class CategoryDashboardAdapter(
    private val categories: List<CategoryDashboardModel>,
    private val onItemClick: (CategoryDashboardModel) -> Unit,
    private val onManageLimitsClick: ((CategoryDashboardModel) -> Unit)? = null
) : RecyclerView.Adapter<CategoryDashboardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ✅ EXACT XML IDs from your layout
        val ivCategoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        val tvUsed: TextView = itemView.findViewById(R.id.tvUsed)
        val tvRemaining: TextView = itemView.findViewById(R.id.tvRemaining)
        val progress: ProgressBar = itemView.findViewById(R.id.progress)
        val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)
        val btnManageLimits: Button = itemView.findViewById(R.id.btnManageLimits)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]

        // ✅ Bind YOUR model properties perfectly
        holder.ivCategoryIcon.setImageResource(category.iconRes)
        holder.tvCategory.text = category.category

        // Status badge (YOUR computed property)
        holder.tvStatusBadge.text = category.statusBadge
        holder.tvStatusBadge.setBackgroundColor(category.statusColor)

        // Usage stats (YOUR computed properties)
        holder.tvUsed.text = "Used: ${category.usedMinutes} min"
        holder.tvRemaining.text = "Remaining: ${category.remaining} min"

        // Progress bar (YOUR percentage)
        holder.progress.max = 100
        holder.progress.progress = category.percentage

        // Button clicks
        holder.btnViewDetails.setOnClickListener {
            onItemClick(category)
        }

        holder.btnManageLimits.setOnClickListener {
            onManageLimitsClick?.invoke(category)
        }

        // Card click (entire item)
        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount() = categories.size
}*/

class CategoryDashboardAdapter(
    private val categories: List<CategoryDashboardModel>,
    private val onItemClick: (CategoryDashboardModel) -> Unit,
    private val onManageLimitsClick: ((CategoryDashboardModel) -> Unit)? = null
) : RecyclerView.Adapter<CategoryDashboardAdapter.ViewHolder>() {

    // ✅ Make categories mutable for updates
    private val categoriesList = categories.toMutableList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCategoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        val tvUsed: TextView = itemView.findViewById(R.id.tvUsed)
        val tvRemaining: TextView = itemView.findViewById(R.id.tvRemaining)
        val progress: ProgressBar = itemView.findViewById(R.id.progress)
        val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)
        val btnManageLimits: Button = itemView.findViewById(R.id.btnManageLimits)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoriesList[position]  // ✅ Use internal list

        holder.ivCategoryIcon.setImageResource(category.iconRes)
        holder.tvCategory.text = category.category
        holder.tvStatusBadge.text = category.statusBadge
        holder.tvStatusBadge.setBackgroundColor(category.statusColor)
        holder.tvUsed.text = "Used: ${category.usedMinutes} min"
        holder.tvRemaining.text = "Remaining: ${category.remaining} min"
        holder.progress.max = 100
        holder.progress.progress = category.percentage

        holder.btnViewDetails.setOnClickListener { onItemClick(category) }
        holder.btnManageLimits.setOnClickListener { onManageLimitsClick?.invoke(category) }
        holder.itemView.setOnClickListener { onItemClick(category) }
    }

    override fun getItemCount() = categoriesList.size

    // ✅ ADD THIS METHOD - Replaces submitList()
    fun updateList(newList: List<CategoryDashboardModel>) {
        categoriesList.clear()
        categoriesList.addAll(newList)
        notifyDataSetChanged()
    }
}