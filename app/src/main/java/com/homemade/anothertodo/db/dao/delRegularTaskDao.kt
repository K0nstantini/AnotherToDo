package com.homemade.anothertodo.db.dao

import androidx.room.*
import com.homemade.anothertodo.add_classes.delTask
import com.homemade.anothertodo.db.entity.delRegularTask
import kotlinx.coroutines.flow.Flow

@Dao
interface delRegularTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskDel: delRegularTask): Long

    @Update
    suspend fun update(task: delTask)

    @Update
    suspend fun updateTasks(taskDels: List<delRegularTask>)

    @Delete
    suspend fun delete(taskDel: delRegularTask)

    @Delete
    suspend fun deleteTasks(taskDels: List<delRegularTask>)

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM task_table ORDER BY name ASC")
    fun getTasksFlow(): Flow<List<delRegularTask>>

    @Query("SELECT * FROM task_table ORDER BY name ASC")
    fun getTasks(): List<delRegularTask>

}