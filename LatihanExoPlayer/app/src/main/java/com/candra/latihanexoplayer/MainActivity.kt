package com.candra.latihanexoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.candra.latihanexoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {

    companion object{
        const val URL_VIDEO_DICODING = "https://github.com/dicodingacademy/assets/releases/download/release-video/VideoDicoding.mp4"
        const val URL_AUDIO = "https://github.com/dicodingacademy/assets/raw/main/android_intermediate_academy/bensound_ukulele.mp3"
    }

    private var player: ExoPlayer? = null

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE){
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.videoView.player = player
    }

    private fun initializePlayer(){
        // Inisiasi Media Item
        val mediaItem = MediaItem.fromUri(URL_VIDEO_DICODING)
        val anotherMediaItem = MediaItem.fromUri(URL_AUDIO)
        // Contoh simple dari instance ExoPlayer
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            /**
             * Setelah membuat instance dari ExoPlayer, Anda bisa menerapkannya pada PlayerView yang ada di berkas layouting. Caranya cukup simpel, cukup panggil method setPlayer dari PlayerView.
             */
            viewBinding.videoView.player = exoPlayer
            exoPlayer.apply {
                /*
                Setelah membuat instance dari ExoPlayer,
                Anda bisa menerapkannya pada PlayerView yang ada di berkas layouting.
                Caranya cukup simpel, cukup panggil method setPlayer dari PlayerView.
                 */

                /*
                Menambahkan MediaItem dalam Playlist
                Untuk menambahkan media item lain, Anda bisa menggunakan fungsi addMediaItem.
                 */
                setMediaItem(mediaItem)
                addMediaItem(anotherMediaItem)
            }
        }
    }

    /*
    Melepaskan ExoPlayer (Releasing)
    Perlu Anda tahu, melepaskan (release) resource saat tidak diperlukan lagi itu adalah hal sangat penting untuk dilakukan.
    Tujuannya adalah membebaskan sumber daya (memori, CPU, dan koneksi jaringan) yang digunakan agar bisa digunakan oleh aplikasi lain, contohnya menjalankan ExoPlayer dalam background.
    Untuk melepaskan ExoPlayer dari aplikasi, cukup panggil fungsi release dan tetapkan menjadi null.
     */
    private fun releasePlayer(){
        player?.release()
        player = null
    }

    /*
    Lalu, bagaimana untuk menyiapkan ExoPlayer? Apakah ada perbedaan antara API 24 dengan API di atasnya? Tentu saja ada.
    Android API level 24 dan yang lebih tinggi mendukung multiple window.
    Karena aplikasi dapat terlihat, tetapi tidak aktif dalam mode split window, maka Anda perlu menginisialisasi ExoPlayer ketika onStart dipanggil.
    Lain halnya dengan Android API level 24 dan yang lebih rendah, Anda harus menunggu selama mungkin hingga Anda selesai mengambil sumber daya yang ada.
    Oleh karena itu, untuk menginisialisasi ExoPlayer harus menunggu hingga onResume dipanggil.
     */

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24){
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        if (Util.SDK_INT < 24 && player == null){
            initializePlayer()
        }
    }

    /*
    Menyesuaikan ExoPlayer dengan Lifecycle Activity
    Lalu kapan kita harus melepas (release) player dari aplikasi? Tentunya kita bisa memanfaatkan lifecycle dari sebuah Activity yang ada.

    Perlu Anda tahu, dengan API Level 23 dan lebih rendah, tidak ada jaminan fungsi onStop akan dipanggil.
    Oleh karena itu, Anda harus melepaskan ExoPlayer sedini mungkin di method onPause.

    Sebaliknya, dengan API Level 24 dan lebih tinggi (yang membawa mode multi-window dan split-window), onStop dijamin pasti akan dipanggil.
    Dalam status paused (dijeda), Activity Anda masih terlihat di layar.
    Jadi, Anda harus menunggu untuk melepaskan ExoPlayer hingga onStop dipanggil.
     */

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 24){
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun hideSystemUI(){
        WindowCompat.setDecorFitsSystemWindows(window,false)
        WindowInsetsControllerCompat(window,viewBinding.videoView).let {
            it.let {
               it.hide(WindowInsetsCompat.Type.systemBars())
               it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    // Kesimpulan
    /*
    Jika Anda perhatikan, selain menambahkan gambar kita juga menambahkan beberapa hal. Berikut detailnya:
    default_artwork = menggunakan aset tertentu sebagai gambar default untuk ExoPlayer.
    use_artwork = mengatur state apakah ingin menggunakan artwork atau tidak.
    reize_mode = menentukan ukuran dari artwork yang akan muncul di layar.
    show_buffering = mengatur apakah perlu manampilkan indikator loading pada keadaan tertentu atau tidak.

    Sebenarnya tidak hanya kedua library tersebut, masih ada banyak library exoplayer yang bisa Anda pakai. Berikut keterangan dari berbagai library tersebut.

    exoplayer-core: Digunakan sebagai fungsi utama (Core functionality) dan sifatnya wajib  ada (required).
    exoplayer-ui: Digunakan untuk menyediakan berbagai komponen UI dan resource yang bisa digunakan oleh ExoPlayer.
    exoplayer-dash: Digunakan untuk mendukung konten Dynamic Adaptive Streaming over HTTP (DASH).
    exoplayer-hls: Digunakan untuk mendukung konten HTTP Live Streaming (HLS).
    exoplayer-rtsp: Digunakan untuk mendukung konten Real Time Streaming Protocol (RTSP).
    exoplayer-smoothstreaming: Digunakan untuk konten Smooth Streaming.
    exoplayer-transformer: Digunakan untuk melakukan transformasi fungsionalitas dari sebuah media.


    Anda bisa menjalankan ExoPlayer dengan menekan tombol yang ada di layar (UI dari PlayerView) atau memanggil fungsi play() dari player tersebut.

    player.play()
    Selain fungsi play, ada beberapa fungsi lain untuk mengontrol player seperti berikut.

    play untuk memulai pemutaran.
    pause untuk menjeda pemutaran.
    seekTo untuk melakukan pencarian di dalam media.
    hasPrevious, hasNext, before, dan next untuk melakukan navigasi melalui daftar putar (playlist).
    setRepeatMode untuk mengontrol jika dan bagaimana media diulang.
    setShuffleModeEnabled untuk mengontrol pengacakan dari daftar putar (playlist).
    setPlaybackParameters untuk menyesuaikan kecepatan pemutaran dan nada audio.


    Jika Anda ingin mengondisikan agar exoplayer berjalan secara otomatis ketika sudah siap,
    manfaatkanlah fungsi playWhenReady dengan input true.
    Artinya ketika media item sudah berhasil disajikan, exoplayer akan menjalankannya secara otomatis.

    Contoh:
    exoPlayer.playWhenReady = true
     */
}