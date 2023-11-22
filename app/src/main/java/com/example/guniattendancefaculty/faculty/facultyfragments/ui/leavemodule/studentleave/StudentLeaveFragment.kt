package com.example.guniattendancefaculty.faculty.facultyfragments.ui.leavemodule.studentleave

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentStudentLeaveBinding
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.CustomProgressDialog
import com.example.guniattendancefaculty.utils.leave.LeaveListAdapter
import com.example.guniattendancefaculty.utils.leave.LeaveListData
import com.example.guniattendancefaculty.utils.leave.LeaveURL
import com.example.guniattendancefaculty.utils.snackbar
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StudentLeaveFragment : Fragment(R.layout.fragment_student_leave) {

    private lateinit var binding: FragmentStudentLeaveBinding
    private lateinit var viewModel: StudentLeaveViewModel
    /*private var customProgressDialog: CustomProgressDialog? = null*/
    private val url = LeaveURL.getLeaveURL()
    private var customeProgressDialog:CustomProgressDialog?=null
    private val TAG = "StudentLeaveFragment"
    var facultyID=""
    var facultyUsername=""



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStudentLeaveBinding.bind(view)

        if(customeProgressDialog==null)
        {
            customeProgressDialog= CustomProgressDialog(requireContext(),requireActivity())
        }
        val dataList = ArrayList<LeaveListData>()

        /*val fragmentLeaveViewDialog = AlertDialog.Builder(context)
        val customLayout = layoutInflater.inflate(R.layout.fragment_leave_view, null)
        val txtType = customLayout.findViewById<TextView>(R.id.txtType)
        val txtStartDate = customLayout.findViewById<TextView>(R.id.txtStartDate)
        val txtEndDate = customLayout.findViewById<TextView>(R.id.txtEndDate)
        val txtReason = customLayout.findViewById<TextView>(R.id.txtReason)
        val btnDocumentDownload = customLayout.findViewById<Button>(R.id.btnDocumentDownload)
        fragmentLeaveViewDialog.setView(customLayout)
        fragmentLeaveViewDialog.create()*/


        binding.btnLeaveLogout.setOnClickListener {
            // Obtain a reference to SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("faculty", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            findNavController().popBackStack()
            findNavController().navigate(R.id.leaveLoginFragment)
        }

        binding.checkboxApproved.setOnCheckedChangeListener { buttonView, isChecked ->
            customeProgressDialog!!.start("Loading....")
            filterListView(dataList)
            customeProgressDialog!!.stop()
        }
        binding.checkboxPending.setOnCheckedChangeListener { buttonView, isChecked ->
            customeProgressDialog!!.start("Loading....")
            filterListView(dataList)
            customeProgressDialog!!.stop()
        }
        binding.checkboxRejected.setOnCheckedChangeListener { buttonView, isChecked ->
            customeProgressDialog!!.start("Loading....")
            filterListView(dataList)
            customeProgressDialog!!.stop()
        }
        binding.checkboxDelivered.setOnCheckedChangeListener { buttonView, isChecked ->
            customeProgressDialog!!.start("Loading....")
            filterListView(dataList)
            customeProgressDialog!!.stop()
        }

        binding.checkboxCanceled.setOnCheckedChangeListener { buttonView, isChecked ->
            customeProgressDialog!!.start("Loading....")
            filterListView(dataList)
            customeProgressDialog!!.stop()
        }

        binding.leaveHistory.setOnItemClickListener { parent, view, position, id ->
            Log.d("ListItemClicked",position.toString())
            val data = dataList[position]

            /*txtType.text=data.type
            txtStartDate.text = data.startDate
            txtEndDate.text = data.endDate
            txtReason.text = data.reason
            btnDocumentDownload.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(data.attachment))
                startActivity(i)
            }
            fragmentLeaveViewDialog.show()*/
            val bundle = Bundle()
            Log.i(TAG, "ItemData: {$data}")
            bundle.putString("id", data.id)
            bundle.putString("studentId", data.studentId)
            bundle.putString("studentName", data.studentName)
            bundle.putString("proctorId", data.proctorId)
            bundle.putString("type", data.type)
            bundle.putString("reason", data.reason)
            bundle.putString("startDate", data.startDate)
            bundle.putString("endDate", data.endDate)
            bundle.putString("requestDate", data.requestDate)
            bundle.putString("requestTime", data.requestTime)
            bundle.putString("attachment", data.attachment)
            bundle.putString("status", data.status)
            bundle.putString("role", data.role)
            findNavController().navigate(R.id.leaveViewFragment,bundle)
        }

        MainScope().launch {
            try{
//                val facultyUsername = MoodleConfig.getAuthUserName(requireContext()).toLowerCase()
                val sharedPreferences = requireActivity().getSharedPreferences("faculty", Context.MODE_PRIVATE)

                facultyID = sharedPreferences.getString("id", "")!!
                facultyUsername = sharedPreferences.getString("name", "")!!
                    .uppercase(Locale.getDefault())

                customeProgressDialog!!.start("Loading....")
                /*val facultyUsername = "hms"*/

                val params: MutableMap<String, String> = HashMap()
                params["function_name"] = "leave_get_role_by_name"
                params["proctor_name"] = facultyUsername

                var request: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { response: String? ->
                    try{
                        val jsonObject = JSONObject(response)
                        if(jsonObject.has("role"))
                        {
                            val role = jsonObject.getString("role")
                            if(role!="null")
                            {
                                val request = object : StringRequest(Method.POST, url, Response.Listener { response: String? ->
                                    Log.d(TAG+"1",response!!)
                                    try{
                                        val jsonArrayLeaveRequest = JSONArray(response)
                                        val request: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { response: String? ->
                                            try {
                                                Log.d(TAG+"2",response!!)
                                                val jsonObjectStudent = JSONObject(response)
                                                for (i in 0 until jsonArrayLeaveRequest.length())
                                                {
                                                    val jsonObject = jsonArrayLeaveRequest.getJSONObject(i)
                                                    val studentInfo = jsonObjectStudent.getJSONObject(jsonObject.getString("student_id"))
                                                    val reason = String(android.util.Base64.decode(jsonObject.getString("reason"), android.util.Base64.DEFAULT),charset("UTF-8"))
                                                    val leaveListData = LeaveListData(
                                                        jsonObject.getString("id"),
                                                        jsonObject.getString("student_id"),
                                                        studentInfo.getString("full_name"),
                                                        jsonObject.getString("proctor_id"),
                                                        jsonObject.getString("type"),
                                                        jsonObject.getString("start_date"),
                                                        jsonObject.getString("end_date"),
                                                        reason,
                                                        jsonObject.getString("attachment"),
                                                        jsonObject.getString("status"),
                                                        jsonObject.getString("request_date"),
                                                        jsonObject.getString("request_time"),
                                                        role
                                                    )
                                                    dataList.add(leaveListData)
                                                }
                                                dataList.reverse()
                                                val leaveListAdapter = LeaveListAdapter(requireContext(), dataList)
                                                binding.leaveHistory.adapter = leaveListAdapter
                                                customeProgressDialog!!.stop()
                                            }
                                            catch (ex:Exception)
                                            {
                                                customeProgressDialog!!.stop()
                                                snackbar(ex.message.toString())
                                                Log.e(TAG+"3",ex.toString())
                                            }
                                        },Response.ErrorListener { error: VolleyError ->
                                            customeProgressDialog!!.stop()
                                            snackbar(error.message.toString())
                                            Log.d(TAG+"4", error.toString())
                                        }){
                                            @Throws(AuthFailureError::class)
                                            override fun getParams(): Map<String, String>? {
                                                val params: MutableMap<String, String> = HashMap()
                                                params["function_name"] = "leave_get_students_info_by_proctor_name"
                                                params["proctor_id"] = facultyID.toString()
                                                params["role"] = role
                                                return params
                                            }
                                        }
                                        val queue = Volley.newRequestQueue(requireContext())
                                        queue.add(request)
                                    }
                                    catch (ex:Exception)
                                    {
                                        customeProgressDialog!!.stop()
                                        snackbar(ex.message.toString())
                                        Log.d(TAG+"5",ex.toString())
                                    }

                                },Response.ErrorListener { error: VolleyError ->
                                    customeProgressDialog!!.stop()
                                    snackbar(error.message.toString())
                                    Log.d(TAG+"6", error.toString())
                                }){
                                    @Throws(AuthFailureError::class)
                                    override fun getParams(): Map<String, String>? {
                                        val params: MutableMap<String, String> = HashMap()
                                        params["function_name"] = "leave_get_leave_request_by_proctor_name"
                                        params["proctor_name"] = facultyUsername
                                        return params
                                    }
                                }
                                val queue = Volley.newRequestQueue(requireContext())
                                queue.add(request)
                            }
                        }
                    }
                    catch (ex:Exception)
                    {
                        customeProgressDialog!!.stop()
                        snackbar(ex.message.toString())
                        Log.d(TAG,ex.toString())
                    }

                },Response.ErrorListener { error: VolleyError ->
                    customeProgressDialog!!.stop()
                    snackbar(error.message.toString())
                    Log.d(TAG,error.toString())
                    return@ErrorListener
                }){
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>? {
                        return params
                    }
                }
                var queue = Volley.newRequestQueue(requireContext())
                queue.add(request)



            }
            catch (ex:Exception)
            {
                customeProgressDialog!!.stop()
                snackbar(ex.message.toString())
                Log.e(TAG,ex.toString())
            }
        }
    }


    fun clearListView()
    {
        val emptyAdapter: ListAdapter = ArrayAdapter(requireContext(), R.layout.request_item_view, ArrayList<LeaveListData>())
        binding.leaveHistory.adapter = emptyAdapter
    }

    fun filterListView(dataList:ArrayList<LeaveListData>)
    {
        clearListView()
        val myList = ArrayList<String>()
        if(binding.checkboxApproved.isChecked)
        {
            myList.add("Approved")
        }
        if(binding.checkboxPending.isChecked)
        {
            myList.add("Pending")
        }
        if(binding.checkboxRejected.isChecked)
        {
            myList.add("Rejected")
        }
        if(binding.checkboxCanceled.isChecked)
        {
            myList.add("Canceled")
        }
        if(binding.checkboxDelivered.isChecked)
        {
            myList.add("Delivered")
        }
        if(myList.size==0)
        {
            val adapter = LeaveListAdapter(requireContext(), dataList)
            binding.leaveHistory.adapter = adapter
            return
        }

        val filterDataList = ArrayList<LeaveListData>()
        for (item in dataList) {
            if(myList.contains(item.status))
            {
                filterDataList.add(item)
            }
        }
        val adapter = LeaveListAdapter(requireContext(), filterDataList)
        binding.leaveHistory.adapter = adapter
    }

}