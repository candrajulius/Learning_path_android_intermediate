package com.candra.latihanmediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG = "MainActivity"
    }

    private var mService: Messenger? = null
    private lateinit var mBoundServiceIntent: Intent
    private var mServiceBound = false


    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName, service: IBinder) {
            mService = Messenger(service)
            mServiceBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mServiceBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnStop = findViewById<Button>(R.id.btn_stop)


        /*
        Dengan memasang nilai MediaService pada action,
        kita dapat menentukan command mana yang ingin kita jalankan.
         Ketika startService() dipanggil maka pada saat itu onStartCommand() berjalan.
         */
        mBoundServiceIntent = Intent(this@MainActivity,MediaService::class.java)
        mBoundServiceIntent.action = MediaService.ACTION_CREATE


        startService(mBoundServiceIntent) // => digunakan untuk membuat dan menghancurkan kelas service
        // untuk mengaitkan kelas service ke kelas MainActivity
        bindService(mBoundServiceIntent,mServiceConnection,Context.BIND_AUTO_CREATE)
        btnPlay.setOnClickListener {
            playClickListener()
        }

        btnStop.setOnClickListener {
            stopClickListener()
        }
    }

    /*
    Pada kali ini kita menggunakan IncomingHandler untuk interaksi antar Activity dengan Service.
    Perhatikan bagian onClick pada button di kelas MainActivity.

    Pada kode di atas, menggunakan bantuan Messenger untuk mengirim perintah PLAY dan STOP
     */
    private fun playClickListener(){
        if (mServiceBound){
            try {
                mService?.send(Message.obtain(null,MediaService.PLAY,0,0))
            }catch (e: RemoteException){
                e.printStackTrace()
            }
        }
    }

    private fun stopClickListener(){
        if (mServiceBound){
            try {
                mService?.send(Message.obtain(null,MediaService.STOP,0,0))
            }catch (e: RemoteException){
                e.printStackTrace()
            }
        }
    }


    /*
    Pada method onDestroy(), kita perlu melepaskan memori MediaPlayer ketika sudah tidak digunakan. Ini penting dilakukan untuk menghindari masalah IllegalStateException. Lihat pada bagian MainActivity:
     */

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        unbindService(mServiceConnection)
        mBoundServiceIntent.action = MediaService.ACTION_DESTROY

        startService(mBoundServiceIntent)
    }


    // Kesimpulan
    /*
    Alur logika proses play sangatlah sederhana.
    Tombol play ditekan → baca audio menggunakan prepareAsync() → tunggu sampai proses baca selesai → metode start() dijalankan pada onPrepared().

     Ketika nilai dari action adalah ACTION_CREATE,
     maka metode init() akan dijalankan. Dan ketika nilai dari action adalah ACTION_DESTROY, maka metode stopSelf() akan dijalan.
     Metode stopSelf() berfungsi untuk menghentikan service.
     */
}