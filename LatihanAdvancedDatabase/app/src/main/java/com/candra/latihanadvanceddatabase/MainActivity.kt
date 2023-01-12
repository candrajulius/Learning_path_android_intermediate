package com.candra.latihanadvanceddatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.latihanadvanceddatabase.adapter.StudentAndUniversityAdapter
import com.candra.latihanadvanceddatabase.adapter.StudentListAdapter
import com.candra.latihanadvanceddatabase.adapter.StudentWithCourseAdapter
import com.candra.latihanadvanceddatabase.adapter.UniversityAndStudentAdapter
import com.candra.latihanadvanceddatabase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as MyApplication).repository)
    }

    private val mainAdapter by lazy { StudentListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStudent.layoutManager = LinearLayoutManager(this)

        getStudent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_single_table -> {
                getStudent()
                return true
            }
            R.id.action_many_to_one -> {
                getStudentAndUniversity()
                true
            }
            R.id.action_one_to_many -> {
                getUniversityAndStudent()
                true
            }

            R.id.action_many_to_many -> {
                getStudentWithCourse()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getStudent() {
        binding.rvStudent.adapter = mainAdapter
        mainViewModel.getAllStudent().observe(this) {
            mainAdapter.submitList(it)
            Log.d(TAG, "getStudent: $it")
        }
    }

    private fun getStudentAndUniversity() {
        val studentAdapter = StudentAndUniversityAdapter()
        val layoutManager1 = LinearLayoutManager(this)
        binding.rvStudent.apply {
            adapter = studentAdapter
            layoutManager = layoutManager1
        }

        mainViewModel.getAllStudentAndUniversity().observe(this){
            studentAdapter.submitList(it)
            Log.d(TAG, "getStudentAndUniversity: $it")
        }
    }

    companion object{
        private const val TAG = "MainActivity"
    }

    private fun getUniversityAndStudent() {
        val mainAdapterUniversityAndStudent = UniversityAndStudentAdapter()
        binding.apply {
            rvStudent.adapter = mainAdapterUniversityAndStudent
        }

        mainViewModel.getAllUniversityAndStudent().observe(this){
            mainAdapterUniversityAndStudent.submitList(it)
            Log.d(TAG, "getUniversityAndStudent: $it")
        }
    }


    private fun getStudentWithCourse() {
        val mainAdapterStudentWithCourse = StudentWithCourseAdapter()
        
        binding.rvStudent.adapter = mainAdapterStudentWithCourse
        
        mainViewModel.getAllStudentWithCourse().observe(this){
            mainAdapterStudentWithCourse.submitList(it)
            Log.d(TAG, "getStudentWithCourse: $it")
        }
    }

}