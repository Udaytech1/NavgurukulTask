package com.app.navgurukultask.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity
data class Student(
    @PrimaryKey val id: String,
    val fullName: String,
    val studentClass: String,
    val gender: String,
    val schoolId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Student::class,
        parentColumns = ["id"],
        childColumns = ["studentId"],
        onDelete = CASCADE
    )]
)
data class ScoreCard(
    @PrimaryKey val id: String,
    val studentId: String,
    val subject: String,
    val score: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: SyncStatus
)

enum class SyncStatus { SYNCED, PENDING, FAILED }
