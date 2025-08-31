package com.app.navgurukultask.presentation.student_detail_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.navgurukultask.data.local.entities.ScoreCard
import com.app.navgurukultask.data.local.entities.Student
import com.app.navgurukultask.data.local.entities.SyncStatus
import com.app.navgurukultask.presentation.dialogs.AddScoreCardDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    navController: NavController,
    studentId: String,
    viewModel: StudentDetailViewModel = hiltViewModel()
) {
    var student by remember { mutableStateOf<Student?>(null) }
    LaunchedEffect(studentId) {
        viewModel.getStudent(studentId).collect { studentData ->
            student = studentData
        }
    }
    val scoreCards by viewModel.getScoreCards(studentId).collectAsState(initial = emptyList())
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Detail") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { openDialog.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add ScoreCard")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            student?.let {
                StudentInfo(it)
                Spacer(Modifier.height(12.dp))
            }

            Text("ScoreCards", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))

            LazyColumn {
                items(scoreCards.size) { index ->
                    val scoreCard= scoreCards[index]
                    ScoreCardItem(
                        scoreCard = scoreCard,
                        onDelete = { viewModel.deleteScoreCard(scoreCard.id) },
                        onRetry = { viewModel.retrySync(scoreCard) }
                    )
                }
            }
        }

        if (openDialog.value) {
            AddScoreCardDialog(
                onDismiss = { openDialog.value = false },
                onSave = { subject, score ->
                    viewModel.addScoreCard(studentId, subject, score)
                    openDialog.value = false
                }
            )
        }
    }
}

@Composable
fun StudentInfo(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(student.fullName, style = MaterialTheme.typography.titleLarge)
            Text("Class: ${student.studentClass}")
            Text("Gender: ${student.gender}")
        }
    }
}

@Composable
fun ScoreCardItem(
    scoreCard: ScoreCard,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                Text("Subject: ${scoreCard.subject}", style = MaterialTheme.typography.titleMedium)
                Text("Score: ${scoreCard.score}")
            }

            when (scoreCard.syncStatus) {
                SyncStatus.SYNCED -> Icon(Icons.Default.CheckCircle, contentDescription = "Synced", tint = Color.Blue)
                SyncStatus.PENDING -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                SyncStatus.FAILED -> IconButton(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.Red)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}



