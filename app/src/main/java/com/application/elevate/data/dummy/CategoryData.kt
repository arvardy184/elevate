package com.application.elevate.data.dummy

import com.application.elevate.R
import com.application.elevate.model.CategoryData

object CategoryData {
    val categories = listOf(
        CategoryData(
            id = "1",
            name = "Design",
            iconResId = R.drawable.ic_category_design
        ),
        CategoryData(
            id = "2",
            name = "Web Development",
            iconResId = R.drawable.ic_category_webdev
        ),
        CategoryData(
            id = "3",
            name = "Digital Marketing",
            iconResId = R.drawable.ic_catagory_digital_marketing
        ),
        CategoryData(
            id = "4",
            name = "Mobile Development",
            iconResId = R.drawable.ic_category_mobile_dev
        ),
        CategoryData(
            id = "6",
            name = "Finance & Accounting",
            iconResId = R.drawable.ic_category_finance
        ),
        CategoryData(
            id = "7",
            name = "HR Management",
            iconResId = R.drawable.ic_category_hr
        ),
        CategoryData(
            id = "8",
            name = "Personal Branding",
            iconResId = R.drawable.ic_category_personal_brand
        ),
        CategoryData(
            id = "5",
            name = "Product Management",
            iconResId = R.drawable.ic_category_product_management
        )
    )
}