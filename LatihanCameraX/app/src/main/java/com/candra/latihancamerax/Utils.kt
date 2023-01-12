package com.candra.latihancamerax

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStampt: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun makeToast(message: String,context: Context){
    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
}

/*
Jika Anda perhatikan sekilas, ada dua cara membuat file yang sudah disediakan aplikasi yakni membuat file dalam aplikasi atau secara temporary.
 */
// Untuk kasus CameraX
fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory,"$timeStampt.jpg")
}

// Untuk kasus Intent Camera
fun createCustomTemptFile(context: Context): File{
    val storage: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStampt,".jpg",storage)
}

fun uriToFile(selectImg: Uri, context: Context): File{
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTemptFile(context)

    val inputStream = contentResolver.openInputStream(selectImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf,0,len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) // Flip Gambar
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

// Mengurangi MB dari upload gambar
/*
Jika Anda lihat, ada beberapa langkah yang bisa dilakukan, di antaranya seperti berikut.

Mengubah File menjadi Bitmap.
Menyiapkan variabel compressQuality. Nantinya nilainya akan berkurang seiring ukuran file yang besar.
Menyiapkan variabel streamLength yang berfungsi untuk menampung ukuran bitmap agar sesuai dengan yang diinginkan.
Melakukan perulangan untuk mengompres ketika ukurannya lebih dari 1MB. Setiap iterasinya nilai compressQuality akan berkurang 5. Sehingga, ukuran file akan menjadi lebih kecil.
Menetapkan Bitmap ke dalam File menggunakan FileOutputStream setelah ukurannya kurang dari 1MB.
Mengembalikan file yang dikompres.
 */
fun reduceFileImage(file: File): File{
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength:Int
    do{
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,compressQuality,bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    }while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG,compressQuality,FileOutputStream(file))
    return file
}