package com.example.guniattendancefaculty.di

import com.example.guniattendancefaculty.faculty.repository.DefaultFacultyRepository
import com.example.guniattendancefaculty.faculty.repository.FacultyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FacultyModule {

    @Singleton
    @Provides
    fun provideFacultyRepository() = DefaultFacultyRepository() as FacultyRepository

}