package com.candra.latihanwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class RandomNumberWidget : AppWidgetProvider() {

    companion object{
       private const val WIDGET_CLICK = "android.appwidget.action.APPWIDGET_UPDATE"
       private const val WIDGET_ID_EXTRA = "widget_id_extra"
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        // View yang ada di xml widget
        val views = RemoteViews(context.packageName, R.layout.random_number_widget)
        // membuat inisiasi untuk widget yang digunakan
        val lastUpdate = "Random: " + NumberGenerator.gererate(100)
        // Ambil view yang ada diwidget lalu kembalikan letakkan ke textView
        views.setTextViewText(R.id.appwidget_text,lastUpdate)
        // Memberikan sebuah aksi pada button ketika diklik.. 2 parameter disini
        // parameter pertama itu id dari button tersebut lalu value nya itu pending intent
        views.setOnClickPendingIntent(R.id.btn_click,getPendinSelfIntent(context,appWidgetId,
            WIDGET_CLICK))
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }



    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        // Gunanya untuk mengupdate semua widget yang ada dilayout
        for (appWidgetId in appWidgetIds) {
            // Upadate widget
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    /*
    Pada metode ini perhatikan nilai baliknya. Nilai balik ini akan di-broadcast ketika Button ditekan.

    Pada obyek Intent, kita menambahkan parameter extra berupa appWidgetId. Ini dimaksudkan agar kita mengetahui widget mana yang ditekan dengan menggunakan appWidgetId sebagai identifier-nya.

    Apa yang di-broadcast tentunya harus ada receiver-nya. Maka dari itu kita melakukan metode override onReceive() pada AppWidgetProvider.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        // jika actionnya WIDGET_CLIK
        if (RandomNumberWidget.WIDGET_CLICK == intent.action){
            // Inisiasi appWidgetManager
            val appWidgetManager = AppWidgetManager.getInstance(context)
            // Inisiasi layout viewnya
            val views = RemoteViews(context.packageName,R.layout.random_number_widget)
            // Inisiasi value yang akan diupdate
            val lastUpdate = "Random: " + NumberGenerator.gererate(100)
            // Ambil data dari pendingIntent dengan menampungnya dalam sebuah variabel dengan
            // key yang sama dan default value 0
            val appWidgetId = intent.getIntExtra(RandomNumberWidget.WIDGET_ID_EXTRA,0)
            // Update text dengan memasukkan value yang ingin diupdate
            views.setTextViewText(R.id.appwidget_text,lastUpdate)
            // Update Widget yang ada di layout
            appWidgetManager.updateAppWidget(appWidgetId,views)
        }
    }
    /*
    Proses penyeleksian dilakukan untuk memeriksa apakah aksi (action) yang terjadi berasal dari event click pada widget yang dimaksud. Pada konteks ini kita menggunakan action berisi static string yang telah kita definisikan sebelumnya pada variabel WIDGET_CLICK.
Pada dasarnya, apa yang terjadi pada metode onReceive() mirip dengan metode updateAppWidget. Yang membedakan hanyalah pada metode onUpdate(), ia dijalankan secara otomatis. Sedangkan metode onReceive() dijalankan ketika aksi klik pada komponen telah di-broadcast.
     */


    /*
    Pada baris kode di atas parameter kedua merupakan PendingIntent yang didapatkan dari getPendingSelfIntent(). Jadi ketika views dengan id btn_click diklik, ia akan menjalankan PendingIntent yang diset.
     */
    private fun getPendinSelfIntent(context:Context,appWidgetId: Int,action: String): PendingIntent{
        // Inisiasi intent
        val intent = Intent(context,RandomNumberWidget::class.java)
        // Ambil action yang ada dintent
        intent.action = action
        // Kirim data dan value yang ada
        intent.putExtra(RandomNumberWidget.WIDGET_ID_EXTRA,appWidgetId)
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else 0
        // kembalikan nilai intent dengan membawa data yang ada di parameter
        return PendingIntent.getBroadcast(context,appWidgetId,intent,data)
    }
}



/*
File XML digunakan untuk mengatur preferensi pada widget. Berikut adalah penjelasan dari masing-masing atribut pada kode di atas:

initialLayout : Mereferensikan widget layout yang sudah dibuat.
minHeight dan minWidth : Ukuran panjang dan lebar dari widget.
previewImage : Gambar ini akan ditampilkan pada saat kita memilih widget.
resizeMode : Untuk mengatur metode dalam resize widget.
updatePeriodMillis : Jangka waktu untuk update widget dalam millisecond.
Nilai minimum yang bisa dimasukkan adalah 30 menit.
widgetCategory : Untuk menentukan kategori widget (homescreen, keyguard, atau searchbox).


 Metode onUpdate() adalah metode yang akan dipanggil ketika widget pertama kali dibuat. Metode ini juga akan dijalankan ketika updatePeriodMillis yang di dalam random_numbers_widget_info.xml mencapai waktunya.

 Pada metode onUpdate() terdapat perulangan dengan menggunakan array appWidgetIds. Perulangan di sini dimaksudkan untuk menentukan widget mana yang akan di-update karena jumlah widget dalam sebuah aplikasi bisa lebih dari 1. Jadi, kita perlu mendefinisikan widget mana yang perlu diperbarui oleh sistem.

 Metode di atas adalah metode yang dipanggil di setiap perulangan appWidgetIds, di mana hampir seluruh proses update ada di dalam metode ini.

 RemoteViews adalah komponen yang dapat kita gunakan untuk mengambil data layout dari widget yang kita pakai. Seperti pada kode setTextViewText() di mana kita mengubah string text pada komponen R.id.appwidget_text.

 Terjadi update widget yang sebenarnya. Parameter 1 adalah id widget yang ingin kita update, sedangkan parameter 2 adalah RemoteViews yang berisikan views yang telah kita modifikasi.

 */