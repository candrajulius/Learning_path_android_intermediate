package com.candra.latihanpagingtigafromnetwork.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.candra.latihanpagingtigafromnetwork.MainCoroutineRule
import com.candra.latihanpagingtigafromnetwork.adapter.QuoteListAdapter
import com.candra.latihanpagingtigafromnetwork.di.DataDummy
import com.candra.latihanpagingtigafromnetwork.getOrAwaitValue
import com.candra.latihanpagingtigafromnetwork.network.QuoteResponseItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var newsViewModel: MainViewModel

    /*
    InstantTaskExecutorRule : rule ini digunakan supaya background process dari Architecture Component seperti LiveData dapat dijalankan secara synchronous.
MainCoroutineRule : custom rule yang digunakan untuk menjalankan coroutine di dalam testing dengan memanfaatkan TestCoroutineDispatcher.

     */

    @Test
    fun `when Get Quote Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyQuotes = DataDummy.generateDummyQuoteResponse()
        val data = PagedTestDataSources.snapshot(dummyQuotes)
        val quote = MutableLiveData<PagingData<QuoteResponseItem>>()
        quote.value = data

        /*
        Pertama Anda perlu meng-observe data dari LiveData dengan menggunakan extension getOrAwaitValue() supaya lebih aman. Yang menjadi masalah adalah data yang didapatkan bukan berupa List, namun PagingData karena kita menggunakan Paging. Nah, untuk membaca data tersebut. Anda memerlukan AsyncPagingDataDiffer untuk bisa mendapatkan datanya. AsyncPagingDataDiffer merupakan helper class yang digunakan di dalam PagingDataAdapter untuk memetakan PagingData pada adapter.
         */
        Mockito. `when`(newsViewModel.quote).thenReturn(quote)

        /*
        Pertama Anda perlu meng-observe data dari LiveData dengan menggunakan extension getOrAwaitValue() supaya lebih aman.
         */
        val actualNews = newsViewModel.quote.getOrAwaitValue()

        /*
         Yang menjadi masalah adalah data yang didapatkan bukan berupa List, namun PagingData karena kita menggunakan Paging. Nah, untuk membaca data tersebut. Anda memerlukan AsyncPagingDataDiffer untuk bisa mendapatkan datanya.
         AsyncPagingDataDiffer merupakan helper class yang digunakan di dalam PagingDataAdapter untuk memetakan PagingData pada adapter.
         */
        val differ = AsyncPagingDataDiffer(
            diffCallback = QuoteListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher,
        )

        /*
         memasukkan PagingData menggunakan submitData.
         */
        differ.submitData(actualNews)

        /*
         fungsi advanceUntilIdle untuk menjalankan semua proses yang sedang tertunda.
         */
        advanceUntilIdle()

        Mockito.verify(newsViewModel).quote

        /*
        Kemudian, Anda bisa mendapatkan data dengan memanggil fungsi snapshot untuk mendapatkan datanya.
         */
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyQuotes.size,differ.snapshot().size)
        Assert.assertEquals(dummyQuotes[0].author,differ.snapshot()[0]?.author)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback{
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

/*
Dengan kelas ini, Anda dapat mengatur data yang tampil dengan menggunakan PagingData.from() pada fungsi snapshot. Selain itu, Anda juga dapat mengatur key untuk halaman yang ingin ditampilkan pada fungsi load. Namun, apabila Anda tidak ingin melakukan pengetesan terkait halaman yang tampil, sebenarnya Anda bisa saja langsung memanggil fungsi PagingData.from() tanpa membuat kelas baru seperti ini.
 */
class PagedTestDataSources private constructor(private val items: List<QuoteResponseItem>):
        PagingSource<Int,LiveData<List<QuoteResponseItem>>>(){

   companion object{
       fun snapshot(item: List<QuoteResponseItem>): PagingData<QuoteResponseItem>{
           return PagingData.from(item)
       }
   }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<QuoteResponseItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<QuoteResponseItem>>> {
        return LoadResult.Page(emptyList(),0,1)
    }

}