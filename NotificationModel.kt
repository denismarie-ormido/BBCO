package com.example.kayara

data class NotificationModel(
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: String, // "signup", "document", "info"
    val isRead: Boolean = false
)