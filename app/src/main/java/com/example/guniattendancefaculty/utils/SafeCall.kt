package com.example.guniattendancefaculty.utils

import android.util.Log

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try{
        action()
    } catch(e: Exception) {
        Log.i("SAFE CALL ERROR:", "$e")
        Resource.Error(e.message ?: "Error: $e")
    }
}