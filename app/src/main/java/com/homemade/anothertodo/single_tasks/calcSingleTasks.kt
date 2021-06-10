package com.homemade.anothertodo.single_tasks

import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.delEmptyGroups


fun getDatesToActivateSingleTasks(
    tasks: List<SingleTask>,
    frequency: Int,
    lastDateActivation: MyCalendar
): List<MyCalendar> {
    val dates = generateDates(frequency, lastDateActivation, MyCalendar().now())
    return when {
        noTaskLastDateActivation(tasks, lastDateActivation) -> listOf(lastDateActivation) + dates
        else -> dates
    }
}

private fun generateDates(
    frequency: Int,
    dateFrom: MyCalendar,
    dateTo: MyCalendar
): List<MyCalendar> {
    val dateNext = generateDate(frequency, dateFrom)
    return when {
        dateNext < dateTo -> listOf(dateNext) + generateDates(frequency, dateNext, dateTo)
        else -> listOf(dateNext)
    }
}

private fun generateDate(frequency: Int, date: MyCalendar) = MyCalendar(
    (if (date.isEmpty()) MyCalendar().now() else date).milli + 60_000L
)

private fun noTaskLastDateActivation(tasks: List<SingleTask>, date: MyCalendar) =
    date.isNoEmpty() && !tasks.any { it.dateActivation == date }

fun getTasksToUpdateDatesActivation(
    tasks: List<SingleTask>,
    dates: List<MyCalendar>
): List<SingleTask> {
    dates.dropLast(1).forEach { date ->
        val tasksToActivate = tasks.filter { it.group || it.readyToActivate }.delEmptyGroups()
        when (val task = generateTask(tasksToActivate)) {
            null -> return@forEach
            else -> task.dateActivation = date
        }
    }
    return tasks.filter { dates.contains(it.dateActivation) }
}


private fun generateTask(tasks: List<SingleTask>, parent: Long = 0L): SingleTask? {
    val task = tasks.filter { it.parent == parent && it.dateActivation.isEmpty() }
        .shuffled()
        .randomOrNull()
    return when {
        task == null -> null
        task.group -> generateTask(tasks, task.id)
        else -> task
    }
}
