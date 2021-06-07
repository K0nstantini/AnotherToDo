package com.homemade.anothertodo.db.dao

import androidx.room.*
import com.homemade.anothertodo.db.entity.SingleTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SingleTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: SingleTask): Long

    @Update
    suspend fun update(task: SingleTask)

    @Delete
    suspend fun delete(task: SingleTask)

    @Delete
    suspend fun deleteTasks(tasks: List<SingleTask>)

    @Query("DELETE FROM single_task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM single_task_table WHERE id = :id")
    fun getTask(id: Long): SingleTask?

    @Query("SELECT * FROM single_task_table ORDER BY name ASC")
    fun getTasksFlow(): Flow<List<SingleTask>>

    @Query("SELECT * FROM single_task_table WHERE dateActivation > 0 ORDER BY name ASC")
    fun getTasksToDoFlow(): Flow<List<SingleTask>>

    @Query("SELECT * FROM single_task_table ORDER BY name ASC")
    fun getTasks(): List<SingleTask>

    @Query("SELECT COUNT(*) FROM single_task_table WHERE `group` = 0")
    fun getCountTasks(): Int

    @Query("SELECT * FROM single_task_table WHERE dateActivation > 0")
    fun getActiveTasks(): List<SingleTask>

    @Query("SELECT * FROM single_task_table WHERE dateActivation = 0")
    fun getNoActiveTasks(): List<SingleTask>

}