package com.application.elevate.ui.home

import com.application.elevate.model.User
import com.application.elevate.model.Course

data class HomeUiState(
    val user: User? = null,
    val searchQuery: String = "",
    val showTutorial: Boolean = false,
    val popularCourses: List<Course> = emptyList()
)