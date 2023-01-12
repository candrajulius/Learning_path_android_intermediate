package com.candra.latihancamerax

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.candra.latihancamerax.api.ApiConfig
import com.candra.latihancamerax.databinding.ActivityMainBinding
import com.candra.latihancamerax.model.FileUploadResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {
    private var getFile: File? = null
    private lateinit var binding: ActivityMainBinding

    companion object{
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setOnclickListener()
    }

    private fun setOnclickListener(){
        binding.apply {
            cameraXButton.setOnClickListener {
                startCameraX()
            }

            cameraButton.setOnClickListener {
                startTakePhotoWithCamera()
            }

            galleryButton.setOnClickListener {
                startGallery()
            }

            uploadButton.setOnClickListener {
                uploadImage()
            }

        }
    }

    /*
    Ketika berpindah dari MainActivity ke CameraActivity, kita menggunakan Intent.
    Lain halnya dengan penggunaan resultForActivity yang dapat menangkap apa yang dikembalikan dalam CameraActivity.
     */
    private fun startCameraX(){
        val intentCameraX = Intent(this,CameraActivity::class.java)
        launcherIntentCameraX.launch(intentCameraX)
    }

    private fun startTakePhotoWithCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        /*
        Kemudian untuk ActivityResult,
        kita ubah menjadi seperti ini.
        Di sini kita menggunakan bantuan Util.createCustomTempFile untuk menampung gambar hasil dari IntentCamera.
        Selanjutnya, kita bisa mencari tahu di mana lokasinya dengan bantuan FileProvider.getUriForFile.
         */
        createCustomTemptFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@MainActivity,
                "com.candra.latihancamerax",
                it
            )

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    // Kasus untuk gallery
    private fun startGallery(){
        val intent = Intent().apply {
            action = ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent,"Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                makeToast("Tidak mendapatkan permission",this@MainActivity)
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }


    /*
    Kasus di atas menjelaskan ketika file telah didapatkan, kita bisa mengubahnya menjadi Bitmap. Setelah diubah menjadi bitmap, kita bisa mengubah posisi dari bitmap tersebut dengan function rotateBitmap.
     */
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == CAMERA_X_RESULT){
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera",false) as Boolean

            getFile = myFile


            val result = rotateBitmap(BitmapFactory.decodeFile(getFile?.path),isBackCamera)

            binding.previewImageView.setImageBitmap(result)
        }
    }

    /*
    Seperti halnya CameraX, kita juga menggunakan ActivityResult untuk mendapatkan hasil dari Intent Camera.
     */
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){

            /*
            Karena currentPhotoPath sudah kita dapatkan sebelum memanggil IntentCamera, maka kita cukup memanggilnya untuk mendapatkan hasil dari Intent Camera.
             */
            val myFile = File(currentPhotoPath)

            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)

            // Ini hanya sebuah thumbnail
            // Hasil yang didapatkan dari result Intent Camera dengan Thumbnail adalah object Bitmap.
//            val imageBitmap = it.data?.extras?.get("data") as Bitmap

            binding.previewImageView.setImageBitmap(result)
        }
    }

    // Untuk kasus intent gallery
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            val selectedImg: Uri = it.data?.data as Uri

            val contentResolver: ContentResolver = contentResolver
            val myFile = uriToFile(selectedImg,this@MainActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadImage(){
        if (getFile != null){
            val file = reduceFileImage(getFile as File)

            val description = "Ini adalah deskripsi gambar".toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            ApiConfig().getApiService().uploadImage(imageMultipart,description).apply {
                enqueue(object: Callback<FileUploadResponse>{
                    override fun onResponse(
                        call: Call<FileUploadResponse>,
                        response: Response<FileUploadResponse>
                    ) {
                        if (response.isSuccessful){
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error){
                                makeToast(responseBody.message,this@MainActivity)
                            }
                        }else{
                            makeToast(response.message(),this@MainActivity)
                        }
                    }

                    override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                        makeToast("Gagal instance Retrofit",this@MainActivity)
                    }

                })
            }

        }else{
            makeToast("Silahkan masukkan berkas gambar terlebih dahulu",this@MainActivity)
        }
    }



}

