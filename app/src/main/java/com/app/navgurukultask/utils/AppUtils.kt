package com.app.navgurukultask.utils

import android.content.Context
import com.app.navgurukultask.workers.DataSyncWorker
import java.util.concurrent.TimeUnit

fun enqueueDataSyncWorker(context: Context) {
    val constraints = androidx.work.Constraints.Builder()
        .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
        .build()

    val req = androidx.work.OneTimeWorkRequestBuilder<DataSyncWorker>()
        .setConstraints(constraints)
        .setBackoffCriteria(
            androidx.work.BackoffPolicy.EXPONENTIAL,
            30, java.util.concurrent.TimeUnit.SECONDS
        )
        .setInitialDelay(10, TimeUnit.MILLISECONDS)
        .build()

    androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
        "sync-students",
        androidx.work.ExistingWorkPolicy.KEEP,
        req
    )
}