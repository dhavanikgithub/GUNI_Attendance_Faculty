package com.example.guniattendancefaculty.faculty.facultyfragments.ui.editstudent

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.data.entity.Student
import com.example.guniattendancefaculty.databinding.FragmentEditStudentBinding
import com.example.guniattendancefaculty.utils.Constants
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.showProgress
import com.example.guniattendancefaculty.utils.snackbar
import com.jianastrero.capiche.doIHave
import com.jianastrero.capiche.iNeed
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditStudentFragment : Fragment(R.layout.fragment_edit_student) {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var binding: FragmentEditStudentBinding
    private lateinit var viewModel: EditStudentViewModel
    private val args: EditStudentFragmentArgs by navArgs()
    private var curImageUri: Uri = Uri.EMPTY
    private var sem = 0
    private var uid = ""
    private lateinit var student: Student

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EditStudentViewModel::class.java]
        subscribeToObserve()

        binding = FragmentEditStudentBinding.bind(view)

        val semesters = requireActivity().resources.getStringArray(R.array.semester)
        val arrayAdapterSem = ArrayAdapter(
            requireContext(),
            androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
            semesters
        )

        val branch = requireActivity().resources.getStringArray(R.array.branch)
        val arrayAdapterBranch = ArrayAdapter(
            requireContext(),
            androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
            branch
        )

        binding.apply {

            autoCompleteTvBranch.setAdapter(arrayAdapterBranch)

            autoCompleteTvSem.setAdapter(arrayAdapterSem)

            autoCompleteTvSem.setOnItemClickListener { _, _, i, _ ->

                autoCompleteTvClass.setText("")
                autoCompleteTvLab.setText("")

                sem = i + 1

                val classes = Constants.DATA[i].second
                val labs = Constants.DATA[i].third

                val arrayAdapterClass = ArrayAdapter(
                    requireContext(),
                    androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
                    classes
                )

                val arrayAdapterLabs = ArrayAdapter(
                    requireContext(),
                    androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
                    labs
                )

                autoCompleteTvClass.setAdapter(arrayAdapterClass)
                autoCompleteTvLab.setAdapter(arrayAdapterLabs)
            }

            btnUpdate.setOnClickListener {
                viewModel.updateStudent(
                    uid = uid,
                    enrolment = etEnrol.text?.trim().toString(),
                    name = etName.text?.trim().toString(),
                    phone = etPhone.text?.trim().toString(),
                    branch = autoCompleteTvBranch.text?.trim().toString(),
                    sem = sem,
                    lec = autoCompleteTvClass.text?.trim().toString(),
                    lab = autoCompleteTvLab.text?.trim().toString()
                )
            }

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

        viewModel.getStudent(args.uid)

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

        viewModel.removeObservers()

        viewModel.curImageUri.observe(viewLifecycleOwner) {
            binding.tvCaptureImage.isVisible = it == Uri.EMPTY
            if (it == Uri.EMPTY) {
                binding.ivImage.setImageResource(R.drawable.ic_round_person_24)
            } else {
                curImageUri = it
                glide.load(curImageUri).into(binding.ivImage)
            }
        }

        viewModel.userStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )
                snackbar(it)
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
            student = it
            updateUI(student)
        })

        viewModel.updateStudentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )
                when (it) {
                    "emptyEmail" -> {
                        binding.etEmail.error = "Email cannot be empty"
                    }
                    "email" -> {
                        binding.etEmail.error = "Enter a valid email"
                    }
                    "emptyPhone" -> {
                        binding.etPhone.error = "Phone number cannot be empty"
                    }
                    "phone" -> {
                        binding.etPhone.error = "Enter a valid phone number"
                    }
                    "emptyBranch" -> {
                        binding.autoCompleteTvBranch.error = "Please enter branch"
                    }
                    "name" -> {
                        binding.etName.error = "Name cannot be empty"
                    }
                    "sem" -> {
                        binding.autoCompleteTvSem.error = "Please select semester"
                    }
                    "emptyEnrolment" -> {
                        binding.etEnrol.error = "Please enter enrolment number"
                    }
                    "enrolment" -> {
                        binding.etEnrol.error = "Enrolment number should be of length 11"
                    }
                    "emptyLec" -> {
                        binding.autoCompleteTvClass.error = "Please enter your class"
                    }
                    "emptyLab" -> {
                        binding.autoCompleteTvLab.error = "Please enter your lab"
                    }
                    else -> snackbar(it)
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
                etEmail.setText("")
                etName.setText("")
                autoCompleteTvSem.setText("")
                autoCompleteTvLab.setText("")
                autoCompleteTvLab.setText("")
                etEnrol.setText("")
                etPhone.setText("")
            }

            showProgress(
                activity = requireActivity(),
                bool = false,
                parentLayout = binding.parentLayout,
                loading = binding.lottieAnimation
            )
            snackbar("User updated")
            findNavController().navigateUp()
        })
    }

    private fun updateUI(student: Student) {
        binding.apply {
            val decodedByteArray: ByteArray = Base64.decode(student.byteArray, Base64.DEFAULT)
            glide.load(decodedByteArray).into(ivImage)
            etEnrol.setText(student.enrolment)
            etName.setText(student.name)
            etEmail.setText(student.email)
            etEmail.isEnabled = false
            etPhone.setText(student.phone)
            sem = student.sem
            val semesters = requireActivity().resources.getStringArray(R.array.semester)
            val classes = Constants.DATA[sem - 1].second
            val labs = Constants.DATA[sem - 1].third
            val arrayAdapterClass = ArrayAdapter(
                requireContext(),
                androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
                classes
            )
            val arrayAdapterLabs = ArrayAdapter(
                requireContext(),
                androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
                labs
            )
            autoCompleteTvClass.setAdapter(arrayAdapterClass)
            autoCompleteTvLab.setAdapter(arrayAdapterLabs)
            autoCompleteTvSem.setText(semesters[sem - 1].toString(), false)
            autoCompleteTvClass.setText(student.lecture, false)
            autoCompleteTvLab.setText(student.lab, false)
            autoCompleteTvBranch.setText(student.branch, false)
            uid = student.uid
        }
        showProgress(
            activity = requireActivity(),
            bool = false,
            parentLayout = binding.parentLayout,
            loading = binding.lottieAnimation
        )
    }

}