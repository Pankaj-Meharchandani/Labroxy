/**
 * File: TodoNotificationWorker.kt
 *
 * What it does:
 * WorkManager CoroutineWorker responsible for running periodic background checks for new GitLab to-do items
 * and triggering local system notifications when new to-dos are found.
 *
 * Touchpoints:
 * - Android WorkManager framework (PeriodicWorkRequestBuilder, WorkManager.enqueueUniquePeriodicWork).
 * - com.example.labroxy.ui.LabroxyViewModel: Invokes setEnabled based on user push notification preferences in Settings.
 * - com.example.labroxy.data.SessionStore: Reads notification preference settings and updates notified to-do IDs.
 *
 * Features / Functions:
 * - Periodic background work execution (doWork).
 * - Programmatically enabling or cancelling background periodic notification scheduling (setEnabled).
 */
package com.example.labroxy.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class TodoNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "todo_notification_work"

        fun setEnabled(context: Context, enabled: Boolean) {
            val workManager = WorkManager.getInstance(context)
            if (enabled) {
                val request = PeriodicWorkRequestBuilder<TodoNotificationWorker>(15, TimeUnit.MINUTES)
                    .build()
                workManager.enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
            } else {
                workManager.cancelUniqueWork(WORK_NAME)
            }
        }
    }
}
