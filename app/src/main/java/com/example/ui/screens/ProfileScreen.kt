package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoDark
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.RoseLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.util.QrCodeUtil

@Composable
fun ProfileScreen(
    currentStudent: Student?,
    allStudents: List<Student>,
    onSwitchStudent: (String) -> Unit,
    onAddStudent: (String, String, String, String) -> Unit,
    onUpdateStudent: (String, String, String, String, String, String, String) -> Unit,
    onDeleteStudent: (String) -> Unit
) {
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var studentBeingEdited by remember { mutableStateOf<Student?>(null) }
    var studentBeingDeleted by remember { mutableStateOf<Student?>(null) }

    // Add student form state
    var newName by remember { mutableStateOf("") }
    var newRollNumber by remember { mutableStateOf("") }
    var newDepartment by remember { mutableStateOf("BS Computer Science") }
    var newSemester by remember { mutableStateOf("Semester 6") }

    // Edit student form state
    var editName by remember { mutableStateOf("") }
    var editRollNumber by remember { mutableStateOf("") }
    var editDepartment by remember { mutableStateOf("") }
    var editSemester by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editAvatarColor by remember { mutableStateOf("#4F46E5") }

    val studentQrBitmap: Bitmap? = remember(currentStudent?.rollNumber) {
        currentStudent?.let {
            QrCodeUtil.generateQrBitmap("GGGC_STUDENT_ID:${it.rollNumber}:${it.name}", size = 320)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen_scroll"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // College Authority Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate100),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(IndigoLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Government Graduate Girls College",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Authority: Student Profile Management Active",
                                fontSize = 11.sp,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldLight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Authorized",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            }
        }

        // Digital College Student ID Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = IndigoDark),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_id_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // ID Card Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GOVT. GRADUATE GIRLS COLLEGE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyanAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "STUDENT PASS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(IndigoLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentStudent?.name?.take(2)?.uppercase() ?: "GG",
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentStudent?.name ?: "Unknown Student",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Roll No: ${currentStudent?.rollNumber ?: "N/A"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanAccent
                            )
                            Text(
                                text = "${currentStudent?.department ?: ""} • ${currentStudent?.semester ?: ""}",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        // Mini Student QR Code
                        if (studentQrBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = studentQrBitmap.asImageBitmap(),
                                    contentDescription = "Student QR",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentStudent?.email ?: "",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        // Authority Edit Button on Active Student Card
                        currentStudent?.let { student ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyanAccent.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .clickable {
                                        studentBeingEdited = student
                                        editName = student.name
                                        editRollNumber = student.rollNumber
                                        editDepartment = student.department
                                        editSemester = student.semester
                                        editEmail = student.email
                                        editAvatarColor = student.avatarColorHex
                                    }
                                    .testTag("btn_edit_current_profile")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = CyanAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Edit Profile",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Student Directory & Management Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Student Profiles Directory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Edit, update, or add student profiles with full authority",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }

                Button(
                    onClick = { showAddStudentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_student")
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Student", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // List of all students with Edit and Delete Authority controls
        items(allStudents) { student ->
            val isActive = student.rollNumber == currentStudent?.rollNumber
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) IndigoLight else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 2.dp else 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_item_${student.rollNumber}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSwitchStudent(student.rollNumber) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isActive) IndigoPrimary else Slate200),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color.White else Slate700,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Slate900
                                )
                                if (isActive) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = IndigoPrimary
                                    ) {
                                        Text(
                                            text = "Active",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${student.rollNumber} • ${student.department}",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                            Text(
                                text = student.email,
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                    }

                    // Authority Actions: Edit Profile and Delete
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                studentBeingEdited = student
                                editName = student.name
                                editRollNumber = student.rollNumber
                                editDepartment = student.department
                                editSemester = student.semester
                                editEmail = student.email
                                editAvatarColor = student.avatarColorHex
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Slate100,
                                contentColor = IndigoPrimary
                            ),
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_edit_student_${student.rollNumber}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Student Profile",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { studentBeingDeleted = student },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = RoseLight,
                                contentColor = RoseDanger
                            ),
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_delete_student_${student.rollNumber}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Student Profile",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Student Profile Dialog (Authority Permission)
    studentBeingEdited?.let { student ->
        AlertDialog(
            onDismissRequest = { studentBeingEdited = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Student Profile", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Authority Mode: Update student information for Government Graduate Girls College.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_input_name")
                    )
                    OutlinedTextField(
                        value = editRollNumber,
                        onValueChange = { editRollNumber = it },
                        label = { Text("Roll Number / ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_input_roll")
                    )
                    OutlinedTextField(
                        value = editDepartment,
                        onValueChange = { editDepartment = it },
                        label = { Text("Department / Program") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_input_dept")
                    )
                    OutlinedTextField(
                        value = editSemester,
                        onValueChange = { editSemester = it },
                        label = { Text("Semester") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_input_sem")
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("College Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_input_email")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editRollNumber.isNotBlank()) {
                            onUpdateStudent(
                                student.rollNumber,
                                editName,
                                editRollNumber,
                                editDepartment,
                                editSemester,
                                editEmail,
                                editAvatarColor
                            )
                            studentBeingEdited = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("btn_save_edit_student")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentBeingEdited = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    studentBeingDeleted?.let { student ->
        AlertDialog(
            onDismissRequest = { studentBeingDeleted = null },
            title = {
                Text("Delete Student Profile?", fontWeight = FontWeight.Bold, color = RoseDanger)
            },
            text = {
                Text(
                    text = "Are you sure you want to remove ${student.name} (${student.rollNumber}) from Government Graduate Girls College? All attendance records for this student will also be removed.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteStudent(student.rollNumber)
                        studentBeingDeleted = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDanger),
                    modifier = Modifier.testTag("btn_confirm_delete_student")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentBeingDeleted = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Student Dialog
    if (showAddStudentDialog) {
        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = {
                Text("Enroll New Student", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enroll a new student into Government Graduate Girls College records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Student Full Name") },
                        placeholder = { Text("e.g. Ayesha Tariq") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_student_name")
                    )
                    OutlinedTextField(
                        value = newRollNumber,
                        onValueChange = { newRollNumber = it },
                        label = { Text("Roll Number / ID") },
                        placeholder = { Text("e.g. GGGC-22-CS-055") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_student_roll")
                    )
                    OutlinedTextField(
                        value = newDepartment,
                        onValueChange = { newDepartment = it },
                        label = { Text("Department / Degree Program") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSemester,
                        onValueChange = { newSemester = it },
                        label = { Text("Semester") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newRollNumber.isNotBlank()) {
                            onAddStudent(newName, newRollNumber, newDepartment, newSemester)
                            showAddStudentDialog = false
                            newName = ""
                            newRollNumber = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("btn_save_new_student")
                ) {
                    Text("Enroll Student")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
