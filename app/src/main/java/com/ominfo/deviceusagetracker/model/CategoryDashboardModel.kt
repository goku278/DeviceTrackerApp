package com.ominfo.deviceusagetracker.model


/*
data class CategoryDashboardModel(
    val category: String,
    val usedMinutes: Long,
    val limitMinutes: Int,
    val iconRes: Int
) {
    val remaining: Long
        get() = (limitMinutes - usedMinutes).coerceAtLeast(0)

    val percentage: Int
        get() = if (limitMinutes == 0) 0
        else ((usedMinutes * 100) / limitMinutes).toInt().coerceIn(0, 100)

    val statusBadge: String
        get() = when {
            usedMinutes >= limitMinutes -> "Limit Reached"
            usedMinutes >= limitMinutes * 0.8 -> "Almost There"
            else -> "On Track"
        }

    val statusColor: Int
        get() = when {
            usedMinutes >= limitMinutes -> android.R.color.holo_red_dark
            usedMinutes >= limitMinutes * 0.8 -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_green_dark
        }

    val isLimitReached: Boolean
        get() = usedMinutes >= limitMinutes && limitMinutes > 0
}*/


import com.ominfo.deviceusagetracker.R

data class CategoryDashboardModel(
    val category: String,
    val usedMinutes: Long,
    val limitMinutes: Int,
    val iconRes: Int
) {
    val remaining: Long
        get() = (limitMinutes - usedMinutes).coerceAtLeast(0)

    val percentage: Int
        get() = if (limitMinutes == 0) 0
        else ((usedMinutes * 100) / limitMinutes).toInt().coerceIn(0, 100)

    val statusBadge: String
        get() = when {
            usedMinutes >= limitMinutes -> "Limit Reached"
            usedMinutes >= limitMinutes * 0.8 -> "Almost There"
            else -> "On Track"
        }

    val statusColor: Int
        get() = when {
            usedMinutes >= limitMinutes -> android.R.color.holo_red_dark
            usedMinutes >= limitMinutes * 0.8 -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_green_dark
        }

    val isLimitReached: Boolean
        get() = usedMinutes >= limitMinutes && limitMinutes > 0
}