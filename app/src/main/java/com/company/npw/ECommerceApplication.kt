package com.company.npw

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ECommerceApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "ecommerce_notifications"
        const val NOTIFICATION_CHANNEL_NAME = "E-Commerce Notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for order updates, promotions, and app updates"
                enableLights(true)
                lightColor = ContextCompat.getColor(this@ECommerceApplication, R.color.primary)
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
