package com.example.guniattendancefaculty.faculty.facultyfragments.ui.studentlist

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guniattendancefaculty.adapter.SessionUserInfo
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.Events
import com.example.guniattendancefaculty.utils.Resource
import com.guni.uvpce.moodleapplibrary.model.MoodleSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(

) : ViewModel() {

    val TAG = "StudentListViewModel"
    private val _list = MutableLiveData<Events<Resource<List<SessionUserInfo>>>>()
    val list: LiveData<Events<Resource<List<SessionUserInfo>>>> = _list

    fun getStudentsList(context: Context, session: MoodleSession) {
        _list.postValue(Events(Resource.Loading()))
        viewModelScope.launch {
            try {
                val result =
                    MoodleConfig.getModelRepo(context).getStudentList(session.course, session.group)
                val listStudent = ArrayList<SessionUserInfo>()
                for(i in result){
                    listStudent.add(SessionUserInfo(i,session))
                }
                _list.postValue(Events(Resource.Success(listStudent)))
            }catch (e:Exception){
                Log.e(TAG, "getStudentsListFilterClass: Error:$e", e)
                _list.postValue(Events(Resource.Error("getStudentsListFilterClass: Error:$e")))
            }
        }
    }
}