package com.example.guniattendancefaculty.authorization.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.guniattendancefaculty.data.entity.Faculty
import com.example.guniattendancefaculty.utils.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepository {

    suspend fun registerFaculty(
        name: String,
        email: String,
        phone: String,
        pin: String,
        bitmap: Bitmap,
        profilePicUri: Uri
    ): Resource<Faculty>

    suspend fun login(
        email: String,
        pin: String
    ): Resource<AuthResult>

    suspend fun loginUser(
        email: String,
        pin: String
    ): Resource<String>

}