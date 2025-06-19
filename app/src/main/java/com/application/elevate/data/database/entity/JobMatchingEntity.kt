package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.application.elevate.data.database.converter.StringListConverter
import com.application.elevate.model.JobMatch
import com.application.elevate.model.AIJobAnalysis

@Entity(tableName = "job_matching")
data class JobMatchingEntity(
    @PrimaryKey
    val id: String,
    val userId: Int?, // CRITICAL: User ID untuk binding data ke user
    val dreamJob: String,
    val matches: List<JobMatch>,
    val aiAnalysis: AIJobAnalysis,
    val createdAt: String,
    val totalMatches: Int,
    val cvSaved: Boolean = false,
    val isSynced: Boolean = false, // Field untuk tracking sinkronisasi
    val cvFilePath: String? = null, // Path file CV lokal
    val syncError: String? = null, // Error message saat sync gagal
    val isOfflineData: Boolean = false // Flag untuk data yang dibuat offline
)

@Entity(tableName = "job_match")
data class JobMatchEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val jobMatchingId: String, // Foreign key ke job_matching
    val jobId: String,
    val title: String,
    val company: String,
    val reasons: List<String>,
    val strengths: List<String>,
    val matchScore: Int,
    val skillMatch: Int,
    val locationMatch: Int,
    val missingSkills: List<String>,
    val experienceMatch: Int,
    val salaryPotential: String
)

@Entity(tableName = "ai_analysis")
data class AIAnalysisEntity(
    @PrimaryKey
    val jobMatchingId: String,
    val summary: String,
    val skillGaps: List<String>,
    val careerPath: String,
    val recommendations: List<String>,
    val dreamJobAlignment: String
) 