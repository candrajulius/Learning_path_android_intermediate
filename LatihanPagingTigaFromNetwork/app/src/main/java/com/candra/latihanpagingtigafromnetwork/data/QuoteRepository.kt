package com.candra.latihanpagingtigafromnetwork.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.candra.latihanpagingtigafromnetwork.database.QuoteDatabase
import com.candra.latihanpagingtigafromnetwork.network.ApiService
import com.candra.latihanpagingtigafromnetwork.network.QuoteResponseItem

class QuoteRepository(private val quoteDatabase: QuoteDatabase, private val apiService: ApiService) {
    fun getQuote(): LiveData<PagingData<QuoteResponseItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = QuoteRemoteMediator(quoteDatabase,apiService),
            pagingSourceFactory = {
//                QuotePagingSource(apiService)
                quoteDatabase.quoteDao().getAllQuote()
            }
        ).liveData
    }
    /*
    Pager merupakan object yang berfungsi untuk mengolah data dari PagingSource untuk menjadi PagingData. Di sini kita juga dapat mengatur konfigurasi menggunakan PagingConfig pada property config. Berikut beberapa hal yang bisa diatur dengan PagingConfig:

pageSize : Mengatur jumlah data yang diambil per halamannya.
initialLoadSize : Mengatur jumlah data yang diambil pertama kali. Default-nya adalah tiga kali pageSize.
prefetchDistance : Menentukan jarak sisa item untuk mengambil data kembali. Default-nya sama dengan pageSize.
enablePlaceholder : Menentukan apakah menggunakan placeholder atau tidak. Efeknya yaitu pada scroll bar yang pada sebelah kanan list. Apabila bernilai true, scroll bar akan menghitung list yang belum tampil. Sedangkan jika bernilai false, scroll bar hanya menghitung list yang tampil saja.
maxSize : Menentukan jumlah maksimum item yang dapat dimuat di PagingData. Default-nya adalah Int.MAX_VALUE. Ini digunakan jika Anda ingin membatasi data yang disimpan di memori.
jumpThreshold : Menentukan batas jumlah item yang bisa dilewati ketika pengguna melakukan scroll dengan cepat. Default-nya adalah Int.MIN_VALUE.

     Selain Pager.liveData, keluaran lain yang bisa Anda ambil adalah Pager.flow untuk flow dan Flowable/Observable untuk RxJava.
     */
}