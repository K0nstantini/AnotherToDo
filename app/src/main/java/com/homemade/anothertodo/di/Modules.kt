package com.homemade.anothertodo.di

import android.content.Context
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.db.AppDatabase
import com.homemade.anothertodo.db.dao.SingleTaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object Modules {

    @Provides
    fun provideSingleTaskDao(@ApplicationContext appContext: Context) : SingleTaskDao {
        return AppDatabase.getDatabase(appContext).SingleTaskDao()
    }

    @Provides
    fun provideRepository(singleTaskDao: SingleTaskDao) = Repository(singleTaskDao)

}