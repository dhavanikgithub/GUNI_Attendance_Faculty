package com.example.guniattendancefaculty.authorization.authfragments.ui.registerfaculty

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.showProgress
import com.example.guniattendancefaculty.utils.snackbar
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.authorization.authfragments.ui.launcherscreen.LauncherScreenFragment
import com.example.guniattendancefaculty.databinding.FragmentFacultyRegisterBinding
import com.example.guniattendancefaculty.moodle.MoodleConfig.Companion.getModelRepo
import com.jianastrero.capiche.doIHave
import com.jianastrero.capiche.iNeed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FacultyRegisterFragment : Fragment(R.layout.fragment_faculty_register) {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var binding: FragmentFacultyRegisterBinding
    private lateinit var viewModel: FacultyRegisterViewModel
    private var curImageUri: Uri = Uri.EMPTY
    lateinit var userid: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FacultyRegisterViewModel::class.java]
        subscribeToObserve()

        binding = FragmentFacultyRegisterBinding.bind(view)

        binding.apply {

            val enrol = ""
            enrollmentText.setText(enrol)
            enrollmentText.isEnabled = false

            //For getting information from moodle and setting output to the text fields.
            MainScope().launch {
                viewModel.getStudentInfo(requireActivity(), enrol, nameText, emailText)
            }


            /*btnRegister.setOnClickListener {
                MainScope().launch {
                    val res = viewModel.uploadUserPic(requireActivity(), enrol, curImageUri)
                    if (res) {
                        findNavController().navigate(FacultyRegisterFragmentDirections.actionFacultyRegisterFragmentToLauncherScreenFragment())
                    } else {
                        snackbar("Upload Error!")
                    }
                }
            }*/

            ivImage.setOnClickListener {
                requireActivity().doIHave(
                    Manifest.permission.CAMERA,
                    onGranted = {
                        startCrop()
                    },
                    onDenied = {
                        requestPermission()
                    })
            }
        }

    }

    private fun requestPermission() {
        requireActivity().iNeed(
            Manifest.permission.CAMERA,
            onGranted = {
                startCrop()
            },
            onDenied = {
                snackbar("Camera permission needed to access features")
            }
        )
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            curImageUri = result.uriContent!!
            viewModel.setCurrentImageUri(curImageUri)
        } else {
            val exception = result.error
            snackbar(exception.toString())
        }
    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(1, 1)
                setCropShape(CropImageView.CropShape.OVAL)
                setOutputCompressQuality(70)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                setImageSource(includeGallery = true, includeCamera = true)
            }
        )
    }

    private fun subscribeToObserve() {
        viewModel.curImageUri.observe(viewLifecycleOwner) {
            binding.tvCaptureImage.isVisible = it == Uri.EMPTY
            if (it == Uri.EMPTY) {
                binding.ivImage.setImageResource(R.drawable.ic_round_person_24)
            } else {
                curImageUri = it
                glide.load(curImageUri).into(binding.ivImage)
            }
        }

        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )
                when (it) {
                    "emptyEmail" -> {
                        binding.emailText.error = "Email cannot be empty"
                    }
                    "name" -> {
                        binding.nameText.error = "Name cannot be empty"
                    }
                    "uri" -> {
                        snackbar("Capture your face")
                    }
                    else -> {
                        Log.d("TAG_REGISTER_FACULTY", "subscribeToObserve: $it")
                        snackbar(it)
                    }
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
            binding.apply {
                emailText.setText("")
                nameText.setText("")
                emailText.setText("")
            }

            showProgress(
                activity = requireActivity(),
                bool = false,
                parentLayout = binding.parentLayout,
                loading = binding.lottieAnimation
            )

            /*findNavController().navigate(
                FacultyRegisterFragmentDirections
                    .actionFacultyRegisterFragmentToLauncherScreenFragment()
            )*/
        })
    }
}