package com.homemade.anothertodo.utils

import com.homemade.anothertodo.db.entity.SingleTask

fun Int.toStrTime(): String {
    return (this / 60).toString().padStart(2, '0') + ':' +
            (this % 60).toString().padStart(2, '0')
}

//FIXME: Не чистая ф-я?
fun List<SingleTask>.nestedTasks(
    task: SingleTask,
    list: MutableList<SingleTask> = mutableListOf()
): List<SingleTask> {
    list.add(task)
    this.filter { it.parent == task.id }.forEach { this.nestedTasks(it, list) }
    return list
}