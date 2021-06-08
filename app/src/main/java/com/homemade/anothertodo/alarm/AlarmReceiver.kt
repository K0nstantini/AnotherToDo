package com.homemade.anothertodo.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.homemade.anothertodo.R
import com.homemade.anothertodo.add_classes.MyCalendar
import io.karn.notify.Notify

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(EXTRA_EXACT_ALARM_TIME, 0L)
        when (intent.action) {
            ACTION_SET_TIME_SINGLE_TASK -> {
                buildNotification(context, MyCalendar(timeInMillis).toString())
            }
        }
    }

    private fun buildNotification(context: Context, message: String) {
        Notify
            .with(context)
            .content {
                title = context.getString(R.string.notification_title_task_to_do_default)
                text = message
            }
            .show()
    }

}