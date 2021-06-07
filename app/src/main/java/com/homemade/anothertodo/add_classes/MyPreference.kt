package com.homemade.anothertodo.add_classes

import android.content.Context
import androidx.preference.PreferenceManager
import com.homemade.anothertodo.utils.delegates.PreferencesDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// FIXME: Del?
class MyPreference @Inject constructor(@ApplicationContext context: Context) {
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    var dateActivationSingleTask: MyCalendar by PreferencesDelegate(
        pref,
        PrefKeys.DATE_ACTIVATION_S_TASK,
        MyCalendar()
    )

    var frequencySingleTasks: Int by PreferencesDelegate(
        pref,
        PrefKeys.FREQUENCY_GENERATION_S_TASKS,
        DefaultValues.FREQUENCY_GENERATE_S_TASKS
    )

    companion object {
        private object PrefKeys {
            const val DATE_ACTIVATION_S_TASK = "date_activation_single_task"
            const val FREQUENCY_GENERATION_S_TASKS = "frequency_single_tasks"
        }

        private object DefaultValues {
            const val FREQUENCY_GENERATE_S_TASKS = 96 // hours
        }
    }
}
