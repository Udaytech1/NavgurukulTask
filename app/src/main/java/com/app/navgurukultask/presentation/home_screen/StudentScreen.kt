package com.app.navgurukultask.presentation.home_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.navgurukultask.data.local.entities.Student
import com.app.navgurukultask.data.local.entities.SyncStatus

import com.app.navgurukultask.presentation.dialogs.StudentDialog
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    navController: NavController,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val students by viewModel.students.collectAsState(initial = emptyList())
    val openDialog = remember { mutableStateOf(false) }
    var editableStudent by remember { mutableStateOf<Student?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student List") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                openDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
            }
        },

    ) { padding ->
        if (students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No students yet.\nPlease add a student.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(students.size) { index ->
                    val item = students[index]
                    StudentItem(
                        student = item,
                        onClick = {
                            navController.navigate("details/${item.id}")
                        },
                        onDelete = {
                            viewModel.deleteStudent(item.id)
                        },
                        onEdit = {
                            openDialog.value = true
                            editableStudent = item
                        },
                        onRetry = {
                            viewModel.retrySync(item)
                        }
                    )
                }
            }
        }

        if (openDialog.value) {
            StudentDialog (
                student = editableStudent,
                onDismiss = {
                    openDialog.value = false
                            },
                onConfirm = { name, clazz, gender ->
                    val currentTime = System.currentTimeMillis()

                    editableStudent?.let { student ->
                        val updatedStudent = student.copy(
                            fullName = name,
                            studentClass = clazz,
                            gender = gender,
                            updatedAt = currentTime
                        )
                        viewModel.updateStudent(updatedStudent)
                        editableStudent = null
                    } ?: run {
                        val newStudent = Student(
                            id = UUID.randomUUID().toString(),
                            fullName = name,
                            studentClass = clazz,
                            gender = gender,
                            schoolId = "SCHOOL123",
                            createdAt = currentTime,
                            updatedAt = currentTime,
                            syncStatus = SyncStatus.PENDING
                        )
                        viewModel.addStudent(newStudent)
                    }

                    openDialog.value = false
                }
            )
        }
    }
}

@Composable
fun StudentItem(
    student: Student,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp).clickable{
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(student.fullName, style = MaterialTheme.typography.titleMedium)
                Text("Class: ${student.studentClass}, Gender: ${student.gender}")
            }

            when (student.syncStatus) {
                SyncStatus.SYNCED -> Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Synced",
                    tint = Color.Blue
                )
                SyncStatus.PENDING -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                SyncStatus.FAILED -> IconButton(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.Red)
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}





