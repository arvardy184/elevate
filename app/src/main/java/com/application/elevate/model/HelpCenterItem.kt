package com.application.elevate.model

data class HelpCenterItem(
    val id: String,
    val question: String,
    val answer: String,
    val isExpanded: Boolean = false
)