package com.candra.latihanstackwidget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf

internal class StackRemoteFactory(private val mContext: Context): RemoteViewsService.RemoteViewsFactory
{

    private val mWidgetItems = ArrayList<Bitmap>()


    override fun onCreate() {

    }

    /*
    Proses onDataSetChanged di sini dapat digunakan untuk memuat semua data yang akan kita gunakan pada widget. Proses load di sini harus kurang dari 20 detik. Jika tidak, akan terjadi ANR (Application Not Responding).
     */
    override fun onDataSetChanged() {
        // Ini berfungsi untuk melakukan refresh saat terjadi perubahan
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources,R.drawable.darth_vader))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources,R.drawable.star_wars_logo))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources,R.drawable.storm_trooper))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources,R.drawable.starwars))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources,R.drawable.falcon))
    }

    override fun onDestroy() {

    }

    /*
    Metode getCount() haruslah mengembalikan nilai jumlah isi dari data yang akan kita tampilkan.

    Jika datanya 0, maka tampilan yang ditampilkan akan sesuai dengan layout yang kita definisikan pada remoteviews.setEmptyView().
     */
    override fun getCount(): Int = mWidgetItems.size

    /*
    Pada metode getViewAt kita memasang item yang berisikan ImageView. Kita akan memasang gambar bitmap dengan memanfaatkan remoteviews. Kemudian item tersebut akan ditampilkan oleh widget.
     */
    override fun getViewAt(position: Int): RemoteViews {
       val rv = RemoteViews(mContext.packageName,R.layout.widget_item)
       rv.setImageViewBitmap(R.id.imageView,mWidgetItems[position])

        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to position
        )
        val fillIntent = Intent()
        fillIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView,fillIntent)
        return rv
    }

    // Mengembalikan
    override fun getLoadingView(): RemoteViews? = null

    /*
    Pada metode getViewTypeCount(), kita perlu mengembalikan nilai yang lebih dari 0. Nilai di sini mewakili jumlah layout item yang akan kita gunakan pada widget.
     */
    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}