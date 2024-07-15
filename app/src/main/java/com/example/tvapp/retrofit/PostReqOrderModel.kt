package com.example.tvapp.retrofit

data class PostReqOrderModel(
    val mac_address: String,
    val orders: List<Order>,
    val payment_method: String,
    val total: Int
)