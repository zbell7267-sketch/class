package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.Student
import com.example.data.model.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [Student::class, Subject::class, AttendanceRecord::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gggc_college_attendance.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.attendanceDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: AttendanceDao) {
                // Initial Students of Government Graduate Girls College
                val students = listOf(
                    Student(
                        rollNumber = "GGGC-21-CS-042",
                        name = "Ayesha Khan",
                        department = "BS Computer Science",
                        semester = "Semester 6",
                        email = "ayesha.khan@gggc.edu",
                        avatarColorHex = "#4F46E5",
                        isCurrent = true
                    ),
                    Student(
                        rollNumber = "GGGC-21-IT-019",
                        name = "Fatima Zahra",
                        department = "BS Information Technology",
                        semester = "Semester 6",
                        email = "fatima.zahra@gggc.edu",
                        avatarColorHex = "#06B6D4",
                        isCurrent = false
                    ),
                    Student(
                        rollNumber = "GGGC-21-DS-088",
                        name = "Zainab Tariq",
                        department = "BS Data Science",
                        semester = "Semester 6",
                        email = "zainab.tariq@gggc.edu",
                        avatarColorHex = "#10B981",
                        isCurrent = false
                    ),
                    Student(
                        rollNumber = "GGGC-21-SE-005",
                        name = "Maryam Noor",
                        department = "BS Software Engineering",
                        semester = "Semester 6",
                        email = "maryam.noor@gggc.edu",
                        avatarColorHex = "#EC4899",
                        isCurrent = false
                    ),
                    Student(
                        rollNumber = "GGGC-21-MATH-014",
                        name = "Sana Ahmed",
                        department = "BS Mathematics",
                        semester = "Semester 6",
                        email = "sana.ahmed@gggc.edu",
                        avatarColorHex = "#8B5CF6",
                        isCurrent = false
                    )
                )
                dao.insertStudents(students)

                // Initial Subjects taught at Government Graduate Girls College
                val subjects = listOf(
                    Subject(
                        code = "CS301",
                        name = "Computer Networks",
                        facultyName = "Dr. Samina Malik",
                        classroom = "Lab 1 - CS Block",
                        totalClasses = 24,
                        attendedClasses = 21,
                        colorHex = "#4F46E5",
                        scheduleTime = "09:00 AM - 10:00 AM"
                    ),
                    Subject(
                        code = "CS302",
                        name = "Database Systems",
                        facultyName = "Prof. Tahira Parveen",
                        classroom = "Hall 2 - Science Wing",
                        totalClasses = 22,
                        attendedClasses = 19,
                        colorHex = "#06B6D4",
                        scheduleTime = "10:15 AM - 11:15 AM"
                    ),
                    Subject(
                        code = "CS303",
                        name = "Operating Systems",
                        facultyName = "Dr. Nargis Bano",
                        classroom = "Lecture Hall A",
                        totalClasses = 26,
                        attendedClasses = 22,
                        colorHex = "#10B981",
                        scheduleTime = "11:30 AM - 12:30 PM"
                    ),
                    Subject(
                        code = "CS304",
                        name = "Artificial Intelligence",
                        facultyName = "Prof. Rehana Kausar",
                        classroom = "Smart Room 3 - Tech Wing",
                        totalClasses = 20,
                        attendedClasses = 16,
                        colorHex = "#F59E0B",
                        scheduleTime = "02:00 PM - 03:00 PM"
                    ),
                    Subject(
                        code = "CS305",
                        name = "Software Engineering",
                        facultyName = "Dr. Bushra Jabeen",
                        classroom = "Seminar Room 1",
                        totalClasses = 18,
                        attendedClasses = 15,
                        colorHex = "#EC4899",
                        scheduleTime = "03:15 PM - 04:15 PM"
                    )
                )
                dao.insertSubjects(subjects)

                // Populate past attendance records for Ayesha Khan
                val now = System.currentTimeMillis()
                val oneDay = 86400000L
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

                val pastDates = listOf(
                    now - 3 * oneDay,
                    now - 2 * oneDay,
                    now - 1 * oneDay
                )

                pastDates.forEach { timestamp ->
                    val d = Date(timestamp)
                    val dateStr = dateFormat.format(d)
                    dao.insertAttendance(
                        AttendanceRecord(
                            studentRoll = "GGGC-21-CS-042",
                            studentName = "Ayesha Khan",
                            subjectCode = "CS301",
                            subjectName = "Computer Networks",
                            classroom = "Lab 1 - CS Block",
                            dateString = dateStr,
                            timeString = timeFormat.format(d),
                            timestamp = timestamp,
                            verificationMethod = "QR_CODE",
                            status = "PRESENT"
                        )
                    )
                    dao.insertAttendance(
                        AttendanceRecord(
                            studentRoll = "GGGC-21-CS-042",
                            studentName = "Ayesha Khan",
                            subjectCode = "CS303",
                            subjectName = "Operating Systems",
                            classroom = "Lecture Hall A",
                            dateString = dateStr,
                            timeString = "11:34 AM",
                            timestamp = timestamp + 7200000L,
                            verificationMethod = "QR_CODE",
                            status = "PRESENT"
                        )
                    )
                }
            }
        }
    }
}
