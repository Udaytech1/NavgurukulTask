package com.app.navgurukultask.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.navgurukultask.data.local.entities.ScoreCard
import com.app.navgurukultask.data.local.entities.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentScorecardDao {

    @Query("SELECT * FROM ScoreCard WHERE studentId = :studentId ORDER BY updatedAt DESC")
    fun getScoreCardsForStudent(studentId: String): Flow<List<ScoreCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScoreCard(scoreCard: ScoreCard)

    @Update
    suspend fun updateScoreCard(scoreCard: ScoreCard)

    @Query("DELETE FROM ScoreCard WHERE id = :id")
    suspend fun deleteScoreCard(id: String)

    @Query("DELETE FROM ScoreCard WHERE studentId = :studentId")
    suspend fun deleteScoreCardsByStudent(studentId: String)

    @Query("SELECT * FROM ScoreCard WHERE syncStatus != :status")
    suspend fun getUnsyncedScoreCards(status: SyncStatus = SyncStatus.SYNCED): List<ScoreCard>
}
