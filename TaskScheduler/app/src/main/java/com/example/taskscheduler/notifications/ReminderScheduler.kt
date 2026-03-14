package com.example.taskscheduler.notifications

import android.content.Context
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    // Task-iin 2 notification-iig schedule hiih function
    // date: "2026-03-18", endTime: "14:30"
    fun schedule(context: Context, taskId: Long, taskTitle: String, date: String, endTime: String) {

        // date esvel endTime hooson bol schedule hiihgui
        if (date.isEmpty() || endTime.isEmpty()) return

        // "2026-03-18 14:30" helbertei string uusgej end tsagiig tооtsоолno
        val sdf       = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val endDate   = sdf.parse("$date $endTime") ?: return
        val endMillis = endDate.time
        val nowMillis = System.currentTimeMillis()

        val delayAt = endMillis - nowMillis

        // Duusah tsag undursen bol schedule hiihgui
        if (delayAt <= 0) return

        // Duusah tsagiin notification - zaavал schedule hiine
        scheduleWorker(
            context   = context,
            taskId    = taskId,
            taskTitle = taskTitle,
            type      = TaskReminderWorker.TYPE_AT,
            delayMs   = delayAt
        )

        // 5 min omno notification - tsag hangaltai baival l schedule hiine
        val delayBefore = delayAt - (5 * 60 * 1000)
        if (delayBefore > 0) {
            scheduleWorker(
                context   = context,
                taskId    = taskId,
                taskTitle = taskTitle,
                type      = TaskReminderWorker.TYPE_BEFORE,
                delayMs   = delayBefore
            )
        }
    }

    // WorkManager-d neg OneTimeWorkRequest uusgej oruulah function
    private fun scheduleWorker(
        context: Context,
        taskId: Long,
        taskTitle: String,
        type: String,
        delayMs: Long
    ) {
        // Worker ruu damjuulah input data
        val data = workDataOf(
            TaskReminderWorker.KEY_TASK_ID    to taskId,
            TaskReminderWorker.KEY_TASK_TITLE to taskTitle,
            TaskReminderWorker.KEY_TYPE       to type
        )

        // Neg udaa ajillah WorkRequest uusgej baina
        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            // Unique tag - ustgah uyeд ashiglana
            .addTag("task_${taskId}_$type")
            .build()

        // WorkManager-d oruulna - REPLACE = adiл tag-tai baival shine-eer darj bichne
        WorkManager.getInstance(context).enqueueUniqueWork(
            "task_${taskId}_$type",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    // Task ustgagdval 2 notification-iig hоёулыг cancel hiih function
    fun cancel(context: Context, taskId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag("task_${taskId}_${TaskReminderWorker.TYPE_BEFORE}")
        WorkManager.getInstance(context).cancelAllWorkByTag("task_${taskId}_${TaskReminderWorker.TYPE_AT}")
    }
}