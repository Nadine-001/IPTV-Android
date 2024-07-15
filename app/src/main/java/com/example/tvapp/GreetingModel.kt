package com.example.tvapp

//{
//    "room_number": 101,
//    "room_type": "Suit Room",
//    "guest_name": "Talitha Padmarini",
//    "guest_gender": "Perempuan",
//    "hotel_greeting": "<!DOCTYPE html>\n<html>\n<head>\n<title>Greeting</title>\n</head>\n<body>\n<h1>Hello, Mr. Bayu!</h1>\n<p>Welcome to our hotel!</p>\n</body>\n</html>"
//}

data class GreetingModel(
    val room_number: Int,
    val room_type: String,
    val guest_name: String,
    val guest_gender: String,
    val hotel_greeting: String,
)
