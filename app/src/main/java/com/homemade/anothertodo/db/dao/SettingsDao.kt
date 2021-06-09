package com.homemade.anothertodo.db.dao

import androidx.room.*
import com.homemade.anothertodo.db.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: Settings): Long

    @Update
    suspend fun update(set: Settings)

    @Delete
    suspend fun delete(set: Settings)

    @Query("DELETE FROM settings_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM settings_table LIMIT 1")
    fun getSettingsFlow(): Flow<List<Settings>>

    @Query("SELECT * FROM settings_table LIMIT 1")
    fun getSettings(): Settings?

}