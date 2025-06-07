package com.application.elevate.model

//data class Pagination(
//    val currentPage: Int,
//    val totalPages: Int,
//    val totalItems: Int,
//    val itemsPerPage: Int
//)

data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val pagination: Pagination? = null
) 