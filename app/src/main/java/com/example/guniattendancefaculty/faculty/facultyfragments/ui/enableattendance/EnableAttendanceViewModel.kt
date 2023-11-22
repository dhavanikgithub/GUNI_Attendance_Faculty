package com.example.guniattendancefaculty.faculty.facultyfragments.ui.enableattendance

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.guniattendancefaculty.faculty.repository.FacultyRepository
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.*
import com.guni.uvpce.moodleapplibrary.model.BaseUserInfo
import com.guni.uvpce.moodleapplibrary.model.MoodleCourse
import com.guni.uvpce.moodleapplibrary.model.MoodleGroup
import com.guni.uvpce.moodleapplibrary.model.MoodleSession
import com.guni.uvpce.moodleapplibrary.repo.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class EnableAttendanceViewModel @Inject constructor(
    var repository: FacultyRepository
): ViewModel() {

    private val TAG = "EnableAttendanceViewModel"
    val mutableData =  MutableLiveData<Events<Resource<Boolean>>>()
    val mutableArrayAdapter =  MutableLiveData<Events<Resource<EnableAttendanceFragment.AttParamMode>>>()

    private var selectedTime: String = ""
    private lateinit var repo: ModelRepository
    suspend fun getRepo(context:Context):ModelRepository{
        if(!this::repo.isInitialized)
            repo = MoodleConfig.getModelRepo(context)
        return repo
    }
    suspend fun getStudentInfo(
        context: Context,
        enrol: String
    ): BaseUserInfo?{
        mutableData.postValue(Events(Resource.Loading()))
        try{
            val res = getRepo(context).getUserInfo(
                userName = enrol
            )
            mutableData.postValue(Events(Resource.Success(true)))
            return res
        }catch (e:Exception){
            mutableData.postValue(Events(Resource.Error(e.toString())))
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e("getStudentInfor", "Error while getting student information of a user: $e", e)
        }
        return null
    }
    suspend fun storeFacData(
        context: Context,
        userID: Int,
        facultyName: String,
        encodedData: String,
    ) {
        mutableData.postValue(Events(Resource.Loading()))
        try {
            repository.storeFacDataToFirebase(
                userID = userID,
                facultyName = facultyName,
                encodedData = encodedData
            )
            mutableData.postValue(Events(Resource.Success(true)))
        } catch (e: Exception){
            mutableData.postValue(Events(Resource.Error(e.toString())))
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e(TAG, "storeFacData: Error while storing faculty data information of the faculty: $e", e)
        }
    }



    suspend fun generalFillDropDown(context: Context, mode: EnableAttendanceFragment.AttParamMode,
                                    callback:suspend ()->ArrayList<String>){
        mutableArrayAdapter.postValue(Events(Resource.Loading()))
        try{
            val arrayData = callback()
            mutableArrayAdapter.postValue(Events(Resource.Success(mode)))

        }catch (e: Exception){
            mutableArrayAdapter.postValue(Events(Resource.Error(e.toString())))
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e(TAG, "generalFillDopDown: Error while getting list and filling it in dropdown: $e", e)
        }
    }

    suspend fun fillFacultyDropDown(
        context: Context,mode: EnableAttendanceFragment.AttParamMode,
        facultiesArrayData: ArrayList<String>,
        facultiesAllArrayData: ArrayList<BaseUserInfo>,
    ){
        generalFillDropDown(context,mode,
            callback = {
                val res = getRepo(context).getFacultyListByCohort()
                facultiesArrayData.clear()
                facultiesAllArrayData.clear()
                facultiesAllArrayData.addAll(res)
                for (i in res) {
                    facultiesArrayData.add(i.firstname + " " + i.lastname)
                }
                return@generalFillDropDown facultiesArrayData
            })
    }
    suspend fun getCoursesByUser(
        context: Context, mode: EnableAttendanceFragment.AttParamMode,
        username: String,
        coursesArrayData: ArrayList<String>,
        coursesObjArrayData: ArrayList<MoodleCourse>,
    ){
        generalFillDropDown(context,mode, callback = {
            val result = getRepo(context).getCourseListEnrolledByUser(
                userName = username
            )
            coursesObjArrayData.clear()
            coursesArrayData.clear()
            coursesObjArrayData.addAll(result)
            for (i in result) {
                coursesArrayData.add(i.Name)
            }
            return@generalFillDropDown coursesArrayData
        })
        /*mutableData.postValue(Events(Resource.Loading()))
        try {
            val result = getRepo(context).getCourseListEnrolledByUser(
                userName = username
            )
            for (i in result) {
                val coursename = i.Name
                coursesArrayData.add(coursename)
                coursesObjArrayData.add(i)
            }

            val arrayAdapterCourses = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                coursesArrayData
            )
            autoCompletetextView.setAdapter(arrayAdapterCourses)
            mutableData.postValue(Events(Resource.Success(true)))

        }catch (e: Exception){
            mutableData.postValue(Events(Resource.Error(e.toString())))
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e(TAG, "getCoursesByUser: Error while getting courses enrolled by user: $e", e)
        }*/
    }
    //val mutableAtt = MutableLiveData<Events<Resource<MoodleAttendance>>>()
    suspend fun fillLectureLabsDropDown(context:Context,mode: EnableAttendanceFragment.AttParamMode,
                                        moodleCourse: MoodleCourse,
                                        lecturesArrayData: ArrayList<String>,
                                        lecturesObjArrayData: ArrayList<MoodleGroup>){
        generalFillDropDown(context,mode, callback = {
            //Find Course List to add in Courses Array Adapter
            val groupResult = getRepo(context).getGroupList(course = moodleCourse,)
            lecturesObjArrayData.clear()
            lecturesArrayData.clear()
            lecturesObjArrayData.addAll(groupResult)
            for(i in groupResult){
                lecturesArrayData.add(i.groupName)
            }
            Log.i(TAG, "fillLectureLabsDropDown: callback:${lecturesArrayData.joinToString("\n")}")
            return@generalFillDropDown lecturesArrayData
        })
        /*val attendanceResult =
            getRepo(context).getAttendance(
                course = moodleCourse,
            )
        attendanceObjArrayData.clear()
        attendanceObjArrayData.add(attendanceResult)*/
    }
    suspend fun fillSessionsDropDown(
        context: Context, mode: EnableAttendanceFragment.AttParamMode,
        moodleCourse: MoodleCourse,
        moodleGroup: MoodleGroup,
        sessionsArrayData: ArrayList<String>,
        sessionsObjArrayData: ArrayList<MoodleSession>,
    ){
        generalFillDropDown(context,mode, callback = {
            val repo = getRepo(context)
            val moodleattendance = repo.getAttendance(moodleCourse)
            val sessionResult = repo.getSessionList(course = moodleCourse,
                attendance = moodleattendance, group = moodleGroup)
            Log.i("Session List", "$sessionResult")
            sessionsArrayData.clear()
            sessionsObjArrayData.clear()
//            sessionsObjArrayData.addAll(sessionResult)
            val sdf = SimpleDateFormat("dd")
            val currentDate = sdf.format(Date())
            for(i in sessionResult){
                val sessionList = getSessionStringName(i)
                val sessionDate = sessionList[0]+""+sessionList[1]
                if((sessionDate == currentDate) || (sessionDate == (currentDate.toInt() - 1).toString())){
                    sessionsArrayData.add(sessionList)
                    sessionsObjArrayData.add(i)
                }
            }
            return@generalFillDropDown sessionsArrayData
        })
    }
    suspend fun getSessionStringName(i:MoodleSession):String{
        return BasicUtils.getDate(i.sessionStartDate, "dd/MM/yyyy hh:mm a") + " - " + BasicUtils.getDate(i.sessionEndDate, "hh:mm a") + " -> " + i.description
    }
    suspend fun createNewSession(
        context: Context,
        moodleGroup: MoodleGroup,
        startTime: Long,
        duration: Long,
        createdby_userid: String,
        description: String,
        sessionsArrayData: ArrayList<String>,
        sessionsObjArrayData: ArrayList<MoodleSession>
    )
    {
        mutableArrayAdapter.postValue(Events(Resource.Loading()))
        try{

            val moodleattendance = getRepo(context).getAttendance(moodleGroup.course)
            val result =
                repo.createSession(
                    group = moodleGroup,
                    attendance = moodleattendance,
                    sessionStartTimeInSeconds = startTime,
                    sessionDuration = duration,
                    created_by_user_id = createdby_userid,
                    description = description
                )
            sessionsArrayData.add(getSessionStringName(result))
            sessionsObjArrayData.add(result)

            mutableArrayAdapter.postValue(Events(Resource.Success(EnableAttendanceFragment.AttParamMode.Session)))
        } catch (e:Exception){
            mutableArrayAdapter.postValue(Events(Resource.Error(e.toString())))
            BasicUtils.errorDialogBox(context, "Application Error", "$e")
            Log.e(TAG, "createNewSession: Error while creating new session and filling it in dropdown: $e", e)
        }

    }

    fun removeObservers() {
        mutableData.value = null
        mutableData.postValue(null)
        mutableArrayAdapter.value = null
        mutableArrayAdapter.postValue(null)
    }
}