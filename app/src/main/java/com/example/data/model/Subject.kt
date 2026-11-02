package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey
    val code: String,
    val name: String,
    val facultyName: String,
    val classroom: String,
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val colorHex: String = "#4F46E5",
    val scheduleTime: String = "09:00 AM - 10:00 AM"
) {
    val attendancePercentage: Float
        get() = if (totalClasses == 0) 100f else (attendedClasses.toFloat() / totalClasses.toFloat()) * 100f

    val isSafeAttendance: Boolean
        get() = attendancePercentage >= 75f
}
