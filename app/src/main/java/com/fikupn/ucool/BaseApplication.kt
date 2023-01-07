package com.fikupn.ucool

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class BaseApplication : Application() {

    companion object {
        const val CHANNEL_ID_1 = "channel_1"
        const val CHANNEL_ID_2 = "channel_2"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(CHANNEL_ID_1, "Channel Satu", NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Ini adalah channel satu"

            val channel2 = NotificationChannel(CHANNEL_ID_2, "Channel Satu", NotificationManager.IMPORTANCE_DEFAULT)
            channel2.description = "Ini adalah channel dua"

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel1)
            manager?.createNotificationChannel(channel2)
        }
    }

}