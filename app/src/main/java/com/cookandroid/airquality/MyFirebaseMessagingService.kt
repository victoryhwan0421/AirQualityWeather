package com.cookandroid.airquality

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.cookandroid.airquality.databinding.FragmentWeatherForecastBinding


class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("TAG", "From:" + remoteMessage.from)
        createNotificationChanel() // 채널생성

        /// 실질적인 알림 생성하기
        val type = remoteMessage.data["type"]
            ?.let {NotificationType.valueOf(it)}
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        type ?: return




        NotificationManagerCompat.from(this)
            .notify(type.id, createNotification(type, title, message))
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // 중요도
            )
            channel.description = CHANNEL_DESCRIPTION // 채널 객체

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel) // 채널 생성완
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {
        val intent = Intent(this, FragmentStorage::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when(type) {
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            ""
                        )
                )
            }
            NotificationType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_nofitication
                        ).apply{
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )

            }
        }

        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_ID = "Emoji Party를 위한 채널"
        private const val CHANNEL_DESCRIPTION = "Channel Id"

        private const val MORNING_EVENT_TIME = 8
        private const val LUNCH_EVENT_TIME = 12
        private const val AFTERNOON_EVENT_TIME = 16
        private const val NIGHT_EVENT_TIME = 20
    }
}
