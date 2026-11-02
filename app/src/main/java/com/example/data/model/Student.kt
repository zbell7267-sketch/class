package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    val rollNumber: String,
    val name: String,
    val department: String,
    val semester: String,
    val email: String,
    val avatarColorHex: String = "#4F46E5",
    val isCurrent: Boolean = false
)
