package com.tailormaster.pro.data

enum class OrderStatus { PENDING, READY, DELIVERED }

data class Order(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val dressType: String = "",
    val color: String = "",
    val quantity: Int = 1,
    val advancePayment: Double = 0.0,
    val totalAmount: Double = 0.0,
    val deliveryDate: Long = 0L,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
) {
    val remainingPayment: Double get() = totalAmount - advancePayment
}
