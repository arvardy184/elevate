package com.application.elevate.service

import com.application.elevate.model.NotificationItem

interface NotificationService {
//    @GET("notifications")
    suspend fun getNotifications(): List<NotificationItem>
}