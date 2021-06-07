package com.homemade.anothertodo.single_tasks

import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.add_classes.MyPreference
import com.homemade.anothertodo.alarm.AlarmService
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.delEmptyGroups
import timber.log.Timber

suspend fun setSingleTasks(pref: MyPreference, repo: Repository, alarm: AlarmService) {

//    pref.dateActivationSingleTask = MyCalendar()
//    return

    val lastDateActivation = pref.dateActivationSingleTask
    val tasks = repo.getSingleTasks()

    Timber.tag("_Timber_").d("last date activation: %s", lastDateActivation.toString())

    if (needToSetSingleTasks(lastDateActivation, tasks)) {
        if (lastDateActivation.isNoEmpty() && !tasks.any { it.dateActivation == lastDateActivation }) {
            val t = generateTask(tasks) // FIXME
                ?.apply { dateActivation = lastDateActivation }
//            .also { repo.updateSingleTask(it) } FIXME
            Timber.tag("_Timber_").d("set last date activation for: %s", t)
        }

        val frequency = pref.frequencySingleTasks

        val listDates = generateDates(frequency, lastDateActivation)
        Timber.tag("_Timber_").d("Count dates: %s", listDates.count())
        listDates.last().also { date ->
            Timber.tag("_Timber_").d("Last date: %s", date.toString())
            setNotification(date, alarm)
//            pref.dateActivationSingleTask = date
        }
        activateSingleTasks(listDates.dropLast(1), tasks, repo)
    }
}

private fun needToSetSingleTasks(
    date: MyCalendar,
    tasks: List<SingleTask>
) = checkDate(date) && checkTasks(tasks)

private fun checkDate(date: MyCalendar) = date < MyCalendar().now()

private fun checkTasks(tasks: List<SingleTask>) = tasks.any { !it.group && it.readyToActivate }

private fun generateDates(frequency: Int, dateActivation: MyCalendar): List<MyCalendar> {
    val date = testGenerateDate(frequency, dateActivation)
    return when {
        date < MyCalendar().now() -> listOf(date) + generateDates(frequency, date)
        else -> listOf(date)
    }
}

private fun setNotification(date: MyCalendar, alarm: AlarmService) {
    alarm.setExactAlarm(date.milli)
}

private suspend fun activateSingleTasks(
    dates: List<MyCalendar>,
    allTasks: List<SingleTask>,
    repo: Repository
) {
    val tasks = allTasks.filter { it.group || it.readyToActivate }.delEmptyGroups()
    dates.forEach {
        val t = generateTask(tasks) // FIXME
            ?.apply { dateActivation = it }
//            .also { repo.updateSingleTask(it) } FIXME
        Timber.tag("_Timber_").d("Activated task: %s, date: %s", t, it)
    }
}

private fun generateTask(tasks: List<SingleTask>, parent: Long = 0L): SingleTask? {
    val task = tasks.filter { it.parent == parent && it.dateActivation.isEmpty() }.randomOrNull()
    return when {
        task == null -> null
        task.group -> generateTask(tasks, task.id)
        else -> task
    }
}

//private fun generateDate(frequency: Int, startDate: MyCalendar) = MyCalendar(
//    (if (startDate.isEmpty()) MyCalendar().now() else startDate).milli +
//            (10_000L..frequency.hoursToMilli()).random()
//)

// FIXME
private fun testGenerateDate(frequency: Int, startDate: MyCalendar) = MyCalendar(
    (if (startDate.isEmpty()) MyCalendar().now() else startDate).milli +
            (10_000L..(5 * 60 * 1_000)).random()
)

