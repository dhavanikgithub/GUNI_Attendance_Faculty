package com.example.guniattendancefaculty.authorization.authfragments.ui.registerfaculty

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.guniattendancefaculty.authorization.repository.AuthRepository
import com.example.guniattendancefaculty.data.entity.Faculty
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacultyRegisterViewModel @Inject constructor(
    private val context: Context,
    private val repository: AuthRepository
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Events<Resource<Faculty>>>()
    val registerStatus: LiveData<Events<Resource<Faculty>>> = _registerStatus

    private val _curImageUri = MutableLiveData<Uri>()
    val curImageUri: LiveData<Uri> = _curImageUri

    fun setCurrentImageUri(uri: Uri) {
        _curImageUri.postValue(uri)
    }


    suspend fun uploadUserPic(
        context: Context,
        username: String,
        curImageUri: Uri
    ): Boolean {
        val res = try{
            MoodleConfig.getModelRepo(context).uploadStudentPicture(
                context,
                userid = username,
                curImageUri = curImageUri,
            )
            true

        } catch (e: Exception){
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e("uploadUserPic", "Error while uploading a picture to moodle of a user: $e", e)
            false
        }
        return res
    }


    suspend fun getStudentInfo(
        context: Context,
        enrol: String,
        nameText: TextView,
        emailText: TextView
    ){
        try{
            val res = MoodleConfig.getModelRepo(context).getUserInfo(enrol)
            nameText.text = res.lastname
            nameText.isEnabled = false

            emailText.text = res.emailAddress
            emailText.isEnabled = false
        } catch (e: Exception){
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e("getStudentInfo", "Error while getting student information of a user: $e", e)
        }

    }

    fun register(
        name: String,
        email: String,
        phone: String,
        pin: String
    ) {

        val error = if (email.isEmpty()) {
            "emptyEmail"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "email"
        } else if (phone.isEmpty()) {
            "emptyPhone"
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            "phone"
        } else if (name.isEmpty()) {
            "name"
        } else if (pin.length != 6) {
            "pin"
        } else if (curImageUri.value == Uri.EMPTY || curImageUri.value == null) {
            "uri"
        } else null

        error?.let {
            _registerStatus.postValue(Events(Resource.Error(error)))
            return
        }

        _registerStatus.postValue(Events(Resource.Loading()))

        val bitmap = BitmapUtils.getBitmapFromUri(context.contentResolver, curImageUri.value!!)

        MainScope().launch(Dispatchers.Main) {
            val result = repository.registerFaculty(
                name = name,
                email = email,
                phone = phone,
                pin = pin,
                bitmap = bitmap,
                profilePicUri = curImageUri.value!!
            )
            _registerStatus.postValue(Events(result))
        }

    }

}