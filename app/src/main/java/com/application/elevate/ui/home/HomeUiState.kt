package com.application.elevate.ui.home

import com.application.elevate.data.dummy.ProfileDummyData.dummyCourses
import com.application.elevate.model.Course
import com.application.elevate.model.User


data class HomeUiState(
    val searchQuery: String = "",
    val courses: List<Course> = dummyCourses,
    val user: User? = null
)