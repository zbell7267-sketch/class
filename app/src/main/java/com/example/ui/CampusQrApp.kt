package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ClassroomSession
import com.example.ui.components.AttendanceResultDialog
import com.example.ui.screens.ClassroomQrScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScanQrScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoDark
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import com.example.ui.viewmodel.AttendanceViewModel
import com.example.ui.viewmodel.NavTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CampusQrApp(
    viewModel: AttendanceViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val attendanceHistory by viewModel.attendanceHistory.collectAsStateWithLifecycle()
    val scanResultDialog by viewModel.scanResultDialog.collectAsStateWithLifecycle()

    val selectedClassroomSubject by viewModel.selectedClassroomSubject.collectAsStateWithLifecycle()
    val classroomQrBitmap by viewModel.classroomQrBitmap.collectAsStateWithLifecycle()
    val activeClassroomSession by viewModel.activeClassroomSession.collectAsStateWithLifecycle()
    val todayClassroomAttendees by viewModel.todayClassroomAttendees.collectAsStateWithLifecycle()
    val historyFilterSubject by viewModel.historyFilterSubject.collectAsStateWithLifecycle()

    // Back button handling: return to Dashboard from any secondary tab
    BackHandler(enabled = currentTab != NavTab.DASHBOARD) {
        viewModel.selectTab(NavTab.DASHBOARD)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // Keep bottom navigation visible on all tabs except during active camera scan
            if (currentTab != NavTab.SCAN_QR) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == NavTab.DASHBOARD,
                        onClick = { viewModel.selectTab(NavTab.DASHBOARD) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == NavTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_tab_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.SCAN_QR,
                        onClick = { viewModel.selectTab(NavTab.SCAN_QR) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == NavTab.SCAN_QR) Icons.Filled.QrCodeScanner else Icons.Outlined.QrCodeScanner,
                                contentDescription = "Scan QR"
                            )
                        },
                        label = { Text("Scan QR", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_tab_scan")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.CLASSROOM_MODE,
                        onClick = { viewModel.selectTab(NavTab.CLASSROOM_MODE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == NavTab.CLASSROOM_MODE) Icons.Filled.QrCode else Icons.Outlined.QrCode,
                                contentDescription = "Classroom Mode"
                            )
                        },
                        label = { Text("Classroom", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_tab_classroom")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.HISTORY,
                        onClick = { viewModel.selectTab(NavTab.HISTORY) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == NavTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                                contentDescription = "History"
                            )
                        },
                        label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_tab_history")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.PROFILE,
                        onClick = { viewModel.selectTab(NavTab.PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == NavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    NavTab.DASHBOARD -> {
                        HomeScreen(
                            student = currentStudent,
                            subjects = allSubjects,
                            attendanceHistory = attendanceHistory,
                            onNavigateToScan = { viewModel.selectTab(NavTab.SCAN_QR) },
                            onNavigateToClassroomMode = { viewModel.selectTab(NavTab.CLASSROOM_MODE) },
                            onQuickScanSubject = { subject ->
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                val todayDateStr = dateFormat.format(Date())
                                val session = ClassroomSession(
                                    subjectCode = subject.code,
                                    subjectName = subject.name,
                                    classroom = subject.classroom,
                                    dateString = todayDateStr,
                                    timeSlot = subject.scheduleTime,
                                    sessionSecret = "ROOM_SEC_${subject.code}"
                                )
                                viewModel.onQrScanned(session.toQrPayload(), isSimulator = true)
                            }
                        )
                    }

                    NavTab.SCAN_QR -> {
                        ScanQrScreen(
                            currentStudent = currentStudent,
                            availableSubjects = allSubjects,
                            onQrScanned = { payload, isSimulator ->
                                viewModel.onQrScanned(payload, isSimulator)
                            },
                            onBack = { viewModel.selectTab(NavTab.DASHBOARD) }
                        )
                    }

                    NavTab.CLASSROOM_MODE -> {
                        ClassroomQrScreen(
                            subjects = allSubjects,
                            selectedSubject = selectedClassroomSubject,
                            classroomSession = activeClassroomSession,
                            qrBitmap = classroomQrBitmap,
                            todayAttendees = todayClassroomAttendees,
                            activeStudent = currentStudent,
                            onSelectSubject = { subject ->
                                viewModel.selectClassroomSubject(subject)
                            },
                            onRefreshQr = {
                                viewModel.refreshClassroomQr()
                            },
                            onSimulateScanCurrentStudent = { payload ->
                                viewModel.onQrScanned(payload, isSimulator = true)
                            }
                        )
                    }

                    NavTab.HISTORY -> {
                        HistoryScreen(
                            attendanceRecords = attendanceHistory,
                            subjects = allSubjects,
                            selectedFilterSubject = historyFilterSubject,
                            onSelectFilterSubject = { filter ->
                                viewModel.setHistoryFilter(filter)
                            }
                        )
                    }

                    NavTab.PROFILE -> {
                        ProfileScreen(
                            currentStudent = currentStudent,
                            allStudents = allStudents,
                            onSwitchStudent = { rollNumber ->
                                viewModel.switchActiveStudent(rollNumber)
                            },
                            onAddStudent = { name, roll, dept, sem ->
                                viewModel.addNewStudent(name, roll, dept, sem)
                            },
                            onUpdateStudent = { oldRoll, name, rollNumber, department, semester, email, avatarColor ->
                                viewModel.updateStudentProfile(oldRoll, name, rollNumber, department, semester, email, avatarColor)
                            },
                            onDeleteStudent = { rollNumber ->
                                viewModel.deleteStudentProfile(rollNumber)
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal dialog for attendance scan verification feedback
    AttendanceResultDialog(
        result = scanResultDialog,
        onDismiss = { viewModel.dismissScanResult() }
    )
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = IndigoPrimary,
    selectedTextColor = IndigoPrimary,
    indicatorColor = IndigoLight,
    unselectedIconColor = Slate500,
    unselectedTextColor = Slate500
)
