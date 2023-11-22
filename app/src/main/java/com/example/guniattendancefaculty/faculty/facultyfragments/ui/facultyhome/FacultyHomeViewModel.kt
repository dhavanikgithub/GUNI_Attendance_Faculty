package com.example.guniattendancefaculty.faculty.facultyfragments.ui.facultyhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guniattendancefaculty.data.entity.Faculty
import com.example.guniattendancefaculty.data.entity.FacultyData
import com.example.guniattendancefaculty.faculty.repository.FacultyRepository
import com.example.guniattendancefaculty.utils.Events
import com.example.guniattendancefaculty.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class FacultyHomeViewModel @Inject constructor(
    private val repository: FacultyRepository
) : ViewModel() {

    private val _userStatus = MutableLiveData<Events<Resource<Faculty>>>()
    val userStatus: LiveData<Events<Resource<Faculty>>> = _userStatus

    fun getUser() {
        _userStatus.postValue(Events(Resource.Loading()))
    }


    private val _addFacultyStatus = MutableLiveData<Events<Resource<FacultyData>>>()
    val addFacultyStatus: LiveData<Events<Resource<FacultyData>>> = _addFacultyStatus

    private val facultyData = FirebaseFirestore.getInstance().collection("FacultyData")



    fun storeFacData(
        userID: Int,
        facultyName: String,
        encodedData: String,
    ) {
        viewModelScope.launch(Dispatchers.Main) {

            val facultyN = facultyData.whereEqualTo("facultyName", facultyName).get().await()
                .toObjects(FacultyData::class.java)


        }
    }

}