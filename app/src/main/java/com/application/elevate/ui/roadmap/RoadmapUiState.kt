package com.application.elevate.ui.roadmap

import com.application.elevate.model.Course
import com.application.elevate.model.CourseWithPosition

data class RoadmapUiState(
    val courses: List<CourseWithPosition> = emptyList()
)

