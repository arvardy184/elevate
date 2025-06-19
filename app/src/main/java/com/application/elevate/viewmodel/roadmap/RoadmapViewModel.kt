package com.application.elevate.viewmodel.roadmap

import androidx.lifecycle.ViewModel
import com.application.elevate.model.CourseWithPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.ui.roadmap.RoadmapUiState

open class RoadmapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState

    init {
        val coursePositions = listOf(
            CourseWithPosition(ProfileDummyData.dummyCourses[0], 0.50f, 0.082f),
            CourseWithPosition(ProfileDummyData.dummyCourses[1], 0.63f, 0.16f),
            CourseWithPosition(ProfileDummyData.dummyCourses[2], 0.3f, 0.125f),
            CourseWithPosition(ProfileDummyData.dummyCourses[3], 0.4f, 0.225f),
            CourseWithPosition(ProfileDummyData.dummyCourses[4], 0.08f, 0.17f)
        )

        _uiState.value = RoadmapUiState(coursePositions)
    }
}
