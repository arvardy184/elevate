package com.application.elevate.model

data class PaymentDetail(
    val consultationFee: Int,
    val discount: Int,
    val serviceCharge: Int,
    val total: Int,
    val methods: List<PaymentMethod>
)