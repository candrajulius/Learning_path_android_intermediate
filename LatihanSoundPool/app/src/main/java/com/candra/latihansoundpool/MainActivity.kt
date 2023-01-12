package com.candra.latihansoundpool

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var sp: SoundPool
    private var soundId: Int = 0
    private var spLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSound = findViewById<Button>(R.id.btn_sound_pool)

        /*
        Inisialisasi variable sp di atas tidak menggunakan constructor karena telah deprecated untuk digunakan pada API 21 Lolipop ke atas.
        Sedangkan paramater 10 yang berada di metode
        setMaxStreams() adalah untuk menentukan jumlah streams secara simultan yang dapat diputar secara bersamaan.
         */
        sp = SoundPool.Builder()
            .setMaxStreams(10)
            .build()

        /*
        SoundPool hanya bisa memainkan berkas yang telah dimuat secara sempurna.
        Maka untuk memastikan bahwa proses pemuatan telah selesai, gunakan listener seperti contoh ini:
         */
        sp.setOnLoadCompleteListener{_,_,status ->
            if (status == 0){
                spLoaded = true
            }else{
                Toast.makeText(this@MainActivity,"Gagal load",Toast.LENGTH_SHORT).show()
            }
        }

        soundId = sp.load(this,R.raw.data,1)

        /*
        Dengan memanfaatkan flag spLoaded, Anda dapat mengetahui apakah pemuatan berkas audio sudah selesai atau belum.
        Jika sudah selesai, maka audio dapat dimainkan.
        Berikut adalah penjelasan dari parameter yang ada pada saat tombol play diklik
         */
        btnSound.setOnClickListener {
            if (spLoaded){
                sp.play(soundId,1f,1f,0,0,1f)
            }
        }
    }

    /*
    Kesimpulan
    pengertian paramter dari load
    Parameter soundID merupakan id dari audio yang Anda muat.
LeftVolume dan RightVolume merupakan parameter float dari besar kecilnya volume yang range-nya dimulai dari 0.0 - 1.0.
Priority merupakan urutan prioritas. Semakin besar nilai priority, semakin tinggi prioritas audio itu untuk dijalankan.
Paremeter loop digunakan untuk mengulang audio ketika telah selesai dimainkan. Nilai -1 menunjukkan bahwa audio akan diulang-ulang tanpa berhenti. Nilai 0 menunjukkan audio akan dimainkan sekali. Nilai 1 menunjukkan audio akan dimainkan sebanyak 2 kali.
Parameter rate mempunyai range dari 0.5 - 2.0. Rate 1.0 akan memainkan audio secara normal, 0.5 akan memainkan audio dengan kecepatan setengah, dan 2.0 akan memainkan audio 2 kali lipat lebih cepat.
     */
}