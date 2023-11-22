package com.example.guniattendancefaculty.faculty.facultyfragments.ui.facultyhome


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.data.entity.Faculty
import com.example.guniattendancefaculty.databinding.FragmentFacultyHomeBinding
import com.example.guniattendancefaculty.faculty.facultyfragments.ui.enableattendance.EnableAttendanceFragment
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.showProgress
import com.example.guniattendancefaculty.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FacultyHomeFragment : Fragment(R.layout.fragment_faculty_home) {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var binding: FragmentFacultyHomeBinding
    private lateinit var viewModel: FacultyHomeViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[FacultyHomeViewModel::class.java]
        subscribeToObserve()

        binding = FragmentFacultyHomeBinding.bind(view)

        val selfFragment = EnableAttendanceFragment()


        setCurrentFragment(selfFragment)

        binding.apply {



        }
        viewModel.getUser()
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().apply {
            this.replace(R.id.flFragment, fragment)
            this.commit()
        }
    }
    private fun subscribeToObserve() {
        viewModel.userStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { error ->
                showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )
                snackbar(error)
            },
            onLoading = {
                showProgress(
                    activity = requireActivity(),
                    bool = true,
                    parentLayout = binding.parentLayout,
                    loading = binding.lottieAnimation
                )
            }
        ) { user ->
            updateUI(user)
        })
    }

    //
    private fun updateUI(user: Faculty) {
        binding.apply {

        }
        showProgress(
            activity = requireActivity(),
            bool = false,
            parentLayout = binding.parentLayout,
            loading = binding.lottieAnimation
        )
    }

}