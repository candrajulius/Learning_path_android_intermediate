package com.candra.latihancamerax

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.candra.latihancamerax.databinding.ActivityCameraBinding
import java.lang.Exception

class CameraActivity: AppCompatActivity()
{
    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    /*
    Di sini, pastikan Anda juga sudah menyediakan selector camera. Fungsinya adalah untuk menentukan kamera mana yang akan digunakan, apakah kamera depan atau belakang.
     */
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            switchCamera.setOnClickListener {
                switchCamera()
            }
            captureImage.setOnClickListener {
                takePhoto()
            }
        }

        hideSystemUI()
    }

    /*
    Penggunaan cameraSelector juga kita atur dalam onclick button switchCamera.
     */
    private fun switchCamera(){
        cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)){
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()

    }


    private fun startCamera(){
        // Instance dari ProcessCameraProvider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        /*
        Kode di atas digunakan untuk mengikat (binding) siklus hidup kamera ke pemilik siklus hidup (bisa dikatakan adalah Activity). Sehingga, kita tidak perlu mengatur kapan menutup dan membuka kamera karena CameraX adalah life-cycle aware atau tahu kapan harus melakukannya.
         */

        // Memanggil method listener dengan dua argumen
        // Argumen pertama adalah Runnable dan argumen kedua adalah ContextCompat.getMainExecutor(this)
        // Yang mengembalikkan executor di MainThread
        cameraProviderFuture.addListener({
            /*
            Di dalam Runnable, kita menambahkan ProcessCameraProvider yang berfungsi untuk mengikat lifecyclekamera ke LifecycleOwner selama jalannya aplikasi.
             */
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            /*
            Di baris selanjutnya kita membuat sebuah object preview. Setelah itu, kita dapat menyediakan surface dari PreviewView yang sudah ditambahkan dalam berkas layout.
             */
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            /*
            Setelah itu, kita menetapkan imageCaptureyang nantinya digunakan function takePhoto untuk mengambil gambar dari kamera.
             */
            imageCapture = ImageCapture.Builder().build()

            /*
            Setelah itu, kita membuat sebuah blok kode try-catch.
            Fungsinya adalah untuk memastikan bahwa kesalahan selama menghubungkan cameraProvider dengan cameraSelector dan preview. Jika ada kesalahan selama proses menghubungkan ketiga hal tersebut,
            Anda bisa menggunakan blok kode catch untuk menangani kesalahan tersebut.
             */
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }catch (exc: Exception){
                makeToast("Gagal memunculkan camera",this@CameraActivity)
            }
        },ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        /*
        Kita telah menginisialisasi imageCapture dan menghubungkannya dengan cameraProvider di dalam function startCamera. Nah, hal pertama yang perlu kita lakukan adalah memastikan imageCapture tidak dalam kondisi null supaya tidak terdapat bug atau crash.
         */
        val imageCapture = imageCapture ?: return


        /*
        Baris selanjutnya adalah menyiapkan file yang akan digunakan untuk menampung hasil gambar dari kamera.
         */
        val photoFile = createFile(application)

        /*
        Setelah file berhasil dibuat, kini saatnya membuat object OutputFileOptions. File ini digunakan untuk menjelaskan secara detail bagaimana output atau hasil dari kamera nantinya.
         */
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        /*
        Selain menyimpan dalam file yang telah tersedia, kita bisa juga menyimpannya dalam MediaStore sehingga aplikasi lain dapat membuka gambar tersebut.
         */


        /*
        Dalam kasus di atas, kita memanggil function takePicture dengan dua argument.
        Pertama adalah outputOptions yang sudah dibuat sebelumnya dan ContextCompat.
        getMainExecutor(this) yang mengembalikan executor di MainThread. Setelah itu, takePicture akan mengembalikan dua function yakni onError dan onImageSave. OnError digunakan untuk menangani jika terdapat kegagalan selama proses pengambilan gambar. Sedangkan onImageSaved digunakan untuk menangani ketika proses pengambilan gambar berhasil dilakukan. Dalam kasus ini, kita menggunakan Intent untuk mengirim hasil file yang didapatkan ke halaman utama.
        Selain itu, kita juga menyimpan state dari camera.
         */
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    makeToast("Berhasil mengambil gambar",this@CameraActivity)
                    val intent = Intent().apply {
                        putExtra("picture",photoFile)
                        putExtra(
                            "isBackCamera",
                            cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                        )
                    }
                    setResult(MainActivity.CAMERA_X_RESULT,intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    makeToast("Gagal mengambil gambar",this@CameraActivity)
                }

            }
        )
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    // Kesimpulan
    /*
    Jika Anda lihat, versi dari library memang masih beta. Namun, tetap akan berjalan dengan baik. Untuk mengikuti perkembangan versi dari library, silakan kunjungi halaman CameraX.

    Berikut detail dari masing-masing library tersebut.

    Camera2 : Menampilkan kamera dalam aplikasi serta mengambil gambar.
    lifecycle : Mengatur daur hidup atau lifecycle dari CameraX.
    view : Sebagai View Camera. Jika Anda lihat dalam berkas activity_camera.xml, komponen tersebut bernama androidx.camera.view.PreviewView.
    Jika Anda perhatikan, di sini memang terdapat kejanggalan, yakni camera2 dalam library tersebut. Sebenarnya base atau core dari CameraX adalah Camera2 yang sudah ada sebelumnya. Nah, peran CameraX adalah menyederhanakan implementasi dari Camera2. Selain itu, CameraX juga mempunyai lebih banyak peningkatan dalam hal fitur jika dibandingkan dengan Camera2.
     */

}