package com.autolearn.ar.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.autolearn.ar.MainActivity
import com.autolearn.ar.R

object ResumeNotifier {
    const val EXTRA_RESUME_PRACTICAL = "com.autolearn.ar.RESUME_PRACTICAL"

    private const val TAG = "ResumeNotifier"
    private const val CHANNEL_ID = "engiscan_resume"
    private const val NOTIFICATION_ID = 4101

    fun show(context: Context, session: InProgressSession) {
        if (!canNotify(context)) {
            Log.w(TAG, "notification permission not granted; in-app resume still available")
            return
        }

        runCatching {
            ensureChannel(context)
            val appContext = context.applicationContext
            val tapIntent = Intent(appContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_RESUME_PRACTICAL, true)
            }
            val pending = PendingIntent.getActivity(
                appContext,
                NOTIFICATION_ID,
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_resume)
                .setContentTitle("Practical paused")
                .setContentText("${session.title} — ${session.stepLabel}. Tap to resume.")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "${session.title} stopped at ${session.stepLabel}. Tap to pick up where you left off."
                    )
                )
                .setContentIntent(pending)
                .addAction(0, "Resume", pending)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build()
            NotificationManagerCompat.from(appContext).notify(NOTIFICATION_ID, notification)
        }.onFailure { error ->
            Log.e(TAG, "failed to show resume notification", error)
        }
    }

    fun cancel(context: Context) {
        runCatching {
            NotificationManagerCompat.from(context.applicationContext).cancel(NOTIFICATION_ID)
        }.onFailure { error ->
            Log.e(TAG, "failed to cancel resume notification", error)
        }
    }

    fun canNotify(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val existing = manager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Paused practicals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Resume a practical that was stopped mid-task."
            }
        )
    }
}
