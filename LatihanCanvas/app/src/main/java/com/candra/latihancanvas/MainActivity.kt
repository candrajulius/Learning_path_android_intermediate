package com.candra.latihancanvas

import android.graphics.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.candra.latihancanvas.databinding.ActivityMainBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
    private val mCanvas = Canvas(mBitmap)
    private val mPaint = Paint()

    private val halfOfWidth = (mBitmap.width/2).toFloat()
    private val halfOfHeight = (mBitmap.height/2).toFloat()

    private val left = 150F
    private val top = 250F
    private val right = mBitmap.width - left
    private val bottom = mBitmap.height.toFloat() - 50F

    private val message = "Apakah kamu suka bermain?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setImageBitmap(mBitmap)
        showText()

        binding.like.setOnClickListener {
            showEars()
            showFace()
            showMouth(true)
            showEyes()
            showNose()
            showHair()
        }

        binding.disLike.setOnClickListener {
            showEars()
            showFace()
            showMouth(false)
            showEyes()
            showNose()
            showHair()
        }

    }

    private fun showFace() {
        val face = RectF(left, top, right, bottom)

        mPaint.color = ResourcesCompat.getColor(resources, R.color.yellow_left_skin, null)
        // Berfungsi untuk menggambar sebuah oval atau lonjong
        mCanvas.drawArc(face, 90F, 180F, false, mPaint)

        mPaint.color = ResourcesCompat.getColor(resources, R.color.yellow_right_skin, null)
        mCanvas.drawArc(face, 270F, 180F, false, mPaint)

    }

    //---------------------------------Latihan Canvas-----------------------------------------------/
    private fun showEyes() {
        mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        mCanvas.drawCircle(halfOfWidth - 100F, halfOfHeight - 10F, 50F, mPaint)
        mCanvas.drawCircle(halfOfWidth + 100F, halfOfHeight - 10F, 50F, mPaint)

        mPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
        mCanvas.drawCircle(halfOfWidth - 120F, halfOfHeight - 20F, 15F, mPaint)
        mCanvas.drawCircle(halfOfWidth + 80F, halfOfHeight - 20F, 15F, mPaint)
    }

    private fun showMouth(isHappy: Boolean) {
        when (isHappy) {
            true -> {
                mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
                val lip = RectF(halfOfWidth - 200F, halfOfHeight - 100F, halfOfWidth + 200F, halfOfHeight + 400F)
                mCanvas.drawArc(lip, 25F, 130F, false, mPaint)

                mPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
                val mouth = RectF(halfOfWidth - 180F, halfOfHeight, halfOfWidth + 180F, halfOfHeight + 380F)
                mCanvas.drawArc(mouth, 25F, 130F, false, mPaint)

            }
            false -> {
                mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
                val lip = RectF(halfOfWidth - 200F, halfOfHeight + 250F, halfOfWidth + 200F, halfOfHeight + 350F)
                mCanvas.drawArc(lip, 0F, -180F, false, mPaint)


                mPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
                val mouth = RectF(halfOfWidth - 180F, halfOfHeight + 260F, halfOfWidth + 180F, halfOfHeight + 330F)
                mCanvas.drawArc(mouth, 0F, -180F, false, mPaint)
            }
        }
    }

    private fun showText() {
        val mPaintText =  Paint(Paint.FAKE_BOLD_TEXT_FLAG).apply {
            textSize = 50F
            color = ResourcesCompat.getColor(resources, R.color.black , null)
        }

        val mBounds = Rect()
        mPaintText.getTextBounds(message, 0, message.length, mBounds)

        val x: Float = halfOfWidth - mBounds.centerX()
        val y = 50F
        mCanvas.drawText(message, x, y, mPaintText)
    }

    //---------------------------------------------Penutup Latihan Canvas---------------------------//


    //---------------------------------------------Latihan Clipping--------------------------------//

    private fun showNose(){
        mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        mCanvas.drawCircle(halfOfWidth - 40F, halfOfHeight + 140F, 15F, mPaint)
        mCanvas.drawCircle(halfOfWidth + 40F, halfOfHeight + 140F, 15F, mPaint)
    }

    private fun showEars(){
        mPaint.color = ResourcesCompat.getColor(resources,R.color.brown_left_hair,null)
        mCanvas.drawCircle(halfOfWidth - 300F, halfOfHeight - 100F,100F,mPaint)

        mPaint.color = ResourcesCompat.getColor(resources,R.color.brown_right_hair,null)
        mCanvas.drawCircle(halfOfWidth + 300F,halfOfHeight - 100F,100F,mPaint)

        mPaint.color = ResourcesCompat.getColor(resources,R.color.red_ear,null)
        mCanvas.drawCircle(halfOfWidth - 300F,halfOfHeight - 100F,60F,mPaint)
        mCanvas.drawCircle(halfOfWidth + 300F,halfOfHeight - 100F,60F,mPaint)
    }

    private fun showHair(){
        // Menyimpan pengaturan canvas saat ini
        mCanvas.save()

        //----------------------------------- Membuat kerangka object yang akan dipotong------------//
        val path = Path()

        path.addCircle(halfOfWidth - 100F,halfOfHeight - 10F,150F,Path.Direction.CCW)
        path.addCircle(halfOfWidth + 100F,halfOfHeight - 10F,150F,Path.Direction.CCW)

        val mouth = RectF(halfOfWidth - 250F,halfOfHeight,halfOfWidth + 250F,halfOfHeight + 500F)
        path.addOval(mouth,Path.Direction.CCW)
        //------------------------------------ Akhir Kerangkan Object Yang DiPotong----------------//

        // Memotong Object Kedalam Canvas
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            mCanvas.clipPath(path,Region.Op.DIFFERENCE)
        }else{
            mCanvas.clipOutPath(path)
        }

        // Object yang tampil akan terpotong berdasarkan path yang telah diatur
        val face = RectF(left,top,right,bottom)

        mPaint.color = ResourcesCompat.getColor(resources,R.color.brown_left_hair,null)
        mCanvas.drawArc(face,90F,180F,false,mPaint)

        mPaint.color = ResourcesCompat.getColor(resources,R.color.brown_right_hair,null)
        mCanvas.drawArc(face,270F,180F,false,mPaint)

        // Mengembalikan posisi canvas sebelum ada ditambah clip
        mCanvas.restore()
    }



    //---------------------------------------------Penutup Latihan Clipping-----------------------//


