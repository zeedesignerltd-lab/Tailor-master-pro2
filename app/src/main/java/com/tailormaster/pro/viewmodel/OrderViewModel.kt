package com.tailormaster.pro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailormaster.pro.data.Order
import com.tailormaster.pro.data.OrderStatus
import com.tailormaster.pro.data.Repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(private val repo: Repository) : ViewModel() {

    val allOrders: StateFlow<List<Order>> = repo.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCount: StateFlow<Int> = repo.countOrdersByStatus(OrderStatus.PENDING)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val readyCount: StateFlow<Int> = repo.countOrdersByStatus(OrderStatus.READY)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val deliveredCount: StateFlow<Int> = repo.countOrdersByStatus(OrderStatus.DELIVERED)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun ordersForCustomer(customerId: String) = repo.getOrdersByCustomer(customerId)

    fun save(order: Order, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.insertOrder(order)
            onDone()
        }
    }

    fun update(order: Order, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.updateOrder(order)
            onDone()
        }
    }

    fun delete(order: Order, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.deleteOrder(order)
            onDone()
        }
    }
}
