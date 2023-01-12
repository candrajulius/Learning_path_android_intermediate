package com.candra.latihanstackwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri

/**
 * Implementation of App Widget functionality.
 */
class ImageBannerWidget : AppWidgetProvider() {


    companion object{

        private const val TOAST_ACTION = "com.candra.latihanstackwidget.TOAST_ACTION"
        const val EXTRA_ITEM = "com.candra.latihanstackwidget.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            // Inisiasi intent dengan mengembalikan dua nilai parameter
            // pertama itu context dan satu lagi StackWidgetService
            val intent = Intent(context,StackWidgetService::class.java)
            // Kirim data berdasarkan satu data widget
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
            // Kirim Uri dengan intent
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName,R.layout.image_banner_widget)
            /*
            Kita memasang RemoteAdapter ke dalam widget dengan menggunakan obyek Intent dan nilai id dari RemoteView yaitu stack_view.
             */
            views.setRemoteAdapter(R.id.stack_view,intent)
            views.setEmptyView(R.id.stack_view,R.id.empty_view)

            val toastIntent = Intent(context,ImageBannerWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)


            val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else 0

            // Menjalankan getBroadcast() untuk melakukan proses broadcast ketika salah satu widget ditekan
            val toastPendingIntent = PendingIntent.getBroadcast(context,0,toastIntent,data)

            views.setPendingIntentTemplate(R.id.stack_view,toastPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId,views)
        }
    }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        /*
        Kode di atas akan dijalankan ketika widget ditekan. Seperti pada latihan sebelumnya, percabangan digunakan untuk membedakan action yang terjadi. Kita dapat mengambil data action tersebut dengan memanfaatkan extra dari sebuah intent.
         */
        val intentAction = intent.action
        intentAction?.let {
            if (intentAction == TOAST_ACTION){
                val viewIndex = intent.getIntExtra(EXTRA_ITEM,0)
                Toast.makeText(context,"Touched view $viewIndex",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

