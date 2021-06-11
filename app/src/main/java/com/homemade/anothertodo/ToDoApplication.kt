package com.homemade.anothertodo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ToDoApplication : Application() {

    @Inject
    lateinit var repo: Repository

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        checkSettings()
    }

    private fun checkSettings() = CoroutineScope(Dispatchers.Default).launch {
        if (repo.getSettings() == null) {
            repo.insertSettings()
        }
    }
}