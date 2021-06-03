package com.homemade.anothertodo

import android.app.Application
import com.homemade.anothertodo.add_classes.MyPreference
import com.homemade.anothertodo.single_tasks.setSingleTasks
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ToDoApplication : Application() {
//    @Inject
//    private lateinit var pref: MyPreference
//
//    @Inject
//    private lateinit var repo: Repository
//
//    override fun onCreate() {
//        super.onCreate()
//        CoroutineScope(Dispatchers.Default).launch { setSingleTasks(pref, repo) }
//    }
}