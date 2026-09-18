package com.application.elevate

import com.application.elevate.data.repository.SampleCourseDetails
import org.junit.Assert.*
import org.junit.Test

class SampleCourseDetailsTest {
    @Test fun existingCourseKeepsItsOwnDetail() {
        assertEquals("1", SampleCourseDetails.find(1)?.id)
    }

    @Test fun missingCourseDoesNotCrashOrReturnAnotherCoursesContent() {
        for (id in listOf(2, 3, 4, 5, -1, Int.MAX_VALUE)) {
            assertNull("Missing ID $id should render the unavailable state", SampleCourseDetails.find(id))
        }
    }
}
