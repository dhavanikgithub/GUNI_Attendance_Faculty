package com.example.guniattendancefaculty.authorization.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import com.example.guniattendancefaculty.data.entity.Faculty
import com.example.guniattendancefaculty.utils.Constants
import com.example.guniattendancefaculty.utils.Resource
import com.example.guniattendancefaculty.utils.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class DefaultAuthRepository : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val faculties = FirebaseFirestore.getInstance().collection("faculty")

    override suspend fun registerFaculty(
        name: String,
        email: String,
        phone: String,
        pin: String,
        bitmap: Bitmap,
        profilePicUri: Uri
    ): Resource<Faculty> = withContext(Dispatchers.IO) {
        safeCall {
            val result = auth.createUserWithEmailAndPassword(email, pin).await()
            auth.currentUser?.updateProfile(
                userProfileChangeRequest {
                    displayName = name
                    photoUri = profilePicUri
                }
            )

            val baos = ByteArrayOutputStream()

            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                Constants.DST_WIDTH,
                Constants.DST_HEIGHT, true
            )

            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val byteArray: String =
                Base64.encodeToString(
                    baos.toByteArray(),
                    Base64.DEFAULT
                )

            val faculty = Faculty(
                phone = phone,
                email = email,
                uid = result.user!!.uid,
                byteArray = byteArray,
                name = name
            )

            faculties.document(result.user!!.uid).set(faculty).await()
            Resource.Success(faculty)
        }
    }

    override suspend fun login(email: String, pin: String): Resource<AuthResult> =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, pin).await()
                Resource.Success(result)
            }
        }

    override suspend fun loginUser(email: String, pin: String): Resource<String> =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, pin).await()
                val uid = result.user!!.uid
                val faculty = faculties.document(uid).get().await().toObject(Faculty::class.java)
                if (faculty == null) {
                    Resource.Success("student")
                } else {
                    Resource.Success("faculty")
                }
            }
        }
}