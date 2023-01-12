package com.candra.latihanpagingtigafromnetwork.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.candra.latihanpagingtigafromnetwork.database.QuoteDatabase
import com.candra.latihanpagingtigafromnetwork.database.RemoteKeys
import com.candra.latihanpagingtigafromnetwork.network.ApiService
import com.candra.latihanpagingtigafromnetwork.network.QuoteResponseItem
import java.lang.Exception

@OptIn(ExperimentalPagingApi::class)
class QuoteRemoteMediator(
    private val databae: QuoteDatabase,
    private val apiService: ApiService
): RemoteMediator<Int,QuoteResponseItem>() {

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    /*
    Anda dapat meng-override fungsi initialize pada RemoteMediator untuk menentukan apakah suatu data yang sudah disimpan kadaluwarsa atau tidak, sehingga perlu dilakukan refresh. Fungsi ini berjalan sebelum fungsi load dijalankan, sehingga Anda dapat mengatur data yang di database terlebih dahulu jika mau.
     */
    override suspend fun initialize(): InitializeAction {
        /*
        Pada latihan ini, Anda menggunakan InitializeAction.LAUNCH_INITIAL_REFRESH supaya data selalu ter-refresh. Dengan begitu, ia akan menentukan LoadType sebagai Refresh yang bisa Anda atur selanjutnya pada fungsi load.

        Selain itu, juga ada InitializeAction.LAUNCH_INITIAL_REFRESH untuk melewatkan pemanggilan fungsi refresh ketika Anda tidak menginginkan untuk memperbarui database.
         */
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, QuoteResponseItem>
    ): MediatorResult {
        val page = when(loadType){
            /*
            Pada bagian ini, kita menentukan halaman yang akan diakses sesuai dengan LoadType yang terjadi. Apabila aplikasi pertama kali dijalankan dengan inisialisasi LAUNCH_INITIAL_REFRESH, getRemoteKeyClosestToCurrentPosition akan bernilai null, sehingga page berisi INITIAL_PAGE_INDEX bernilai 1.
             */
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosesToCurrentPosition(state)
                /*
                Sedangkan, apabila ada di tengah-tengah dan kita ingin melakukan refresh, maka page berisi nextKey - 1, yang berarti halaman yang sama (sekarang).
                 */
                remoteKeys?.nextKey?.minus(1)?: INITIAL_PAGE_INDEX
            }

            /*
            Kemudian LoadType.PREPEND terpanggil ketika kita scroll ke batas atas, lalu ia akan mengambil nilai remote key yang paling awal di database. Jika sudah mentok dan remoteKey bernilai null, artinya aplikasi sudah mencapai akhir dari halaman (endOfPaginationReached) dan tidak melakukan request lagi.
             */
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            /*
            Sebaliknya LoadType.APPEND terpanggil ketika kita scroll ke batas bawah, lalu ia akan mengambil nilai remote key yang paling bawah di database. Jika sudah mentok dan remoteKey bernilai null, artinya aplikasi sudah mencapai akhir dari halaman (endOfPaginationReached) dan tidak melakukan request lagi.
             */
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }



        try {
            val responseData = apiService.getQuote(page,state.config.pageSize)

            val endOfPaginantionReached = responseData.isEmpty()

            /*
            Setelah halaman sudah ditentukan, Anda dapat melakukan request dengan halaman yang sudah ditentukan dan size sesuai dengan pengaturan melalui state.config.pageSize. Setelah request selesai, Anda menjalankan proses coroutine di dalam block withTransaction. Langkah pertama yaitu menghapus semua data, jika LoadType bertipe REFRESH, tentu hal ini perlu dilakukan jika Anda ingin mendapatkan data yang terbaru saja.

            Kemudian pada langkah selanjutnya, Anda menentukan prevKey dan nextKey seperti yang sudah dipelajari pada materi sebelumnya. Lalu, ia akan dibungkus dalam variabel key dan disimpan ke dalam database, begitu juga data dari network-nya.
             */
            databae.withTransaction {
                if (loadType == LoadType.REFRESH){
                    databae.remoteKeyDao().deleteRemoteKeys()
                    databae.quoteDao().deleteAll()
                }
                /*
                Kemudian pada langkah selanjutnya, Anda menentukan prevKey dan nextKey seperti yang sudah dipelajari pada materi sebelumnya. Lalu, ia akan dibungkus dalam variabel key dan disimpan ke dalam database, begitu juga data dari network-nya.
                 */
              val prevKey = if (page == 1) null else page - 1
              val nextKey = if (endOfPaginantionReached) null else page + 1
              val keys = responseData.map {
                  RemoteKeys(id = it.id,prevKey = prevKey,nextKey = nextKey)
              }
                databae.remoteKeyDao().insertAll(keys)
                databae.quoteDao().inserQuote(responseData)
            }
           return MediatorResult.Success(endOfPaginationReached = endOfPaginantionReached)
        }catch (exception: Exception){
          return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int,QuoteResponseItem>): RemoteKeys?{
        return state.pages.lastOrNull{ it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
            databae.remoteKeyDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, QuoteResponseItem>): RemoteKeys?{
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            databae.remoteKeyDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosesToCurrentPosition(state: PagingState<Int, QuoteResponseItem>): RemoteKeys?{
        return state.anchorPosition?.let { position ->
           state.closestItemToPosition(position)?.id?.let { id ->
               databae.remoteKeyDao().getRemoteKeysId(id)
           }
        }
    }

}
