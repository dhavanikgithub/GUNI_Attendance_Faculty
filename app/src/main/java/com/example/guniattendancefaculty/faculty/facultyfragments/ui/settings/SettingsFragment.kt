package com.example.guniattendancefaculty.faculty.facultyfragments.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.guniattendancefaculty.R
import com.example.guniattendancefaculty.databinding.FragmentSettingsBinding
import com.example.guniattendancefaculty.moodle.MoodleConfig
import com.example.guniattendancefaculty.utils.CustomProgressDialog
import com.example.guniattendancefaculty.utils.showProgress
import com.example.guniattendancefaculty.utils.snackbar
import com.guni.uvpce.moodleapplibrary.model.MoodleBasicUrl
import com.guni.uvpce.moodleapplibrary.repo.ModelRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    var selectedURL:String?=null
    private val TAG = "SettingFragment"
    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var progressDialog:CustomProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Application Settings"

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        progressDialog= CustomProgressDialog(requireContext(),requireActivity())

        val savedURLPref: SharedPreferences = requireActivity().getSharedPreferences("savedURL", 0)
        val savedURLEditor: SharedPreferences.Editor = savedURLPref.edit()

        val checkboxTogglePref: SharedPreferences = requireActivity().getSharedPreferences("buttonToggle", 0)
        val checkboxToggleEditor: SharedPreferences.Editor = checkboxTogglePref.edit()

        lateinit var urlList:List<MoodleBasicUrl>

        binding.apply {
            s1UrlList.keyListener=null
            MainScope().launch{
                requireActivity().runOnUiThread {
                    progressDialog.start("Please wait URL is fetching...")
                }

                urlList = MoodleConfig.getMoodleUrlList(requireActivity())

                val urlStringArrayList=ArrayList<String>()

                for(item in urlList)
                {
                    urlStringArrayList.add(item.url)
                }
                try{
                    requireActivity().runOnUiThread {
                        val checkboxCheck = checkboxTogglePref.getBoolean("buttonToggle", true)
                        toggleUrlDialog.isChecked = checkboxCheck

                        val adapter = ArrayAdapter(
                            requireActivity(),
                            R.layout.url_list_spinner_item,
                            urlStringArrayList
                        )
                        adapter.setDropDownViewResource(R.layout.url_list_spinner_item)
                        s1UrlList.setAdapter(adapter)
                        s1UrlList.setText(urlStringArrayList[0],false)
                        //val savedURL = ModelRepository.getStoredMoodleUrl(requireActivity()).url

                        if (savedURLPref.contains("url")) {
                            val setUrl = savedURLPref.getString("url", null)
                            if (setUrl != null) {
                                s1UrlList.setText(setUrl,false)
                                progressDialog.stop()
                            }
                            progressDialog.stop()
                        }
                        progressDialog.stop()
                        selectedURL = s1UrlList.text.toString()
                    }
                }
                catch (ex:Exception)
                {
                    snackbar(ex.message.toString())
                }


            }
            s1UrlList.setOnItemClickListener { parent, view, position, id ->
                selectedURL = parent.getItemAtPosition(position).toString()
            }
            saveBtn.setOnClickListener {
                progressDialog.start("")
//                showProgress(activity = requireActivity(), bool = true, parentLayout = parentLayout, loading = lottieAnimation)
                if(!toggleUrlDialog.isChecked){
                    checkboxToggleEditor.putBoolean("buttonToggle", toggleUrlDialog.isChecked)
                    checkboxToggleEditor.apply()
                }else {
                    checkboxToggleEditor.putBoolean("buttonToggle", toggleUrlDialog.isChecked)
                    checkboxToggleEditor.apply()
                }

                if(selectedURL != null)
                {
                    for (i in urlList.indices)
                    {
                        if(urlList[i].url==selectedURL)
                        {
                            try{
                                ModelRepository.setMoodleUrlSetting(requireActivity(), urlList[i])
                                savedURLEditor.putString("url", selectedURL)
                                savedURLEditor.apply()
                                progressDialog.stop()
//                                showProgress(activity = requireActivity(), bool = false, parentLayout = parentLayout, loading = lottieAnimation)
                                findNavController().popBackStack()
                            }
                            catch (ex:Exception)
                            {
                                snackbar(ex.message.toString())
                            }
                            break
                        }
                    }

                }
                else{
                    progressDialog.stop()
//                    showProgress(activity = requireActivity(), bool = false, parentLayout = parentLayout, loading = lottieAnimation)
                    snackbar("URL not selected")
                }
            }
        }
    }

}