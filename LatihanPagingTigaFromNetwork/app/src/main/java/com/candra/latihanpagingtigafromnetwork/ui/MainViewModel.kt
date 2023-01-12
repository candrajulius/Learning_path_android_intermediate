package com.candra.latihanpagingtigafromnetwork.ui

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.candra.latihanpagingtigafromnetwork.data.QuoteRepository
import com.candra.latihanpagingtigafromnetwork.di.Injection
import com.candra.latihanpagingtigafromnetwork.network.QuoteResponseItem
import kotlinx.coroutines.launch

class MainViewModel(private val quoteRepository: QuoteRepository) : ViewModel() {
    private val _quote = MutableLiveData<List<QuoteResponseItem>>()

    /*
    Pada bagian ini, kita mengubah data yang sebelumnya List menjadi PagingData. PagingData adalah list khusus yang digunakan untuk menyimpan data tiap halaman. Di sini juga terdapat operator cachedIn yang berfungsi untuk menyimpan data ke dalam cache. Sehingga tidak terjadi error ketika terjadi configuration change karena adanya duplikasi.
     */
    val quote: LiveData<PagingData<QuoteResponseItem>> = quoteRepository.getQuote().cachedIn(viewModelScope)

}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}