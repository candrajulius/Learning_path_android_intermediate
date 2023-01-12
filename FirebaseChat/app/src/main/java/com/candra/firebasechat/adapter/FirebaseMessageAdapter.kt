package com.candra.firebasechat.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.candra.firebasechat.R
import com.candra.firebasechat.databinding.ItemMessageBinding
import com.candra.firebasechat.model.Message
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

// Recyclervivew yang ada di firebase adapter
class FirebaseMessageAdapter (
    options: FirebaseRecyclerOptions<Message>,
    private val currentUsername: String?
    ): FirebaseRecyclerAdapter<Message,FirebaseMessageAdapter.MessageViewHolder>(options)
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FirebaseMessageAdapter.MessageViewHolder {

        return MessageViewHolder(
            ItemMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(
        holder: FirebaseMessageAdapter.MessageViewHolder,
        position: Int,
        model: Message
    ) {
       holder.bind(model)
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: Message) {
            with(binding){
                tvMessage.text = item.text
                setTextColor(item.name,tvMessage)
                tvMessenger.text = item.name
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .circleCrop()
                    .into(ivMessenger)
                if (item.timeStampt != null){
                    /*
                    Di sini kita menggunakan fungsi getRelativeTimeSpanString dari DateUtils untuk menghasilkan data berupa lama waktu yang telah berlalu dari data berupa timestamp. Selain menggunakan ini, Anda juga bisa menggunakan library android-ago untuk menampilkan data yang selalu update.
                     */
                    tvTimestamp.text = DateUtils.getRelativeTimeSpanString(item.timeStampt)
                }
            }
        }

        /*
        Apabila currentUserName sama dengan data yang ada di database, dialog akan berwarna biru, sedangkan jika tidak sama (alias nama akun lain), dialog akan berwarna kuning
         */
        private fun setTextColor(username: String?, textView: TextView){
            if (currentUsername == username && username != null){
                textView.setBackgroundColor(R.drawable.rounded_message_blue)
            }else{
                textView.setBackgroundResource(R.drawable.rounded_message_yellow)
            }
        }

    }

}