package com.example.data.repository

import com.example.data.local.AttendanceDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.ClassroomSession
import com.example.data.model.ScanAttendanceResult
import com.example.data.model.Student
import com.example.data.model.Subject
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceRepository(
    private val attendanceDao: AttendanceDao
) {
    val currentStudent: Flow<Student?> = attendanceDao.getCurrentStudent()
    val allStudents: Flow<List<Student>> = attendanceDao.getAllStudents()
    val allSubjects: Flow<List<Subject>> = attendanceDao.getAllSubjects()
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()

    fun getAttendanceForStudent(rollNumber: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceForStudent(rollNumber)
    }

    fun getTodayRecordsForSubject(subjectCode: String, dateString: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getTodayRecordsForSubject(subjectCode, dateString)
    }

    suspend fun switchActiveStudent(rollNumber: String) {
        attendanceDao.switchActiveStudent(rollNumber)
    }

    suspend fun addStudent(student: Student) {
        attendanceDao.insertStudent(student)
    }

    suspend fun updateStudent(oldRoll: String, updatedStudent: Student) {
        attendanceDao.updateStudentWithCascade(oldRoll, updatedStudent)
    }

    suspend fun removeStudent(rollNumber: String) {
        attendanceDao.removeStudent(rollNumber)
    }

    suspend fun addSubject(subject: Subject) {
        attendanceDao.insertSubjects(listOf(subject))
    }

    suspend fun recordAttendanceFromQr(
        payload: String,
        student: Student,
        isSimulator: Boolean = false
    ): ScanAttendanceResult {
        val session = ClassroomSession.fromQrPayload(payload)
            ?: return ScanAttendanceResult.InvalidQr("Invalid QR Code. Please scan an authorized Classroom QR code.")

        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val currentDateStr = dateFormat.format(Date(now))
        val currentTimeStr = timeFormat.format(Date(now))

        // Check if student has already marked attendance for this subject today
        val existingRecord = attendanceDao.findTodayRecord(
            rollNumber = student.rollNumber,
            subjectCode = session.subjectCode,
            dateString = currentDateStr
        )

        if (existingRecord != null) {
            return ScanAttendanceResult.AlreadyMarked(existingRecord)
        }

        // Insert new attendance record
        val newRecord = AttendanceRecord(
            studentRoll = student.rollNumber,
            studentName = student.name,
            subjectCode = session.subjectCode,
            subjectName = session.subjectName,
            classroom = session.classroom,
            dateString = currentDateStr,
            timeString = currentTimeStr,
            timestamp = now,
            verificationMethod = if (isSimulator) "QR_SIMULATOR" else "CAMERA_QR_SCAN",
            status = "PRESENT"
        )

        attendanceDao.insertAttendance(newRecord)
        attendanceDao.incrementAttended(session.subjectCode)

        return ScanAttendanceResult.Success(newRecord)
    }
}
