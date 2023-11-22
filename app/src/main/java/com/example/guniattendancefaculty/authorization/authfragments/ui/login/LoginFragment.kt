package com.example.guniattendancefaculty.authorization.authfragments.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.guniattendancefaculty.faculty.FacultyActivity
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.hideKeyboard
import com.example.guniattendancefaculty.utils.showProgress
import com.example.guniattendancefaculty.utils.snackbar
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    lateinit var facultyUserName: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        subscribeToObserve()

        binding = FragmentLoginBinding.bind(view)

        binding.apply {
            btnLogin.setOnClickListener {
                hideKeyboard(requireActivity())
                viewModel.login(etUsername.text?.trim().toString(), pinView.text.toString())
                facultyUserName = etUsername.text.toString()
            }
        }

    }

    private fun subscribeToObserve() {
        viewModel.loginStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { error ->
                showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )
                when (error) {
                    "emptyEmail" -> {
                        binding.etUsername.error = "Email cannot be empty"
                    }
                    "email" -> {
                        binding.etUsername.error = "Enter a valid email"
                    }
                    "pin" -> {
                        snackbar("Pin should be of 6 length")
                    }
                    else -> snackbar(error)
                }
            },
            onLoading = {
                showProgress(
                    activity = requireActivity(),
                    bool = true,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )
            }
        ) { role ->
            showProgress(
                activity = requireActivity(),
                bool = false,
                parentLayout = binding.parentLayout,
                loading = binding.lottieAnimation
            )
            binding.apply {
                etUsername.setText("")
                pinView.setText("")
            }
            snackbar("Logged in successfully!!")
            if (role == "student") {
                snackbar("Trying to login using students account")
                Firebase.auth.signOut()
            } else {
                Intent(requireActivity(), FacultyActivity::class.java).also { intent ->
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        })
    }

}