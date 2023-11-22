package com.example.guniattendancefaculty.faculty.facultyfragments.ui.leavemodule.leaveview

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentLeaveViewBinding
import com.example.guniattendancefaculty.utils.leave.LeaveURL
import com.example.guniattendancefaculty.utils.snackbar
import org.json.JSONObject

class LeaveViewFragment : Fragment(R.layout.fragment_leave_view) {

    private lateinit var binding: FragmentLeaveViewBinding
    private lateinit var viewModel: LeaveViewViewModel
    private val TAG="LeaveViewFragment"
    private val url = LeaveURL.getLeaveURL()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLeaveViewBinding.bind(view)

        var id = requireArguments().getString("id")
        var studentId = requireArguments().getString("studentId")
        var studentName = requireArguments().getString("studentName")
        var proctorId = requireArguments().getString("proctorId")
        var type = requireArguments().getString("type")
        var reason = requireArguments().getString("reason")
        var startDate = requireArguments().getString("startDate")
        var endDate = requireArguments().getString("endDate")
        var requestDate = requireArguments().getString("requestDate")
        var requestTime = requireArguments().getString("requestTime")
        var role  = requireArguments().getString("role")

        var attachment = requireArguments().getString("attachment")
        if(attachment=="null")
        {
            binding.layoutDocument.visibility=View.GONE
        }
        else
        {
            binding.layoutDocument.visibility=View.VISIBLE
        }
        var status = requireArguments().getString("status")


        binding.txtType.text=type
        binding.txtStartDate.text=startDate
        binding.txtEndDate.text=endDate
        binding.txtReason.movementMethod = ScrollingMovementMethod()
        binding.txtReason.text=reason
        binding.txtEnrollment.text=studentId
        binding.txtStudentName.text=studentName
        updateStatusLabel(status.toString(),role.toString())


        binding.btnDocumentDownload.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(attachment))
            startActivity(i)
        }

        binding.btnApprove.setOnClickListener {
            updateStatus(id.toString(),"Approved",role.toString())
        }

        binding.btnReject.setOnClickListener {
            updateStatus(id.toString(),"Rejected",role.toString())
        }

        binding.btnDeliverToHOD.setOnClickListener {
            deliver_to_hod(id.toString(),studentId.toString(),role.toString())
        }
    }

    fun updateStatus(requestID:String,status:String,role:String)
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
                    updateStatusLabel(status,role)
                    snackbar(jsonObject.getString("success"))
                }
                else{
                    snackbar(response.toString())
                }
            }
            catch (ex:Exception)
            {
                progress.dismiss()
                snackbar(ex.message.toString())
                Log.e(TAG,ex.toString())
            }
        }, Response.ErrorListener { error: VolleyError ->
            progress.dismiss()
            snackbar(error.message.toString())
            Log.d(TAG, error.toString())
        }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["function_name"] = "leave_set_leave_status"
                params["leave_request_id"] = requestID
                params["status"] = status
                return params
            }
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    fun deliver_to_hod(requestID:String,studentID:String,role:String)
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
                        updateStatusLabel("Delivered",role)
                        snackbar(jsonObject.getString("success"))
                    }
                    else{
                        snackbar(response.toString())
                    }
                }
                catch (ex:Exception)
                {
                    progress.dismiss()
                    snackbar(ex.message.toString())
                    Log.e(TAG,ex.toString())
                }
            }, Response.ErrorListener { error: VolleyError ->
                progress.dismiss()
                snackbar(error.message.toString())
                Log.d(TAG, error.toString())
            }){
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["function_name"] = "leave_delivered_to_hod"
                    params["leave_request_id"] = requestID
                    params["student_id"] = studentID
                    return params
                }
            }
            val queue = Volley.newRequestQueue(context)
            queue.add(request)
    }

    fun updateStatusLabel(status:String,role:String)
    {
        if(status=="Pending")
        {
            if(!binding.btnApprove.isVisible)
            {
                binding.btnApprove.visibility=View.VISIBLE
            }
            if(!binding.btnReject.isVisible)
            {
                binding.btnReject.visibility=View.VISIBLE
            }
            if(!binding.btnDeliverToHOD.isVisible)
            {
                if(role=="proctor")
                {
                    binding.btnDeliverToHOD.visibility=View.VISIBLE
                }
            }
        }
        else{
            if(binding.btnApprove.isVisible)
            {
                binding.btnApprove.visibility=View.GONE
            }
            if(binding.btnReject.isVisible)
            {
                binding.btnReject.visibility=View.GONE
            }
            if(binding.btnDeliverToHOD.isVisible)
            {
                binding.btnDeliverToHOD.visibility=View.GONE
            }
        }
        binding.txtStatus.text=status
    }
}