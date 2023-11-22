package com.example.guniattendancefaculty.faculty.facultyfragments.ui.studentlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.adapter.ListAdapter
import com.example.guniattendancefaculty.databinding.FragmentStudentListBinding
import com.example.guniattendancefaculty.faculty.facultyfragments.ui.enableattendance.EnableAttendanceFragment
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.snackbar
import com.guni.uvpce.moodleapplibrary.model.MoodleSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudentListFragment : Fragment(R.layout.fragment_student_list) {

    @Inject
    lateinit var listAdapter: ListAdapter

    private lateinit var viewModel: StudentListViewModel
    private lateinit var binding: FragmentStudentListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[StudentListViewModel::class.java]
        subscribeToObserve()
        binding = FragmentStudentListBinding.bind(view)
        setUpRecyclerView()
        requireArguments().getString("session").let {
            MoodleSession.fromJsonObject(it!!)
        }.let {
            EnableAttendanceFragment.selectedSession?.let { it1 ->
                viewModel.getStudentsList(requireContext(),
                    it1
                )
            }
        }
        /*listAdapter.setOnStudentClickListener {

        }*/
        /*listAdapter.setOnEditClickListener {
            findNavController().navigate(
                StudentListFragmentDirections
                    .actionStudentListFragmentToEditStudentFragment(uid = it.uid)
            )
        }*/
    }

    private fun setUpRecyclerView() {
        binding.rvListStudent.apply {
            adapter = listAdapter
        }
    }

    private fun subscribeToObserve() {
        viewModel.list.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.lottieAnimation.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.lottieAnimation.isVisible = true
                listAdapter.students = listOf()
            }
        ) { students ->
            listAdapter.students = students
            binding.lottieAnimation.isVisible = false
        })
    }
}