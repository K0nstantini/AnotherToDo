package com.homemade.anothertodo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.homemade.anothertodo.R
import com.homemade.anothertodo.add_classes.MyCalendar
import dagger.hilt.android.qualifiers.ApplicationContext

private const val NOTIFICATION_ID = 1

// TODO: Del?
fun NotificationManager.sendNotification(
    builder: NotificationCompat.Builder,
    messageBody: String,
    date: MyCalendar
//    applicationContext: Context
) {
//    val builder = NotificationCompat.Builder(
//        applicationContext,
//        applicationContext.getString(R.string.single_task_notification_channel_id)
//    )
//      .setSmallIcon(R.drawable.cooked_egg) // TODO: Add icon?
//        .setContentTitle(applicationContext.getString(R.string.notification_title))
    builder
        .setSmallIcon(R.drawable.ic_close)
        .setContentText(messageBody)
        .setWhen(date.milli)

    notify(NOTIFICATION_ID, builder.build())
}