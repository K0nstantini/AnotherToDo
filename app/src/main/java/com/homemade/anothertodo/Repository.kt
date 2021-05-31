package com.homemade.anothertodo

import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.homemade.anothertodo.db.dao.SingleTaskDao
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.nestedTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(private val singleTaskDao: SingleTaskDao) {

    val singleTasksFlow: Flow<List<SingleTask>> = singleTaskDao.getTasksFlow()
//    private val singleTasks: List<SingleTask> = singleTaskDao.getTasks()
    val singleTasksGroups: Flow<List<SingleTask>> = singleTaskDao.getGroups()

    suspend fun getTask(id: Long) = withContext(Dispatchers.IO) { singleTaskDao.getTask(id) }
    suspend fun getTasks() = withContext(Dispatchers.IO) { singleTaskDao.getTasks() }

    //    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSingleTask(task: SingleTask) = singleTaskDao.insert(task)

    @WorkerThread
    suspend fun updateSingleTask(task: SingleTask) = singleTaskDao.insert(task)

    @WorkerThread
    suspend fun deleteSingleTask(task: SingleTask) {
        singleTaskDao.deleteTasks(getTasks().nestedTasks(task))
    }

    @WorkerThread
    suspend fun deleteSingleTasks(tasks: List<SingleTask>) {
        tasks.forEach { task ->
            singleTaskDao.deleteTasks(getTasks().nestedTasks(task))
        }
    }

}