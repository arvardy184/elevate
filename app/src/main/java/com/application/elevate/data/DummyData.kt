package com.application.elevate.data

import com.application.elevate.model.Course
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import com.application.elevate.R
import com.application.elevate.model.GrowthHub
import com.application.elevate.model.User

val categories = listOf("Design", "Web Development", "Digital Marketing")

val growthHubItems = listOf(
    GrowthHub("Counseling", R.drawable.counseling),
    GrowthHub("CV Review", R.drawable.cv_review),
    GrowthHub("job & Skill", R.drawable.job_skill)
)

val dummyCourses = listOf(
    Course("UI/UX Design", "2h 45min", 9, 100, R.drawable.ui_ux ),
    Course("Front-End Dev", "5h 10min", 10, 60, R.drawable.front_end ),
    Course("SEO & Content Marketing", "1h 28min", 13, 80, R.drawable.content_marketing)
)

val dummyUser = User(
    name = "Keisya Marshanda",
    photoUrl = "R.drawable.profile" // nanti casting ke Int
)