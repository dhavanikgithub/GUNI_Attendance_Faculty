package com.example.guniattendancefaculty.authorization.authfragments.ui.launcherscreen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentLauncherScreenBinding
import com.example.guniattendancefaculty.faculty.FacultyActivity
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.*
import com.guni.uvpce.moodleapplibrary.model.BaseUserInfo
import com.guni.uvpce.moodleapplibrary.repo.ModelRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LauncherScreenFragment : Fragment(R.layout.fragment_launcher_screen) {

    private lateinit var binding: FragmentLauncherScreenBinding
    private lateinit var viewModel: LauncherScreenViewModel
    var res: Boolean = false
    private var customProgressDialog: CustomProgressDialog? = null
    private val sharedPref by lazy {
        requireContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentLauncherScreenBinding.inflate(layoutInflater)
        /*if(MoodleConfig.getAuthUserName(requireContext()).isNotEmpty())
        {
            Intent(
                requireActivity(),
                FacultyActivity::class.java
            ).also { intent ->
                startActivity(intent)
                requireActivity().finish()
            }
            return
        }*/
        if(customProgressDialog==null)
        {
            customProgressDialog= CustomProgressDialog(requireContext(),requireActivity())
        }
        val checkboxTogglePref: SharedPreferences = requireActivity().getSharedPreferences("buttonToggle", 0)
        val checkboxCheck = checkboxTogglePref.getBoolean("buttonToggle", false)

        MainScope().launch {
            if(checkboxCheck){
                try{
                    //showProgress(requireActivity(), true, binding.parentLayout, binding.lottieAnimation)
                    customProgressDialog!!.start("Preparing....")
                    MoodleConfig.getModelRepo(requireContext())

                    val url = ModelRepository.getMoodleUrlObject(requireContext()).url

                    customProgressDialog!!.stop()
                    //showProgress(requireActivity(), false, binding.parentLayout, binding.lottieAnimation)

                    AlertDialog.Builder(requireActivity()).setTitle("Current Moodle URL")
                        .setMessage(url)
                        .setPositiveButton("Continue"){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .setNegativeButton("Change URL"){ dialog, _ ->
                            findNavController().navigate(
                                LauncherScreenFragmentDirections
                                    .actionLauncherScreenFragmentToSettingsFragment()
                            )
                            dialog.dismiss()
                        }
                        .create().show()
                }
                catch (Ex:Exception)
                {
                    snackbar(Ex.message.toString())
                }

            }
        }
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LauncherScreenViewModel::class.java]

        viewOberservers()
        binding = FragmentLauncherScreenBinding.bind(view)
        if(customProgressDialog==null)
        {
            customProgressDialog= CustomProgressDialog(requireContext(),requireActivity())
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var count = 0
        binding.apply {

            moodleusernameText.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(!moodlepasswordText.text.toString().isEmpty() && !moodleusernameText.text.toString().isEmpty())
                    {
                        btnLogin.performClick()
                    }
                    else{
                        snackbar("Moodle Username or Password is empty!")
                    }
                }
                false
            }

            moodlepasswordText.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(!moodleusernameText.text.toString().isEmpty() && !moodlepasswordText.text.toString().isEmpty())
                    {
                        btnLogin.performClick()
                    }
                    else{
                        snackbar("Moodle Username or Password is empty!")
                    }
                }
                false
            }

            btnLogin.setOnClickListener {

                hideKeyboard(requireActivity())

                val recievedMoodleUsername = moodleusernameText.text.toString().lowercase()
                val recievedmoodlePassword = moodlepasswordText.text.toString()


                if(recievedMoodleUsername.isEmpty() && recievedmoodlePassword.isEmpty())
                {
                    snackbar("Moodle Username or Password is empty!")
                }
                else
                {
                    if(sharedPref?.getInt("count",-1) == null)
                    {
                        sharedPref?.edit()?.putInt("Count", 0)?.apply()
                    }
                    else
                    {
                        Log.i("TAG", "Count is: ${sharedPref?.getInt("count", 0)}")
                        count = sharedPref?.getInt("count", 0)!!
                    }
                    if(count<3) {
                        MainScope().launch {
                            try{
                                val result = checkUser(recievedMoodleUsername,recievedmoodlePassword)
                                if (result!=null && result) {
                                    //Go to Faculty page:
                                    Intent(
                                        requireActivity(),
                                        FacultyActivity::class.java
                                    ).also { intent ->
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                } else {
                                    count++
                                    sharedPref.edit().putInt("count", count).apply()
                                    snackbar("Incorrect Username/Password Or Check Your Internet Connection")
                                }
                            }
                            catch (ex:Exception)
                            {
                                snackbar(ex.message.toString())
                            }

                        }

                    } else{
                        snackbar("Your account is Locked/Blocked! Contact Admin!")
                    }
                }
            }
            btnSetting.setOnClickListener {
                try{
                    findNavController().navigate(
                        LauncherScreenFragmentDirections.
                        actionLauncherScreenFragmentToSettingsFragment()
                    )
                }
                catch (ex:java.lang.Exception)
                {
                    snackbar(ex.message.toString())
                }
            }

        }

    }

    private suspend fun checkUser(recievedMoodleUsername:String,recievedmoodlePassword:String): Boolean?
    {
        if(customProgressDialog==null)
        {
            customProgressDialog=CustomProgressDialog(requireContext(),requireActivity())
        }
        var result: Boolean? =null
        customProgressDialog!!.start("Authenticating....")
        val parentJob= GlobalScope.launch {
            try {
                result = viewModel.doLogin(
                    requireActivity(),
                    recievedMoodleUsername,
                    recievedmoodlePassword
                )
            }
            catch (ex:Exception)
            {
                return@launch
            }
            return@launch
        }
        delay(10000)
        parentJob.cancel()
        parentJob.join()
        customProgressDialog!!.stop()
        return result
    }

    fun viewOberservers(){

        if(customProgressDialog==null)
        {
            customProgressDialog= CustomProgressDialog(requireContext(),requireActivity())
        }
        viewModel.delOberserveers()

        viewModel.mutableLoginStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                /*showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )*/
                customProgressDialog!!.stop()
                Log.i("LauncherScreenFragment", "ViewModel:MiscStatus:onError: $it")
            },
            onLoading = {
                /*showProgress(
                    activity = requireActivity(),
                    bool = true,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )*/
                customProgressDialog!!.start("Loading....")
                Log.i("LauncherScreenFragment", "ViewModel:MiscStatus:onLoading: LOADING ANIMATION")
            },
            onSuccess = {
                /*showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )*/
                customProgressDialog!!.stop()
                Log.i("LauncherScreenFragment", "ViewModel:MiscStatus:OnSuccess: $it")
            }
        ))
    }
}