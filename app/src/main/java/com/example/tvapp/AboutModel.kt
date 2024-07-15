package com.example.tvapp

import java.sql.Time


//{
//    "hotel_name": "Hotel ATNAVA",
//    "hotel_class": 4,
//    "hotel_about": "<!DOCTYPE html>\n<html>\n<head>\n<title>Hotel About</title>\n</head>\n<body>\n<h1>Hotel ATNAVA</h1>\n<p>Blablablabla</p>\n</body>\n</html>",
//    "hotel_check_in": "12:00:00",
//    "hotel_check_out": "14:00:00",
//    "hotel_photo": "http://localhost:8000/uploads/hotel_about/1708140807-1707392574458.jpg"
//}

data class AboutModel(
    val hotel_name: String,
    val hotel_class: Int,
    val hotel_about: String,
    val hotel_check_in: String,
    val hotel_check_out: String,
    val hotel_photo: String,
)
