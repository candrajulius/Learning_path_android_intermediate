package com.candra.advanceddatabase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.candra.advanceddatabase.database.Student
import com.candra.advanceddatabase.databinding.ItemStudentBinding


/*
ListAdapter
Setelah mendapatkan data menggunakan Dao, langkah selanjutnya yaitu menampilkan data pada RecyclerView seperti biasanya. Perbedaannya, di sini Anda menggunakan ListAdapter yang merupakan ekstensi dari RecyclerView.Adapter. Dengan ListAdapter, Anda dapat memeriksa perubahan data menggunakan DiffUtil.

Apabila Anda menggunakan RecyclerView.Adapter biasa, semua item akan diperbarui ketika ada perubahan data. Dengan ListAdapter, hanya item yang datanya berubah saja yang akan diperbarui sehingga menjadi lebih efisien.

Berikut ilustrasi perbedaan antara RecyclerView.Adapter dan ListAdapter.
 */

class StudentListAdapter :
/*
Penggunaan PagedListAdapter hampir mirip dengan ListAdapter. Bedanya yaitu adapter ini dikhususkan untuk menerima PagedList.
 */
    PagedListAdapter<Student, StudentListAdapter.WordViewHolder>(WordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val student = getItem(position) as Student
        holder.bind(student)
    }

    class WordViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Student) {
            binding.tvItemName.text = data.name
        }
    }

    class WordsComparator : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.name == newItem.name
        }
    }
}