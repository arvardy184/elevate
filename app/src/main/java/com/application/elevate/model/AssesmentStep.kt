package com.application.elevate.model

enum class QuestionType {
    RADIO,
    DROPDOWN
}

data class AssessmentStep(
    val key: String,
    val title: String,
    val subtitle: String,
    val optionTitle: String,
    val optionSubtitle: String,
    val options: List<String>,
    val type: QuestionType
)