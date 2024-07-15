package com.example.tvapp

data class PostOrderFoodModel(
    val mac_address: String,
    val orders: MutableList<Order>,
    val payment_method: String,
    val total: Int,
)