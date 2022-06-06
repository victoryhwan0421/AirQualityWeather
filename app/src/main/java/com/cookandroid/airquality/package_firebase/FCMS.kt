package com.cookandroid.airquality.package_firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.cookandroid.airquality.MainActivity
import com.cookandroid.airquality.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMS : FirebaseMessagingService() {
    val TAG = "FCMS"

    override fun onMessageReceived(RM: RemoteMessage) {
        Log.d(TAG, "msg : ${RM.toString()}")

        if (RM.data.isNotEmpty()) {
            Log.d(TAG, "data : ${RM.data.toString()}")
            sendTopNotification(RM.data["title"].toString(), RM.data["body"].toString())
            if(true){
                //scheduleJob()
            }else{
                handleNow()
            }
        }
    }

//    fun scheduleJob(){
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//        WorkManager.getInstance().beginWith(work).enqueue()
//    }

    fun handleNow(){
        Log.d(TAG, "Short lived task is done.")
    }

    /// data를 통해 받은 제목과 내용을 알림창에 설정하여 알림을 띄우는 코드
    /// 백그라운드 상태에서 알림을 받기 위해서는 notification이 아니라 data 형식으로 보내야 함
    private fun sendTopNotification(title: String?, body: String) {
        val CHANNEL_DEFAULT_IMPORTANCE = "channel_id"
        val ONGOING_NOTIFICATION = 1

        // 알림 클릭시 앱 화면 띄우는 intent 생성
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        // 알림 생성
        val notification = Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_DEFAULT_IMPORTANCE, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(ONGOING_NOTIFICATION, notification)
    }

    override fun onNewToken(RM: String) {
        super.onNewToken(RM)
        Log.i(TAG, "Refreshed token: +$RM")
    }
}