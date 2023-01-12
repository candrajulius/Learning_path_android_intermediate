package com.candra.latihanpagingtigafromnetwork.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.candra.latihanpagingtigafromnetwork.network.ApiService
import com.candra.latihanpagingtigafromnetwork.network.QuoteResponseItem

class QuotePagingSource(private val apiService: ApiService): PagingSource<Int,QuoteResponseItem>()
{

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    /*
    Fungsi getRefreshKey berguna untuk mengetahui kapan mengambil data terbaru dan menggantikan data yang sudah tampil. Biasanya, proses refresh ini dilakukan di sekitar PagingState.anchorPosition, yakni indeks yang paling baru diakses. Apabila prevKey bernilai null, anchorPage-nya adalah halaman pertama. Lalu, apabila nextKey bernilai null, anchorPage-nya adalah halaman terakhir, sedangkan jika kedua nilai prevKey dan nextKey bernilai null, anchorPage-nya adalah initial page.
     */
    override fun getRefreshKey(state: PagingState<Int, QuoteResponseItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val acnhorPage = state.closestPageToPosition(anchorPosition)
            acnhorPage?.prevKey?.plus(1)?: acnhorPage?.nextKey?.minus(1)
        }
    }

    /*
        Lalu, untuk nilai prevKey pada latihan ini akan selalu bernilai null karena initial page dimulai dari 1. Cobalah untuk mengganti INITIAL_PAGE_INDEX menjadi angka lain jika Anda ingin melakukan request ke halaman sebelumnya ketika scroll ke atas.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuoteResponseItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getQuote(position,params.loadSize)

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        }catch (exception: Exception){
            return LoadResult.Error(exception)
        }
    }

    /*
    Fungsi ini akan membuat aplikasi bisa mengambil data selanjutnya. Caranya yaitu dengan menentukan page terlebih dahulu. Ketika aplikasi pertama kali dijalankan, params.key akan bernilai null dan mengambil nilai dari INITIAL_PAGE_INDEX, yakni 1. Sehingga, pada baris selanjutnya ia akan melakukan request dengan parameter page bernilai 1 dan parameter size diambil dari params.loadSize bernilai 15.

    Loh, dari mana nilai 15? Bukankah kita mengaturnya 5 pada bagian Pager. Ingat, ketika aplikasi pertama kali dijalankan, ia menggunakan initialLoadSize, bukan loadSize. Lalu, apabila kita tidak mendefinisikan initialLoadSize, nilai default-nya adalah 3 kali loadSize, yang artinya 3x5 = 15.

    Pada pemanggilan fungsi load selanjutnya, nilai params.key akan selalu bertambah satu, dan size bernilai 5 sesuai dengan yang diatur pada bagian Pager.
     */

}