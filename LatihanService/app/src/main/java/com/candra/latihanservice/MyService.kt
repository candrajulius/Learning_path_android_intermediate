package com.candra.latihanservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class MyService : Service() {

    companion object{
        internal val TAG = MyService::class.java.simpleName
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented!!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand: Service dijalankan....")

        serviceScope.launch {
            delay(3000)
            stopSelf()
            Log.d(TAG, "onStartCommand: Service dihentikan")
        }


        /*
        menandakan bahwa bila service tersebut dimatikan oleh sistem Android karena kekurangan memori, ia akan diciptakan kembali jika sudah ada memori yang bisa digunakan.

         Metode onStartCommand() juga akan kembali dijalankan.

         stopSelf() berfungsi untuk  memberhentikan atau mematikan MyService dari sistem Android.
         */
        return START_STICKY
    }
}