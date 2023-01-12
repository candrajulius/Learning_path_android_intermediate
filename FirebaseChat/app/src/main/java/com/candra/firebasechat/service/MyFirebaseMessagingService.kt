package com.candra.firebasechat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.candra.firebasechat.MainActivity
import com.candra.firebasechat.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService()
{
    /*
    Token berfungsi sebagai id penerima ketika notification dikirimkan dari server.
    Token ini berbeda untuk setiap aplikasi dan akan berubah ketika cache aplikasi dibersihkan.
    Biasanya token juga dikirimkan ke backend jika ingin mengirimkan data dari server sendiri.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
    }

    /*
    Fungsi ini akan terpanggil ketika ada push notification yang datang.
    Pesan tersebut akan diterima dalam bentuk RemoteMessage.
    RemoteMessage berisi berbagai macam data seperti id pengirim,
    data dari additional options serta data notifikasi seperti judul dan body.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: ${message.from}")
        Log.d(TAG, "Message data payload: ${message.data}")
        Log.d(TAG, "Message notification body: ${message.notification?.body}")

        message.notification?.let { sendNotification(it.title,it.body) }
    }


    private fun sendNotification(title: String?, body: String?) {
        val contentIntent = Intent(applicationContext,MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext,
            NOTIFICATION_CHANNEL_ID
            )
            .setSmallIcon(R.drawable.ic_baseline_account_circle_24)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build())
    }

    companion object{
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "Firebase Channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Firebase Notification"
    }


    // Kesimpulan
    /*
    Pada AndroidManifest.xml terdapat service yang meng-extends MyFirebaseMessagingService. Tujuannya agar aplikasi bisa menerima pesan dari server Firebase pada background.
     */
}