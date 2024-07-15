package com.example.tvapp

data class UnpaidOrder(
    val item_id: Int,
    val menu_description: String,
    val menu_id: Int,
    val menu_image: Any,
    val menu_name: String,
    val menu_price: Int,
    val quantity: Int
)