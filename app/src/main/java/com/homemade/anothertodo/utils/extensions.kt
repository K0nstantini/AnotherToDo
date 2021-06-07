package com.homemade.anothertodo.utils

import com.homemade.anothertodo.db.entity.SingleTask

fun Int.toStrTime(): String {
    return (this / 60).toString().padStart(2, '0') + ':' +
            (this % 60).toString().padStart(2, '0')
}

fun Int.hoursToMilli(): Long = this.toLong() * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLI_IN_SECOND

//FIXME: Не чистая ф-я?
fun List<SingleTask>.nestedTasks(
    task: SingleTask,
    list: MutableList<SingleTask> = mutableListOf()
): List<SingleTask> {
    list.add(task)
    this.filter { it.parent == task.id }.forEach { this.nestedTasks(it, list) }
    return list
}

//FIXME: Не чистая ф-я?
fun List<SingleTask>.delEmptyGroups(): List<SingleTask> {
    val emptyGroups = mutableListOf<SingleTask>()
    this.filter { it.group }.forEach { task ->
        if (this.nestedTasks(task).all { it.group }) {
            emptyGroups.add(task)
        }
    }
    val noEmptyGroups: MutableList<SingleTask> = this.toMutableList()
    emptyGroups.forEach { task ->
        noEmptyGroups.remove(task)
    }
    return noEmptyGroups
}
