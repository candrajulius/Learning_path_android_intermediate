package com.candra.advanceddatabase.database

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

data class UniversityAndStudent(

    /*
    Class UniversityAndStudent merupakan class baru untuk mendefinisikan parent entity dan list child entity. Pada kode di atas, terdapat annotation @Embedded untuk menambahkan data utama yang ingin ditampilkan, dalam hal ini adalah tabel University.
     */

    @Embedded
    val university: University,

    /*
     Field ini diberi annotation @Relation untuk mengetahui bagaimana hubungan antara kedua tabel. Dari parameternya, kita bisa melihat parentColumn berisi universityId yang merupakan Primary Key dari tabel University, sedangkan entityColumn berisi univId yang merupakan Foreign Key dari tabel Student.
     Kedua id sengaja dibuat berbeda namanya supaya Anda mengetahui sumber keyword universityId diambil dari mana
     */

    @Relation(
        parentColumn = "universityId",
        entityColumn = "univId"
    )

    /*
    Karena kode di atas merupakan relasi One to Many, maka field student berbentuk List. Namun, jika kode tersebut merupakan relasi One to One, maka Anda tidak perlu menggunakan List pada field student.
     */

    val student: List<Student>
)


/*
sId merupakan id baru yang mereferensikan studentId, sedangkan cId adalah id baru yang mereferensikan untuk courseId.
 */
@Entity(primaryKeys = ["sId","cId"])
data class CourseStudentCrossRef(
    val sId: Int,
    @ColumnInfo(index = true)
    val cId: Int
)

/*
Jika dilihat, cara ini sama dengan relasi One to Many. Namun, perbedaannya adalah terdapat properti entity untuk menentukan kelas yang digunakan sebagai entity dari parent, dalam hal ini adalah Course, karena kelas utamanya adalah Student.
 */
data class StudentWithCourse(
    @Embedded
    val student: Student,

    @Relation(
        parentColumn = "studentId",
        entity = Course::class,
        entityColumn = "courseId",
        associateBy = Junction(
            value = CourseStudentCrossRef::class,
            parentColumn = "sId",
            entityColumn = "cId"
        )
    )
    val course: List<Course>
)

/*
Jika dilihat, cara ini sama dengan relasi One to Many. Namun, perbedaannya adalah terdapat properti entity untuk menentukan kelas yang digunakan sebagai entity dari parent, dalam hal ini adalah Course, karena kelas utamanya adalah Student.

Kemudian juga terdapat properti associateBy untuk menambahkan referensi hubungan antara kedua kelas, yaitu membuat Junction dengan property value berisi class relasi, yakni CourseStudentCrossRef. Selain itu, properti associateBy juga menentukan manakah parent dan entity pada kelas referensi tersebut. Dalam hal ini, yaitu sId untuk parent dan cId sebagai entity.

Sebagai catatan, apabila id pada masing-masing tabel (Student dan Course) sama dengan id pada tabel relasi (CourseStudentCrossRef), Anda tidak perlu menambahkan property parentColumn dan entityColumn pada Junction.
 */
data class StudentUnviersityWithCourse(
    @Embedded
    val studentAndUniversity: StudentAndUniversity,

    @Relation(
        parentColumn = "studentId",
        entity = Course::class,
        entityColumn = "courseId",
        associateBy = Junction(
            value = CourseStudentCrossRef::class,
            parentColumn = "sId",
            entityColumn = "cId"
        )
    )

    val course: List<Course>
)