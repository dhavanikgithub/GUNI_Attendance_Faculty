package com.example.guniattendancefaculty.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.guni.uvpce.moodleapplibrary.util.Utility
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class BasicUtils {

    companion object{

        fun errorDialogBox(
            context: Context,
            title: String,
            message: String
        ){
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle(title)
            alertDialog.setMessage(message)
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, _ -> dialog.dismiss()
                })

            alertDialog.create().show()
        }

        fun getMillis(date:Int, month:Int, year:Int,hour:Int, min:Int):Long{
            val setCalc = Calendar.getInstance()
            setCalc[Calendar.DATE] = date
            setCalc[Calendar.MONTH] = month
            setCalc[Calendar.YEAR] = year
            setCalc[Calendar.HOUR_OF_DAY] = hour
            setCalc[Calendar.MINUTE] = min
            setCalc[Calendar.SECOND] = 0
            return setCalc.timeInMillis
        }
        fun getSeconds(date:Int, month:Int, year:Int,hour:Int, min:Int):Long{
            return getMillis(date,month,year,hour,min)/1000
        }
        fun getSecondTime(d:Int,m:Int,y:Int,str1:String):Long{
            val str = str1.split(":")
            if(str.size != 2)
                return 0
            val r = Utility().getSeconds(d,m,y,str[0].toInt(),str[1].toInt())
            Log.i("TAG", "getSecondTime: $r")
            return r
        }

        fun getDate(milliSeconds: Long, dateFormat: String): String? {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        fun qrGenerateAndDisplay(qrImg: ImageView, qrData: String){
            val writer = QRCodeWriter()
            val size = 300
            try{
                val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, size, size)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                    }
                }
                qrImg.setImageBitmap(bmp)
            }
            catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        fun setQrCode(qrImg: ImageView,content:String,size:Int) {
            try {
                val bitmap = content.encodeAsQrCodeBitmap(
                    size.dpToPx()
                )
                bitmap?.let { qrImg.setImageBitmap(it) } // replace imageView with your view

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        fun showTimerDialog(context:Context,
                            layoutInflater:LayoutInflater, title:String,
                            confirmOnClick:(d:Int,m:Int,y:Int,description:String,
                                            selectedStartTime:Long,selectedEndTime:Long,
                                            duration:Long,isError:Boolean,ErrorText:String)->Boolean,
                            needDescription:Boolean = true
        ){
            var d by Delegates.notNull<Int>()
            var m by Delegates.notNull<Int>()
            var y by Delegates.notNull<Int>()
            val currentDateTime = Calendar.getInstance()
            val startYear = currentDateTime.get(Calendar.YEAR)
            val startMonth = currentDateTime.get(Calendar.MONTH)
            val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

            d = startDay
            m = startMonth
            y = startYear

            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            val simpleTimeFormat = SimpleDateFormat("hh:mm a")

            var isStartTimeSelected = false
            var isEndTimeSelected = false

            var selectedStartTime = ""
            var selectedEndTime = ""

            val alertDialog = AlertDialog.Builder(context)

            val customLayout = layoutInflater.inflate(com.example.guniattendancefaculty.R.layout.fragment_session_dialog, null)
            val startSelectTime: Button = customLayout.findViewById(com.example.guniattendancefaculty.R.id.start_select_time)

            val endSelectTime: Button = customLayout.findViewById(com.example.guniattendancefaculty.R.id.end_select_time)
            val selectedDate: Button = customLayout.findViewById(com.example.guniattendancefaculty.R.id.select_date)
            val descEditText: EditText = customLayout.findViewById(com.example.guniattendancefaculty.R.id.input_description)
            if(!needDescription){
                descEditText.visibility = View.GONE
            }
            else{
                descEditText.visibility = View.VISIBLE
            }
            customLayout.findViewById<TextView>(com.example.guniattendancefaculty.R.id.title).text =
                title +"\n Date:${simpleDateFormat.format(currentDateTime.timeInMillis)}"
            alertDialog.setView(customLayout)

            startSelectTime.setOnClickListener {
                pickTime(context,com.example.guniattendancefaculty.R.id.start_select_time,
                    customLayout
                ) {
                    isStartTimeSelected = true
                    selectedStartTime = it
                }
            }

            endSelectTime.setOnClickListener {
                pickTime(context, com.example.guniattendancefaculty.R.id.end_select_time,
                    customLayout
                ) {
                    isEndTimeSelected = true
                    selectedEndTime = it
                }
            }

            selectedDate.setOnClickListener{
                DatePickerDialog(context, { _, year, month, day ->
                    selectedDate.text = "${day}/${month+1}/${year}"
                    d = day
                    m = month
                    y = year
                }, startYear, startMonth, startDay).show()

                //isDateSelected = true
            }

            alertDialog.setPositiveButton("CONFIRM"){
                    dialog, _ ->
                run {
                        var isError = false
                        var errorText = ""
                        var startTime:Long = -1
                        var endTime:Long = -1
                        var duration:Long = -1
                        if(isStartTimeSelected && isEndTimeSelected){
                        startTime = getSecondTime(d, m, y, selectedStartTime)
                        endTime = getSecondTime(d, m, y, selectedEndTime)
                        duration = endTime - startTime

                        if(duration <= 0){
                            isError = true
                            errorText = "Please select End Time greater than Start Time!"
                        }
                    }
                    else {
                        isError = true
                        errorText = "Please select Start Time and End Time!"
                        // snack()
                    }
                    if (confirmOnClick(d,m,y,descEditText.text.toString(),startTime,endTime,duration,isError,errorText))
                        dialog.dismiss()
                }
            }
            alertDialog.setNegativeButton("CANCEL"){
                    dialog, _ ->
                dialog.dismiss()
            }

            alertDialog.create()
            alertDialog.show()
        }

        fun isDateWithinOneWeek(inputDate: String): Boolean {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val currentDate = Date()
            val calendar = Calendar.getInstance()

            try {
                val parsedDate = dateFormat.parse(inputDate)
                calendar.time = parsedDate

                // Calculate the difference between the input date and the current date in days
                val differenceInDays = ((calendar.timeInMillis - currentDate.time) / (24 * 60 * 60 * 1000)).toInt()

                // Check if the difference is within 7 days
                return differenceInDays in 0..6
            } catch (e: Exception) {
                // Handle parsing errors
                return false
            }
        }

        private fun pickTime(context: Context, buttonResId:Int,customLayout:View,selectedTime:(String)->Unit){
            val currentDateTime = Calendar.getInstance()
            val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
            val startMinute = currentDateTime.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(context,
                { _, hour, minute ->
                    val stime = "$hour:$minute"
                    selectedTime(stime)
                    customLayout.findViewById<Button>(buttonResId).text = stime
                }, startHour, startMinute, false
            )
            timePickerDialog.show()
        }
    }
}