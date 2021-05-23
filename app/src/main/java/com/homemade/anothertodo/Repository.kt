package com.homemade.anothertodo

import androidx.annotation.WorkerThread
import com.homemade.anothertodo.db.dao.SingleTaskDao
import com.homemade.anothertodo.db.entity.SingleTask
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor (private val singleTaskDao: SingleTaskDao) {

    val singleTasks: Flow<List<SingleTask>> = singleTaskDao.getTasks()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSingleTask(task: SingleTask) {
        singleTaskDao.insert(task)
    }

}