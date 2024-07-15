package com.example.tvapp

data class PostOrderResponse(
    val order_id: String,
    val payment_url: String,
    val database_id: Int
)
