package com.candra.latihanmediaplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException
import java.lang.ref.WeakReference

class MediaService : Service(),MediaPlayerCallback {

    private var mMediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    companion object{
        const val ACTION_CREATE = "com.candra.latihanmediaplayer.mediaservice.create"
        const val ACTION_DESTROY = "com.candra.latihanmediaplayer.mediaservice.destroy"
        const val TAG = "Media Service"
        const val PLAY = 0
        const val STOP = 1
        const val CHANNEL_DEFAULT_IMPORTANCE = "Channel_Test"
        const val ONGOING_NOTIFICATION_ID = 1
    }

    // Masukkan logic anda disini
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // ambil intent action
        val action = intent.action

        /*
        Terdapat 2 percabangan pada metode onStartCommand() yaitu ACTION_CREATE dan ACTION_DESTROY.  Kedua command atau perintah berikut hanya berupa switch-case obyek string yang nilainya Anda sendiri yang menentukan.
         */
        action.let {
            when(action){
                ACTION_CREATE -> if (mMediaPlayer == null){
                    init()
                }
                ACTION_DESTROY -> if (mMediaPlayer?.isPlaying as Boolean){
                    stopSelf()
                }
                else -> init()
            }
        }

        Log.d(TAG, "onStartCommand: ")

        return flags
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: ")
        return mMessanger.binder
    }

    private fun showNotif(){
        val notificationIntent = Intent(this@MediaService,MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getActivity(this,0,notificationIntent,0)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
            .setContentTitle("TEST")
            .setContentText("TEST2")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setTicker("TEST3")
            .build()

        createChannel(CHANNEL_DEFAULT_IMPORTANCE)

        startForeground(ONGOING_NOTIFICATION_ID,notification)
    }

    private val mMessanger = Messenger(IncomingHandler(this))

    internal class IncomingHandler(playerCallback: MediaPlayerCallback): Handler(Looper.getMainLooper()){
        /*
        Dengan bantuan WeakReference sebagai penerima dari pesan callback MainActivity. Jadi ketika button PLAY ditekan maka akan menjalankan fungsi dari onPlay:
         */
        private val mediaPlayerCallbackWeakReference: WeakReference<MediaPlayerCallback> = WeakReference(playerCallback)

        /*
        Lihat penerima pesan atau IncomingHandler di bagian MediaService:
         */
        override fun handleMessage(msg: Message) {
            when(msg.what){
                PLAY -> mediaPlayerCallbackWeakReference.get()?.onPlay()
                STOP -> mediaPlayerCallbackWeakReference.get()?.onStop()
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun createChannel(CHANNEL_ID: String){
        val mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,"Battery",
            NotificationManager.IMPORTANCE_DEFAULT)
            channel.setShowBadge(false)
            channel.setSound(null,null)
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    private fun stopNotif(){
        stopForeground(false)
    }

    override fun onPlay() {
        onPlayMusic()
    }

    override fun onStop() {
        onStopMusic()
    }

    /**
     * Lihat pada bagian di atas, kode tersebut berguna untuk memperbarui MediaPlayer.
     */
    private fun init(){
        mMediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        mMediaPlayer?.setAudioAttributes(attribute)
        val afd = applicationContext.resources.openRawResourceFd(R.raw.guitar_background)

        /**
         * Kode di atas berfungsi untuk mengambil file suara dalam folder R.raw, kemudian di masukkan ke dalam MediaPlayer.
         */
        try {
            /**
             * Kode setDataSource berfungsi untuk memasukkan detail informasi dari asset atau musik yang akan diputar.
             */
            mMediaPlayer?.setDataSource(afd.fileDescriptor,afd.startOffset,afd.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mMediaPlayer?.setOnPreparedListener {
            isReady = false
            mMediaPlayer?.start()
            showNotif()
        }
        mMediaPlayer?.setOnErrorListener { _, _, _ -> false  }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: ")
        return super.onUnbind(intent)
    }

    /*
    Perintah prepareAsync() berfungsi untuk menyiapkan MediaPlayer jika belum disiapkan atau diperbarui.
    Ketika prepareAsync() dijalankan maka proses ini bersifat asynchronous.
    Ini untuk memastikan aplikasi tetap berjalan secara responsif.
    Sebenarnya, mediaplayer menyediakan metode prepare() yang berjalan secara synchronous.
    Tetapi untuk proses yang memakan waktu lama, Anda sebaiknya menggunakan prepareAsync().
     */
    private fun onPlayMusic(){
        mMediaPlayer?.let {
            if (!isReady){
                it.prepareAsync()
            }else{
                if (it.isPlaying){
                    it.pause()
                }else{
                    it.start()
                    showNotif()
                }
            }
        }
    }

    /*
    Setelah MediaPlayer sudah siap untuk dijalankan, apakah sedang menjalankan musik atau tidak?
    Jika sedang menjalankan musik maka perintah yang digunakan adalah pause(), yang berfungsi untuk memberikan jeda atau memberhentikan sementara kepada MediaPlayer.
    Jika tidak dikondisi itu, maka menggunakan start() untuk melanjutkan musik dari MediaPlayer.

     Fungsi mMediaPlayer.stop() digunakan untuk menghentikan MediaPlayer yang sedang berjalan (play).
     */
    private fun onStopMusic(){
        mMediaPlayer?.let {
            if (it.isPlaying || isReady){
                it.stop()
                isReady = false
                stopNotif()
            }
        }
    }
}