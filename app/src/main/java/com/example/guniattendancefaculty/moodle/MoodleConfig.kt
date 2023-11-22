package com.example.guniattendancefaculty.moodle

import android.content.Context
import androidx.preference.PreferenceManager
import com.guni.uvpce.moodleapplibrary.model.MoodleBasicUrl
import com.guni.uvpce.moodleapplibrary.repo.ModelRepository

class MoodleConfig {
    companion object {
        suspend fun getModelRepo(context: Context): ModelRepository {
            return ModelRepository.getModelRepo(context)
        }

        suspend fun getMoodleUrlList(context: Context): List<MoodleBasicUrl> {
            return ModelRepository.getMoodleUrlList(context)
        }
        fun getAuthUserName(context:Context):String{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getString("LoggedInUserName","")!!
        }
        suspend fun setLogin(context:Context,userName:String,password:String):Boolean{
            val result = getModelRepo(context).login(
                recievedMoodleUsername = userName,
                recievedmoodlePassword = password
            )
            if(result)
            {
                val preference = PreferenceManager.getDefaultSharedPreferences(context)
                preference.edit().putString("LoggedInUserName",userName).apply()
                return true
            }
            return false
        }
        fun logout(context: Context){
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            preference.edit().putString("LoggedInUserName","").apply()
        }
    }
}