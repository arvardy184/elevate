package com.application.elevate.data.dummy

import com.application.elevate.R
import com.application.elevate.model.*

object ProfileDummyData {
    val currentUser = User(
        id = "1",
        firstName = "Keisya Marshanda",
        lastName = "Setiandini",
        email = "keisyaamrsh@gmail.com",
        photoUrl = "",
        address = "Sunflower Regency K-6, Malang",
        phoneNumber = "+62 822-3081-9191",
        gender = "Perempuan",
        birthDate = "30/03/2004"
    )

    val activities = listOf(
        Activity(
            id = "1",
            type = "certificate",
            title = "UI/UX Design",
            score = 90,
            imageUrl = ""
        ),
        Activity(
            id = "2",
            type = "course",
            title = "UI/UX Design",
            duration = "2h40min",
            lessons = 9,
            imageUrl = ""
        )
    )

    val notificationSettings = listOf(
        NotificationSetting(
            id = "1",
            title = "Notification",
            isEnabled = false,
            category = "general"
        ),
        NotificationSetting(
            id = "2",
            title = "Sound",
            isEnabled = false,
            category = "general"
        ),
        NotificationSetting(
            id = "3",
            title = "Vibrate",
            isEnabled = false,
            category = "general"
        ),
        NotificationSetting(
            id = "4",
            title = "Application Updates",
            isEnabled = false,
            category = "system"
        ),
        NotificationSetting(
            id = "5",
            title = "Bill Reminder",
            isEnabled = false,
            category = "system"
        ),
        NotificationSetting(
            id = "6",
            title = "Promotion",
            isEnabled = false,
            category = "system"
        ),
        NotificationSetting(
            id = "7",
            title = "Bill Request",
            isEnabled = false,
            category = "system"
        ),
        NotificationSetting(
            id = "8",
            title = "New services available",
            isEnabled = false,
            category = "more"
        )
    )

    val helpCenterItems = listOf(
        HelpCenterItem(
            id = "1",
            question = "What is elevate?",
            answer = "Elevate is a learning platform that helps you improve your skills and knowledge."
        ),
        HelpCenterItem(
            id = "2",
            question = "How do I use the CV Review feature?",
            answer = "You can upload your CV and our experts will review it and provide feedback."
        ),
        HelpCenterItem(
            id = "3",
            question = "How do I book a session with consultant?",
            answer = "Go to the consultant page, select a consultant, and book a session based on their availability."
        ),
        HelpCenterItem(
            id = "4",
            question = "How do I use the Job & Skill Match feature?",
            answer = "Upload your CV and we will match your skills with available job opportunities."
        ),
        HelpCenterItem(
            id = "5",
            question = "How do I use the CV Review feature?",
            answer = "You can upload your CV and our experts will review it and provide feedback."
        )
    )

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

}