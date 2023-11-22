package com.example.guniattendancefaculty.faculty.facultyfragments.ui.qrcode

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.guniattendancefaculty.data.entity.QRCode
import com.example.guniattendancefaculty.faculty.repository.FacultyRepository
import com.example.guniattendancefaculty.utils.BasicUtils
import com.example.guniattendancefaculty.utils.Events
import com.example.guniattendancefaculty.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(var repository: FacultyRepository): ViewModel() {
    val mutableQrCodeStatus = MutableLiveData<Events<Resource<QRCode?>>>()

    val TAG = "QrCodeViewModel"
    suspend fun getQRData(
        context: Context,
        facultyUserName: String
    )
    {
        Log.i(TAG, "getQRData: FacultyUserName: $facultyUserName")
        mutableQrCodeStatus.postValue(Events(Resource.Loading()))
        try{
            val result = repository.getQRCodes(
                facultyName = facultyUserName
            )
            mutableQrCodeStatus.postValue(Events(result))
        } catch (e:Exception){
            mutableQrCodeStatus.postValue(Events(Resource.Error(e.toString())))
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e("LauncherScreenViewModel", "doLogin: Error while logging a user: $e", e)
        }
    }

    /*suspend fun fillQRDropDown(
        context: Context,
        autoCompletetextView: AutoCompleteTextView,
        facultyList:ArrayList<String>
    )
    {
        val arrayAdapterGroups = ArrayAdapter(
            context,
            R.layout.support_simple_spinner_dropdown_item,
            facultyList
        )
        autoCompletetextView.setAdapter(arrayAdapterGroups)
    }*/
}