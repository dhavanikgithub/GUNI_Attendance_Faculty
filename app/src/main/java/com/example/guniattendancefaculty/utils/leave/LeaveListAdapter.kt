package com.example.guniattendancefaculty.utils.leave

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.faculty.facultyfragments.ui.leavemodule.studentleave.StudentLeaveFragment
import com.example.guniattendancefaculty.utils.BasicUtils
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class LeaveListAdapter(private val context: Context, private val dataList: ArrayList<LeaveListData>) : BaseAdapter() {
    private val TAG = "LeaveListAdapter"
    private val url = LeaveURL.getLeaveURL()


    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.request_item_view, parent, false)
        }

        var data = getItem(position) as LeaveListData
        val enrollNoTextView = view!!.findViewById<TextView>(R.id.enroll_no)
        val fullNameTextView = view.findViewById<TextView>(R.id.name)
        val leaveReasonTextView = view.findViewById<TextView>(R.id.leave_reason)
        val startDateTextView = view.findViewById<TextView>(R.id.start_date)
        val endDateTextView = view.findViewById<TextView>(R.id.end_date)
        val leaveStatusTextView = view.findViewById<TextView>(R.id.leaveStatus)
        val requestDate = view.findViewById<TextView>(R.id.requestDate)
        val requestTime = view.findViewById<TextView>(R.id.requestTime)
        val requestType = view.findViewById<TextView>(R.id.leave_type)
        val btnApprove = view.findViewById<Button>(R.id.btnApprove)
        val btnReject = view.findViewById<Button>(R.id.btnReject)
        val btnDeliverToHOD = view.findViewById<Button>(R.id.btnDeliverToHOD)


        enrollNoTextView.text=data.studentId
        fullNameTextView.text=data.studentName
        leaveReasonTextView.text=data.reason
        startDateTextView.text=data.startDate
        endDateTextView.text=data.endDate

        if(data.role=="proctor" && data.status.contains("Pending"))
        {
            btnDeliverToHOD.visibility=View.VISIBLE
        }
        if(data.status.contains("Approved"))
        {
            leaveStatusTextView.text=data.status
            leaveStatusTextView.setTextColor(Color.parseColor("#45C560"))
            btnApprove.visibility=View.GONE
            btnReject.visibility=View.GONE
            btnDeliverToHOD.visibility=View.GONE
        }
        else if(data.status.contains("Rejected"))
        {
            leaveStatusTextView.text=data.status
            leaveStatusTextView.setTextColor(Color.parseColor("#FF2531"))
            btnApprove.visibility=View.GONE
            btnReject.visibility=View.GONE
            btnDeliverToHOD.visibility=View.GONE
        }
        else if(data.status.contains("Canceled")){
            leaveStatusTextView.text=data.status
            leaveStatusTextView.setTextColor(Color.parseColor("#FF2531"))
            btnApprove.visibility=View.GONE
            btnReject.visibility=View.GONE
            btnDeliverToHOD.visibility=View.GONE
        }
        else if(data.status.contains("Delivered") && data.role=="proctor"){
            leaveStatusTextView.text=data.status
            leaveStatusTextView.setTextColor(Color.parseColor("#F07421"))
            btnApprove.visibility=View.GONE
            btnReject.visibility=View.GONE
            btnDeliverToHOD.visibility=View.GONE
        }
        else if(!BasicUtils.isDateWithinOneWeek(data.requestDate))
        {
            leaveStatusTextView.setTextColor(Color.parseColor("#000000"))
            leaveStatusTextView.text="Request Time OUT"
            btnApprove.visibility=View.GONE
            btnReject.visibility=View.GONE
            btnDeliverToHOD.visibility=View.GONE
        }
        else{
            leaveStatusTextView.text=data.status
            leaveStatusTextView.setTextColor(Color.parseColor("#EDA200"))
            btnApprove.visibility=View.VISIBLE
            btnReject.visibility=View.VISIBLE
        }



        requestDate.text=data.requestDate
        requestTime.text=data.requestTime
        requestType.text=data.type

        val sharedPreferences = context.getSharedPreferences("faculty", Context.MODE_PRIVATE)

        val facultyUsername = sharedPreferences.getString("name", "")!!

        btnApprove.setOnClickListener {
            if(data.role=="hod")
            {
                setStatus(position,"Approved by ${facultyUsername}")
            }
            else{
                setStatus(position,"Approved by ${facultyUsername}")
            }

        }
        btnReject.setOnClickListener {
            if(data.role=="hod")
            {
                setStatus(position,"Rejected by ${facultyUsername}")
            }
            else{
                setStatus(position,"Rejected by ${facultyUsername}")
            }
        }
        btnDeliverToHOD.setOnClickListener {
            deliveredtohod(position)
        }
        return view
    }
    fun deliveredtohod(pos:Int)
    {
        val progress = ProgressDialog(context)
        progress.setMessage("Loading....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.isIndeterminate = true
        progress.show()
        val request: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { response: String? ->
            try {
                Log.d(TAG,response.toString())
                progress.dismiss()
                val jsonObject = JSONObject(response!!)
                if(jsonObject.has("success"))
                {
                    val sharedPreferences = context.getSharedPreferences("faculty", Context.MODE_PRIVATE)

                    val facultyUsername = sharedPreferences.getString("name", "")!!
                    dataList[pos].status="Delivered to HOD by ${facultyUsername}"
                    notifyDataSetChanged()
                }
            }
            catch (ex:Exception)
            {
                progress.dismiss()
                Log.e(TAG,ex.toString())
            }
        }, Response.ErrorListener { error: VolleyError ->
            progress.dismiss()
            Log.d(TAG, error.toString())
        }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["function_name"] = "leave_delivered_to_hod"
                params["leave_request_id"] = dataList[pos].id
                params["student_id"] = dataList[pos].studentId
                return params
            }
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }
    fun setStatus(pos:Int,statusString:String)
    {
        val progress = ProgressDialog(context)
        progress.setMessage("Loading....")
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.isIndeterminate = true
        progress.show()
        val request: StringRequest = object : StringRequest(Method.POST, url, Response.Listener { response: String? ->
            try {
                progress.dismiss()
                val jsonObject = JSONObject(response)
                if(jsonObject.has("success"))
                {
                    dataList[pos].status=statusString
                    notifyDataSetChanged()
                }
            }
            catch (ex:Exception)
            {
                progress.dismiss()
                Log.e(TAG,ex.toString())
            }
        }, Response.ErrorListener { error: VolleyError ->
            progress.dismiss()
            Log.d(TAG, error.toString())
        }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["function_name"] = "leave_set_leave_status"
                params["leave_request_id"] = dataList[pos].id
                params["status"] = statusString
                return params
            }
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }
}
