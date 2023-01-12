package com.candra.latihanservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.candra.latihanservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mServiceBound = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var mBoundService: MyBoundService

    // Ini adalah sebuah listener callback dimana ada dua fungsi
    private val mServiceConnection = object: ServiceConnection{
        // fungsi pertama yaitu menghubungkan Activity dengan Service
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val myBinder = service as MyBoundService.MyBinder
            mBoundService = myBinder.getService
            mServiceBound = true
        }

        // Apabila kelas service sudah terputus
        override fun onServiceDisconnected(p0: ComponentName?) {
            mServiceBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickButtonListener(binding)
    }

    private fun setOnClickButtonListener(binding: ActivityMainBinding){
        with(binding){
            btnStartService.setOnClickListener {
                val mStartServiceIntent = Intent(this@MainActivity,MyService::class.java)
                startService(mStartServiceIntent)
            }
            btnStartBoundService.setOnClickListener {
                /*
                Pada kode di atas kita menggunakan bindService yang digunakan untuk memulai mengikat kelas MyBoundService ke kelas MainActivity.
                Sedangkan mBoundServiceIntent adalah sebuah intent eksplisit yang digunakan untuk menjalankan komponen dari dalam sebuah aplikasi.
                Sedangkan mServiceConnection adalah sebuah ServiceConnection berfungsi sebagai callback dari kelas MyBoundService.
                Kemudian ada juga BIND_AUTO_CREATE yang membuat sebuah service jika service tersebut belum aktif. S
                 */
                val mBoundIntentService = Intent(this@MainActivity,MyBoundService::class.java)
                bindService(mBoundIntentService,mServiceConnection, BIND_AUTO_CREATE)
            }
            btnStopBoundService.setOnClickListener {
                /*
                Setelah service mulai terikat, maka mTimer akan berjalan sesuai dengan yang ditentukan.
                Selanjutnya untuk mengakhiri MyBoundService yang masih terikat, bisa gunakan kode berikut.
                 */
                unbindService(mServiceConnection)
                /*
                Kode di atas berfungsi untuk melepaskan service dari activity pemanggil.
                Secara tidak langsung maka ia akan memanggil metode onUnbind yang ada di kelas MyBoundService.
                 */
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*
        Kode di atas adalah kelas yang dipanggil di metode onServiceConnected untuk memanggil kelas service.
        Fungsinya untuk mengikat kelas service.
        Kelas MyBinder yang diberi turunan kelas Binder,
        mempunyai fungsi untuk melakukan mekanisme pemanggilan prosedur jarak jauh
         */
        if (mServiceBound){
            unbindService(mServiceConnection)
        }
    }
    /*
    Kesimpulan
    Selain BIND_AUTO_CREATE, fungsi-fungsi lain yang memungkinkan adalah:
    BIND_ABOVE_CLIENT : digunakan ketika sebuah service lebih penting daripada aplikasi itu sendiri.
    BIND_ADJUST_WITH_ACTIVITY : saat mengikat sebuah service dari activity, maka ia akan mengizinkan untuk menargetkan service mana yang lebih penting berdasarkan activity yang terlihat oleh pengguna.
    BIND_ALLOW_OOM_MANAGEMENT : memungkinkan untuk mengikat service hosting untuk mengatur memori secara normal.
    BIND_AUTO_CREATE : secara otomatis membuat service selama binding-nya aktif.
    BIND_DEBUG_UNBIND : berfungsi sebagai bantuan ketika debug mengalami masalah pada pemanggilan unBind.
    BIND_EXTERNAL_SERVICE : merupakan service yang terikat dengan service eksternal yang terisolasi
    BIND_IMPORTANT : service ini sangat penting bagi klien, jadi harus dibawa ke tingkat proses foreground.
    BIND_NOT_FOREGROUND : pada service ini tak disarankan untuk mengubah ke tingkat proses foreground.
    BIND_WAIVE_PRIORITY : service ini tidak akan mempengaruhi penjadwalan atau prioritas manajemen memori dari target proses layanan hosting.

    Metode onDestroy() yang ada di MyBoundService ini berfungsi untuk melakukan penghapusan kelas MyBoundService dari memori. Jadi setelah service sudah terlepas dari kelas MainActivity, kelas MyBoundService juga terlepas dari memori android.
     */
}