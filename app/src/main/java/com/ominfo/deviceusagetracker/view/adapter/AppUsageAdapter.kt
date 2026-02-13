package com.ominfo.deviceusagetracker.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ominfo.deviceusagetracker.data.db.AppUsageEntity
import com.ominfo.deviceusagetracker.databinding.ItemAppUsageBinding

class AppUsageAdapter : RecyclerView.Adapter<AppUsageAdapter.ViewHolder>() {

    private var items = listOf<AppUsageEntity>()

    fun submit(newList: List<AppUsageEntity>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppUsageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(
        private val binding: ItemAppUsageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppUsageEntity) {
            binding.tvAppName.text = item.appName
            binding.tvPackageName.text = item.packageName
            binding.tvUsageTime.text = "${item.usageMinutes} min"
        }
    }
}