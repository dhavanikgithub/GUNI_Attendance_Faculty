package com.example.guniattendancefaculty.faculty.facultyfragments.ui.editstudent

import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guniattendancefaculty.data.entity.Student
import com.example.guniattendancefaculty.faculty.repository.FacultyRepository
import com.example.guniattendancefaculty.utils.BitmapUtils
import com.example.guniattendancefaculty.utils.Events
import com.example.guniattendancefaculty.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EditStudentViewModel @Inject constructor(
    private val context: Context,
    private val repository: FacultyRepository
) : ViewModel() {

    private val _userStatus = MutableLiveData<Events<Resource<Student>>>()
    val userStatus: LiveData<Events<Resource<Student>>> = _userStatus

    fun getStudent(uid: String) {

    }

    private val _curImageUri = MutableLiveData<Uri>()
    val curImageUri: LiveData<Uri> = _curImageUri

    fun setCurrentImageUri(uri: Uri) {
        _curImageUri.postValue(uri)
    }

    private val students = FirebaseFirestore.getInstance().collection("student")

    private val _updateStudentStatus = MutableLiveData<Events<Resource<Any>>>()
    val updateStudentStatus: LiveData<Events<Resource<Any>>> = _updateStudentStatus

    fun updateStudent(
        uid: String,
        enrolment: String,
        name: String,
        phone: String,
        branch: String,
        sem: Int,
        lec: String,
        lab: String
    ) {


    }

    fun removeObservers() {
        _userStatus.value = null
        _userStatus.postValue(null)
        _updateStudentStatus.value = null
        _updateStudentStatus.postValue(null)
        _curImageUri.value = Uri.EMPTY
        _curImageUri.postValue(Uri.EMPTY)
    }

}