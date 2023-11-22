package com.example.guniattendancefaculty.faculty

import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.utils.LiveNetworkMonitor
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FacultyActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var snackbar:Snackbar
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        /*snackbar= Snackbar.make(
            findViewById(android.R.id.content),
            "",
            Snackbar.LENGTH_INDEFINITE
        )*/
        alertDialog =  AlertDialog.Builder(this)
            .setMessage("Check Your Internet Connection")
            .setCancelable(false)
            .create()
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val liveNetworkMonitor = LiveNetworkMonitor(connectivityManager)
        liveNetworkMonitor.observe(this,{showNetworkMessage(it)})

        this.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container_faculty) as NavHostFragment

        appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.facultyHomeFragment,
                R.id.leaveLoginFragment,
                R.id.studentLeaveFragment
            )
        ).build()

        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun showNetworkMessage(isConnected:Boolean)
    {
        if(!isConnected)
        {
            alertDialog.show()
            /*snackbar=Snackbar.make(
                findViewById(android.R.id.content),
                "No internet connection",
                Snackbar.LENGTH_LONG
            )
            snackbar.view.setBackgroundColor(Color.parseColor("#FF3939"))
            snackbar.setTextColor(Color.WHITE)
            snackbar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackbar?.show()*/
        }
        else{
            alertDialog.dismiss()
//            snackbar?.dismiss()
        }
    }
}