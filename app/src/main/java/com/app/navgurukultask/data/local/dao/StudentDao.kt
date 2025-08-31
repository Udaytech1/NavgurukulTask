package com.app.navgurukultask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.navgurukultask.data.local.entities.Student
import com.app.navgurukultask.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM Student ORDER BY updatedAt DESC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM Student WHERE id = :id")
    fun getStudentDetailById(id: String): Flow<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("DELETE FROM Student WHERE id = :studentId")
    suspend fun deleteStudent(studentId: String)

    @Query("SELECT * FROM Student WHERE syncStatus != :status")
    suspend fun getUnsyncedStudents(status: SyncStatus = SyncStatus.SYNCED): List<Student>
}
