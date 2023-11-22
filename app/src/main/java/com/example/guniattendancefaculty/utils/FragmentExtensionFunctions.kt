package com.example.guniattendancefaculty.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar

fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    val currentFocusedView = activity.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun showProgress(
    activity: Activity,
    bool: Boolean,
    parentLayout: ConstraintLayout,
    loading: LottieAnimationView
) {
    loading.isVisible = bool
    if (bool) {
//        parentLayout.alpha = 0.5f
        activity.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    } else {
//        parentLayout.alpha = 1f
        activity.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}

fun Fragment.snackbar(text: String) {
    try{
        Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
    catch(ex:Exception)
    {
        Log.e("FragmentExtensionFunctions",ex.message.toString())
    }

}