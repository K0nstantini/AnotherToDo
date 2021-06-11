package com.homemade.anothertodo.di

import android.content.Context
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.alarm.AlarmService
import com.homemade.anothertodo.db.AppDatabase
import com.homemade.anothertodo.db.dao.RegularTaskDao
import com.homemade.anothertodo.db.dao.SettingsDao
import com.homemade.anothertodo.db.dao.SingleTaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Modules {

    @Provides
    @Singleton
    fun provideSettingsDao(@ApplicationContext appContext: Context): SettingsDao {
        return AppDatabase.getDatabase(appContext, CoroutineScope(SupervisorJob())).SettingsDao()
    }

    @Provides
    @Singleton
    fun provideSingleTaskDao(@ApplicationContext appContext: Context): SingleTaskDao {
        return AppDatabase.getDatabase(appContext, CoroutineScope(SupervisorJob())).SingleTaskDao()
    }

    @Provides
    @Singleton
    fun provideRegularTaskDao(@ApplicationContext appContext: Context): RegularTaskDao {
        return AppDatabase.getDatabase(appContext, CoroutineScope(SupervisorJob()))
            .RegularTaskDao()
    }

    @Provides
    @Singleton
    fun provideRepository(
        settingsDao: SettingsDao,
        singleTaskDao: SingleTaskDao,
        regularTaskDao: RegularTaskDao
    ) = Repository(settingsDao, singleTaskDao, regularTaskDao)

    @Provides
    @Singleton
    fun provideAlarmService(@ApplicationContext appContext: Context) = AlarmService(appContext)

}