package com.homemade.anothertodo

import androidx.annotation.WorkerThread
import com.homemade.anothertodo.db.dao.SingleTaskDao
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.nestedTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(private val singleTaskDao: SingleTaskDao) {

    val singleTasksFlow: Flow<List<SingleTask>> = singleTaskDao.getTasksFlow()
    val singleTasksToDoFlow: Flow<List<SingleTask>> = singleTaskDao.getTasksToDoFlow()

    suspend fun getSingleTask(id: Long) = withContext(Dispatchers.IO) { singleTaskDao.getTask(id) }

    suspend fun getActiveSingleTasks() =
        withContext(Dispatchers.IO) { singleTaskDao.getActiveTasks() }

    suspend fun getNoActiveSingleTasks() =
        withContext(Dispatchers.IO) { singleTaskDao.getNoActiveTasks() }

    suspend fun countSingleTasks() = withContext(Dispatchers.IO) { singleTaskDao.getCountTasks() }

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

}