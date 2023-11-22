package com.example.guniattendancefaculty.faculty.facultyfragments.ui.enableattendance

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.authorization.AuthActivity
import com.example.guniattendancefaculty.databinding.FragmentEnableAttendanceBinding
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.*
import com.guni.uvpce.moodleapplibrary.model.*
import com.jianastrero.capiche.doIHave
import com.jianastrero.capiche.iNeed
import kotlinx.coroutines.*
import org.json.JSONArray
import java.text.SimpleDateFormat


class EnableAttendanceFragment : Fragment(R.layout.fragment_enable_attendance) {
    enum class AttParamMode{
        Faculty,
        Course,
        Group,
        Session
    }
    private lateinit var binding: FragmentEnableAttendanceBinding
    private lateinit var viewModel: EnableAttendanceViewModel
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var curLocation: Location? = null
    private val coursesArrayData = ArrayList<String>()
    private val coursesObjArrayData = ArrayList<MoodleCourse>()
    private val groupArrayData = ArrayList<String>()
    private val groupObjArrayData = ArrayList<MoodleGroup>()
    private val sessionsArrayData = ArrayList<String>()
    private val placeArrayData = ArrayList<String>()
    private val placeLocationArrayData = ArrayList<String>()
    private var sessionsObjArrayData = ArrayList<MoodleSession>()
    private var facultiesArrayData = ArrayList<String>()
    private var facultiesAllArrayData = ArrayList<BaseUserInfo>()
    private var progressDialog:CustomProgressDialog?=null

    //private lateinit var moodleatt: MoodleAttendance
    private var selectedCourse: MoodleCourse?=null
    private var selectedGroup: MoodleGroup? =null
    private var selectedPlace:Int=-1
    companion object{
        var selectedSession: MoodleSession?=null
    }

    private var attendanceStartDate:Long = -1
    private var attendanceEndDate:Long = -1
    private var attendanceDuration:Long = -1
    private var TAG = "EnableAttendanceFragment"
    private var loggedInUserId: BaseUserInfo? = null
    private var selectedUserId: BaseUserInfo? = null

