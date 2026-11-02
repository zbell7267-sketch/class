package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentRoll: String,
    val studentName: String,
    val subjectCode: String,
    val subjectName: String,
    val classroom: String,
    val dateString: String,      // YYYY-MM-DD
    val timeString: String,      // HH:mm a
    val timestamp: Long,
    val verificationMethod: String = "QR_CODE", // QR_CODE or SIMULATOR
    val status: String = "PRESENT"
)
