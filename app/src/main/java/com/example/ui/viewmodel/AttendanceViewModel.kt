package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.ClassroomSession
import com.example.data.model.ScanAttendanceResult
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.repository.AttendanceRepository
import com.example.util.QrCodeUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class NavTab {
    DASHBOARD,
    SCAN_QR,
    CLASSROOM_MODE,
    HISTORY,
    PROFILE
}

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AttendanceRepository(database.attendanceDao())
    }

    private val _currentTab = MutableStateFlow(NavTab.DASHBOARD)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    val currentStudent: StateFlow<Student?> = repository.currentStudent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val attendanceHistory: StateFlow<List<AttendanceRecord>> = currentStudent
        .flatMapLatest { student ->
            if (student != null) {
                repository.getAttendanceForStudent(student.rollNumber)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _scanResultDialog = MutableStateFlow<ScanAttendanceResult?>(null)
    val scanResultDialog: StateFlow<ScanAttendanceResult?> = _scanResultDialog.asStateFlow()

    // Classroom / Teacher Mode State
    private val _selectedClassroomSubject = MutableStateFlow<Subject?>(null)
    val selectedClassroomSubject: StateFlow<Subject?> = _selectedClassroomSubject.asStateFlow()

    private val _classroomQrBitmap = MutableStateFlow<Bitmap?>(null)
    val classroomQrBitmap: StateFlow<Bitmap?> = _classroomQrBitmap.asStateFlow()

    private val _activeClassroomSession = MutableStateFlow<ClassroomSession?>(null)
    val activeClassroomSession: StateFlow<ClassroomSession?> = _activeClassroomSession.asStateFlow()

    private val _historyFilterSubject = MutableStateFlow<String?>(null)
    val historyFilterSubject: StateFlow<String?> = _historyFilterSubject.asStateFlow()

    // Today's attendee list for selected classroom session
    @OptIn(ExperimentalCoroutinesApi::class)
    val todayClassroomAttendees: StateFlow<List<AttendanceRecord>> = _activeClassroomSession
        .flatMapLatest { session ->
            if (session != null) {
                repository.getTodayRecordsForSubject(session.subjectCode, session.dateString)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Generate initial classroom session once subjects are loaded
        viewModelScope.launch {
            allSubjects.collect { subjects ->
                if (subjects.isNotEmpty() && _selectedClassroomSubject.value == null) {
                    selectClassroomSubject(subjects.first())
                }
            }
        }
    }

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun selectClassroomSubject(subject: Subject) {
        _selectedClassroomSubject.value = subject
        generateClassroomQr(subject)
    }

    fun refreshClassroomQr() {
        val subject = _selectedClassroomSubject.value ?: return
        generateClassroomQr(subject)
    }

    private fun generateClassroomQr(subject: Subject) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = dateFormat.format(Date())
        val sessionSecret = UUID.randomUUID().toString().take(8).uppercase()

        val session = ClassroomSession(
            subjectCode = subject.code,
            subjectName = subject.name,
            classroom = subject.classroom,
            dateString = todayStr,
            timeSlot = subject.scheduleTime,
            sessionSecret = sessionSecret
        )
        _activeClassroomSession.value = session

        val payload = session.toQrPayload()
        val bitmap = QrCodeUtil.generateQrBitmap(payload, size = 640)
        _classroomQrBitmap.value = bitmap
    }

    fun onQrScanned(payload: String, isSimulator: Boolean = false) {
        val student = currentStudent.value ?: return
        viewModelScope.launch {
            val result = repository.recordAttendanceFromQr(payload, student, isSimulator)
            _scanResultDialog.value = result
            if (result is ScanAttendanceResult.Success) {
                triggerHapticFeedback()
            }
        }
    }

    fun dismissScanResult() {
        _scanResultDialog.value = null
    }

    fun switchActiveStudent(rollNumber: String) {
        viewModelScope.launch {
            repository.switchActiveStudent(rollNumber)
        }
    }

    fun addNewStudent(name: String, rollNumber: String, department: String, semester: String) {
        viewModelScope.launch {
            val trimmedRoll = rollNumber.trim().uppercase()
            val newStudent = Student(
                rollNumber = trimmedRoll,
                name = name.trim(),
                department = department.trim(),
                semester = semester.trim(),
                email = "${trimmedRoll.lowercase().replace("-", ".")}@gggc.edu",
                avatarColorHex = listOf("#4F46E5", "#06B6D4", "#10B981", "#F59E0B", "#EC4899", "#8B5CF6").random(),
                isCurrent = true
            )
            repository.addStudent(newStudent)
            repository.switchActiveStudent(newStudent.rollNumber)
        }
    }

    fun updateStudentProfile(
        oldRoll: String,
        name: String,
        rollNumber: String,
        department: String,
        semester: String,
        email: String,
        avatarColorHex: String
    ) {
        viewModelScope.launch {
            val isCurrent = currentStudent.value?.rollNumber == oldRoll
            val updated = Student(
                rollNumber = rollNumber.trim().uppercase(),
                name = name.trim(),
                department = department.trim(),
                semester = semester.trim(),
                email = email.trim(),
                avatarColorHex = avatarColorHex,
                isCurrent = isCurrent
            )
            repository.updateStudent(oldRoll, updated)
            if (isCurrent) {
                repository.switchActiveStudent(updated.rollNumber)
            }
        }
    }

    fun deleteStudentProfile(rollNumber: String) {
        viewModelScope.launch {
            repository.removeStudent(rollNumber)
        }
    }

    fun setHistoryFilter(subjectCode: String?) {
        _historyFilterSubject.value = subjectCode
    }

    private fun triggerHapticFeedback() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(150)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibrator unavailable in emulator
        }
    }
}
