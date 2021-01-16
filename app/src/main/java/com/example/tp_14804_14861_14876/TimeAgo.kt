package com.example.tp_14804_14861_14876

import java.util.*
import java.util.concurrent.TimeUnit

class TimeAgo {
    fun getTimeAgo(duration: Long): String {
        val now = Date()

        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - duration)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - duration)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - duration)

        return if (seconds < 60) {
            "Just now"
        } else if (minutes == 1L) {
            "a minute ago"
        } else if (minutes > 1 && minutes < 60) {
            minutes.toString() + " minutes ago"
        } else if (hours == 1L) {
            "an hour ago"
        } else if (hours > 1 && hours < 24) {
            hours.toString() + " hours ago"
        } else days.toString() + " days ago"
    }
}