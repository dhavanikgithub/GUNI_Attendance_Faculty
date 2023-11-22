package com.example.guniattendancefaculty.faculty.facultyfragments.ui.leavemodule.leavelogin

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentLeaveLoginBinding
import com.example.guniattendancefaculty.utils.CustomProgressDialog
import com.example.guniattendancefaculty.utils.leave.LeaveURL
import com.example.guniattendancefaculty.utils.snackbar
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LeaveLoginFragment : Fragment() {

    companion object {
        fun newInstance() = LeaveLoginFragment()
    }

    private lateinit var viewModel: LeaveLoginViewModel
    private lateinit var binding:FragmentLeaveLoginBinding
    private val url = LeaveURL.getLeaveURL()
    private var selectedRole:String="faculty"
    private lateinit var customProgressDialog: CustomProgressDialog

    override fun onResume() {
        super.onResume()
        val roleDataList = ArrayList<String>()
        roleDataList.add("faculty")
        roleDataList.add("hod")

        val adapter = ArrayAdapter(
            requireActivity(),
            R.layout.url_list_spinner_item,
            roleDataList
        )
        adapter.setDropDownViewResource(R.layout.url_list_spinner_item)
        binding.s1Role.setAdapter(adapter)
        binding.s1Role.setText(roleDataList[0],false)
        selectedRole="faculty"
    }

    override fun onStop() {
        super.onStop()
        binding.s1Role.setAdapter(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leave_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentLeaveLoginBinding.bind(view)

        customProgressDialog = CustomProgressDialog(requireActivity(),requireActivity())

        binding.s1Role.setOnItemClickListener { parent, view, position, id ->
            selectedRole = parent.getItemAtPosition(position).toString()
        }

        val sharedPreferences = requireActivity().getSharedPreferences("faculty", Context.MODE_PRIVATE)

        val facultyID = sharedPreferences.getString("id", "")!!
        if(facultyID!="")
        {
            findNavController().popBackStack()
            findNavController().navigate(R.id.studentLeaveFragment)
        }

        binding.apply {
            btnLeaveLogin.setOnClickListener {
                customProgressDialog.start("Loading...")
                val request: StringRequest =
                    object : StringRequest(
                        Method.POST, url, Response.Listener { response: String? ->
                            try{
                                Log.i("Login",response.toString())
                                val jsonObjUserInfo = JSONObject(response!!)
                                if(jsonObjUserInfo.has("success"))
                                {
                                    val res = jsonObjUserInfo.getString("success")
                                    val resName = jsonObjUserInfo.getString("name")
                                    val resID = jsonObjUserInfo.getString("id")
                                    if(res=="true")
                                    {
                                        // Obtain a reference to SharedPreferences
                                        val sharedPreferences = requireActivity().getSharedPreferences("faculty", Context.MODE_PRIVATE)

                                        val editor = sharedPreferences.edit()

                                        editor.putString("id", resID)
                                        editor.putString("name", resName)

                                        editor.apply()
                                        customProgressDialog.stop()
                                        findNavController().popBackStack()
                                        findNavController().navigate(R.id.studentLeaveFragment)
                                    }
                                    else{
                                        customProgressDialog.stop()
                                        snackbar("User not found")
                                    }
                                }

                            }
                            catch (ex:Exception)
                            {
                                customProgressDialog.stop()
                                snackbar(ex.message.toString())
                            }

                        },
                        Response.ErrorListener { error: VolleyError ->
                            customProgressDialog.stop()
                            snackbar(error.message.toString())
                        }){
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String>? {
                            val params: MutableMap<String, String> = HashMap()
                            params["function_name"] = "leave_faculty_auth"
                            params["name"] = leaveMoodleusernameText.text.toString()
                            params["password"] = leaveMoodlepasswordText.text.toString()
                            params["role"] = selectedRole
                            return params
                        }
                    }
                val queue = Volley.newRequestQueue(requireContext())
                queue.add(request)
            }
        }
    }

}