package com.candra.latihan_aplikasi_geofencing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        if (intent.action == ACTION_GEOFENCE_EVENT){
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            /*
            Pada bagian ini, kita mendapatkan data geofence dengan menggunakan fungsi GeofencingEvent.fromIntent. Kemudian periksalah menggunakan fungsi hasError() untuk mengetahui apakah ada eror ketika menerapkan Geofence. Beberapa eror yang mungkin terjadi, antara lain:

            GEOFENCE_NOT_AVAILABLE : Geofence tidak tersedia.
            GEOFENCE_TOO_MANY_GEOFENCES : terlalu banyak Geofence.
            GEOFENCE_TOO_MANY_PENDING_INTENTS : terlalu banyak pending intent.
            GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION : belum mendapatkan permission.
             */
            if (geofencingEvent.hasError()){
                val errorMessage =
                    GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, "onReceive: $errorMessage" )
                sendNotification(context,errorMessage)
                return
            }

            val geofenceTransition = geofencingEvent.geofenceTransition

            /*
             Pada langkah awal, kita cek terlebih dahulu apakah broadcast yang masuk sesuai dengan action yang kita tentukan atau tidak. Jika iya, akan lanjut ke bagian selanjutnya.
         */
            // Membaca Transisi
            /*
            Langkah selanjutnya yaitu membaca aksi transisi yang masuk menggunakan geofencingEvent.geofenceTransition. Karena kita hanya fokus pada aksi enter dan dwell, maka gunakanlah if else
             */
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)
            {
                /*
                Selanjutnya kita membuat pesan yang disampaikan dalam notifikasi sesuai dengan aksi yang masuk. Kemudian kita juga dapat mengambil requestId menggunakan geofencingEvent.triggeringGeofences[0].requestId.

                 Bisa saja geofence yang dibuat lebih dari satu. Oleh karena itu, triggeringGeofences berbentuk list, dan untuk mendapatkan list yang pertama kita ambil index ke-0.

                 Sehingga hasil dari kode di atas terdapat dua kemungkinan, yakni:

                Apabila aksi enter, hasilnya “Anda telah memasuki area kampus”.
                Apabila aksi dwell, hasilnya “Anda telah di dalam area kampus”.
                Setelah usai, kita kirim teks tersebut ke dalam notifikasi seperti yang Anda lihat di device.
                 */
                val geofenceTransitionString =
                    when(geofenceTransition){
                        Geofence.GEOFENCE_TRANSITION_ENTER -> "Anda telah memasuki area"
                        Geofence.GEOFENCE_TRANSITION_DWELL -> "Anda telah di dalam area"
                        else -> "Invalid transition type"
                    }

                val triggeringGeofences = geofencingEvent.triggeringGeofences

                val requestId = triggeringGeofences[0].requestId

                val geofenceTransitionDetails = "$geofenceTransitionString $requestId"
                Log.i(TAG, "onReceive: $geofenceTransitionDetails")
                sendNotification(context,geofenceTransitionDetails)
            }else{
                val errorMessage = "Invalid transition type: $geofenceTransition"
                Log.e(TAG, "onReceive: $errorMessage")
                sendNotification(context,errorMessage)
            }
        }
        // -------------------------------------------------------------------------------------------------------
    }

    private fun sendNotification(context: Context, errorMessage: String) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder = NotificationCompat.Builder(context,CHANNEL_ID)
            .setContentTitle(errorMessage)
            .setContentText("Anda sudah bisa absen sekarang :)")
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()

        mNotificationManager.notify(NOTIFICATION_ID,notification)

    }

    companion object{
        private const val TAG = "GeofenceBroadcast"
        const val ACTION_GEOFENCE_EVENT = "GeofenceEvent"
        private const val CHANNEL_ID = "1"
        private const val CHANNEL_NAME = "Geofence Channel"
        private const val NOTIFICATION_ID = 1
    }
}