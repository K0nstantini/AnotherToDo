package com.homemade.anothertodo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homemade.anothertodo.add_classes.MyCalendar

private const val FREQUENCY_GENERATE_S_TASKS = 96 // hours

@Entity(tableName = "settings_table")
data class Settings(
    @PrimaryKey val id: Int = 1,

    /** Single tasks */
    var dateActivationSingleTask: MyCalendar = MyCalendar(),
    var frequencySingleTasks: Int = FREQUENCY_GENERATE_S_TASKS,
    var pointsSingleTasks: Int = 0
) {


}