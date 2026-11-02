package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import com.example.data.model.Student
import com.example.data.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    // --- Students ---
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentStudent(): Flow<Student?>

    @Query("SELECT * FROM students WHERE rollNumber = :rollNumber LIMIT 1")
    suspend fun getStudentByRoll(rollNumber: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE rollNumber = :rollNumber")
    suspend fun deleteStudentByRoll(rollNumber: String)

    @Query("UPDATE students SET isCurrent = 0")
    suspend fun clearCurrentStudent()

    @Query("UPDATE students SET isCurrent = 1 WHERE rollNumber = :rollNumber")
    suspend fun setCurrentStudent(rollNumber: String)

    @Transaction
    suspend fun switchActiveStudent(rollNumber: String) {
        clearCurrentStudent()
        setCurrentStudent(rollNumber)
    }

    @Query("UPDATE attendance_records SET studentRoll = :newRoll, studentName = :newName WHERE studentRoll = :oldRoll")
    suspend fun updateAttendanceStudentInfo(oldRoll: String, newRoll: String, newName: String)

    @Transaction
    suspend fun updateStudentWithCascade(oldRoll: String, updatedStudent: Student) {
        if (oldRoll != updatedStudent.rollNumber) {
            deleteStudentByRoll(oldRoll)
            insertStudent(updatedStudent)
            updateAttendanceStudentInfo(oldRoll, updatedStudent.rollNumber, updatedStudent.name)
        } else {
            updateStudent(updatedStudent)
            updateAttendanceStudentInfo(oldRoll, updatedStudent.rollNumber, updatedStudent.name)
        }
    }

    @Query("DELETE FROM attendance_records WHERE studentRoll = :rollNumber")
    suspend fun deleteAttendanceForStudent(rollNumber: String)

    @Transaction
    suspend fun removeStudent(rollNumber: String) {
        deleteStudentByRoll(rollNumber)
        deleteAttendanceForStudent(rollNumber)
        // If the removed student was current, set another student as current
        val remaining = getAnyStudent()
        remaining?.let { setCurrentStudent(it.rollNumber) }
    }

    @Query("SELECT * FROM students LIMIT 1")
    suspend fun getAnyStudent(): Student?

    // --- Subjects ---
    @Query("SELECT * FROM subjects ORDER BY code ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE code = :code LIMIT 1")
    suspend fun getSubject(code: String): Subject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("UPDATE subjects SET attendedClasses = attendedClasses + 1 WHERE code = :code")
    suspend fun incrementAttended(code: String)

    // --- Attendance Records ---
    @Query("SELECT * FROM attendance_records WHERE studentRoll = :rollNumber ORDER BY timestamp DESC")
    fun getAttendanceForStudent(rollNumber: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records ORDER BY timestamp DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE studentRoll = :rollNumber AND subjectCode = :subjectCode AND dateString = :dateString LIMIT 1")
    suspend fun findTodayRecord(rollNumber: String, subjectCode: String, dateString: String): AttendanceRecord?

    @Query("SELECT * FROM attendance_records WHERE subjectCode = :subjectCode AND dateString = :dateString ORDER BY timestamp DESC")
    fun getTodayRecordsForSubject(subjectCode: String, dateString: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long
}
