package com.application.elevate.data.dummy

import com.application.elevate.R
import com.application.elevate.model.Category

object CategoryData {
    val categories = listOf(
        Category(
            id = "1",
            name = "Design",
            iconResId = R.drawable.ic_category_design
        ),
        Category(
            id = "2",
            name = "Web Development",
            iconResId = R.drawable.ic_category_webdev
        ),
        Category(
            id = "3",
            name = "Digital Marketing",
            iconResId = R.drawable.ic_catagory_digital_marketing
        ),
        Category(
            id = "4",
            name = "Mobile Development",
            iconResId = R.drawable.ic_category_mobile_dev
        ),
        Category(
            id = "5",
            name = "Product Management",
            iconResId = R.drawable.ic_category_product_management
        ),
        Category(
            id = "6",
            name = "Finance & Accounting",
            iconResId = R.drawable.ic_category_finance
        ),
        Category(
            id = "7",
            name = "HR Management",
            iconResId = R.drawable.ic_category_hr
        ),
        Category(
            id = "8",
            name = "Personal Branding",
            iconResId = R.drawable.ic_category_personal_brand
        )
    )
} 