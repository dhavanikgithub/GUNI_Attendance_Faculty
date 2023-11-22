package com.example.guniattendancefaculty.authorization.authfragments.ui.launcherscreen

import androidx.lifecycle.ViewModel
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.guniattendancefaculty.faculty.repository.FacultyRepository
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.BasicUtils
import com.example.guniattendancefaculty.utils.Events
import com.example.guniattendancefaculty.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LauncherScreenViewModel @Inject constructor(
    var repository: FacultyRepository
): ViewModel() {

    val mutableLoginStatus = MutableLiveData<Events<Resource<Boolean>>>()

    suspend fun doLogin(
        context: Context,
        recievedMoodleUsername: String,
        recievedmoodlePassword: String
    ):Boolean {
        mutableLoginStatus.postValue(Events(Resource.Loading()))
        val res = try{
            val result = MoodleConfig.setLogin(context, recievedMoodleUsername, recievedmoodlePassword)
            //val misc = repository.getMisc(recievedMoodleUsername, recievedmoodlePassword)
            mutableLoginStatus.postValue(Events(Resource.Success(true)))
            result

        } catch (e: Exception){
            mutableLoginStatus.postValue(Events(Resource.Error(e.toString())))
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e("LauncherScreenViewModel", "doLogin: Error while logging a user: $e", e)
            false
        }
        return res
    }

    fun delOberserveers(){
        mutableLoginStatus.value = null
        mutableLoginStatus.postValue(null)
        mutableLoginStatus.value = null
        mutableLoginStatus.postValue(null)
    }

//
//    suspend fun registrationFaceCheck(
//        context: Context,
//        username: String
//    ): Boolean {
//        val res = try{
//            isStudentRegisterForFace(
//                context = context,
//                enrollmentNo = username
//            ).hasUserUploadImg
//            //does this return res = true?
//
//        } catch (e: Exception){
//            BasicUtils.errorDialogBox(context, "Application Error", "$e")
//            Log.e("registrationFaceCheck", "Error while registration of face of a user: $e", e)
//
//            false
//        }
//        Log.i("TAG", "registrationFaceCheck: result:$res")
//        return res
//    }
//    fun getUrlPluginPhp(moodleUrl:String,url:String):String{
//        if(url.indexOf("pluginfile.php") != -1)
//            return "$moodleUrl/webservice/"+url.substring(url.indexOf("pluginfile.php"))
//        else if(url.indexOf("image.php") != -1)
//            return "$moodleUrl/theme/"+url.substring(url.indexOf("image.php"))
//        else
//            throw java.lang.Exception("Moodle URL: $url has no either pluginfile or image php file.")
//
//    }
//    fun getFileTokenURL(appRepo:AttendanceRepository,url: String): String {
//        var finalurl = getUrlPluginPhp(" http://202.131.126.214",url)
//        finalurl = finalurl.split("?")[0]
//        finalurl += "?token=${appRepo.UPLOAD_FILE_TOKEN}"
//        Log.i("LaunchScreenViewModel", "getFileTokenURL: $finalurl")
//        return finalurl
//    }
//    suspend fun isStudentRegisterForFace(context: Context,enrollmentNo:String): BaseUserInfo {
//        val result = MoodleConfig.getModelRepo(context).getUserInfo(enrollmentNo)
//        val url = getStoredMoodleUrl(context)
//        val attRepo = AttendanceRepository(context,url)
//        Log.i("LaunchScreenViewModel", "isStudentRegisterForFace: ${result.imageUrl}")
//        result.userImage = Utility().convertUrlToBitmap(getFileTokenURL(attRepo,result.imageUrl))
//        val profileImage = Utility().convertBitmapToBase64(result.userImage!!)
//        Log.i("LaunchScreenViewModel", "isStudentRegisterForFace:profileImage: $profileImage")
//        val defaultPic = Utility().convertUrlToBase64(attRepo.getDefaultUserPictureUrl())
//        Log.i("LaunchScreenViewModel", "isStudentRegisterForFace:defaultPic: $defaultPic")
//        val vall = StringSimilarity().similarity(profileImage.trim(), defaultPic.trim())
//        Log.i("LaunchScreenViewModel", "isStudentRegisterForFace:similarity score: $vall")
//        result.hasUserUploadImg =  vall> 0.9
//        Log.i("LaunchScreenViewModel", "isStudentRegisterForFace:result.hasUserUploadImg: ${result.hasUserUploadImg}")
//        return result
//    }
}