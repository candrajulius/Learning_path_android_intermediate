package com.candra.latihanadvanceddatabase.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.candra.latihanadvanceddatabase.helper.InitialDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
Kemudian setelah mendefinisikan spesifikasi, Anda menambahkan properti autoMigration pada annotation @Database seperti berikut:
 */

@Database(entities = [Student::class, University::class, Course::class,CourseStudentCrossRef::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 2, to = 3, spec = StudentDatabase.MyAutoMigration::class),
    ],
    exportSchema = true)
abstract class StudentDatabase() : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    /*
    AutoMigration
    Perhatikan kode untuk membuat spesifikasi migrasi pada StudentDatabase berikut:


    Anda dapat membuat spesifikasi dengan membuat class yang implement AutoMigrationSpec.
    Kemudian beri annotation untuk jenis perubahan yang terjadi, misalnya di sini adalah @RenameColumn untuk mengubah nama kolom.
    Kemudian isi tableName dengan tabel apa yang ingin diubah, fromColumnName dengan nama kolom yang lama, dan toColumnName dengan nama kolom yang baru

    Kemudian setelah mendefinisikan spesifikasi, Anda menambahkan properti autoMigration pada annotation @Database seperti berikut

    Pada properti ini, Anda menentukan versi awal pada property from dan versi selanjutnya pada property to, kemudian ada juga property spec untuk memasukkan spesifikasi yang sudah dibuat sebelumnya. Anda bisa saja tidak mendefinisikan AutoMigrationSpec jika perubahannya hanyalah penambahan tabel dan kolom

    Hal yang perlu diperhatikan juga adalah isi dari property berupa autoMigration, artinya Anda juga dapat menambahkan spesifikasi pada peningkatan versi yang lain, misal dari 2 ke 3.
     */
    @RenameColumn(tableName = "University",fromColumnName = "name",toColumnName = "universityName")
    class MyAutoMigration: AutoMigrationSpec

    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context,applicationScope: CoroutineScope): StudentDatabase {
            if (INSTANCE == null) {
                synchronized(StudentDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        StudentDatabase::class.java, "student_database")
                        .fallbackToDestructiveMigration()
//                        .createFromAsset("student_database.db")
                        .addCallback(object: Callback(){
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                INSTANCE?.let { database ->
                                    applicationScope.launch {
                                        val studentDao = database.studentDao()
                                        studentDao.insertStudent(InitialDataSource.getStudents())
                                        studentDao.insertUniversity(InitialDataSource.getUniversities())
                                        studentDao.insertCourse(InitialDataSource.getCourses())
                                        studentDao.insertCourseStudentCrossRef(InitialDataSource.getCourseStudentRelation())
                                    }
                                }
                            }
                        })
                        .build()
                }
            }
            return INSTANCE as StudentDatabase
        }

    }

    // Kesimpulan
    /*
    Dengan memanfaatkan fungsi addCallback ketika bisa menambahkan aksi tambahan ketika database selesai dibuat pada fungsi onCreate.
    Ingat bahwa untuk melakukan suatu aksi ke database, kita perlu menjalankannya di background thread.
    Nah, di sini kita menggunakan applicationScope untuk menjalankan suspend function.
    Apabila tidak menggunakan Coroutine, Anda juga bisa menggunakan Executor untuk mengeksekusi aksi ini.


    Mantap! Anda telah mempelajari cara menggunakan addCallback pada database. Selain membuat kelas untuk inisialisasi data awal,
    Anda juga dapat mengambil data dari file json yang disimpan di lokal.
     Anda hanya perlu melakukan konversi data JSON tersebut menjadi class terlebih dahulu sebelum dimasukkan ke database.
     Cara ini merupakan hal yang sering muncul di ujian sertifikasi Associate Android Developer (AAD).
    Jadi, perhatikan baik-baik, ya!
     */

    /*
    Di sini kita menentukan lokasi export skema pada property arguments. Caranya yaitu dengan memasukkan key room.schemaLocation yang berisi value berupa nama project diikuti dengan direktori schemas.
     */

}


