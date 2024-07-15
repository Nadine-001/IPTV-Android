package com.example.tvapp

data class CartServiceModelItem(
    val item_id: Int,
    val note: Any,
    var quantity: Int,
    val room_service_id: Int,
    val room_service_image: String,
    val room_service_name: String
)