package com.example.guniattendancefaculty.authorization.authfragments.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.faculty.FacultyActivity
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.LiveNetworkMonitor
import com.example.guniattendancefaculty.utils.PermissionsUtils
import com.example.guniattendancefaculty.utils.snackbar
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import org.json.JSONArray

@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    //private val faculties = FirebaseFirestore.getInstance().collection("faculty")
    private lateinit var progress: LottieAnimationView
    private val TAG = "SplashScreenFragment"

    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()

        if(!PermissionsUtils.checkPermission(requireContext()))
        {
            PermissionsUtils.requestPermission(requireActivity())
        }
        val pm: PackageManager = requireContext().getPackageManager()
        val pkgName: String = requireContext().getPackageName()
        var pkgInfo: PackageInfo? = null
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val applicationVersion = pkgInfo!!.versionName
        val connectivityManager = requireContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val liveNetworkMonitor = LiveNetworkMonitor(connectivityManager)
        liveNetworkMonitor.observe(requireActivity()) {
            if(it)
            {
                GlobalScope.launch {
                    try{
                        val versionFileURL = "https://script.google.com/macros/s/AKfycbzRZq71El7QfSmce5IGNY7yEHhoEpOYFpQeoYcDwPLE_RnzQIhYI68C8NAOejH6ayLOwA/exec"
                        val mRequestQueue = Volley.newRequestQueue(context)
                        val request = object : StringRequest(
                            Method.GET, versionFileURL,
                            { response ->
                                val jsonArray = JSONArray(response)
                                Log.i(TAG,jsonArray.toString(4))
                                if(jsonArray.getJSONObject(0).getString("FacultyAppVersion")==applicationVersion)
                                {
                                    var res = false
                                    try{
                                        res = MoodleConfig.getAuthUserName(requireContext()).isNotEmpty()
                                    }
                                    catch (ex:IllegalStateException)
                                    {
                                        res = false
                                        snackbar(ex.message.toString())
                                        Log.e(TAG,ex.message.toString())
                                    }

                                    if(res)
                                    {
                                        try{
                                            Intent(
                                                requireActivity(),
                                                FacultyActivity::class.java
                                            ).also { intent ->
                                                startActivity(intent)
                                                requireActivity().finish()
                                            }
                                        }
                                        catch (ex:Exception)
                                        {
                                            ex.printStackTrace()
                                        }

                                    }
                                    else{
                                        findNavController().navigate(R.id.launcherScreenFragment)
                                    }
                                }
                                else{
                                    android.app.AlertDialog.Builder(requireActivity()).setTitle("App Update")
                                        .setMessage("Your Application version is old please Update the APP")
                                        .setPositiveButton("UPDATE"){ dialog, _ ->
                                            val openURL = Intent(Intent.ACTION_VIEW)
                                            openURL.data = Uri.parse("https://drive.google.com/drive/folders/1AVeXVQ23xYpo1iQXzFE94aqLu9eMYJWy?usp=sharing")
                                            startActivity(openURL)
                                            dialog.dismiss()
                                        }
                                        .setCancelable(false)
                                        .create().show()
                                }

                            },
                            { error ->
                                Log.e(TAG,error.toString())
                            }){}
                        request.retryPolicy = object : RetryPolicy {
                            override fun getCurrentTimeout(): Int {
                                return 7000
                            }

                            override fun getCurrentRetryCount(): Int {
                                return 500
                            }

                            @Throws(VolleyError::class)
                            override fun retry(error: VolleyError) {
                            }
                        }
                        mRequestQueue.add(request)

                    }
                    catch(ex:Exception)
                    {
                        snackbar(ex.message.toString())
                    }

                }
            }
        }

        /*val defaultsRate: HashMap<String, Any> = HashMap()
        defaultsRate["new_version_code_student"] = java.lang.String.valueOf(getVersionCode())

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(10) // change to 3600 on published app
            .build()

        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(defaultsRate)

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(
            requireActivity()
        ) { task ->
            if (task.isSuccessful) {
                val newVersionCode: String =
                    firebaseRemoteConfig.getString("new_version_code_student")
                val newAppLink: String = firebaseRemoteConfig.getString("new_app_link_student")
                if (newVersionCode.toInt() > getVersionCode()) {
                    progress.isVisible = false
                    showTheDialog(
                        newAppLink,
                        newVersionCode
                    )
                } else {
                    val pm: PackageManager = requireContext().getPackageManager()
                    val pkgName: String = requireContext().getPackageName()
                    var pkgInfo: PackageInfo? = null
                    try {
                        pkgInfo = pm.getPackageInfo(pkgName, 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                    val applicationVersion = pkgInfo!!.versionName
                    GlobalScope.launch {
                        try{
                            val versionFileURL = "https://script.google.com/macros/s/AKfycbzRZq71El7QfSmce5IGNY7yEHhoEpOYFpQeoYcDwPLE_RnzQIhYI68C8NAOejH6ayLOwA/exec"
                            val mRequestQueue = Volley.newRequestQueue(context)
                            val request = object : StringRequest(
                                Method.GET, versionFileURL,
                                { response ->
                                    val jsonArray = JSONArray(response)
                                    Log.i(TAG,jsonArray.toString(4))
                                    if(jsonArray.getJSONObject(0).getString("FacultyAppVersion")==applicationVersion)
                                    {
                                        //goAhead()
                                        if(MoodleConfig.getAuthUserName(requireContext()).isNotEmpty())
                                        {
                                            Intent(
                                                requireActivity(),
                                                FacultyActivity::class.java
                                            ).also { intent ->
                                                startActivity(intent)
                                                requireActivity().finish()
                                            }
                                        }
                                        else{
                                            findNavController().navigate(R.id.launcherScreenFragment)
                                        }
                                    }
                                    else{
                                        android.app.AlertDialog.Builder(requireActivity()).setTitle("App Update")
                                            .setMessage("Your Application version is old please Update the APP")
                                            .setPositiveButton("UPDATE"){ dialog, _ ->
                                                val openURL = Intent(Intent.ACTION_VIEW)
                                                openURL.data = Uri.parse("https://drive.google.com/drive/folders/1AVeXVQ23xYpo1iQXzFE94aqLu9eMYJWy?usp=sharing")
                                                startActivity(openURL)
                                                dialog.dismiss()
                                            }
                                            .setCancelable(false)
                                            .create().show()
                                    }

                                },
                                { error ->
                                    Log.e(TAG,error.toString())
                                }){}
                            request.retryPolicy = object : RetryPolicy {
                                override fun getCurrentTimeout(): Int {
                                    return 7000
                                }

                                override fun getCurrentRetryCount(): Int {
                                    return 500
                                }

                                @Throws(VolleyError::class)
                                override fun retry(error: VolleyError) {
                                }
                            }
                            mRequestQueue.add(request)
                        }
                        catch(ex:Exception)
                        {
                            snackbar(ex.message.toString())
                        }

                    }

                }
            } else Log.e(TAG, "mFirebaseRemoteConfig.fetchAndActivate() NOT Successful")
        }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = view.findViewById(R.id.lottieAnimation)

        /*try{
            val locationRequest: LocationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 10000 / 2
            val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
            locationSettingsRequestBuilder.addLocationRequest(locationRequest)
            locationSettingsRequestBuilder.setAlwaysShow(true)
            val settingsClient = LocationServices.getSettingsClient(requireContext())
            val task: Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())
            task.addOnFailureListener(requireActivity()) { e ->
                if (e is ResolvableApiException) {
                    try {
                        e.startResolutionForResult(
                            requireActivity(),
                            0x1
                        )
                    } catch (sendIntentException: IntentSender.SendIntentException) {
                        sendIntentException.printStackTrace()
                    }
                }
            }
        }
        catch (ex:Exception)
        {
            snackbar(ex.message.toString())
        }*/

    }

    /*private fun goAhead() {
        if (FirebaseAuth.getInstance().currentUser != null) {

            CoroutineScope(Dispatchers.Main).launch {
                val faculty =
                    faculties.document(FirebaseAuth.getInstance().currentUser!!.uid).get().await()
                        .toObject(Faculty::class.java)

                val role = if (faculty == null) {
                    "student"
                } else {
                    "faculty"
                }

                /*findNavController().navigate(
                    SplashScreenFragmentDirections
                        .actionSplashScreenFragmentToAppPinFragment(role)
                )*/
            }
        } else {
            if(MoodleConfig.getAuthUserName(requireContext()).isNotEmpty())
            {
                Intent(
                    requireActivity(),
                    FacultyActivity::class.java
                ).also { intent ->
                    startActivity(intent)
                    requireActivity().finish()
                }
                return
            }
            else{
                try{
                    findNavController().navigate(R.id.launcherScreenFragment)
                }
                catch (ex:java.lang.Exception)
                {
                    throw Exception(ex.message)
                }
            }

        }
    }*/
    /*private fun showTheDialog(newAppLink: String, versionFromRemoteConfig: String) {
        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Update")
            .setMessage("This version is absolute, please update to version: $versionFromRemoteConfig")
            .setPositiveButton("UPDATE") { _, _ ->
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(newAppLink)
                ).also {
                    startActivity(it)
                }
            }
            .show()
        dialog.setCancelable(false)
    }*/

    private var pInfo: PackageInfo? = null
    /*private fun getVersionCode(): Int {
        pInfo = null
        try {
            pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.i("MYLOG", "NameNotFoundException: " + e.message)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo!!.longVersionCode.toInt()
        } else {
            pInfo!!.versionCode
        }
    }*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtils.onRequestPermissionResult(requireContext(), requireActivity(), grantResults)
    }

}