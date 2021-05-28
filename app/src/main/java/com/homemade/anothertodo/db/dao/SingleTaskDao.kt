package com.homemade.anothertodo.db.dao

import androidx.room.*
import com.homemade.anothertodo.db.entity.SingleTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SingleTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: SingleTask)

    @Update
    suspend fun update(task: SingleTask)

    @Delete
    suspend fun delete(task: SingleTask)

    @Delete
    suspend fun deleteTasks(tasks: List<SingleTask>)

    @Query("DELETE FROM single_task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM single_task_table WHERE id = :id")
    fun getTask(id: Int): SingleTask?

    @Query("SELECT * FROM single_task_table ORDER BY name ASC")
    fun getTasks(): Flow<List<SingleTask>>

    @Query("SELECT * FROM single_task_table WHERE 'group' = 1 ORDER BY name ASC")
    fun getGroups(): Flow<List<SingleTask>>

}