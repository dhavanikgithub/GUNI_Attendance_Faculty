package com.example.guniattendancefaculty.faculty.facultyfragments.ui.addeditclass

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.adapter.ClassAdapter
import com.example.guniattendancefaculty.databinding.FragmentAddEditClassesBinding
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditClassFragment : Fragment(R.layout.fragment_add_edit_classes) {

    private val classAdapter = ClassAdapter()
    private lateinit var viewModel: AddEditClassViewModel
    private lateinit var binding: FragmentAddEditClassesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[AddEditClassViewModel::class.java]
        subscribeToObserve()

        binding = FragmentAddEditClassesBinding.bind(view)

        setUpRecyclerView()

        binding.apply {

            btnDeleteAll.setOnClickListener {

            }

            fabAddClass.setOnClickListener {

            }

            rvListClass.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 10 && fabAddClass.isShown) {
                        fabAddClass.hide()
                    }
                    if (dy < -10 && !fabAddClass.isShown) {
                        fabAddClass.show()
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        fabAddClass.show()
                    }
                }
            })
        }

    }

    private fun setUpRecyclerView() {
        binding.rvListClass.apply {
            adapter = classAdapter
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
                classAdapter.classes = listOf()
            }
        ) { classes ->
            classAdapter.classes = classes
            binding.lottieAnimation.isVisible = false
        })
    }

}