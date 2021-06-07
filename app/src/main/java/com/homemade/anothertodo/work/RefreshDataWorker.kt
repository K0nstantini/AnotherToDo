package com.homemade.anothertodo.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.homemade.anothertodo.Repository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Del?
//@AndroidEntryPoint
//class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
//    CoroutineWorker(appContext, params) {
//
//    @Inject
//    lateinit var repo: Repository
//
//    override suspend fun doWork(): Result {
//        return Result.success()
//    }
//}