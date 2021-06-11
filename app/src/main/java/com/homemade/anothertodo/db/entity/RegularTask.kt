package com.homemade.anothertodo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homemade.anothertodo.add_classes.BaseTask

@Entity(tableName = "regular_random_task_table")
data class RegularTask(
    @PrimaryKey(autoGenerate = true) override val id: Long = 0L,
    override var name: String = "",
    override var group: Boolean = false,
    override var groupOpen: Boolean = false,
    override var parent: Long = 0L,
    var frequencyFrom: Int = 0,
    var frequencyTo: Int = 0,
    var periodGeneration: Int = 0,                                  // Период генерации задач в днях, например 0-3 задач за 2 дня
    var workingTime: String = "",                                   // Дни, когда будет активироваться задача (например только в будни или 2 через 2 дня)
    var forWholeGroup: Boolean = false,                             // Задачи будут рандомно выбираться из всей группы
    var time: Long = 0L,                                            // Время активации задачи
) : BaseTask()