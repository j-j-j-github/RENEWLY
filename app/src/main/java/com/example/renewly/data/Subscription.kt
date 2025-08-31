package com.example.renewly.data

data class Subscription(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val cycleInDays: Int = 30,
    val nextDueDate: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val iconKey: String = "🧩"
)