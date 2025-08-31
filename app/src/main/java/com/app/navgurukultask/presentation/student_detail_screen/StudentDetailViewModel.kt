package com.app.navgurukultask.presentation.student_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.navgurukultask.data.local.entities.ScoreCard
import com.app.navgurukultask.data.local.entities.Student
import com.app.navgurukultask.data.local.entities.SyncStatus
import com.app.navgurukultask.data.repository.ScoreCardRepository
import com.app.navgurukultask.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val scoreCardRepository: ScoreCardRepository
) : ViewModel() {

    suspend fun getStudent(id: String): Flow<Student?> =
        repository.getStudentById(id)

    fun getScoreCards(studentId: String): Flow<List<ScoreCard>> =
        scoreCardRepository.getScoreCardsForStudent(studentId)

    fun addScoreCard(studentId: String, subject: String, score: Int) {
        val scoreCard = ScoreCard(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            subject = subject,
            score = score,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING
        )
        viewModelScope.launch { scoreCardRepository.insertScoreCard(scoreCard) }
    }

    fun deleteScoreCard(id: String) {
        viewModelScope.launch { scoreCardRepository.deleteScoreCard(id) }
    }

    fun retrySync(scoreCard: ScoreCard) {
        viewModelScope.launch { scoreCardRepository.retrySync(scoreCard) }
    }
}
