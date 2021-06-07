package com.homemade.anothertodo

import com.homemade.anothertodo.db.entity.SingleTask
import org.junit.Test

import org.junit.Assert.*
import timber.log.Timber

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private fun getTasksToShow(tasks: List<SingleTask>, id: Long = 0): List<SingleTask> {
        val children = tasks.filter { it.parent == id }
        return children + children
            .filter { it.groupOpen }
            .map { getTasksToShow(tasks, it.id) }
            .fold(emptyList<SingleTask>()) { sum, element -> sum + element }
            .sortedWith(compareByDescending<SingleTask> { it.group }.thenBy { it.name })

//        val a = children.filter { it.groupOpen }
//        val b = a.map { getTasksToShow(tasks, it.id) }
//        val c = b.fold(emptyList<SingleTask>()) { sum, element -> sum + element }
//        return children + c.sortedWith(compareByDescending<SingleTask> { it.group }.thenBy { it.name })

    }

    @Test
    fun testGetTasksToShow_isCorrect() {
        val tasks1 = listOf(
            SingleTask(id = 1, name = "Group-1", group = true),
            SingleTask(id = 2, name = "Task-1", parent = 1),
            SingleTask(id = 3, name = "Task-2", parent = 1),
            SingleTask(id = 4, name = "Group-2", group = true),
            SingleTask(id = 5, name = "Task-3", parent = 4)
        )
        val tasks2 = listOf(
            SingleTask(id = 1, name = "Group-1", group = true, groupOpen = true),
            SingleTask(id = 2, name = "Task-1", parent = 1),
            SingleTask(id = 3, name = "Task-2", parent = 1),
            SingleTask(id = 4, name = "Group-2", group = true, groupOpen = true),
            SingleTask(id = 5, name = "Task-2", parent = 4)
        )
        val showTasks1 = getTasksToShow(tasks1)
        assertEquals(2, showTasks1.count())
        assertEquals(showTasks1[0].id, 1)
        assertEquals(showTasks1[1].id, 4)

        val showTasks2 = getTasksToShow(tasks2)
        assertEquals(5, showTasks2.count())
    }

}