package com.example.ppet.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ppet.MainActivity
import com.example.ppet.R
import kotlinx.coroutines.*
import java.util.*

class PetNotificationService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_ID = "pet_notification_channel"
        const val NOTIFICATION_ID = 1001
        const val FOREGROUND_NOTIFICATION_ID = 1002

        private val petMessages = listOf(
            "펫이 배고파해요! 밥을 주러 오세요!",
            "펫과 함께 놀아주세요!",
            "펫이 당신을 기다리고 있어요!",
            "펫의 경험치를 확인해보세요!",
            "새로운 퀘스트가 도착했어요!",
            "❤펫이 사랑을 표현하고 있어요!",
            "오늘의 목표를 달성해보세요!",
            "펫과 함께 모험을 떠나요!"
        )
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Foreground Service 시작 즉시 startForeground() 호출
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())

        startPeriodicNotifications()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "펫 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "펫과 관련된 알림을 받습니다"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("PPET 서비스 실행 중")
            .setContentText("펫 알림 서비스가 백그라운드에서 실행되고 있습니다")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun startPeriodicNotifications() {
        serviceScope.launch {
            while (true) {
                delay(2 * 60 * 60 * 1000L) // 2시간마다 알림
                showPetNotification()
            }
        }
    }

    private fun showPetNotification() {
        val message = petMessages.random()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("PPET")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
