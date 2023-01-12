package com.candra.latihanpagingtigafromnetwork.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.latihanpagingtigafromnetwork.R
import com.candra.latihanpagingtigafromnetwork.adapter.LoadingStateAdapter
import com.candra.latihanpagingtigafromnetwork.adapter.QuoteListAdapter
import com.candra.latihanpagingtigafromnetwork.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Candra Julius Sinaga"
            subtitle = getString(R.string.app_name)
        }

        binding.rvQuote.layoutManager = LinearLayoutManager(this)

        getData()
    }

    private fun getData() {
        val mainAdapter = QuoteListAdapter()
        /*
        Kemudian untuk menggunakan LoadStateAdapter, Anda cukup memanggil fungsi withLoadStateFooter seperti berikut:
         */
        binding.rvQuote.adapter = mainAdapter.withLoadStateFooter(
            /*
            withLoadStateFooter digunakan untuk menampilkan loading pada footer (bagian bawah), sedangkan withLoadStateHeader untuk menampilkan loading pada header (bagian atas). Selain itu, withLoadStateHeaderAndFooter juga dapat digunakan untuk menampilkan loading di keduanya
             */
            footer = LoadingStateAdapter{
                /*
                Pada LoadingStateAdapter kita mengisi lambda dengan memanggil fungsi adapter.retry. Fungsi ini akan mengulang kembali fungsi load yang sebelumnya gagal.
                 */
                mainAdapter.retry()
            }
        )
        /*
        Pada PagingDataAdapter kita menggunakan fungsi submitData seperti berikut:
        Apabila keluarannya dalam bentuk LiveData, kita perlu menambahkan parameter lifecycle seperti contoh di bawah.
         */
        mainViewModel.quote.observe(this) {
            mainAdapter.submitData(lifecycle,it)
        }
    }
}