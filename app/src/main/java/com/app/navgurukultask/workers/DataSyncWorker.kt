package com.app.navgurukultask.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.navgurukultask.data.local.dao.StudentDao
import com.app.navgurukultask.data.local.dao.StudentScorecardDao
import com.app.navgurukultask.data.local.database.AppDatabase
import com.app.navgurukultask.data.local.entities.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DataSyncWorker(
    val context: Context,
    workerParams: WorkerParameters,
    ) : CoroutineWorker(context, workerParams) {

    val studentDao: StudentDao =
        AppDatabase.Companion.getInstance(context).studentDao()
    val scoreDao: StudentScorecardDao =
        AppDatabase.Companion.getInstance(context).scoreCardDao()
    val firestore = FirebaseFirestore.getInstance()

        override suspend fun doWork(): Result {

            return try {
                val unsyncedStudents = studentDao.getUnsyncedStudents()
                Log.d("WORKER", "Student list: ${unsyncedStudents.size}")

                unsyncedStudents.forEach { student ->
                    val docRef = firestore.collection("students").document(student.id.toString())
                    Log.d("WORKER", student.id)

                    docRef.set(student)
                        .await()

                    studentDao.updateStudent(student.copy(syncStatus = SyncStatus.SYNCED))
                }

                val unsyncedStudentsScore = scoreDao.getUnsyncedScoreCards()
                unsyncedStudentsScore.forEach { score ->
                    val docRef = firestore.collection("student_scores").document(score.id.toString())
                    Log.d("WORKER", score.id)

                    docRef.set(score)
                        .await()

                    scoreDao.updateScoreCard(score.copy(syncStatus = SyncStatus.SYNCED))
                }

                Result.success()
            } catch (e: Exception) {
                Log.e("WORKER", e.message ?: "ERRRR")
                e.printStackTrace()
                Result.retry()
            }
        }
}