package com.example.guniattendancefaculty.faculty.facultyfragments.ui.addeditclass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guniattendancefaculty.data.entity.Classes
import com.example.guniattendancefaculty.faculty.repository.FacultyRepository
import com.example.guniattendancefaculty.utils.Events
import com.example.guniattendancefaculty.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditClassViewModel @Inject constructor(
    private val repository: FacultyRepository
) : ViewModel() {

    private val _list = MutableLiveData<Events<Resource<List<Classes>>>>()
    val list: LiveData<Events<Resource<List<Classes>>>> = _list


}