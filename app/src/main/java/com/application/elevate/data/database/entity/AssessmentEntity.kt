package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.elevate.model.AssessmentHistory
import com.application.elevate.model.AssessmentRequest

@Entity(tableName = "assessment")
data class AssessmentEntity(
    @PrimaryKey val id: Int,
    val userId: Int, // Foreign key ke user
    val studentStatus: String?,
    val majorStudy: String?,
    val currentSemester: String?,
    val currentField: String?,
    val interestedField: String?,
    val dreamJob: String?,
    val mainGoal: String?,
    val createdAt: String?,
    val isSynced: Boolean = true, // Track sync status
    val lastModified: Long = System.currentTimeMillis(),
    val syncError: String? = null // Store error message if sync fails
)

// Extension function untuk convert ke AssessmentHistory model
fun AssessmentEntity.toAssessmentHistory(): AssessmentHistory {
    return AssessmentHistory(
        id = id,
        studentStatus = studentStatus ?: "",
        majorStudy = majorStudy ?: "",
        currentSemester = currentSemester ?: "",
        currentField = currentField ?: "",
        interestedField = interestedField ?: "",
        dreamJob = dreamJob ?: "",
        mainGoal = mainGoal ?: "",
        createdAt = createdAt ?: ""
    )
}

// Extension function untuk convert ke AssessmentRequest model
fun AssessmentEntity.toAssessmentRequest(): AssessmentRequest {
    return AssessmentRequest(
        studentStatus = studentStatus ?: "",
        majorStudy = majorStudy ?: "",
        currentSemester = currentSemester ?: "",
        currentField = currentField ?: "",
        interestedField = interestedField ?: "",
        dreamJob = dreamJob ?: "",
        mainGoal = mainGoal ?: ""
    )
}

// Extension function untuk convert dari AssessmentHistory model
fun AssessmentHistory.toAssessmentEntity(userId: Int, isSynced: Boolean = true): AssessmentEntity {
    return AssessmentEntity(
        id = id,
        userId = userId,
        studentStatus = studentStatus,
        majorStudy = majorStudy,
        currentSemester = currentSemester,
        currentField = currentField,
        interestedField = interestedField,
        dreamJob = dreamJob,
        mainGoal = mainGoal,
        createdAt = createdAt,
        isSynced = isSynced,
        lastModified = System.currentTimeMillis()
    )
}

// Extension function untuk convert dari AssessmentRequest model
fun AssessmentRequest.toAssessmentEntity(userId: Int, assessmentId: Int = 0, isSynced: Boolean = true): AssessmentEntity {
    return AssessmentEntity(
        id = assessmentId,
        userId = userId,
        studentStatus = studentStatus,
        majorStudy = majorStudy,
        currentSemester = currentSemester,
        currentField = currentField,
        interestedField = interestedField,
        dreamJob = dreamJob,
        mainGoal = mainGoal,
        createdAt = "", // Akan diisi oleh server
        isSynced = isSynced,
        lastModified = System.currentTimeMillis()
    )
} 