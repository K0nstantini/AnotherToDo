package com.homemade.anothertodo.db.dao

import androidx.room.*
import com.homemade.anothertodo.db.entity.RegularTask
import kotlinx.coroutines.flow.Flow

@Dao
interface RegularTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: RegularTask): Long

    @Update
    suspend fun update(task: RegularTask)

    @Update
    suspend fun updateTasks(tasks: List<RegularTask>)

    @Delete
    suspend fun delete(task: RegularTask)

    @Delete
    suspend fun deleteTasks(tasks: List<RegularTask>)

    @Query("DELETE FROM regular_random_task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM regular_random_task_table ORDER BY name ASC")
    fun getTasksFlow(): Flow<List<RegularTask>>

    @Query("SELECT * FROM regular_random_task_table ORDER BY name ASC")
    fun getTasks(): List<RegularTask>

}