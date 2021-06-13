package com.homemade.anothertodo.db.dao

import androidx.room.*
import com.homemade.anothertodo.db.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Update
    suspend fun updateTasks(tasks: List<Task>)

    @Delete
    suspend fun delete(task: Task)

    @Delete
    suspend fun deleteTasks(tasks: List<Task>)

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM task_table WHERE id = :id")
    fun getTask(id: Long): Task?

    @Query("SELECT * FROM task_table WHERE type = :type ORDER BY name ASC")
    fun getTasksFlow(type: String): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE dateActivation > 0 ORDER BY dateActivation ASC")
    fun getTasksToDoFlow(): Flow<List<Task>>

    @Query("SELECT * FROM task_table ORDER BY name ASC")
    fun getTasks(): List<Task>

    @Query("SELECT COUNT(*) FROM task_table WHERE `group` = 0")
    fun getCountTasks(): Int

    @Query("SELECT * FROM task_table WHERE dateActivation > 0")
    fun getActiveTasks(): List<Task>

    @Query("SELECT * FROM task_table WHERE dateActivation = 0")
    fun getNoActiveTasks(): List<Task>

}