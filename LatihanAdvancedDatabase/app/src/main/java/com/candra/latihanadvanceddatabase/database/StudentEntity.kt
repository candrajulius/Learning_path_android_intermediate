package com.candra.latihanadvanceddatabase.database

import androidx.room.*

@Entity
data class Student(
    @PrimaryKey
    val studentId: Int,
    val name: String,
    val univId: Int,
)

@Entity
data class University(
    @PrimaryKey
    val universityId: Int,
    @ColumnInfo(name = "universityName")
    val name: String,
)

@Entity
data class Course(
    @PrimaryKey
    val courseId: Int,
    val name: String,
)

data class StudentAndUniversity(
    @Embedded
    val student: Student,

    @Relation(
        parentColumn = "univId",
        entityColumn = "universityId"
    )
    val university: University? = null
)

// Relasi One To Many
data class UniversityAndStudent(
    @Embedded // => Menambahkan data utama yang ingin ditampilkan, dalam hal ini adalah tabel University
    val university: University, // => Ini adalah data utama yang ingin ditampilkan

    // Relation => Digunakan untuk mengetahui bagaimana hubungan antara kedua tabel.
    @Relation(
        parentColumn = "universityId", // => Mengambil Id dari table University
        entityColumn = "univId" // => Mengambil id dari tabel Student atau bisa dikatakan foreigh key
    )
    val student: List<Student>
)

@Entity(primaryKeys = ["sId","cId"])
data class CourseStudentCrossRef(
    val sId: Int,
    @ColumnInfo(index = true)
    val cId: Int
)

/*
sId merupakan id baru yang mereferensikan studentId, sedangkan cId adalah id baru yang mereferensikan untuk courseId.

 Sebaliknya, apabila data utamanya adalah Student, relasinya akan dibalik seperti berikut:
 */
data class StudentWithCourse(
    @Embedded
    val student: Student,

    /*
    Kemudian juga terdapat properti associateBy untuk menambahkan referensi hubungan antara kedua kelas, yaitu membuat Junction dengan property value berisi class relasi, yakni CourseStudentCrossRef. Selain itu, properti associateBy juga menentukan manakah parent dan entity pada kelas referensi tersebut. Dalam hal ini, yaitu sId untuk parent dan cId sebagai entity.
     */

    /*
    Sebagai catatan, apabila id pada masing-masing tabel (Student dan Course) sama dengan id pada tabel relasi (CourseStudentCrossRef), Anda tidak perlu menambahkan property parentColumn dan entityColumn pada Junction.
     */
    @Relation(
        parentColumn = "studentId",
        entity = Course::class,
        entityColumn = "courseId",
        associateBy = Junction( // => dengan property value berisi class relasi, yakni CourseStudentCrossRef.
            value = CourseStudentCrossRef::class,
            parentColumn = "sId",
            entityColumn = "cId"
        )
    )
    val course: List<Course>
)

// Kesimpulan
/*
Jika dilihat, cara ini sama dengan relasi One to Many. Namun, perbedaannya adalah terdapat properti entity untuk menentukan kelas yang digunakan sebagai entity dari parent, dalam hal ini adalah Course, karena kelas utamanya adalah Student.

 */