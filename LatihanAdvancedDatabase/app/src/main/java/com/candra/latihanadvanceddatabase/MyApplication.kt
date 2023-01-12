package com.candra.latihanadvanceddatabase

import android.app.Application
import com.candra.latihanadvanceddatabase.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { StudentDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { StudentRepository(database.studentDao())}
}