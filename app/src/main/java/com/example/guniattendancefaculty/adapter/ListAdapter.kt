package com.example.guniattendancefaculty.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.guniattendancefaculty.databinding.ItemListBinding
import com.google.android.material.imageview.ShapeableImageView
import com.guni.uvpce.moodleapplibrary.model.MoodleSession
import com.guni.uvpce.moodleapplibrary.model.MoodleSessionAttendanceLog
import com.guni.uvpce.moodleapplibrary.model.MoodleUserInfo
import java.net.URL
import javax.inject.Inject

class SessionUserInfo(val moodleUserInfo: MoodleUserInfo, val session: MoodleSession){

    val TAG = "SessionUserInfo"
    private var attendance:String = ""
    fun getAttendanceInfo():String{
        if(attendance.isNotEmpty()){
            return attendance
        }
        //Log.i(TAG, "getAttendanceInfo: Count:${session.attendanceLog.size} UserInfo id:${moodleUserInfo.toString()}")
        val obj: MoodleSessionAttendanceLog = session.attendanceLog.find { it.studentId == moodleUserInfo.id }
            ?: return ""

        //Log.i(TAG, "getAttendanceInfo: UserInfo id:${moodleUserInfo.id} AttendanceLog = $obj")
        val obj1 = session.statusList.find { it.id == obj.statusId } ?: return ""
        //Log.i(TAG, "getAttendanceInfo: UserInfo id:${moodleUserInfo.id} AttendanceLog Status= $obj1")
        attendance = obj1.description
        return attendance
    }
}
class ListAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<SessionUserInfo>() {
        override fun areItemsTheSame(oldItem: SessionUserInfo, newItem: SessionUserInfo): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: SessionUserInfo, newItem: SessionUserInfo): Boolean {
            return oldItem.moodleUserInfo.id == newItem.moodleUserInfo.id
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var students: List<SessionUserInfo>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ListViewHolder(binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val ivProfilePic: ShapeableImageView = binding.ivProfilePictureList
        val tvEnrolNo: TextView = binding.tvEnrolNoList
        val tvName: TextView = binding.tvName
        val tvAttendance: TextView = binding.tvAttendance
        val ivEdit: AppCompatImageButton = binding.ivEdit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    private fun convertUrlToBitmap(url: String): Bitmap? {
        val newurl: URL
        var bitmap: Bitmap? = null
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            newurl = URL(url)
            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val student = students[position]
        holder.apply {
            if(student.moodleUserInfo.userImage == null){
                student.moodleUserInfo.userImage = convertUrlToBitmap(student.moodleUserInfo.imageUrl)
            }
            if(student.moodleUserInfo.userImage != null){
                //val decodedByteArray: ByteArray = Utility().convertBitmapToBase64(student.userImage!!).toByteArray()
                glide.load(student.moodleUserInfo.userImage).into(ivProfilePic)
            }


            tvEnrolNo.text = "Enrolment No.: ${student.moodleUserInfo.username}"
            tvName.text = "Name: ${student.moodleUserInfo.lastname}"
            tvAttendance.text = "${student.getAttendanceInfo()}"
            if(tvAttendance.text == "Absent"){
                tvAttendance.setTextColor(Color.RED)
            }
            else{
                tvAttendance.setTextColor(Color.GREEN)
            }
            itemView.setOnClickListener {
                onStudentClickListener?.let { click ->
                    click(student)
                }
            }

            ivEdit.setOnClickListener {
                onEditClickListener?.let { click ->
                    click(student)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return students.size
    }

    private var onStudentClickListener: ((SessionUserInfo) -> Unit)? = null

    fun setOnStudentClickListener(listener: (SessionUserInfo) -> Unit) {
        onStudentClickListener = listener
    }

    private var onEditClickListener: ((SessionUserInfo) -> Unit)? = null

    fun setOnEditClickListener(listener: (SessionUserInfo) -> Unit) {
        onEditClickListener = listener
    }
}