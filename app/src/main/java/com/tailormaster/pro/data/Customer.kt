package com.tailormaster.pro.data

data class Customer(
    val id: String = "",
    val customerCode: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val photoPath: String? = null,
    val notes: String = "",

    // Measurements
    val shoulder: String = "",
    val chest: String = "",
    val waist: String = "",
    val hip: String = "",
    val neck: String = "",
    val sleeve: String = "",
    val shirtLength: String = "",
    val shalwarLength: String = "",
    val bottomWidth: String = "",
    val cuff: String = "",
    val pocket: String = "",
    val collar: String = "",

    val createdAt: Long = System.currentTimeMillis()
)
