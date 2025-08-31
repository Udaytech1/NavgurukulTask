package com.app.navgurukultask.data.repository

import com.app.navgurukultask.data.local.dao.StudentDao
import com.app.navgurukultask.data.local.entities.Student
import com.app.navgurukultask.data.local.entities.SyncStatus

import com.app.navgurukultask.utils.NetworkHelper
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton


@Singleton
class StudentRepository @Inject constructor(
    private val studentDao: StudentDao,
    private val networkHelper: NetworkHelper
) {
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllStudents(): Flow<List<Student>> = studentDao.getAllStudents()

    suspend fun getAllUnsyncStudents(): List<Student> = studentDao.getUnsyncedStudents()


    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(student)
        if(networkHelper.isNetworkConnected()){
            syncWithServer(student)
        }
    }

    suspend fun getStudentById (id: String): Flow<Student?> {
      return  studentDao.getStudentDetailById(id)
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student)
    }

    suspend fun deleteStudent(studentId: String) {
        studentDao.deleteStudent(studentId)
        if (networkHelper.isNetworkConnected()){
            try {
                firestore.collection("students").document(studentId).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun retrySync (student: Student){
        syncWithServer(student)
    }

    suspend fun syncWithServer (student: Student){
        var updateStudent = student.copy(syncStatus = SyncStatus.SYNCED)

        val collection = firestore.collection("students")
        try {
            collection.document(student.id.toString())
                .set(student)
                .await()

            updateStudent = student.copy(syncStatus = SyncStatus.SYNCED)

        } catch (e: Exception) {
            updateStudent = student.copy(syncStatus = SyncStatus.FAILED)
            e.printStackTrace()
        } finally {
            studentDao.updateStudent(updateStudent)
        }
    }

    suspend fun syncStudents() {
        val students = studentDao.getUnsyncedStudents()
        val collection = firestore.collection("students")

        for (student in students) {
            var updateStudent = student.copy(syncStatus = SyncStatus.SYNCED)

            try {
                collection.document(student.id.toString())
                    .set(student)
                    .await()

                updateStudent = student.copy(syncStatus = SyncStatus.SYNCED)

            } catch (e: Exception) {
                updateStudent = student.copy(syncStatus = SyncStatus.FAILED)
                e.printStackTrace()
            } finally {
                studentDao.updateStudent(updateStudent)
            }
        }
    }
}

