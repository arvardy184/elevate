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
        Course("1","UI/UX Design", "2h 45min", 9, 100,5.0f, 201, R.drawable.ui_ux ),
        Course("2","Front-End Dev", "5h 10min", 10, 60, 4.5f, 140, R.drawable.front_end ),
        Course("3","SEO & Content Marketing", "1h 28min", 13, 80, 4.0f, 96, R.drawable.content_marketing),
        Course("4","Social Media Marketing", "2h 20min", 10, 40, 4.5f, 140, R.drawable.social_media_marketing),
        Course("5","Android Development", "3h 15min", 13, 20, 5.0f, 201, R.drawable.android_development),
        Course("6","Basic Accounting", "2h 4min", 11, 90, 4.0f, 96, R.drawable.basic_accounting)
    )

    val dummyCourseDetails = listOf(
        CourseDetail(
            id = "1",
            title = "UI/UX Design",
            subtitle = "Fundamental UX",
            description = "UX focuses on creating products that are easy to use, useful, and enjoyable. It involves understanding user needs, designing with empathy, and improving through testing and feedback.",
            imageRes = R.drawable.ui_ux,
            videoList = listOf(
                VideoItem("Understanding Users: Research & Personas", "12:00", R.drawable.ui_ux),
                VideoItem("Wireframing & Prototyping", "10:00", R.drawable.ui_ux),
                VideoItem("UI Design Principles & Visual Hierarchy", "10:00", R.drawable.ui_ux),
                VideoItem("Typography, Color, and Iconography", "10:00", R.drawable.ui_ux)
            ),
            quizList = listOf(
                QuizItem("Quiz: Research & Personas", "05:00", R.drawable.ui_ux),
                QuizItem("Quiz: Prototyping & UI Principles", "05:00", R.drawable.ui_ux),
                QuizItem("Quiz: Visual Hierarchy & Color", "05:00", R.drawable.ui_ux)
            )
        )
        // Tambahkan course lainnya jika diperlukan
    )



    val dummyNotifications = listOf(
        NotificationItem(
            id = "1",
            title = "New Message",
            subtitle = "You have a new message from Alex",
            avatarUrl = null,
            time = "10:00 AM",
            dateLabel = "Today"
        ),
        NotificationItem(
            id = "2",
            title = "Weekly Report",
            subtitle = "Your weekly activity report is ready",
            avatarUrl = null,
            time = "8:30 AM",
            dateLabel = "Today"
        ),
        NotificationItem(
            id = "3",
            title = "System Update",
            subtitle = "A new system update is available",
            avatarUrl = null,
            time = "7:00 PM",
            dateLabel = "Yesterday"
        )
    )

    val categoriesCounseling = listOf(
        CounselingCategory("1", "Design", R.drawable.ic_category_conseling_design),
        CounselingCategory("2", "Development", R.drawable.ic_category_conseling_development),
        CounselingCategory("3", "Finance", R.drawable.ic_category_conseling_finance),
        CounselingCategory("4", "Programming", R.drawable.ic_category_conseling_programming)
    )

    val consultants = listOf(
        Consultant(
            id = "1",
            name = "Barbie S.Ds., M.Ds.",
            title = "UI/UX Design Consultant",
            rating = 4.0f,
            reviewCount = 191,
            categoryId = "1", // Design
            price = 25000,
            oldPrice = 29999,
            imageResId = R.drawable.barbie
        ),
        Consultant(
            id = "2",
            name = "Ken S.Kom., M.M.S.I.",
            title = "Front-End Developer",
            rating = 5.0f,
            reviewCount = 204,
            categoryId = "2", // Development
            price = 30000,
            oldPrice = 39999,
            imageResId = R.drawable.ken
        ),
        Consultant(
            id = "3",
            name = "Alan S.Ds., M.Ds.",
            title = "Graphic Designer",
            rating = 5.0f,
            reviewCount = 178,
            categoryId = "1", // Design
            price = 40000,
            oldPrice = 49999,
            imageResId = R.drawable.alan
        ),
        Consultant(
            id = "4",
            name = "Peter D.Ds., M.Ds.",
            title = "UI/UX Design Consultant",
            rating = 4.5f,
            reviewCount = 178,
            categoryId = "1", // Design
            price = 30000,
            oldPrice = 39000,
            imageResId = R.drawable.peter
            ),
        Consultant(
            id = "5",
            name = "Jane S.Psi., M.Psi.",
            title = "Personal Development Coach",
            rating = 4.0f,
            reviewCount = 104,
            categoryId = "2", // Programming
            price = 30000,
            oldPrice = 39000,
            imageResId = R.drawable.jane
        )
    )


    }