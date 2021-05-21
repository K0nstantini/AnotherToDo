package com.homemade.anothertodo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homemade.anothertodo.add_classes.MyCalendar

const val DEFAULT_DEADLINE_SINGLE_TASK = 24

@Entity(tableName = "single_task_table")
data class SingleTask(
    @PrimaryKey var name: String = "",
    var dateActivation: MyCalendar = MyCalendar(),          // Дата активации задачи
    var dateStart: MyCalendar = MyCalendar().today(),       // Дата, начиная с которой, задача становиться активной
    var dateUntilToDo: MyCalendar = MyCalendar(),           // Задача должна быть сгенерирована до этой даты
    var deadline: Int = DEFAULT_DEADLINE_SINGLE_TASK,
    var group: Boolean = false,
    var parent: String = "",
    var toDoAfterTask: String = ""                          // Задача будет сегенрирована только после выполнения другой задачи
)