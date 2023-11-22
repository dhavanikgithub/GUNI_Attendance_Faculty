package com.example.guniattendancefaculty.data.entity

data class Classes(
    val uid: String = "",
    val className: String = "",
    val createdBy: String = "",
    val sheetUrl: String = "",
    val sheetRowNumber: Int = 0,
    val sheetColNumber: Int = 0,
    val type: String = ""
)
