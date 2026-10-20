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

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.labroxy.data.GitLabRepository
import com.example.labroxy.data.SessionStore
import com.example.labroxy.R
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class TodoNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val store = SessionStore(context)
        val settings = store.settings.first()
        val session = store.session.first()
        if (!settings.pushNotifications || !session.isReady) return Result.success()

        return runCatching {
            val todos = GitLabRepository(session).todos("pending")
            val notified = store.notifiedTodoIds.first()
            val newTodos = todos.filter { it.id != 0L && it.id !in notified }
            ensureChannel(context)
            newTodos.forEach { todo ->
                val title = todo.target?.title?.takeIf { it.isNotBlank() } ?: "New GitLab to-do"
                val project = todo.project?.name?.takeIf { it.isNotBlank() }
                val text = listOfNotNull(project, todo.body?.takeIf { it.isNotBlank() }).joinToString(" · ")
                    .ifBlank { todo.action.replace('_', ' ') }
                val notification = android.app.Notification.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_labroxy)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setCategory(android.app.Notification.CATEGORY_REMINDER)
                    .build()
                NotificationManagerCompat.from(context).notify(todo.id.hashCode(), notification)
            }
            store.saveNotifiedTodoIds((notified + todos.map { it.id }).filter { it != 0L }.toSet())
            Result.success()
        }.getOrElse { Result.retry() }
    }

    companion object {
        private const val WORK_NAME = "todo_notification_work"
        private const val IMMEDIATE_WORK_NAME = "todo_notification_work_immediate"
        private const val CHANNEL_ID = "gitlab_todos"
        private const val TEST_NOTIFICATION_ID = 0x4C425859

        fun sendTestNotification(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) return

            ensureChannel(context)
            val notification = android.app.Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_labroxy)
                .setContentTitle("Labroxy test notification")
                .setContentText("Your notification is working correctly.")
                .setAutoCancel(true)
                .setCategory(android.app.Notification.CATEGORY_REMINDER)
                .build()
            NotificationManagerCompat.from(context).notify(TEST_NOTIFICATION_ID, notification)
        }

        private fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        "GitLab to-dos",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = "New GitLab to-do items" }
                )
            }
        }

        fun setEnabled(context: Context, enabled: Boolean) {
            val workManager = WorkManager.getInstance(context)
            if (enabled) {
                ensureChannel(context)
                workManager.enqueueUniqueWork(
                    IMMEDIATE_WORK_NAME,
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequestBuilder<TodoNotificationWorker>().build()
                )
                val request = PeriodicWorkRequestBuilder<TodoNotificationWorker>(15, TimeUnit.MINUTES)
                    .build()
                workManager.enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
            } else {
                workManager.cancelUniqueWork(WORK_NAME)
                workManager.cancelUniqueWork(IMMEDIATE_WORK_NAME)
            }
        }
    }
}
