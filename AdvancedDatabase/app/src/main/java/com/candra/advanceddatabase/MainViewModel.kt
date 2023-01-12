package com.candra.advanceddatabase

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.candra.advanceddatabase.database.*
import com.candra.advanceddatabase.helper.SortType

class MainViewModel(private val studentRepository: StudentRepository) : ViewModel() {

    private val _sort = MutableLiveData<SortType>()

    init {
//        insertAllData()
        _sort.value = SortType.ASCENDING
    }


    fun changeType(sortType: SortType) {
        _sort.value = sortType
    }

    fun getAllStudent(): LiveData<PagedList<Student>> = _sort.switchMap {
        studentRepository.getAllStudent(it)
    }


    fun getAllStudentAndUniversity(): LiveData<List<StudentAndUniversity>> = studentRepository.getAllStudentAndUniversity()
    fun getAllStudentWithCourse(): LiveData<List<StudentWithCourse>> = studentRepository.getAllStudentWithCourse()
    fun getAllUniverisityAndStudent(): LiveData<List<UniversityAndStudent>> = studentRepository.geAllUniversityAndStudent()
    fun getAllUniversityWithCourse(): LiveData<List<StudentUnviersityWithCourse>> = studentRepository.getAllUniversityWithCourse()

//    private fun insertAllData() = viewModelScope.launch {
//        studentRepository.insertAllData()
//    }


}

class ViewModelFactory(private val repository: StudentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}