// Kesimpulan
/*
Kode onRequestPermissionsResult digunakan untuk menanggapi apakah aplikasi sudah mendapatkan izin atau belum, sedangkan ActivityCompat.requestPermissions digunakan untuk melakukan permintaan izin (request permission). Selain itu, Anda juga bisa menggunakan bantuan library third party untuk menangani permission. Salah satu contohnya adalah EasyPermission. https://github.com/googlesamples/easypermissions.

 Thumbnail
Anda dapat menggunakan Thumbnail dari Intent Camera untuk icon atau logo dalam sebuah aplikasi dan tidak lebih dari itu. Sebab, ketika Anda menampilkannya selayaknya CameraX dan Intent Gallery, maka yang ada hanyalah gambar blur.

Full Size
Untuk mendapatkan gambar ukuran penuh sedikit lebih rumit dibandingkan dengan mengambil gambar thumbnail dari Intent Camera. Ada beberapa yang perlu Anda lakukan.



/*
Kita kembali memanfaatkan Utils.createCustomTempFile untuk menampung hasil dari Intent Gallery secara sementara. Setelah itu, kita memanfaatkan contentResolver, inputStream, dan outputStream untuk menulis data dari Uri ke dalam myFile. Sehingga, nantinya myFile bisa kita gunakan untuk fitur mengunggah file ke server.

Jika Anda perhatikan, dalam kode tersebut kita menentukan bahwa tipe data yang akan kita cari adalah “image/*” atau gambar dengan tipe data apa pun. Sesaat setelah kode dipanggil, aplikasi akan membuka gallery dan Anda bisa memilih gambar apa pun yang ada di sana. Setelah memilih salah satu gambar, hasilnya akan ditampilkan dalam aplikasi.

Perlu Anda tahu juga bahwa saat ini Google sedang membuat abstraksi dari MediaStore untuk mengambil Foto. Sayangnya saat ini masih dalam versi alpha. Anda bisa melihat perkembangannya di Photo Picker.
 */

 */
 */

/*
Library Retrofit
Sebenarnya, untuk melakukan pemanggilan endpoint atau dalam kasus ini adalah mengunggah file, tidak harus menggunakan retrofit. Masih banyak library networking yang bisa Anda gunakan, seperti Fast Android, OkHttp, Ion, Android Sync, dan masih banyak lagi. Namun, dalam latihan kali ini kita menggunakan Retrofit untuk mengatasi pemanggilan endpoint tersebut.

Retrofit adalah satu library buatan Square yang populer digunakan untuk melakukan Networking ke Web API. Dengan Retrofit, mengatur endpoint API dan parsing JSON jadi jauh lebih mudah. Untuk menggunakannya, Anda perlu menambahkan beberapa library terlebih dahulu seperti berikut.


 Selain menggunakan Retrofit, kita juga menggunakan beberapa hal dalam builder retrofit seperti di bawah ini.
    HttpLoggingInterceptor : Menampilkan log permintaan dan informasi tanggapan selama pemanggilan endpoint.

    OkHTTPClient : Membangun koneksi dengan client.

    GsonConverterFactory : Menambahkan converter factory untuk object serialization dan deserialization.


String.toRequestBody : Digunakan untuk mengubah string menjadi request body. Request body adalah informasi yang akan digunakan untuk melakukan permintaan ke API. Tak hanya string, kita juga bisa mengubah beberapa tipe data menjadi request body, contohnya adalah ByteString dan ByteArray.
String.toMediaType : Digunakan untuk mengubah nilai string menjadi sebuah media type.
“text/plain” : Digunakan sebagai MediaType, yang berarti data yang digunakan berupa teks.
File.asRequestBody : Digunakan untuk mengubah file menjadi request body. Seperti halnya String.toRequestBody, bedanya hanya tipe data yang digunakan, yakni file.
String.toMediaTypeOrNull : Digunakan untuk mengubah nilai string menjadi sebuah media type. Bedanya dengan .toMediaType adalah nilai yang dikembalikan oleh .toMediaTypeOrNull bersifat nullable.
“image/jpeg” : Digunakan sebagai MediaType sehingga data yang digunakan berupa gambar dengan format jpeg.
MultipartBody.Part.createFormData: Digunakan untuk membuat form data yang dibutuhkan oleh endpoint. Ia memiliki tiga argument yang memiliki kegunaan masing-masing seperti berikut.
name : Sebagai kata kunci yang sesuai dengan endpoint.
filename : Memberikan nama pada file yang akan dikirim.
body : Melampirkan file yang sudah dibungkus dalam bentuk Request Body.

 */