package com.candra.advanceddatabase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.candra.advanceddatabase.database.StudentUnviersityWithCourse
import com.candra.advanceddatabase.databinding.ItemStudentBinding

class StudentUniversityAndCourseAdapter :
    ListAdapter<StudentUnviersityWithCourse, StudentUniversityAndCourseAdapter.WordViewHolder>(
        WordsComparator()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WordViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StudentUnviersityWithCourse) {
            binding.tvItemName.text = data.studentAndUniversity.student.name
            val arrayCourse = arrayListOf<String>()
            data.course.forEach {
                arrayCourse.add(it.name)
            }
            binding.tvItemCourse.text = arrayCourse.joinToString(separator = ", ")
            binding.tvItemCourse.visibility = View.VISIBLE
        }
    }

    class WordsComparator : DiffUtil.ItemCallback<StudentUnviersityWithCourse>() {
        override fun areItemsTheSame(oldItem: StudentUnviersityWithCourse, newItem: StudentUnviersityWithCourse): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: StudentUnviersityWithCourse, newItem: StudentUnviersityWithCourse): Boolean {
            return oldItem.studentAndUniversity.student.name == newItem.studentAndUniversity.student.name
        }
    }
}