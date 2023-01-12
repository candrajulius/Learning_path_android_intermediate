package com.candra.advanceddatabase.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface StudentDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudent(student: List<Student>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUniversity(university: List<University>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourse(course: List<Course>)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourseStudentCrossRef(courseStudentCrossRef: List<CourseStudentCrossRef>)


    /*
    Anotasi @RawQuery digunakan untuk menandai bahwa fungsi tersebut menggunakan fitur RawQuery. Oleh karena itu, Anda harus menambahkan parameter berupa SupportSQLiteQuery. Selain itu supaya data tersebut bisa di-observe ketika ada perubahan data, gunakanlah properti observedEntities = Student.class.
     */
    @RawQuery(observedEntities = [Student::class])
    fun getAllStudent(query: SupportSQLiteQuery): DataSource.Factory<Int,Student>
    /*
    DataSource adalah sebuah kelas dasar untuk mengatur seberapa banyak data yang diambil ke dalam PagedList. Karena itulah terdapat Int yang dapat menentukan jumlah data yang diambil.
     */


    @Transaction
    @Query("SELECT * from student")
    fun getAllStudentAndUniversity(): LiveData<List<StudentAndUniversity>>

    @Transaction
    @Query("SELECT * from student")
    fun getAllStudentWithCourse(): LiveData<List<StudentWithCourse>>

    /*
    Setelah membuat data class tadi, Anda tinggal menjadikannya sebagai nilai kembalian pada fungsi Dao. Perhatikan pada query, Anda mengambil data dari tabel University yang merupakan tabel utama yang diambil pada class UniversityAndStudent (ditandai dengan @Embedded).
     */

    /*
    Kemudian ada juga annotation tambahan yang belum pernah kita gunakan sebelumnya, yakni @Transaction. Annotation ini dibutuhkan jika Anda menjalankan lebih dari satu query secara bersamaan. Lalu, mengapa kita perlu menambahkan annotation? Padahal jika dilihat hanya ada satu query. Hal tersebut karena ketika menggunakan relasi database, sejatinya Anda melakukan query pada dua tabel, yakni tabel University dan Student secara bersamaan.
     */
    @Transaction
    @Query("Select * from university")
    fun getAllUnviersityAndStudent(): LiveData<List<UniversityAndStudent>>

    @Transaction
    @Query("Select * from student")
    fun getUniversityAndStudentWithCourse(): LiveData<List<StudentUnviersityWithCourse>>

}