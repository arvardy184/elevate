package com.application.elevate.data.database.entity

/**
 * Data class for debugging course download status
 * Used to check what data exists for a course
 */
data class CourseDownloadStatus(
    val hasDetail: Int,        // 1 if course detail exists, 0 if not
    val videoCount: Int,       // Number of videos available
    val quizCount: Int,        // Number of quizzes available  
    val isDownloaded: Int?,    // 1 if marked as downloaded, 0 if not, null if course not found
    val isEnrolled: Int?       // 1 if enrolled, 0 if not, null if course not found
) {
    val hasDetailExists: Boolean get() = hasDetail > 0
    val hasVideos: Boolean get() = videoCount > 0
    val hasQuizzes: Boolean get() = quizCount > 0
    val isMarkedAsDownloaded: Boolean get() = isDownloaded == 1
    val isUserEnrolled: Boolean get() = isEnrolled == 1
    
    override fun toString(): String {
        return "CourseDownloadStatus(detail: ${if (hasDetailExists) "✅" else "❌"}, videos: $videoCount, quizzes: $quizCount, downloaded: ${if (isMarkedAsDownloaded) "✅" else "❌"}, enrolled: ${if (isUserEnrolled) "✅" else "❌"})"
    }
} 