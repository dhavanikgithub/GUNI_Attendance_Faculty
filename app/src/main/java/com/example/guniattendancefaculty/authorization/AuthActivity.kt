package com.example.guniattendancefaculty.authorization

import android.content.Intent
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
import com.example.guniattendancefaculty.faculty.FacultyActivity
import com.example.guniattendancefaculty.utils.LiveNetworkMonitor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var snackbar:Snackbar
    private lateinit var alertDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Intent(this,FacultyActivity::class.java).apply {
            startActivity(this)
        }
        finish()

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
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container_auth) as NavHostFragment

        appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.splashScreenFragment,
                R.id.appPinFragment,
                R.id.launcherScreenFragment
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
            /*snackbar=Snackbar.make(
                findViewById(android.R.id.content),
                "No internet connection",
                Snackbar.LENGTH_LONG
            )
            snackbar.view.setBackgroundColor(Color.parseColor("#FF3939"))
            snackbar.setTextColor(Color.WHITE)
            snackbar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackbar?.show()*/
            alertDialog.show()
        }
        else{
//            snackbar?.dismiss()
            alertDialog.dismiss()
        }
    }
}