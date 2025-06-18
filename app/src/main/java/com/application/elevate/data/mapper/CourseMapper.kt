package com.application.elevate.data.mapper

import com.application.elevate.R
import com.application.elevate.model.CourseItem
import com.application.elevate.model.CourseDetailItem
import com.application.elevate.model.*
import com.application.elevate.data.database.entity.*

/**
 * Extension function untuk mendapatkan drawable resource berdasarkan title course
 * Digunakan ketika thumbnail dari API kosong atau null
 */
fun CourseItem.getLocalImageRes(): Int {
    return when {
        title.contains("UI/UX", ignoreCase = true) -> R.drawable.ui_ux
        title.contains("Front-End", ignoreCase = true) || title.contains("Frontend", ignoreCase = true) -> R.drawable.front_end
        title.contains("SEO", ignoreCase = true) || title.contains("Content Marketing", ignoreCase = true) -> R.drawable.content_marketing
        title.contains("Social Media", ignoreCase = true) -> R.drawable.social_media_marketing
        title.contains("Android", ignoreCase = true) -> R.drawable.android_development
        title.contains("Accounting", ignoreCase = true) -> R.drawable.basic_accounting
        else -> R.drawable.ui_ux // Default fallback
    }
}

/**
 * Extension function untuk CourseDetailItem juga
 */
fun CourseDetailItem.getLocalImageRes(): Int {
    return when {
        title.contains("UI/UX", ignoreCase = true) -> R.drawable.ui_ux
        title.contains("Front-End", ignoreCase = true) || title.contains("Frontend", ignoreCase = true) -> R.drawable.front_end
        title.contains("SEO", ignoreCase = true) || title.contains("Content Marketing", ignoreCase = true) -> R.drawable.content_marketing
        title.contains("Social Media", ignoreCase = true) -> R.drawable.social_media_marketing
        title.contains("Android", ignoreCase = true) -> R.drawable.android_development
        title.contains("Accounting", ignoreCase = true) -> R.drawable.basic_accounting
        else -> R.drawable.ui_ux // Default fallback
    }
}

// Convert API CourseItem to CourseEntity
fun CourseItem.toEntity(): CourseEntity {
    return CourseEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        thumbnail = this.thumbnail,
        categoryId = this.categoryId,
        isPaid = this.isPaid,
        price = this.price,
        createdById = this.createdById,
        createdAt = this.createdAt,
        categoryName = this.category.name,
        isEnrolled = this.isEnrolled
    )
}

// Convert CourseEntity to API CourseItem
fun CourseEntity.toApiModel(): CourseItem {
    return CourseItem(
        id = this.id,
        title = this.title,
        description = this.description,
        thumbnail = this.thumbnail,
        categoryId = this.categoryId,
        isPaid = this.isPaid,
        price = this.price,
        createdById = this.createdById,
        createdAt = this.createdAt,
        category = Category(id = this.categoryId.toString(), name = this.categoryName, iconResId = R.drawable.ic_category_design),
        isEnrolled = this.isEnrolled
    )
}

// Convert API CourseDetailItem to CourseDetailEntity
fun CourseDetailItem.toEntity(): CourseDetailEntity {
    return CourseDetailEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        thumbnail = this.thumbnail,
        categoryId = this.categoryId,
        isPaid = this.isPaid,
        price = this.price,
        createdById = this.createdById,
        createdAt = this.createdAt,
        categoryName = this.category.name,
        averageRating = this.averageRating,
        totalReviews = this.totalReviews
    )
}

// Convert CourseDetailEntity to API CourseDetailItem
fun CourseDetailEntity.toApiModel(): CourseDetailItem {
    return CourseDetailItem(
        id = this.id,
        title = this.title,
        description = this.description,
        thumbnail = this.thumbnail,
        categoryId = this.categoryId,
        isPaid = this.isPaid,
        price = this.price,
        createdById = this.createdById,
        createdAt = this.createdAt,
        category = CourseCategory(id = this.categoryId, name = this.categoryName),
        averageRating = this.averageRating,
        totalReviews = this.totalReviews,
        isEnrolled = false // Default, will be updated from courses table
    )
}

// Convert API CourseVideoItem to CourseVideoEntity
fun CourseVideoItem.toEntity(): CourseVideoEntity {
    return CourseVideoEntity(
        id = this.id,
        courseId = this.courseId,
        title = this.title,
        videoUrl = this.videoUrl,
        isLocked = this.isLocked,
        order = this.order,
        s3Key = this.s3Key,
        originalUrl = this.originalUrl,
        isProxied = this.isProxied
    )
}

// Convert CourseVideoEntity to API CourseVideoItem
fun CourseVideoEntity.toApiModel(): CourseVideoItem {
    return CourseVideoItem(
        id = this.id,
        courseId = this.courseId,
        title = this.title,
        videoUrl = this.videoUrl,
        isLocked = this.isLocked,
        order = this.order,
        s3Key = this.s3Key,
        originalUrl = this.originalUrl,
        isProxied = this.isProxied
    )
}

// Convert API CourseQuizItem to CourseQuizEntity
fun CourseQuizItem.toEntity(): CourseQuizEntity {
    return CourseQuizEntity(
        id = this.id,
        courseId = this.courseId,
        question = this.question,
        options = this.options,
        correctAnswer = this.correctAnswer,
        isLocked = this.isLocked
    )
}

// Convert CourseQuizEntity to API CourseQuizItem
fun CourseQuizEntity.toApiModel(): CourseQuizItem {
    return CourseQuizItem(
        id = this.id,
        courseId = this.courseId,
        question = this.question,
        options = this.options,
        correctAnswer = this.correctAnswer,
        isLocked = this.isLocked
    )
}

// Convert API CategoryItem to CategoryEntity
fun CategoryItem.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        icon = this.icon
    )
}

// Convert CategoryEntity to API CategoryItem
fun CategoryEntity.toApiModel(): CategoryItem {
    return CategoryItem(
        id = this.id,
        name = this.name,
        description = this.description,
        icon = this.icon
    )
}

// Convert QuizCompletionData to QuizCompletionEntity
fun com.application.elevate.viewmodel.course.QuizCompletionData.toEntity(courseId: Int, userId: Int): com.application.elevate.data.database.entity.QuizCompletionEntity {
    return com.application.elevate.data.database.entity.QuizCompletionEntity(
        id = com.application.elevate.data.database.entity.QuizCompletionEntity.generateId(userId, courseId),
        userId = userId,
        courseId = courseId,
        score = this.score,
        totalQuestions = this.totalQuestions,
        isPassed = this.isPassed,
        completedAt = this.completedAt,
        isSynced = false
    )
}

// Convert QuizCompletionEntity to QuizCompletionData
fun com.application.elevate.data.database.entity.QuizCompletionEntity.toQuizCompletionData(): com.application.elevate.viewmodel.course.QuizCompletionData {
    return com.application.elevate.viewmodel.course.QuizCompletionData(
        score = this.score,
        totalQuestions = this.totalQuestions,
        isPassed = this.isPassed,
        completedAt = this.completedAt
    )
} 