package com.homemade.anothertodo.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.homemade.anothertodo.add_classes.MyCalendar
import kotlin.random.Random

const val ACTION_SET_TIME_SINGLE_TASK = "ACTION_SET_EXACT"
const val EXTRA_EXACT_ALARM_TIME = "EXTRA_EXACT_ALARM_TIME"

class AlarmService(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setExactAlarm(date: MyCalendar) = setAlarm(
        date.milli,
        getPendingIntent(getIntent().apply {
            action = ACTION_SET_TIME_SINGLE_TASK
            putExtra(EXTRA_EXACT_ALARM_TIME, date.milli)
        }
        )
    )

    private fun getPendingIntent(intent: Intent) = PendingIntent.getBroadcast(
        context,
        Random.nextInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    private fun setAlarm(milli: Long, pendingIntent: PendingIntent) = alarmManager?.let {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, milli, pendingIntent)
    }

    private fun getIntent() = Intent(context, AlarmReceiver::class.java)

}