package com.application.elevate.data.dummy

import com.application.elevate.R
import com.application.elevate.model.CategoryData as CategoryDataModel

object CategoryData {
    val categories = listOf(
        CategoryDataModel(
            id = "1",
            name = "Design",
            iconResId = R.drawable.ic_category_design
        ),
        CategoryDataModel(
            id = "2",
            name = "Web Development",
            iconResId = R.drawable.ic_category_webdev
        ),
        CategoryDataModel(
            id = "3",
            name = "Digital Marketing",
            iconResId = R.drawable.ic_catagory_digital_marketing
        ),
        CategoryDataModel(
            id = "4",
            name = "Mobile Development",
            iconResId = R.drawable.ic_category_mobile_dev
        ),
        CategoryDataModel(
            id = "5",
            name = "Product Management",
            iconResId = R.drawable.ic_category_product_management
        ),
        CategoryDataModel(
            id = "6",
            name = "Finance & Accounting",
            iconResId = R.drawable.ic_category_finance
        ),
        CategoryDataModel(
            id = "7",
            name = "HR Management",
            iconResId = R.drawable.ic_category_hr
        ),
        CategoryDataModel(
            id = "8",
            name = "Personal Branding",
            iconResId = R.drawable.ic_category_personal_brand
        )
    )
} 