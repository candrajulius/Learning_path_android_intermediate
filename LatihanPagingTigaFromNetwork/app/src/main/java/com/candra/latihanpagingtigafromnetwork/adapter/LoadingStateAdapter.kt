package com.candra.latihanpagingtigafromnetwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.candra.latihanpagingtigafromnetwork.databinding.ItemLoadingBinding

/*
Pada dasarnya LoadStateAdapter sama dengan RecycerView.Adapter, yang membedakan adalah terdapat parameter LoadState pada fungsi onBindViewHolder. Dengan menggunakan object ini, Anda dapat mengatur tampilan sesuai dengan kondisi yang sedang terjadi seperti ini
 */
class LoadingStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {
    override fun onBindViewHolder(
        holder: LoadingStateAdapter.LoadingStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateAdapter.LoadingStateViewHolder {
        return LoadingStateViewHolder(
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context),parent,false),retry
        )
    }

    class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit):
            RecyclerView.ViewHolder(binding.root)
    {
                init {
                    binding.retryButton.setOnClickListener {
                        retry.invoke()
                    }
                }

        /*
        Apabila kondisinya adalah sedang loading, maka progressBar akan tampil. Namun, apabila kondisinya eror, akan tampil pesan eror pada TextView errorMsg beserta tombol retryButton. Lalu, ketika tombol retryButton ditekan, lambda retry: () -> Unit akan terpanggil.
         */
        fun bind(loadState: LoadState){
            with(binding){
                if (loadState is LoadState.Error){
                    errorMsg.text = loadState.error.localizedMessage
                }
                progressBar.isVisible = true
                retryButton.isVisible = true
                errorMsg.isVisible = true
            }
        }
    }
}