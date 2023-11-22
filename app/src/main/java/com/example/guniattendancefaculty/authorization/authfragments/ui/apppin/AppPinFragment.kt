package com.example.guniattendancefaculty.authorization.authfragments.ui.apppin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.guniattendancefaculty.faculty.FacultyActivity
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.hideKeyboard
import com.example.guniattendancefaculty.utils.showProgress
import com.example.guniattendancefaculty.utils.snackbar
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentAppPinBinding

class AppPinFragment : Fragment(R.layout.fragment_app_pin) {

    private lateinit var binding: FragmentAppPinBinding
    private lateinit var viewModel: AppPinViewModel
    private val args: AppPinFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AppPinViewModel::class.java]
        subscribeToObserve()

        binding = FragmentAppPinBinding.bind(view)

        binding.apply {
            fabLogin.setOnClickListener {
                hideKeyboard(requireActivity())
                viewModel.login(pinView.text.toString())
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
        ) {
            showProgress(
                activity = requireActivity(),
                bool = false,
                parentLayout = binding.parentLayout,
                loading = binding.lottieAnimation
            )
            binding.apply {
                pinView.setText("")
            }
            snackbar("Logged in successfully!!")

            Intent(requireActivity(), FacultyActivity::class.java).also { intent ->
                startActivity(intent)
                requireActivity().finish()
            }
        })

    }

}