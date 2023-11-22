package com.example.guniattendancefaculty.faculty.repository

import android.graphics.Bitmap
import com.example.guniattendancefaculty.data.entity.*
import com.example.guniattendancefaculty.utils.Resource

interface FacultyRepository {

    suspend fun storeFacDataToFirebase(
        userID: Int,
        facultyName: String,
        encodedData: String
    ): Resource<FacultyData>
    suspend fun getFacultyQRList(): Resource<List<QRCode>>
    suspend fun getQRCodes(facultyName: String): Resource<QRCode?>
}