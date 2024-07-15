package com.example.tvapp
//
//{
//    "room_number": 102,
//    "room_type": "Deluxe Room",
//    "room_facility": "it's just where you sleep.",
//    "room_image": "http://localhost:8000/uploads/about/1708143844-download20240204170710.png"
//}

data class DescriptionModel (
    val room_number: Int,
    val room_type: String,
    val room_facility: String,
    val room_description: String,
    val room_image: String,
)

