package com.application.elevate.data.dummy

import com.application.elevate.R
import com.application.elevate.model.CategoryCourse

object CategoryCoursesData {
    // Kursus untuk kategori Design
    val designCourses = listOf(
        CategoryCourse(
            id = "d1",
            title = "UI/UX Design",
            duration = "2h 40min",
            lessons = 9,
            progressPercent = 100,
            imageRes = R.drawable.ui_ux,
            isCompleted = true,
            categoryId = "1" // Design
        ),
        CategoryCourse(
            id = "d2",
            title = "Graphic Design",
            duration = "3h 50min",
            lessons = 10,
            progressPercent = 0,
            imageRes = R.drawable.front_end, // Gunakan gambar yang tersedia
            isCompleted = false,
            categoryId = "1" // Design
        ),
        CategoryCourse(
            id = "d3",
            title = "Illustration",
            duration = "2h 40min",
            lessons = 9,
            progressPercent = 0,
            imageRes = R.drawable.ui_ux, // Gunakan gambar yang tersedia
            isCompleted = false,
            categoryId = "1" // Design
        ),
        CategoryCourse(
            id = "d4",
            title = "Front-End Development",
            duration = "3h 50min",
            lessons = 10,
            progressPercent = 0,
            imageRes = R.drawable.front_end,
            isCompleted = false,
            categoryId = "1" // Design (tetapi sebenarnya Web Development)
        )
    )
    
    // Kursus untuk kategori Web Development
    val webDevCourses = listOf(
        CategoryCourse(
            id = "w1",
            title = "Front-End Development",
            duration = "3h 50min",
            lessons = 10,
            progressPercent = 0,
            imageRes = R.drawable.front_end,
            isCompleted = false,
            categoryId = "2" // Web Development
        ),
        CategoryCourse(
            id = "w2",
            title = "Back-End Development",
            duration = "4h 20min",
            lessons = 12,
            progressPercent = 0,
            imageRes = R.drawable.android_development, // Gunakan gambar yang tersedia
            isCompleted = false,
            categoryId = "2" // Web Development
        ),
        CategoryCourse(
            id = "w3",
            title = "Full Stack Development",
            duration = "5h 30min",
            lessons = 15,
            progressPercent = 0,
            imageRes = R.drawable.front_end, // Gunakan gambar yang tersedia
            isCompleted = false,
            categoryId = "2" // Web Development
        )
    )
    
    // Kursus untuk kategori Digital Marketing
    val digitalMarketingCourses = listOf(
        CategoryCourse(
            id = "m1",
            title = "SEO & Content Marketing",
            duration = "1h 28min",
            lessons = 13,
            progressPercent = 80,
            imageRes = R.drawable.content_marketing,
            isCompleted = false,
            categoryId = "3" // Digital Marketing
        ),
        CategoryCourse(
            id = "m2",
            title = "Social Media Marketing",
            duration = "2h 20min",
            lessons = 10,
            progressPercent = 40,
            imageRes = R.drawable.social_media_marketing,
            isCompleted = false,
            categoryId = "3" // Digital Marketing
        )
    )
    
    // Kursus untuk kategori Mobile Development
    val mobileCourses = listOf(
        CategoryCourse(
            id = "md1",
            title = "Android Development",
            duration = "3h 15min",
            lessons = 13,
            progressPercent = 20,
            imageRes = R.drawable.android_development,
            isCompleted = false,
            categoryId = "4" // Mobile Development
        ),
        CategoryCourse(
            id = "md2",
            title = "iOS Development",
            duration = "3h 45min",
            lessons = 14,
            progressPercent = 0,
            imageRes = R.drawable.android_development, // Gunakan gambar yang tersedia
            isCompleted = false,
            categoryId = "4" // Mobile Development
        )
    )
    
    // Kursus untuk kategori Finance & Accounting
    val financeCourses = listOf(
        CategoryCourse(
            id = "f1",
            title = "Basic Accounting",
            duration = "2h 04min",
            lessons = 11,
            progressPercent = 90,
            imageRes = R.drawable.basic_accounting,
            isCompleted = false,
            categoryId = "6" // Finance
        ),
        CategoryCourse(
            id = "f2",
            title = "Financial Analysis",
            duration = "2h 30min",
            lessons = 12,
            progressPercent = 0,
            imageRes = R.drawable.basic_accounting, // Gunakan gambar yang tersedia
            isCompleted = false,
            categoryId = "6" // Finance
        )
    )
    
    // Fungsi untuk mendapatkan courses berdasarkan categoryId
    fun getCoursesByCategory(categoryId: String): List<CategoryCourse> {
        return when(categoryId) {
            "1" -> designCourses
            "2" -> webDevCourses
            "3" -> digitalMarketingCourses
            "4" -> mobileCourses
            "6" -> financeCourses
            else -> emptyList()
        }
    }
    
    // Fungsi untuk mendapatkan nama kategori berdasarkan categoryId
    fun getCategoryName(categoryId: String): String {
        return when(categoryId) {
            "1" -> "Design"
            "2" -> "Web Development"
            "3" -> "Digital Marketing"
            "4" -> "Mobile Development"
            "5" -> "Product Management"
            "6" -> "Finance & Accounting"
            "7" -> "HR Management"
            "8" -> "Personal Branding"
            else -> "Unknown Category"
        }
    }
} 