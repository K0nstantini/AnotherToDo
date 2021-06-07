package com.homemade.anothertodo.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.homemade.anothertodo.R
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.MyPreference
import com.homemade.anothertodo.alarm.AlarmService
import com.homemade.anothertodo.db.AppDatabase
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
    fun provideSingleTaskDao(@ApplicationContext appContext: Context): SingleTaskDao {
        return AppDatabase.getDatabase(appContext, CoroutineScope(SupervisorJob())).SingleTaskDao()
    }

    @Provides
    @Singleton
    fun provideRepository(singleTaskDao: SingleTaskDao) = Repository(singleTaskDao)

    @Provides
    @Singleton
    fun providePreference(@ApplicationContext appContext: Context) = MyPreference(appContext)

    @Provides
    @Singleton
    fun provideAlarmService(@ApplicationContext appContext: Context) = AlarmService(appContext)

}