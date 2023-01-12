package com.candra.latihanadvanceddatabase.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudent(student: List<Student>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUniversity(university: List<University>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourse(course: List<Course>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourseStudentCrossRef(courseStudentCrossRef: List<CourseStudentCrossRef>)

    @Query("SELECT * from student")
    fun getAllStudent(): LiveData<List<Student>>

    @Transaction
    @Query("SELECT * from student")
    fun getAllStudentAndUniversitiy(): LiveData<List<StudentAndUniversity>>

    @Transaction
    @Query("SELECT * from university")
    fun getAllUniversityAndStudent(): LiveData<List<UniversityAndStudent>>

    @Transaction // => Anotasi ini dijalankan apabila kita menggunakan lebih dari 1 query secara bersamaan
    @Query("SELECT * from student")
    fun getAllStudentWithCourse(): LiveData<List<StudentWithCourse>>
}

// Kesimpulan
/*
Kemudian ada juga annotation tambahan yang belum pernah kita gunakan sebelumnya, yakni @Transaction. Annotation ini dibutuhkan jika Anda menjalankan lebih dari satu query secara bersamaan. Lalu, mengapa kita perlu menambahkan annotation? Padahal jika dilihat hanya ada satu query. Hal tersebut karena ketika menggunakan relasi database, sejatinya Anda melakukan query pada dua tabel, yakni tabel University dan Student secara bersamaan.
 */