package com.example.kayara

data class MemberModel(
    val fullName: String,
    val details: String,
    val role: String,
    val avatarColorStart: String = "#0B2E6B",
    val avatarColorEnd: String = "#2A4E92",
    val applicationNote: String = ""   // bag-o: message gikan sa Student pag-apply
)