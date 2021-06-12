package com.homemade.anothertodo

import androidx.annotation.WorkerThread
import com.homemade.anothertodo.db.dao.SettingsDao
import com.homemade.anothertodo.db.dao.TaskDao
import com.homemade.anothertodo.db.entity.Settings
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TypeTask
import com.homemade.anothertodo.utils.nestedTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(private val settingsDao: SettingsDao, private val taskDao: TaskDao) {

    /** Settings */

    val settingsFlow: Flow<Settings> = settingsDao.getSettingsFlow()

    @WorkerThread
    suspend fun insertSettings() = settingsDao.insert(Settings())

    @WorkerThread
    suspend fun updateSettings(set: Settings) = settingsDao.update(set)

    suspend fun getSettings() = withContext(Dispatchers.IO) { settingsDao.getSettings() }

    /** ======================================================================================= */

    /** Regular Tasks */

    fun getTasksFlow(type: TypeTask) = when (type) {
        TypeTask.REGULAR_TASK -> taskDao.getTasksFlow(type.name)
        TypeTask.SINGLE_TASK -> taskDao.getTasksFlow(type.name)
    }

    @WorkerThread
    suspend fun updateTask(task: Task) = taskDao.update(task)

    @WorkerThread
    suspend fun deleteTasks(tasks: List<Task>) = tasks.forEach { task ->
        taskDao.deleteTasks(getSingleTasks().nestedTasks(task))
    }

    /** ======================================================================================= */

    /** Single tasks */

    suspend fun getSingleTask(id: Long) = withContext(Dispatchers.IO) { taskDao.getTask(id) }

    suspend fun getSingleTasks() = withContext(Dispatchers.IO) { taskDao.getTasks() }


    //    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSingleTask(task: Task) = taskDao.insert(task)

    @WorkerThread
    suspend fun updateSingleTask(task: Task) = taskDao.update(task)

    @WorkerThread
    suspend fun updateSingleTasks(tasks: List<Task>) = taskDao.updateTasks(tasks)

    @WorkerThread
    suspend fun deleteSingleTask(task: Task) =
        taskDao.deleteTasks(getSingleTasks().nestedTasks(task))

    @WorkerThread
    suspend fun deleteSingleTasks(tasks: List<Task>) {
        tasks.forEach { task ->
            taskDao.deleteTasks(getSingleTasks().nestedTasks(task))
        }
    }

    /** ======================================================================================= */


}