    @SuppressLint("InflateParams", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[EnableAttendanceViewModel::class.java]
        binding = FragmentEnableAttendanceBinding.bind(view)
        subscribeToObserve()
        initAllTextView()
        hideAllView()
        /*try{
            requireActivity().doIHave(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                onGranted = {
                    curLocation = getLocation()
                },
                onDenied = {
                    requestPermission()
                })
        }
        catch(ex:Exception)
        {
            snackbar(ex.message.toString())
        }*/


        MainScope().launch {
            val facultyUsername = MoodleConfig.getAuthUserName(requireContext())
            loggedInUserId = viewModel.getStudentInfo(requireContext(), facultyUsername)
            if(loggedInUserId == null){
                return@launch
            }
            selectedUserId = loggedInUserId
            loadAllFaculties()
            loadAllCourses(selectedUserId!!,loggedInUserId!!)
        }

        binding.apply {
            autoCompleteFaculty.keyListener=null
            autoCompleteCourse.keyListener=null
            autoCompleteTvLectureLabs.keyListener=null
            autoCompleteTvSession.keyListener=null
            autoCompleteTvplace.keyListener=null

            btnLeave.setOnClickListener {
                findNavController().navigate(R.id.studentLeaveFragment)
            }

            btnLogout.setOnClickListener {
                MoodleConfig.logout(requireContext())
                //Go to Authentication page:

                Intent(
                    requireActivity(),
                    AuthActivity::class.java
                ).also { intent ->
                    startActivity(intent)
                    requireActivity().finish()
                }
            }

            sessionAdd.setOnClickListener{
                autoCompleteTvSession.text = null
                autoCompleteTvAttendance.text = null
                val input_description = R.id.input_description
                input_description.apply {
                    it.visibility = View.VISIBLE
                }
                if(selectedGroup == null){
                    BasicUtils.errorDialogBox(requireContext(),"Error","Group is not selected yet")
                    return@setOnClickListener
                }
                BasicUtils.showTimerDialog(
                    requireContext(),layoutInflater,
                    title = "SESSION INFORMATION",
                    {d,m,y,description,selectedStartTime,selectedEndTime,duration,isError,errorText->
                        if(isError){
                            snackbar(errorText)
                            return@showTimerDialog false
                        }
                        MainScope().launch {
                            val desc = "Session taken by ${selectedUserId!!.username} | $description"

                            viewModel.createNewSession(
                                requireActivity(),
                                selectedGroup!!,
                                selectedStartTime,
                                duration,
                                selectedUserId!!.id,
                                desc,
                                sessionsArrayData,
                                sessionsObjArrayData
                            )
                        }
                        return@showTimerDialog true
                    })

            }

            tlSession.setEndIconOnClickListener{
                updateSessionList()
            }

            autoCompleteFaculty.onItemClickListener = OnItemClickListener { parent, view, position, rowId ->
                MainScope().launch {
                    tlAttendance.visibility = View.GONE
                    tlSession.visibility = View.GONE
                    tlLabs.visibility = View.GONE
                    sessionAdd.visibility=View.GONE
                    tlPlace.visibility=View.GONE
                    binding.tlRange.visibility=View.GONE
                    coursesArrayData.clear()
                    coursesObjArrayData.clear()
                    sessionsArrayData.clear()
                    sessionsObjArrayData.clear()
                    selectedCourse = null
                    selectedUserId = facultiesAllArrayData[position]
                    autoCompleteCourse.text=null
                    autoCompleteTvLectureLabs.text=null
                    autoCompleteTvSession.text=null
                    autoCompleteTvAttendance.text=null
                    tlCourses.visibility=View.VISIBLE

                    loadAllCourses(selectedUserId!!,loggedInUserId!!)
                    Log.i(TAG, "selectedFaculty: ${selectedUserId!!.username}")
                }
            }

            autoCompleteCourse.onItemClickListener =
                OnItemClickListener { parent, view, position, rowId ->
                    MainScope().launch {
                        selectedCourse = coursesObjArrayData[position]
                        autoCompleteTvLectureLabs.text=null
                        autoCompleteTvSession.text=null
                        autoCompleteTvAttendance.text=null
                        tlLabs.isVisible = true
                        groupArrayData.clear()
                        groupObjArrayData.clear()
                        sessionsArrayData.clear()
                        sessionsObjArrayData.clear()
                        selectedGroup = null
                        //btnStartAttQr.isActivated = true
                        btnstartAtt.isActivated = true
                        btnstopAtt.isActivated = true
                        tlAttendance.visibility = View.GONE
                        tlSession.visibility = View.GONE
                        sessionAdd.visibility=View.GONE
                        tlPlace.visibility=View.GONE
                        binding.tlRange.visibility=View.GONE

                        Log.i(TAG, "onViewCreated: Course Selection:${selectedCourse!!.Name}")
                        viewModel.fillLectureLabsDropDown(requireActivity(),AttParamMode.Group,
                            selectedCourse!!, groupArrayData, groupObjArrayData)
                        Log.i(TAG, "onViewCreated: Group List:${groupObjArrayData.joinToString("\n")}")
                    }

                }

            autoCompleteTvLectureLabs.onItemClickListener =
                OnItemClickListener { parent, view, position, rowId ->
                    MainScope().launch {
                        selectedGroup = groupObjArrayData[position]
                        autoCompleteTvSession.text=null
                        autoCompleteTvAttendance.text=null
                        autoCompleteTvplace.text=null
                        binding.tlSession.visibility = View.GONE
                        binding.tlAttendance.visibility=View.GONE
                        binding.sessionAdd.visibility = View.GONE
                        tlPlace.visibility=View.VISIBLE
                        try{
                            fillAutoCompleteTextViewPlace("https://script.google.com/macros/s/AKfycbzRZq71El7QfSmce5IGNY7yEHhoEpOYFpQeoYcDwPLE_RnzQIhYI68C8NAOejH6ayLOwA/exec",selectedGroup!!.groupName)
                        }
                        catch (ex:Exception)
                        {
                            snackbar(ex.message.toString())
                        }
                    }
                }

            autoCompleteTvplace.onItemClickListener =
                OnItemClickListener { parent, view, position, rowId ->
                    selectedPlace = position
                    try{
                        autoCompleteTvSession.text=null
                        autoCompleteTvAttendance.text=null
                        sessionsArrayData.clear()
                        sessionsObjArrayData.clear()
                        selectedSession = null
                        binding.sessionAdd.visibility=View.VISIBLE
                        binding.tlSession.visibility=View.VISIBLE
                        binding.tlAttendance.visibility=View.GONE
                        updateSessionList()
                    }
                    catch(ex:java.lang.Exception)
                    {
                        Log.e(TAG,ex.message.toString())
                    }
                }

            autoCompleteTvSession.onItemClickListener =
                OnItemClickListener { parent, view, position, rowId ->
                    autoCompleteTvAttendance.text=null
                    selectedSession = if(position >= 0)
                        sessionsObjArrayData[position]
                    else
                        sessionsObjArrayData[0]

                    btnStudentlist.isVisible = true
                    tlAttendance.isVisible = true
                    //btnStartAttQr.isActivated = true
                    btnstartAtt.isActivated = true
                    btnstopAtt.isActivated = true
                    binding.tlRange.visibility=View.GONE
                }

            autoCompleteTvAttendance.setOnClickListener {
                BasicUtils.showTimerDialog(requireContext(), layoutInflater,
                    title = "ATTENDANCE INFORMATION",
                    confirmOnClick =
                    { d, m, y, description, selectedStartTime, selectedEndTime, duration, isError, errorText ->
                        if(isError){
                            snackbar(errorText)
                            return@showTimerDialog false
                        }
                        attendanceStartDate = selectedStartTime
                        attendanceEndDate = selectedEndTime
                        attendanceDuration = duration
                        autoCompleteTvAttendance.text = getAttendanceText()
                        btnstartAtt.isActivated = true
                        btnstopAtt.isActivated = true
                        //btnStartAttQr.isActivated = true
                        //btnStartAttQr.isVisible = true
                        btnstartAtt.visibility = View.VISIBLE
                        btnstopAtt.visibility = View.VISIBLE
                        binding.tlRange.visibility=View.VISIBLE

                        return@showTimerDialog true
                    },needDescription = false
                )
            }

            /*btnStartAttQr.setOnClickListener { it2 ->
                if (!it2.isActivated) {
                    if (it2.alpha == 1F) {
                        snackbar("Select class/lab first before generating QR Code")
                    } else {
                        snackbar("Attendance already enabled")
                    }
                }
                else {
                    hideKeyboard(requireActivity())
                    if(selectedUserId==null)
                    {
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Faculty")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedCourse == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Course")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedGroup == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Group")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedPlace==-1)
                    {
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Place")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedSession == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Session")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    val loc = placeLocationArrayData[selectedPlace].split(',')
                    MainScope().launch {
                        progressDialog!!.start("QR code is generating...")
                        val obj = QRMessageData(
                            sessionId = selectedSession!!.sessionId,
                            sessionStartDate = selectedSession!!.sessionStartDate/1000,
                            sessionEndDate = selectedSession!!.sessionEndDate/1000,
                            courseId = selectedCourse!!.id,
                            courseName = selectedCourse!!.Name,
                            groupId = selectedSession!!.group.groupid,
                            groupName = selectedSession!!.group.groupName,
                            loggedInFacultyUserId = loggedInUserId!!.id,
                            attendanceByFacultyId = selectedUserId!!.id,
                            facultyLocationLat = loc[0],
                            facultyLocationLong = loc[1],
                            attendanceStartDate = attendanceStartDate,
                            attendanceEndDate = attendanceEndDate,
                            attendanceDuration = attendanceDuration/60,
                            statusList = selectedSession!!.getCompactStatusList()
                        )
                        Log.i(TAG, "onViewCreated: QR Object:${obj}")
                        val encodedData = MoodleConfig.getModelRepo(requireActivity()).getQRDataString(obj)

                        // This is for Setting the Data. (it will replace the data if already exist)
                        viewModel.storeFacData(
                            context = requireActivity(),
                            userID = selectedUserId!!.id.toInt(),
                            facultyName = selectedUserId!!.username,
                            encodedData = encodedData!!
                        )
                        progressDialog!!.stop()
                        snackbar("Generated QR Code!")
                        val bundle = Bundle()
                        bundle.putString("faculty_username", selectedUserId!!.username)
                        //Switch Fragment to show QR Code!
                        findNavController().navigate(
                            R.id.qrCodeFragment,
                            bundle
                        )
                    }
                }

            }*/

            btnstartAtt.setOnClickListener { it2->
                if (!it2.isActivated) {
                    if (it2.alpha == 1F) {
                        snackbar("Select class/lab first before generating QR Code")
                    } else {
                        snackbar("Attendance already enabled")
                    }
                }
                else
                {
                    hideKeyboard(requireActivity())

                    if(selectedUserId==null)
                    {
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Faculty")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedCourse == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Course")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedGroup == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Group")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedPlace==-1)
                    {
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Place")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedSession == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Session")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    val loc = placeLocationArrayData[selectedPlace].split(',')
                    MainScope().launch {
                        progressDialog!!.start("Attendance is starting please wait....")
                        val pattern = Regex("\\d+")
                        var range = autoCompleteTvRange.text.toString()
                        if(range=="")
                        {
                            range="15"
                        }
                        if (!pattern.containsMatchIn(range))
                        {
                            BasicUtils.errorDialogBox(requireContext(),"Error","Range not valid")
                            progressDialog!!.stop()
                            return@launch
                        }
                        val obj = QRMessageData(
                            sessionId = selectedSession!!.sessionId,
                            sessionStartDate = selectedSession!!.sessionStartDate/1000,
                            sessionEndDate = selectedSession!!.sessionEndDate/1000,
                            courseId = selectedCourse!!.id,
                            courseName = selectedCourse!!.Name,
                            groupId = selectedSession!!.group.groupid,
                            groupName = selectedSession!!.group.groupName,
                            loggedInFacultyUserId = loggedInUserId!!.id,
                            attendanceByFacultyId = selectedUserId!!.id,
                            facultyLocationLat = loc[0],
                            facultyLocationLong = loc[1],
                            attendanceStartDate = attendanceStartDate,
                            attendanceEndDate = attendanceEndDate,
                            attendanceDuration = attendanceDuration/60,
                            statusList = selectedSession!!.getCompactStatusList()
                        )
                        Log.i(TAG, "onViewCreated: QR Object:${getQRText(obj)}")
                        val userList = MoodleConfig.getModelRepo(requireContext()).getStudentList(selectedCourse!!,selectedGroup!!)

                        val spliutUserList = userList.chunked(20)

                        var attendance = 0
                        for(i in 0 until spliutUserList.size)
                        {
                            val st = startAttendance(obj,spliutUserList[i])
                            withContext(Dispatchers.IO) {
                                Thread.sleep(1_000)
                            }
                            if(st)
                            {
                                attendance++
                            }
                        }
                        Log.i(TAG,"User List: ${userList}")

                        if(attendance==spliutUserList.size)
                        {
                            progressDialog!!.stop()
                            snackbar("Attendance Start Successful")
                        }
                        else{
                            progressDialog!!.stop()
                            snackbar("Unable to Start Attendance")
                        }
                    }
                }
            }

            btnstopAtt.setOnClickListener { it2->
                if (!it2.isActivated) {
                    if (it2.alpha == 1F) {
                        snackbar("Select class/lab first before start attendance")
                    } else {
                        snackbar("Attendance already enabled")
                    }
                }
                else
                {
                    hideKeyboard(requireActivity())
                    if(selectedUserId==null)
                    {
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Faculty")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedCourse == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Course")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedGroup == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Group")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedPlace==-1)
                    {
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Place")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    else if(selectedSession == null){
                        BasicUtils.errorDialogBox(requireContext(),"Error","Select Session")
                        progressDialog!!.stop()
                        return@setOnClickListener
                    }
                    MainScope().launch{
                        progressDialog!!.start("Attendance is stopping please wait....")
                        val userList = MoodleConfig.getModelRepo(requireContext()).getStudentList(selectedCourse!!,selectedGroup!!)

                        val spliutUserList = userList.chunked(20)

                        var attendance = 0
                        for(i in 0 until spliutUserList.size)
                        {
                            val st = stopAttendance(spliutUserList[i])
                            withContext(Dispatchers.IO) {
                                Thread.sleep(1_000)
                            }
                            if(st)
                            {
                                attendance++
                            }
                        }
                        Log.i(TAG,"User List: ${userList}")

                        if(attendance==spliutUserList.size)
                        {
                            progressDialog!!.stop()
                            snackbar("Attendance Stop Successful")
                        }
                        else{
                            progressDialog!!.stop()
                            snackbar("Unable to Stop Attendance")
                        }
                    }
                }

            }

            btnStudentlist.setOnClickListener {
                //For testing of QR Fragment, im setting up navigation here.
                val bundle = Bundle()
                bundle.putString("session", selectedSession.toString())
                findNavController().navigate(
                    R.id.studentListFragment,
                    bundle
                )
            }

            /*btnQrlist.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("faculty_username", selectedUserId!!.username)
                findNavController().navigate(
                    R.id.qrCodeFragment,
                    bundle
                )
                /*parentFragmentManager.beginTransaction().apply {
                    this.replace(R.id.flFragment, QrCodeFragment())
                    this.commit()
                }*/
            }*/
        }
    }
    private fun initAllTextView(){
        initAutoCompleteTextView(coursesArrayData,binding.autoCompleteCourse)
        initAutoCompleteTextView(facultiesArrayData,binding.autoCompleteFaculty)
        initAutoCompleteTextView(groupArrayData,binding.autoCompleteTvLectureLabs)
        initAutoCompleteTextView(sessionsArrayData,binding.autoCompleteTvSession)
    }
    private fun initAutoCompleteTextView(arrayData:java.util.ArrayList<String>,autoCompletetextView:AutoCompleteTextView){
        val obj = ArrayAdapter(requireContext(),
            androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
            arrayData)
        //obj.setNotifyOnChange(true)
        autoCompletetextView.setAdapter(
            obj
        )
        /*if(autoCompletetextView.adapter == null){
                val obj = ArrayAdapter(requireContext(),
                    androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
                    arrayData)
                obj.setNotifyOnChange(true)
                autoCompletetextView.setAdapter(
                    obj
                )
            }
            else{
                val arrayAdapter = autoCompletetextView.adapter as ArrayAdapter<String>
                arrayAdapter.clear()
                arrayAdapter.addAll(arrayData)
                arrayAdapter.notifyDataSetChanged()
            }*/
    }

    fun fillAutoCompleteTextViewPlace(url:String,filter:String) {
        progressDialog!!.start("Preparing Data....")
        val mRequestQueue = Volley.newRequestQueue(context)
        val request = object : StringRequest(
            Method.GET, url,
            { response ->
                try{
                    placeArrayData.clear()
                    val jsonArray = JSONArray(response)
                    var f = "Labs"
                    if (filter=="Lecture")
                    {
                        f="Classes"
                    }
                    for(i in 0 until jsonArray.length())
                    {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val placeName= jsonObject.getString(f)
                        val placeLocation = jsonObject.getString(f+"Location")
                        if(placeName!="" && placeLocation!="")
                        {
                            placeArrayData.add(placeName)
                            placeLocationArrayData.add(placeLocation)
                        }
                    }

                    val adapter = ArrayAdapter(
                        requireActivity(),
                        androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
                        placeArrayData
                    )
                    binding.autoCompleteTvplace.setAdapter(adapter)
                    progressDialog!!.stop()

                }
                catch (ex:Exception)
                {
                    progressDialog!!.stop()
                    Log.e(TAG,ex.message.toString())
                }
            },
            { error ->
                Log.e(TAG,error.toString())
                progressDialog!!.stop()
                BasicUtils.errorDialogBox(requireContext(),"Connection","Internet connection is poor!")
            }){}
        mRequestQueue.add(request)
    }

    fun getQRText(data: QRMessageData):String{
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val simpleTimeFormat = SimpleDateFormat("hh:mm a")
        return "Course: ${data.courseName} (" +
                "Group: ${data.groupName})\n\n" +
                "Session Date:${simpleDateFormat.format(data.sessionStartDate*1000)}\n" +
                " From:${simpleTimeFormat.format(data.sessionStartDate*1000)}" +
                " To:${simpleTimeFormat.format(data.sessionEndDate*1000)}\n\n"+
                "Attendance Date:${simpleDateFormat.format(data.attendanceStartDate*1000)}\n" +
                " From:${simpleTimeFormat.format(data.attendanceStartDate*1000)}"+
                " To:${simpleTimeFormat.format(data.attendanceEndDate*1000)}\n" +
                "Duration: ${data.attendanceDuration}mins"
    }
    private suspend fun startAttendance(messageData:QRMessageData,userList:List<MoodleUserInfo>):Boolean
    {
        return MoodleConfig.getModelRepo(requireContext()).sendMessageToStudents(messageData,userList)
    }
    private suspend fun stopAttendance(userList:List<MoodleUserInfo>):Boolean
    {
        return MoodleConfig.getModelRepo(requireContext()).sendToStopAttToStudents(userList)
    }
    private fun setAutoCompleteTextView(mode:AttParamMode){
        when(mode){
            AttParamMode.Faculty->initAutoCompleteTextView(facultiesArrayData,binding.autoCompleteFaculty)
            AttParamMode.Course->initAutoCompleteTextView(coursesArrayData,binding.autoCompleteCourse)
            AttParamMode.Group->initAutoCompleteTextView(groupArrayData,binding.autoCompleteTvLectureLabs)
            AttParamMode.Session->initAutoCompleteTextView(sessionsArrayData,binding.autoCompleteTvSession)
        }
    }
    private suspend fun loadAllFaculties(){
        viewModel.fillFacultyDropDown(requireActivity(),AttParamMode.Faculty, facultiesArrayData, facultiesAllArrayData)
        //if(selectedUserId != null)
        //binding.autoCompleteFaculty.setText(selectedUserId!!.username)
    }
    private suspend fun loadAllCourses(selectUser:BaseUserInfo,logInUser:BaseUserInfo) {
        setUserText(selectUser,logInUser)
        viewModel.getCoursesByUser(
            requireActivity(),AttParamMode.Course, selectUser.username,
            coursesArrayData, coursesObjArrayData
        )
    }
    private fun setUserText(selectUser:BaseUserInfo,logInUser:BaseUserInfo){
        val userCheck = selectUser.username == logInUser.username
        binding.loggedInText.text = "Logged in: ${logInUser.username.uppercase()}" + if(userCheck) "" else "\nProxy as: ${selectUser.username.uppercase()}"
    }
    private fun updateSessionList(){
        Log.i(TAG, "updateSessionList: 1")
        MainScope().launch {
            binding.autoCompleteTvSession.text=null

            if(selectedGroup == null){
                BasicUtils.errorDialogBox(requireContext(),"Error","Course is not selected yet")
                return@launch
            }
//            if(selectedGroup == null){
//                BasicUtils.errorDialogBox(requireContext(),"Error","Group is not selected yet")
//                return@launch
//            }

            viewModel.fillSessionsDropDown(requireActivity(),AttParamMode.Session,
                selectedCourse!!, selectedGroup!!,
                sessionsArrayData, sessionsObjArrayData
            )
            binding.tlSession.isVisible = true
            binding.sessionAdd.isVisible = true
            Log.i(TAG, "setEndIconOnClickListener: Refreshed Session Data Set!")
        }
    }
    private fun getAttendanceText():String{
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val simpleTimeFormat = SimpleDateFormat("hh:mm a")
        return  "Attendance Date:${simpleDateFormat.format(attendanceStartDate*1000)}\n" +
                " From:${simpleTimeFormat.format(attendanceStartDate*1000)}"+
                " To:${simpleTimeFormat.format(attendanceEndDate*1000)}\n" +
                "Duration: ${(attendanceDuration/60)}mins"
    }
    private fun hideAllView(){
        /*binding.btnStartAttQr.isActivated = false
        binding.btnStartAttQr.visibility = View.GONE*/

        binding.btnstartAtt.isActivated = false
        binding.btnstartAtt.visibility = View.GONE

        binding.btnstopAtt.isActivated = false
        binding.btnstopAtt.visibility = View.GONE

        binding.btnStudentlist.visibility = View.GONE

        binding.tlAttendance.visibility = View.GONE
        binding.tlSession.visibility = View.GONE
        binding.tlLabs.visibility = View.GONE
        binding.tlPlace.visibility=View.GONE
        binding.sessionAdd.visibility = View.GONE

        binding.tlRange.visibility=View.GONE


    }
    private fun subscribeToObserve() {
        if(progressDialog==null)
        {
            progressDialog= CustomProgressDialog(requireContext(),requireActivity())
        }
        viewModel.removeObservers()

        viewModel.mutableData.observe(viewLifecycleOwner, EventObserver(
            onError = {
//                showProgress(
//                    activity = requireActivity(),
//                    bool = false,
//                    parentLayout = binding.parentLayout,
//                    loading = binding.lottieAnimation
//                )
                progressDialog!!.stop()
            },
            onLoading = {
//                showProgress(
//                    activity = requireActivity(),
//                    bool = true,
//                    parentLayout = binding.parentLayout,
//                    loading = binding.lottieAnimation
//                )
                progressDialog!!.start("Preparing Data....")
            },
            onSuccess = {
//                showProgress(
//                    activity = requireActivity(),
//                    bool = false,
//                    parentLayout = binding.parentLayout,
//                    loading = binding.lottieAnimation
//                )
                progressDialog!!.stop()
            }
        ))
        viewModel.mutableArrayAdapter.observe(viewLifecycleOwner, EventObserver(
            onError = {
//                showProgress(
//                    activity = requireActivity(),
//                    bool = false,
//                    parentLayout = binding.parentLayout,
//                    loading = binding.lottieAnimation
//                )
                progressDialog!!.stop()
            },
            onLoading = {
//                showProgress(
//                    activity = requireActivity(),
//                    bool = true,
//                    parentLayout = binding.parentLayout,
//                    loading = binding.lottieAnimation
//                )
                progressDialog!!.start("Preparing Data....")
            },
            onSuccess = {
//                showProgress(
//                    activity = requireActivity(),
//                    bool = false,
//                    parentLayout = binding.parentLayout,
//                    loading = binding.lottieAnimation
//                )
                progressDialog!!.stop()
                setAutoCompleteTextView(it)
                Log.i(TAG, "subscribeToObserve: $it")
                //it.notifyDataSetChanged()
            }
        ))
    }

    private fun requestPermission() {
        try{
            requireActivity().iNeed(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                onGranted = {
                    curLocation = getLocation()
                },
                onDenied = {
                    snackbar("Location permission needed to access features!")
                }
            )
        }
        catch (ex:java.lang.Exception)
        {
            snackbar(ex.message.toString())
        }

    }
