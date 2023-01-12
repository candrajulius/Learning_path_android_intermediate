package com.candra.advanceddatabase

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.candra.advanceddatabase.database.*
import com.candra.advanceddatabase.helper.InitialDataSource
import com.candra.advanceddatabase.helper.SoftUtils
import com.candra.advanceddatabase.helper.SortType

@Suppress("DEPRECATION")
class StudentRepository(private val studentDao: StudentDao) {


    /*
    LivePagedListBuilder digunakan untuk merubah DataSource menjadi PagedList dalam bentuk LiveData. PagedList adalah list khusus yang digunakan untuk menyimpan data tiap halaman.
     */
    fun getAllStudent(sortType: SortType): LiveData<PagedList<Student>> {
        val query = SoftUtils.getSortedQuery(sortType)

        val student = studentDao.getAllStudent(query)


        /*
        Di sini kita juga dapat membuat konfigurasi data yang diambil dengan menggunakan PagedList.Config, seperti:

        pageSize : Mengatur jumlah data yang diambil per halamannya.
        initialLoadSize : Mengatur jumlah data yang diambil pertama kali. Default-nya adalah tiga kali pageSize.
        prefetchDistance : Menentukan jarak sisa item untuk mengambil data kembali. Default-nya sama dengan pageSize.
        enablePlaceholder : Menentukan apakah menggunakan placeholder atau tidak.
        maxSize : Menentukan jumlah maksimum item yang dapat dimuat di PagedList. Default-nya adalah Int.MAX_VALUE.
        jumpThreshold : Menentukan batas jumlah item yang sedang dimuat. Default-nya adalah Int.MIN_VALUE.
         */
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(30)
            .setPageSize(18)
            .build()

        return LivePagedListBuilder(student,config).build()
    }

    fun getAllStudentAndUniversity(): LiveData<List<StudentAndUniversity>> = studentDao.getAllStudentAndUniversity()
    fun getAllStudentWithCourse(): LiveData<List<StudentWithCourse>> = studentDao.getAllStudentWithCourse()
    fun geAllUniversityAndStudent(): LiveData<List<UniversityAndStudent>> = studentDao.getAllUnviersityAndStudent()
    fun getAllUniversityWithCourse(): LiveData<List<StudentUnviersityWithCourse>> = studentDao.getUniversityAndStudentWithCourse()



//    suspend fun insertAllData() {
//        studentDao.insertStudent(InitialDataSource.getStudents())
//        studentDao.insertUniversity(InitialDataSource.getUniversities())
//        studentDao.insertCourse(InitialDataSource.getCourses())
//        studentDao.insertCourseStudentCrossRef(InitialDataSource.getCourseStudentRelation())
//    }
}