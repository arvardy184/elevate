package com.application.elevate.data.mapper

import com.application.elevate.data.database.entity.CourseEntity
import com.application.elevate.model.Course
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CourseMapper {

  private val gson = Gson()

  fun toEntity(course: Course, categoryName: String = "Unknown"): CourseEntity {
    return CourseEntity(
      id = course.id,
      title = course.title,
      description = course.description,
      duration = course.duration,
      lessons = course.lessons,
      progressPercent = course.progressPercent,
      rating = course.rating,
      ratingCount = course.ratingCount,
      imageUrl = course.thumbnail,
      imageRes = course.imageRes,
      isLocked = course.isLocked,
      isPopular = course.rating >= 4.5f && course.ratingCount >= 100,
      categoryId = course.categoryId,
      categoryName = categoryName,
      price = course.price,
      instructorName = null, // Will be enhanced when instructor data is available
      level = determineLevelFromCourse(course),
      tags = generateSearchTags(course, categoryName)
    )
  }

  fun fromEntity(entity: CourseEntity): Course {
    return Course(
      id = entity.id,
      title = entity.title,
      duration = entity.duration,
      lessons = entity.lessons,
      progressPercent = entity.progressPercent,
      rating = entity.rating,
      ratingCount = entity.ratingCount,
      imageRes = entity.imageRes ?: 0,
      isLocked = entity.isLocked,
      categoryId = entity.categoryId
    )
  }

  fun toEntityList(courses: List<Course>, getCategoryName: (Int) -> String = { "" }): List<CourseEntity> {
    return courses.map { course ->
      toEntity(course, getCategoryName(course.categoryId))
    }
  }

  fun fromEntityList(entities: List<CourseEntity>): List<Course> {
    return entities.map { fromEntity(it) }
  }

  // Determine course level based on lessons count and rating
  private fun determineLevelFromCourse(course: Course): String {
    return when {
      course.lessons <= 5 -> "Beginner"
      course.lessons <= 12 -> "Intermediate" 
      else -> "Advanced"
    }
  }

  // Generate searchable tags for better discoverability
  private fun generateSearchTags(course: Course, categoryName: String): String {
    val tags = mutableSetOf<String>()
    
    // Add title words
    course.title.split(" ").forEach { word ->
      if (word.length > 2) {
        tags.add(word.lowercase().trim())
      }
    }
    
    // Add category name
    if (categoryName.isNotBlank()) {
      tags.add(categoryName.lowercase())
    }
    
    // Add technology/skill specific tags based on title
    when {
      course.title.contains("UI/UX", ignoreCase = true) -> {
        tags.addAll(listOf("design", "user-interface", "user-experience", "ui", "ux"))
      }
      course.title.contains("Development", ignoreCase = true) -> {
        tags.addAll(listOf("programming", "coding", "developer", "development"))
      }
      course.title.contains("Marketing", ignoreCase = true) -> {
        tags.addAll(listOf("marketing", "digital", "social-media", "content"))
      }
      course.title.contains("Android", ignoreCase = true) -> {
        tags.addAll(listOf("mobile", "app", "android", "kotlin"))
      }
      course.title.contains("Front", ignoreCase = true) -> {
        tags.addAll(listOf("frontend", "web", "html", "css", "javascript"))
      }
    }
    
    // Add level tag
    tags.add(determineLevelFromCourse(course).lowercase())
    
    // Add popularity tags
    if (course.rating >= 4.5f) tags.add("top-rated")
    if (course.ratingCount >= 200) tags.add("popular")
    
    return gson.toJson(tags.toList())
  }

  // Helper to extract tags from JSON
  fun getTagsFromEntity(entity: CourseEntity): List<String> {
    return try {
      val type = object : TypeToken<List<String>>() {}.type
      gson.fromJson(entity.tags, type) ?: emptyList()
    } catch (e: Exception) {
      emptyList()
    }
  }
} 