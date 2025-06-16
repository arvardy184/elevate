package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.application.elevate.model.User

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int,
    val email: String?,
    val role: String?,
    val firstName: String?,
    val lastName: String?,
    val photoUrl: String?,
    val address: String?,
    val phoneNumber: String?,
    val gender: String?,
    val birthDate: String?,
    val isAssessmentCompleted: Boolean = false,
    val isSynced: Boolean = true, // Track sync status
    val lastModified: Long = System.currentTimeMillis(),
    val syncError: String? = null // Store error message if sync fails
)

// Extension function untuk convert ke User model
fun ProfileEntity.toUser(): User {
    return User(
        id = id,
        email = email,
        role = role,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        address = address,
        phoneNumber = phoneNumber,
        gender = gender,
        birthDate = birthDate,
        isAssessmentCompleted = isAssessmentCompleted
    )
}

// Extension function untuk convert dari User model
fun User.toProfileEntity(isSynced: Boolean = true): ProfileEntity {
    return ProfileEntity(
        id = id ?: 0,
        email = email,
        role = role,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        address = address,
        phoneNumber = phoneNumber,
        gender = gender,
        birthDate = birthDate,
        isAssessmentCompleted = isAssessmentCompleted,
        isSynced = isSynced,
        lastModified = System.currentTimeMillis()
    )
} 