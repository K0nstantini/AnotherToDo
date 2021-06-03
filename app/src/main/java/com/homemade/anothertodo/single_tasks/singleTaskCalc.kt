package com.homemade.anothertodo.single_tasks

import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.add_classes.MyPreference
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.delEmptyGroups
import com.homemade.anothertodo.utils.hoursToMilli

suspend fun setSingleTasks(pref: MyPreference, repo: Repository) {

//    pref.setDateActivationSingleTask(MyCalendar())

    val dateActivation = pref.getDateActivationSingleTask()
    val tasks = repo.getSingleTasks()

    if (needToSetSingleTasks(dateActivation, tasks)) {
        val frequency = pref.getFrequencySingleTasks()

        val listDates = generateDates(frequency, dateActivation)
        listDates.last().also {
            setNotification(it)
            pref.setDateActivationSingleTask(it)
        }
        activateSingleTasks(listDates.dropLast(1), tasks, repo)
    }
}

private fun needToSetSingleTasks(
    date: MyCalendar,
    tasks: List<SingleTask>
) = checkDate(date) && checkTasks(tasks)

private fun checkDate(date: MyCalendar) = date < MyCalendar().now()

private fun checkTasks(tasks: List<SingleTask>) = tasks.any { !it.group && tasksToGenerate(it) }

private fun tasksToGenerate(task: SingleTask) =
    task.dateActivation.isEmpty() && task.dateStart > MyCalendar().now()

private fun generateDates(frequency: Int, dateActivation: MyCalendar): List<MyCalendar> {
    val date = generateDate(frequency, dateActivation)
    return when {
        date < MyCalendar().now() -> listOf(date) + generateDates(frequency, date)
        else -> listOf(date)
    }
}

private fun setNotification(date: MyCalendar) {
    TODO("Not yet implemented")
}

private suspend fun activateSingleTasks(
    dates: List<MyCalendar>,
    allTasks: List<SingleTask>,
    repo: Repository
) {
    val tasks = allTasks.filter { it.group || tasksToGenerate(it) }.delEmptyGroups()
    dates.forEach {
        val task = generateTask(tasks)
        task.dateActivation = it
        repo.updateSingleTask(task)
    }
}

private fun generateTask(tasks: List<SingleTask>, parent: Long = 0L): SingleTask {
    val task = tasks.filter { it.parent == parent }.random()
    return when {
        (task.group) -> generateTask(tasks, task.id)
        else -> task
    }
}

private fun generateDate(frequency: Int, startDate: MyCalendar) = MyCalendar(
    (if (startDate.isEmpty()) MyCalendar().now() else startDate).milli +
            (10_000L..frequency.hoursToMilli()).random()
)

