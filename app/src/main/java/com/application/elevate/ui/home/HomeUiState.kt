package com.application.elevate.ui.home

import com.application.elevate.data.dummyCourses
import com.application.elevate.model.Course


data class HomeUiState(
    val searchQuery: String = "",
    val courses: List<Course> = dummyCourses
)