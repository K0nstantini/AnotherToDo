package com.homemade.anothertodo

import androidx.annotation.WorkerThread
import com.homemade.anothertodo.db.dao.SingleTaskDao
import com.homemade.anothertodo.db.entity.SingleTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(private val singleTaskDao: SingleTaskDao) {

    val singleTasks: Flow<List<SingleTask>> = singleTaskDao.getTasks()
    val singleTasksGroups: Flow<List<SingleTask>> = singleTaskDao.getGroups()

    suspend fun getTask(id: Int) = withContext(Dispatchers.IO) { singleTaskDao.getTask(id) }

    //    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSingleTask(task: SingleTask) = singleTaskDao.insert(task)

    @WorkerThread
    suspend fun updateSingleTask(task: SingleTask) = singleTaskDao.insert(task)

    @WorkerThread
    suspend fun deleteSingleTask(task: SingleTask) = singleTaskDao.delete(task)

    @WorkerThread
    suspend fun deleteSingleTasks(tasks: List<SingleTask>) = singleTaskDao.deleteTasks(tasks)

}