/*
    Kesimpulan
    Fungsi drawArc membutuhkan 5 parameter, yakni:
    * RectF yang digunakan untuk menentukan posisi dari object oval atau lonjong.
    * Sudut pertama atau sebagai titik pertama sebuah object akan digambar.
    * Total sudut yang akan digambar pada object tersebut.
    * Menggambar object dengan titik pusat atau intinya.
    * Paint yang digunakan untuk mewarnai object.


     drawCircle
     Dalam teori, kita sempat membahas bagaimana membangun lingkaran dalam sebuah canvas. Berbeda dengan oval, untuk lingkaran yang harus diketahui adalah di mana titik lingkaran tersebut akan ditempatkan


    drawText
    Seperti pembahasan sebelumnya, ketika Anda ingin menuliskan teks ke dalam canvas, Anda perlu membuat kontainer untuk teks tersebut. Fungsinya agar kita bisa dengan mudah menentukan lokasi dari teks tersebut, terlebih jika Anda ingin menampilkan secara rata tengah.

     Perhatikan! Dalam kode tersebut kita telah menginisialisasi mBounds yang nantinya digunakan untuk membungkus teks yang telah dibuat. Dari sana kita bisa mengetahui titik tengah dari teks tersebut dengan mBounds.center. Setelah diketahui posisi x dan y dari teks tersebut, cukup masukkan kedua posisi tersebut sebagai parameter di dalam fungsi drawText. Jangan lupa untuk mewarnai dan memberikan teks yang akan ditampilkan.


     Menghapus Object dalam Canvas
     Mungkin Anda saat ini bertanya, bagaimana caranya menghapus object yang ada di dalam canvas? Jawabannya cukup mudah, yakni dengan menempatkan object atau warna ke dalam canvas tersebut.
     mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC)

     Path
     Kita baru menemukan penggunaan path dalam latihan kali ini. Path digunakan untuk menggambar sebuah garis yang saling berkaitan, hingga membentuk sebuah object. Anda bisa menggambar sebuah object path ke dalam Canvas menggunakan drawPath. Tentunya, ketika memanggil fungsi drawPath, Anda perlu menentukan warna pada object tersebut menggunakan paint.


     Jika Anda perhatikan, saat ini path memiliki beberapa bagian, yakni 2 lingkaran dan 1 oval. Dengan bantuan add, Anda akan lebih mudah untuk menggambar sebuah object ke dalam sebuah path. Setelah itu, gunakan dan terapkan path tersebut ke dalam canvas. Tujuannya adalah untuk memotong object lain berdasarkan object yang ada dalam path.

     Clip
    Seperti yang kita tahu, Anda bisa memotong dan menempelkan sebuah object dengan mudah melalui clipping. Dalam kasus ini, kita telah membuat rambut untuk object wajah tersebut.
    Ada sedikit perbedaan di antara clipPath dan clipOutPath, yakni dalam parameter tambahan Region.Op.DIFFERENCE. Parameter Region.Op digunakan untuk menentukan bagaimana object clip dimodifikasi. Terdapat beberapa opsi untuk nilai dari Region.OP, salah satunya adalah Difference. Ia memiliki kemampuan untuk memotong dan menyisakan bagian yang tidak beririsan. Sehingga, memungkinkan parent view dapat memotong canvas untuk menentukan area yang akan digambar. Jika Anda bingung, silakan lihat beberapa opsi Region.OP berikut:
     Setelah membuat object clip ke dalam canvas, setiap ada object yang dimasukkan ke dalam canvas akan terpotong. Oleh karena itu, object berikut akan terpotong.
     Pertanyaannya, bagaimana jika kita ingin menggambar di atas object yang terpotong dalam Canvas? Kita bisa menggunakan fungsi save dan restore untuk menyimpan dan mengembalikan pengaturan canvas yang ada.

     Save dan Restore
     Pasti Anda bertanya-tanya, bagaimana jika ada object yang ingin digambar pada object yang dipotong (clipping)? Jawabannya, Anda bisa menyimpan pengaturan yang ada di dalam Canvas. Caranya dengan memanfaatkan fungsi save() dan restore(). Seperti inilah penerapannya:
     Dengan memanfaatkan save(), kita bisa menyimpan beberapa pengaturan yang telah Anda atur dalam canvas. Kemudian kita bisa memodifikasinya agar sesuai dengan keinginan. Setelah itu, ketika Anda ingin mengembalikan ke pengaturan sebelumnya, cukup panggil restore(). Kurang lebih alurnya menjadi seperti ini:
     */

}