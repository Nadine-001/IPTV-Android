package com.example.tvapp

data class PendingTransaksiModel(
    val food_request_id: Int,
    val total: Int,
    val unpaid_order: List<UnpaidOrder>
)