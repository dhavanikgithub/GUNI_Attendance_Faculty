package com.example.guniattendancefaculty.faculty.facultyfragments.ui.qrcode

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentQrCodeBinding
import com.example.guniattendancefaculty.utils.BasicUtils
import com.example.guniattendancefaculty.utils.EventObserver
import com.example.guniattendancefaculty.utils.showProgress
import com.guni.uvpce.moodleapplibrary.model.QRMessageData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class QrCodeFragment : Fragment(R.layout.fragment_qr_code) {

    private lateinit var viewModel: QrCodeViewModel
    private lateinit var binding: FragmentQrCodeBinding
    private var TAG = "QRCodeFragment"

    companion object {
        fun newInstance() = QrCodeFragment()
    }

/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr_code, container, false)
    }*/
    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[QrCodeViewModel::class.java]
        binding = FragmentQrCodeBinding.bind(view)
        MainScope().launch {
            requireArguments().getString("faculty_username")?.let { viewModel.getQRData(requireContext(), it) }
        }
        oberservers()

//        binding.apply {
//
//            MainScope().launch {
//                val facultyData = MoodleConfig.getModelRepo(requireContext()).getFacultyListByCohort()
//                val facultyList = ArrayList<String>()
//                for (i in 0 until facultyData.size)
//                {
//                    facultyList.add(facultyData[i].fullname)
//                }
//                val arrayAdapterGroups = ArrayAdapter(
//                    requireContext(),
//                    androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
//                    facultyList
//                )
//                autoCompleteQR.setAdapter(arrayAdapterGroups)
//                viewModel.fillQRDropDown(requireActivity(),autoCompleteQR,facultiesArrayData)
//            }
//        }

    }

    private fun oberservers(){
        viewModel.mutableQrCodeStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(
                    activity = requireActivity(),
                    bool = false,
                    parentLayout = binding.root,
                    loading = binding.lottieAnimation
                )
            },
            onLoading = {
                showProgress(
                    activity = requireActivity(),
                    bool = true,
                    parentLayout = binding.root,
                    loading = binding.lottieAnimation
                )
            }
        ){
            showProgress(
                activity = requireActivity(),
                bool = false,
                parentLayout = binding.root,
                loading = binding.lottieAnimation
            )
            if(it == null)
            {
                BasicUtils.errorDialogBox(requireContext(),"Error","No Data of QR Code")
                return@EventObserver
            }

            binding.txtQrFaculty.text = getQRText(QRMessageData.getQRMessageObject(it.encodedData)!!)
            BasicUtils.setQrCode(binding.imageView,it.encodedData,400)

            Log.e(TAG, "QR: $it")
        })
    }
    fun getQRText(data:QRMessageData):String{
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val simpleTimeFormat = SimpleDateFormat("hh:mm a")
        return "Course: ${data.courseName} (" +
                "Group: ${data.groupName})\n\n" +
                "Session Date:${simpleDateFormat.format(data.sessionStartDate*1000)}\n" +
                " From:${simpleTimeFormat.format(data.sessionStartDate*1000)}" +
                " To:${simpleTimeFormat.format(data.sessionEndDate*1000)}\n\n"+
                "Attendance Date:${simpleDateFormat.format(data.attendanceStartDate*1000)}\n" +
                " From:${simpleTimeFormat.format(data.attendanceStartDate*1000)}"+
                " To:${simpleTimeFormat.format(data.attendanceEndDate*1000)}\n" +
                "Duration: ${data.attendanceDuration}mins"
    }

}