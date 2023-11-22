package com.example.guniattendancefaculty.data.entity

data class QRCode(
    var encodedData: String = "",
    var facultyName: String = "",
    var userID: Int = 0
)