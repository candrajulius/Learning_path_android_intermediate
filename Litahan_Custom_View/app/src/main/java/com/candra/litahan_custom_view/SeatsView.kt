package com.candra.litahan_custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat


@Suppress("UNREACHABLE_CODE")
class SeatsView : View
{

    private val seats: ArrayList<Seat> = arrayListOf() // Array Kosong

    var seat: Seat? = null // ini adalah objek dari data kelas yang telah dibuat

    private val backgroundPaint = Paint()
    private val armesPaint = Paint()
    private val bottomSeatPaint = Paint()
    private val mBounds = Rect()
    private val numberSeatPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
    private val titlePaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)

    // Render View
    // Digunakan untu menggambar. karena terdapat 8 kursi yang sama
    // kita cukup melakukan perulangan saja
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (seat in seats){
            drawSeat(canvas,seat)
        }
        val text = "Silahkan Pilih Kursi"
        titlePaint.apply {
            textSize = 50F
        }
        canvas?.drawText(text,(width / 2F) - 197F,100F,titlePaint)
    }

    private fun drawSeat(canvas: Canvas?, seat: Seat) {
        // Mengatur Warna ketika sudah Dibooking
        if (seat.isBooked){
            backgroundPaint.color = ResourcesCompat.getColor(resources,R.color.grey_200,null)
            armesPaint.color = ResourcesCompat.getColor(resources,R.color.grey_200,null)
            bottomSeatPaint.color = ResourcesCompat.getColor(resources,R.color.grey_200,null)
            numberSeatPaint.color = ResourcesCompat.getColor(resources,R.color.black,null)
        }else{
            backgroundPaint.color =  ResourcesCompat.getColor(resources,R.color.blue_500,null)
            armesPaint.color =  ResourcesCompat.getColor(resources,R.color.blue_700,null)
            bottomSeatPaint.color = ResourcesCompat.getColor(resources,R.color.blue_200,null)
            numberSeatPaint.color = ResourcesCompat.getColor(resources,R.color.grey_200,null)
        }

        // Menyimpan state
        canvas?.save()

        // Background
        canvas?.translate(seat.x as Float, seat.y as Float)

        val backgroundPath = Path()
        // AddRect untuk menggambar persegi

        backgroundPath.addRect(0F,0F,200F,200F,Path.Direction.CCW)
        backgroundPath.addCircle(100F,50F,75F,Path.Direction.CCW)
        canvas?.drawPath(backgroundPath,backgroundPaint)

        // Sandaran tangan
        val armreshPath = Path()
        armreshPath.addRect(0F,0F,50F,200F,Path.Direction.CCW)
        canvas?.drawPath(armreshPath,armesPaint)
        canvas?.translate(150F,0F)
        armreshPath.addRect(0F,0F,50F,200f,Path.Direction.CCW)
        canvas?.drawPath(armreshPath,armesPaint)

        // Bagian Bawah Kursi
        canvas?.translate(-150f,175F)
        val bottomSeatPath = Path()
        bottomSeatPath.addRect(0F,0F,200F,25F,Path.Direction.CCW)
        canvas?.drawPath(bottomSeatPath,bottomSeatPaint)


        // Menulis Nomor Kursi
        canvas?.translate(0F,-175F)
        numberSeatPaint.apply {
            textSize = 50F
            numberSeatPaint.getTextBounds(seat.name,0,seat.name.length,mBounds)
        }

        canvas?.drawText(seat.name,100F - mBounds.centerX(),100F,numberSeatPaint)

        // Mengembalikan ke pengaturan sebelumnya
        canvas?.restore()
    }

    init {
        seats.apply {
            add(Seat(id = 1,name = "A1",isBooked = true))
            add(Seat(id = 2,name = "A2",isBooked = false))
            add(Seat(id = 3,name = "B1",isBooked = false))
            add(Seat(id = 4,name = "B2",isBooked = false))
            add(Seat(id = 5,name = "C1",isBooked = false))
            add(Seat(id = 6,name = "C2",isBooked = false))
            add(Seat(id = 7,name = "D1",isBooked = false))
            add(Seat(id = 8,name = "D2",isBooked = false))
        }


    }

    // View Dimension
    // Disini kita menggunakan onMeasure untuk menentukan posisi X dan Y dari masing
    // Masing kursi yang kosong tersebut
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth,widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight,heightMeasureSpec)

        val halfOfHeight = height / 2
        val halfOfWidth = width / 2
        var value = -600F

        for (i in 0..7){
            if (i.mod(2) == 0){
                seats[i].apply {
                    x = halfOfWidth - 300F
                    y = halfOfHeight + value
                }
            }else{
                seats[i].apply {
                    x = halfOfWidth + 100F
                    y = halfOfHeight + value
                }
                value += 300F
            }
        }
    }


    // View Iniitialization
    constructor(context: Context): super(context)

    // View Iniitialization
    constructor(context: Context, attrs: AttributeSet): super(context,attrs)

    // View Iniitialization
    constructor(context: Context,attrs: AttributeSet,defStyleAttr: Int): super(context,attrs,defStyleAttr)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val halfOfHeight = height / 2
        val halfOfWidth = width / 2

        val widthColumnOne = (halfOfWidth - 300F)..(halfOfWidth - 100F)
        val widthColumnTwo = (halfOfWidth + 100F)..(halfOfWidth + 300F)

        val heightRowOne = (halfOfHeight - 600F)..(halfOfHeight - 400F)
        val heightRowTwo = (halfOfHeight - 300F)..(halfOfHeight - 100F)

        val heightRowThree = (halfOfHeight + 0F)..(halfOfHeight + 200F)
        val heightRowFour = (halfOfHeight + 300F)..(halfOfHeight + 500F)

        when(event?.action){
            ACTION_DOWN -> {
                if (event.x in widthColumnOne && event.y in heightRowOne){
                    booking(0)
                }else if (event.x in widthColumnTwo && event.y in heightRowOne){
                    booking(1)
                }else if (event.x in widthColumnOne && event.y in heightRowTwo){
                    booking(2)
                }else if (event.x in widthColumnTwo && event.y in heightRowTwo){
                    booking(3)
                }else if (event.x in widthColumnOne && event.y in heightRowThree){
                    booking(4)
                }else if (event.x in widthColumnTwo && event.y in heightRowThree){
                    booking(5)
                }else if (event.x in widthColumnOne && event.y in heightRowFour){
                    booking(6)
                }else if (event.x in widthColumnTwo && event.y in heightRowFour){
                    booking(7)
                }
            }
        }
        return true
    }

    private fun booking(booking: Int) {
        for (seat in seats){
            seat.isBooked = false
        }
        seats[booking].apply {
            seat = this
            isBooked = true
        }
        invalidate() // => Digunakan untuk meletakkan state changed dari view
    }


    // Kesimpulan

    /*
    Start : Sebuah Custom View telah siap untuk dipanggil dalam sebuah tampilan.
View Initialize : Proses terjadinya inisialisasi dari pemanggilan Custom View. Anda bisa mengatur proses inisialisasi tersebut melalui konstruktor.
View Dimension : Digunakan untuk mengatur dimensi (baik lebar atau panjang) dari sebuah Custom View melalui metode onMeasure(). Selain itu, kita juga bisa menetapkan dimensi dari masing-masing object yang akan digambar dalam Canvas.
View size changed : Digunakan untuk mendefinisikan perubahan yang terjadi. Jika Custom View yang telah didefinisikan terdapat perubahan sewaktu-waktu, Anda dapat memperbarui dimensi dari masing-masing object melalui metode onSizeChanged.
Determine child view layout : Digunakan untuk menentukan mengatur view lain yang akan ditampilkan dalam Custom View melalui metode onLayout.
RenderView : Digunakan untuk menggambar object ke dalam Canvas melalui metode onDraw.
User Operation : Proses interaksi pengguna dengan Custom View. Anda dapat memanggil fungsi invalidate untuk memperbarui status yang diberikan pengguna.
Finish : Proses akhir dari sebuah Custom View. Biasanya proses tersebut terjadi Custom View berada dalam Activity atau Fragment yang akan berakhir.
     */


}