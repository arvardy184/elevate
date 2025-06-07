package com.application.elevate.model

data class RoadmapCheckpoint(
    val id: Int,
    val courseId: String,
    val offsetX: Int,
    val offsetY: Int,
    val isLocked: Boolean
)