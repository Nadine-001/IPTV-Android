package com.example.tvapp

data class PostReqServiceModel(
    val mac_address: String,
    val requests: MutableList<Request> // Mengubah tipe data menjadi MutableList
)