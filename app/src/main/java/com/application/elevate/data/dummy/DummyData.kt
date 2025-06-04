package com.application.elevate.data.dummy

import com.application.elevate.R
import com.application.elevate.model.*

object ProfileDummyData {
    // User default untuk preview
    val currentUser = User(
        id = 0,
        firstName = "Guest",
        lastName = "User",
        email = "guest@example.com",
        photoUrl = "",
        address = "Default Address",
        phoneNumber = "+62 000-0000-0000",
        gender = "Unspecified",
        birthDate = "01/01/2000",
        role = "user",
        isAssessmentCompleted = false
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
        Course("1","UI/UX Design", "2h 45min", 9, 100,5.0f, 201, R.drawable.ui_ux, true ),
        Course("2","Front-End Dev", "5h 10min", 10, 60, 4.5f, 140, R.drawable.front_end, true ),
        Course("3","SEO & Content Marketing", "1h 28min", 13, 80, 4.0f, 96, R.drawable.content_marketing, false),
        Course("4","Social Media Marketing", "2h 20min", 10, 40, 4.5f, 140, R.drawable.social_media_marketing, false),
        Course("5","Android Development", "3h 15min", 13, 20, 5.0f, 201, R.drawable.android_development, false),
        Course("6","Basic Accounting", "2h 4min", 11, 90, 4.0f, 96, R.drawable.basic_accounting, false)
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

    // assessment/DummyData.kt



    val majorOptions = listOf(
        "Computer Science",
        "Information Systems",
        "Software Engineering",
        "Data Science",
        "Information Technology",
        "Cybersecurity",
        "Business Administration",
        "Accounting",
        "Marketing",
        "Finance",
        "Psychology",
        "Law",
        "Industrial Engineering",
        "Mechanical Engineering",
        "Architecture",
        "Design Communication Visual",
        "Education",
        "Public Health"
    )

    val interestedFields = listOf(
        "UI/UX Design",
        "Data Analytics",
        "Project Management",
        "Software Development",
        "Cybersecurity",
        "Digital Marketing",
        "Finance & Accounting",
        "Entrepreneurship",
        "AI & Machine Learning"
    )

    val goalOptions = listOf(
        "Get Internship",
        "Full-Time Job",
        "Skill Improvement",
        "Career Switch",
        "Networking",
        "Freelancing Opportunities"
    )

    val assessmentDummyData = listOf(
        AssessmentStep(
            key = "studentStatus",
            title = "Let's Get to Know You",
            subtitle = "Tell us a bit about your academic journey to personalize your experience.",
            optionTitle = "Student Status",
            optionSubtitle = "Select your current status:",
            options = listOf("Active Student", "On Academic Leave", "Graduated"),
            type = QuestionType.RADIO
        ),
        AssessmentStep(
            key = "major",
            title = "Let's Get to Know You",
            subtitle = "Tell us a bit about your academic journey to personalize your experience.",
            optionTitle = "Major / Field of Study",
            optionSubtitle = "What are you studying?",
            options = majorOptions,
            type = QuestionType.DROPDOWN
        ),
        AssessmentStep(
            key = "semester",
            title = "Let's Get to Know You",
            subtitle = "Tell us a bit about your academic journey to personalize your experience.",
            optionTitle = "Current Semester",
            optionSubtitle = "Pick your semester",
            options = listOf("1", "2", "3", "4", "5", "6", "7", "8+"),
            type = QuestionType.DROPDOWN
        ),
        AssessmentStep(
            key = "currentField",
            title = "What Drives Your Passion?",
            subtitle = "Choose the field you're most interested in to shape your learning path.",
            optionTitle = "Current Field",
            optionSubtitle = "What field are you currently in?",
            options = interestedFields,
            type = QuestionType.DROPDOWN
        ),
        AssessmentStep(
            key = "interestedField",
            title = "What Drives Your Passion?",
            subtitle = "Choose the field you're most interested in to shape your learning path.",
            optionTitle = "Interested Field",
            optionSubtitle = "What field are you most interested in exploring?",
            options = interestedFields,
            type = QuestionType.RADIO
        ),
        AssessmentStep(
            key = "dreamJob",
            title = "What Drives Your Passion?",
            subtitle = "Choose the field you're most interested in to shape your learning path.",
            optionTitle = "Dream Job",
            optionSubtitle = "What's your dream role or career goal?",
            options = listOf(
                "UI/UX Designer", "Data Analyst", "Project Manager",
                "Software Engineer", "Cybersecurity Specialist",
                "Digital Marketer", "Financial Analyst", "Entrepreneur", "AI Engineer"
            ),
            type = QuestionType.DROPDOWN
        ),
        AssessmentStep(
            key = "goal",
            title = "What do you want to achieve?",
            subtitle = "What is your main goal with Elevate?",
            optionTitle = "Career Goal",
            optionSubtitle = "Choose what you aim to achieve",
            options = goalOptions,
            type = QuestionType.RADIO
        )
    )





}