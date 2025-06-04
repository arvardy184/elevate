package com.application.elevate.ui.roadmap

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.application.elevate.data.dummy.ProfileDummyData.dummyCourses
import com.application.elevate.model.CourseWithPosition
import com.application.elevate.model.RoadmapCheckpoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.State
import com.application.elevate.data.dummy.ProfileDummyData

open class RoadmapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState

    init {
        // Dummy course list

        (
        dummyCourses
        )

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
