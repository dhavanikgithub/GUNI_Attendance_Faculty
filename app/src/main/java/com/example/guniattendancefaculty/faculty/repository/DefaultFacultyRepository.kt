package com.example.guniattendancefaculty.faculty.repository

import android.util.Log
import com.example.guniattendancefaculty.data.entity.*
import com.example.guniattendancefaculty.utils.Resource
import com.example.guniattendancefaculty.utils.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultFacultyRepository : FacultyRepository {

    private val auth = FirebaseAuth.getInstance()
    private val facultyData = FirebaseFirestore.getInstance().collection("FacultyData")
    override suspend fun getQRCodes(facultyName: String): Resource<QRCode?> =
        withContext(Dispatchers.IO) {
        safeCall {
            val res = facultyData.document(facultyName)
                .get()
                .await()
                .toObject(QRCode::class.java)
            Resource.Success(res)
        }
    }



    override suspend fun storeFacDataToFirebase(
        userID: Int,
        facultyName: String,
        encodedData: String
    ): Resource<FacultyData> = withContext(Dispatchers.IO) {
        safeCall {
            val fData = FacultyData(
                userID = userID,
                facultyName = facultyName,
                encodedData = encodedData
            )

            facultyData.document(facultyName).set(fData).await()
            Resource.Success(fData)
        }
    }
    override suspend fun getFacultyQRList(): Resource<List<QRCode>> =
        withContext(Dispatchers.IO) {
            safeCall {
                var facultyList = listOf<QRCode>()
                try {
                    facultyList = facultyData
                        /*.whereEqualTo("sem", sem)
                        .whereEqualTo("lab", lab)
                        .orderBy("enrolment", Query.Direction.ASCENDING)*/
                        .get()
                        .await()
                        .toObjects(QRCode::class.java)
                } catch (e: Exception) {
                    Log.d("TAG_STUDENT_LIST", "getFacultyQRList: ${e.message}")
                }

                Resource.Success(facultyList)
            }
        }
}