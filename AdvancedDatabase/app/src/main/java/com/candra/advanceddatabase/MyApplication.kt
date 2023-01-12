package com.candra.advanceddatabase

import android.app.Application
import com.candra.advanceddatabase.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { StudentDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { StudentRepository(database.studentDao())}
}