package com.candra.advanceddatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.candra.advanceddatabase.adapter.*
import com.candra.advanceddatabase.databinding.ActivityMainBinding
import com.candra.advanceddatabase.helper.SortType

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory((application as MyApplication).repository)
    }

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
            
            R.id.action_all_to_many -> {
                getUniversityAndStudentWithCourse()
                return true
            }

            R.id.action_sort -> {
                showSortingPopupMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortingPopupMenu(isShow: Boolean) {
       val view = findViewById<View>(R.id.action_sort) ?: return
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun getUniversityAndStudentWithCourse(){
        val mainAdapter = StudentUniversityAndCourseAdapter()
        binding.rvStudent.adapter = mainAdapter
        mainViewModel.getAllUniversityWithCourse().observe(this){
            mainAdapter.submitList(it)
            Log.d(TAG, "getUniversityAndStudentWithCourse: $it")
        }
    }

    /*
    Untuk menampilkan Popup Menu pada saat icon sort diklik, Anda menggunakan object PopupMenu dan menempelkannya pada option menu.

     Kemudian kode setOnMenuItemClickListener digunakan untuk memberikan aksi ketika Popup Menu dipilih. Di sini Anda menentukan variabel sort pada ViewModel berdasarkan menu yang dipilih menggunakan fungsi changeSortType. Karena terdapat observer, data akan secara otomatis diperbarui ketika opsi berubah.
     */
        private fun showSortingPopupMenu(){
        val view = findViewById<View>(R.id.action_sort)?: return
        PopupMenu(this,view).run {
            menuInflater.inflate(R.menu.sorting_menu,menu)

            setOnMenuItemClickListener {
                mainViewModel.changeType(
                    when(it.itemId){
                        R.id.action_ascending -> SortType.ASCENDING
                        R.id.action_descending -> SortType.DESCENDING
                        else -> SortType.RANDOM
                    }
                )
                true
            }
            show()
        }
    }

    private fun getStudent() {
        val adapter = StudentListAdapter()
        binding.rvStudent.adapter = adapter
        mainViewModel.getAllStudent().observe(this) {
            adapter.submitList(it)
            Log.d(TAG, "getStudent: $it")
        }
        showSortingPopupMenu(true)
    }

    private fun getStudentAndUniversity() {
        val mainAdapter = StudentAndUniversityAdapter()
        binding.rvStudent.adapter = mainAdapter

        mainViewModel.getAllStudentAndUniversity().observe(this){
            mainAdapter.submitList(it)
            Log.d(TAG, "getStudentAndUniversity: $it")
        }
        showSortingPopupMenu(false)
    }

    private fun getUniversityAndStudent() {
        val mainAdapter = UniversityAndStudentAdapter()
        binding.rvStudent.adapter = mainAdapter

        mainViewModel.getAllUniverisityAndStudent().observe(this){
            mainAdapter.submitList(it)
            Log.d(TAG, "getUniversityAndStudent: $it")
        }
        showSortingPopupMenu(false)
    }


    private fun getStudentWithCourse() {
        val mainAdapter =  StudentWithCourseAdapter()
        binding.rvStudent.adapter = mainAdapter
        mainViewModel.getAllStudentWithCourse().observe(this){
            Log.d(TAG, "getStudentWithCourse: $it")
            mainAdapter.submitList(it)
        }
        showSortingPopupMenu(false)
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}