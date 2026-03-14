package com.example.taskscheduler.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.taskscheduler.R

object NotificationHelper {

    // Notification channel-iin unique id
    const val CHANNEL_ID = "task_reminder_channel"

    // Notification channel-iig uusgeh function
    // App anh ehelhed neg udaa duudagdana
    fun createChannel(context: Context) {

        // Channel-iin haragdah ner bolon tovchlol
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Даалгаврын сануулга",
            // IMPORTANCE_HIGH = dуун + delgerengui notification
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Daalgavriin duusah tsagiin sanuulaga"
        }

        // System-d channel-iig burtgej baina
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    // Notification haragduulah function
    // id - neg notification-iig oorchdoos ni yalgah unique dugaar
    fun showNotification(context: Context, id: Int, title: String, message: String) {

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tasks)   // notification icon
            .setContentTitle(title)
            .setContentText(message)
            // AUTO_CANCEL = hereglegch darval notification arilna
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(id, notification)
    }
}