/*    private fun addAttendanceToSheet(attendance: Attendance) {

        val names = attendance.students.toString()
            .replace("[", "").replace("]", "").replace(" ", "")

//        val type = if (attendance.className.startsWith("C")) {
//            "lecture"
//        } else if (attendance.subject == "ML Ops") {
//            if (attendance.className.startsWith("M")) {
//                "AILEC"
//            } else {
//                "AILAB"
//            }
//        } else {
//            "lab"
//        }


        val sheetUrl = when (attendance.type) {
            "lecture" -> {
                val index = DATA[attendance.sem - 1].second.indexOf(attendance.className)
                SHEET_URLS_LECTURE[attendance.sem - 1][attendance.subject]!![index]
            }
            "AILEC" -> {
                SHEET_URLS_LECTURE[attendance.sem - 1][attendance.subject]!![0]
            }
            "AILAB" -> {
                SHEET_URLS_LABS[attendance.sem - 1][attendance.subject]!![0]
            }
            else -> {
                val index = DATA[attendance.sem - 1].third.indexOf(attendance.className)
                SHEET_URLS_LABS[attendance.sem - 1][attendance.subject]!![index]
            }
        }

        val params = JSONObject()
        params.put("enrolment", names)
        params.put("action", SHEET_ACTION)
        params.put("url", sheetUrl)
        params.put("sheetName", attendance.className)

        val obj = URLEncoder.encode(params.toString(), "UTF-8")

        val url =
            "$GOOGLE_SPREADSHEET_LINK?params=$obj"


        Log.d("TAG_TEST", "addAttendanceToSheet: $url")

        val client = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val request: Request = Request.Builder()
                    .url(url)
                    .post(RequestBody.create(null, ByteArray(0)))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        requireActivity().runOnUiThread {
                            if (e.message?.trim().toString() == "timeout")
                                onSuccess("The system is taking longer time to mark attendance, it will get updated in background!")
                            else
                                showError(e.message.toString())
                        }
                        Log.d("TAG_TEST", "onFailure: ${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            requireActivity().runOnUiThread {
                                Log.d("TAG_TEST", "addItemToSheet: ${response.body()?.string()}")
                                onSuccess("Added attendance")
                            }
                        } else {
                            Log.d("TAG_TEST", "onResponse: ${response.body()?.string()}")
                            requireActivity().runOnUiThread {
                                showError("Something went wrong")
                            }
                        }
                    }
                })
            } catch (e: java.lang.Exception) {
                Log.d("TAG_API_CALL", "apiRequestTakeAttendance: ${e.message}")
                requireActivity().runOnUiThread {
                    showError(e.message.toString())
                }
            }

        }

    }*/

    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener {
            curLocation = it
        }
        return try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        } catch (e: Exception) {
            Log.d("TAG_ERROR", "getLocation: ${e.message}")
            null
        }

    }
}