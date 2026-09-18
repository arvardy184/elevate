package com.application.elevate.data.repository

import com.application.elevate.data.dummy.ProfileDummyData
import com.application.elevate.model.CourseDetail

/** Sample details only; unknown IDs must render a missing-content state. */
object SampleCourseDetails {
    fun find(courseId: Int): CourseDetail? =
        ProfileDummyData.dummyCourseDetails.find { it.id == courseId.toString() }
}
