package com.homemade.anothertodo.add_classes

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val S_DATE_ACTIVATION_SINGLE_TASK = "date_activation_single_task"
private const val S_FREQUENCY_GENERATION_SINGLE_TASKS = "frequency_single_tasks"
private const val DEFAULT_FREQUENCY_GENERATE_SINGLE_TASKS = 96 // hours

class MyPreference @Inject constructor(@ApplicationContext context: Context) {
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    /** Date activation sTask */
    fun getDateActivationSingleTask(): MyCalendar =
        MyCalendar(pref.getLong(S_DATE_ACTIVATION_SINGLE_TASK, 0L))

    fun setDateActivationSingleTask(value: MyCalendar) =
        pref.edit().putLong(S_DATE_ACTIVATION_SINGLE_TASK, value.milli).apply()

    /** Frequency generation sTask */
    fun getFrequencySingleTasks(): Int =
        pref.getInt(S_FREQUENCY_GENERATION_SINGLE_TASKS, DEFAULT_FREQUENCY_GENERATE_SINGLE_TASKS)

    fun setFrequencySingleTasks(value: Int) =
        pref.edit().putInt(S_FREQUENCY_GENERATION_SINGLE_TASKS, value).apply()
}