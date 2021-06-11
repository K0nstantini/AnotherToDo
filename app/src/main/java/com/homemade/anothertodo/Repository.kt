package com.homemade.anothertodo

import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.homemade.anothertodo.db.dao.SettingsDao
import com.homemade.anothertodo.db.dao.SingleTaskDao
import com.homemade.anothertodo.db.entity.Settings
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.nestedTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val settingsDao: SettingsDao,
    private val singleTaskDao: SingleTaskDao
) {

    /** Settings */

    val settingsFlow: Flow<Settings> = settingsDao.getSettingsFlow()

    @WorkerThread
    suspend fun insertSettings() = settingsDao.insert(Settings())

    @WorkerThread
    suspend fun updateSettings(set: Settings) = settingsDao.update(set)

    suspend fun getSettings() = withContext(Dispatchers.IO) { settingsDao.getSettings() }

    /** ======================================================================================= */

    /** Single tasks */

    val singleTasksFlow: Flow<List<SingleTask>> = singleTaskDao.getTasksFlow()

    suspend fun getSingleTask(id: Long) = withContext(Dispatchers.IO) { singleTaskDao.getTask(id) }

    suspend fun getSingleTasks() = withContext(Dispatchers.IO) { singleTaskDao.getTasks() }


    //    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSingleTask(task: SingleTask) = singleTaskDao.insert(task)

    @WorkerThread
    suspend fun updateSingleTask(task: SingleTask) = singleTaskDao.update(task)

    @WorkerThread
    suspend fun updateSingleTasks(tasks: List<SingleTask>) = singleTaskDao.updateTasks(tasks)

    @WorkerThread
    suspend fun deleteSingleTask(task: SingleTask) {
        singleTaskDao.deleteTasks(getSingleTasks().nestedTasks(task))
    }

    @WorkerThread
    suspend fun deleteSingleTasks(tasks: List<SingleTask>) {
        tasks.forEach { task ->
            singleTaskDao.deleteTasks(getSingleTasks().nestedTasks(task))
        }
    }

    /** ======================================================================================= */


}