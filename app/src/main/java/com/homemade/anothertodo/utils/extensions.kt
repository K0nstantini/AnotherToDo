package com.homemade.anothertodo.utils

import android.app.Application
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.homemade.anothertodo.R
import com.homemade.anothertodo.db.entity.Task

fun FragmentActivity.toast(res: Int) =
    Toast.makeText(this, this.getString(res), Toast.LENGTH_SHORT).show()

private fun FragmentActivity.appBar() =
    findViewById<Toolbar>(R.id.topAppBar)

fun FragmentActivity.setCloseIcon() =
    appBar().setNavigationIcon(R.drawable.ic_close)

fun FragmentActivity.setAppTitle(_title: String?): Toolbar = appBar().apply {
    title = when (_title) {
        null, "" -> title
        else -> _title
    }
}

fun Int.toStrTime(): String {
    return (this / 60).toString().padStart(2, '0') + ':' +
            (this % 60).toString().padStart(2, '0')
}

fun Int.toArray(app: Application): Array<String> = app.resources.getStringArray(this)

fun Int.hoursToMilli(): Long = this.toLong() * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLI_IN_SECOND

//FIXME: Не чистая ф-я?
fun List<Task>.nestedTasks(
    task: Task,
    list: MutableList<Task> = mutableListOf()
): List<Task> {
    list.add(task)
    this.filter { it.parent == task.id }.forEach { this.nestedTasks(it, list) }
    return list
}

//FIXME: Не чистая ф-я?
fun List<Task>.delEmptyGroups(): List<Task> {
    val emptyGroups = mutableListOf<Task>()
    this.filter { it.group }.forEach { task ->
        if (this.nestedTasks(task).all { it.group }) {
            emptyGroups.add(task)
        }
    }
    val noEmptyGroups: MutableList<Task> = this.toMutableList()
    emptyGroups.forEach { task ->
        noEmptyGroups.remove(task)
    }
    return noEmptyGroups
}