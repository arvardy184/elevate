package com.application.elevate.model

data class CourseDetail(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val videoList: List<VideoItem>,
    val quizList: List<QuizItem>
)

data class VideoItem(
    val title: String,
    val duration: String,
    val thumbnail: Int
)

data class QuizItem(
    val title: String,
    val duration: String,
    val thumbnail: Int
)