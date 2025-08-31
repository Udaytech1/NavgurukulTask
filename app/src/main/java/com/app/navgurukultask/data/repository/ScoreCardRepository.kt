package com.app.navgurukultask.data.repository

import com.app.navgurukultask.data.local.dao.StudentScorecardDao
import com.app.navgurukultask.data.local.entities.ScoreCard
import com.app.navgurukultask.data.local.entities.SyncStatus
import com.app.navgurukultask.utils.NetworkHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoreCardRepository @Inject constructor(
    private val scoreCardDao: StudentScorecardDao,
    private val networkHelper: NetworkHelper
) {
    private val firestore = FirebaseFirestore.getInstance()

    fun getScoreCardsForStudent(studentId: String): Flow<List<ScoreCard>> =
        scoreCardDao.getScoreCardsForStudent(studentId)

    suspend fun insertScoreCard(scoreCard: ScoreCard) {
        scoreCardDao.insertScoreCard(scoreCard)
        if (networkHelper.isNetworkConnected()){
            syncScoreCardToServer(scoreCard)
        }
    }

    suspend fun deleteScoreCard(id: String) {
        scoreCardDao.deleteScoreCard(id)
        if (networkHelper.isNetworkConnected()){
            try {
                firestore.collection("scorecards").document(id).delete().await()
            } catch (e: Exception) {
            }
        }
    }

    suspend fun retrySync(scoreCard: ScoreCard) {
        if (networkHelper.isNetworkConnected()){
            syncScoreCardToServer(scoreCard)
        }
    }

    suspend fun syncScoreCardToServer (scoreCard: ScoreCard){
        val collection = firestore.collection("scorecards")
        try {
            collection.document(scoreCard.id.toString())
                .set(scoreCard)
                .await()

            scoreCardDao.updateScoreCard(
                scoreCard.copy(syncStatus = SyncStatus.SYNCED)
            )

        } catch (e: Exception) {
            scoreCardDao.updateScoreCard(
                scoreCard.copy(syncStatus = SyncStatus.FAILED)
            )
        }
    }

    
    suspend fun syncScoreCards() {
        val unsyncedScoreCards = scoreCardDao.getUnsyncedScoreCards() 
        val collection = firestore.collection("scorecards")

        for (scoreCard in unsyncedScoreCards) {
            try {
                collection.document(scoreCard.id.toString())
                    .set(scoreCard)
                    .await()

                scoreCardDao.updateScoreCard(
                    scoreCard.copy(syncStatus = SyncStatus.SYNCED)
                )

            } catch (e: Exception) {
                scoreCardDao.updateScoreCard(
                    scoreCard.copy(syncStatus = SyncStatus.FAILED)
                )
            }
        }
    }

}
