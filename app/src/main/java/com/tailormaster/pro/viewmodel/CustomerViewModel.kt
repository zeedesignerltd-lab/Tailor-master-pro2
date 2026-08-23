package com.tailormaster.pro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailormaster.pro.data.Customer
import com.tailormaster.pro.data.Repository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CustomerViewModel(private val repo: Repository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val customers: StateFlow<List<Customer>> = _query
        .debounce(200)
        .flatMapLatest { q -> if (q.isBlank()) repo.getAllCustomers() else repo.searchCustomers(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerCount: StateFlow<Int> = repo.getCustomerCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onQueryChange(q: String) { _query.value = q }

    suspend fun getById(id: String) = repo.getCustomerById(id)

    suspend fun generateCode(name: String) = repo.generateCustomerCode(name)

    fun save(customer: Customer, onDone: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.insertCustomer(customer)
            onDone(id)
        }
    }

    fun update(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.updateCustomer(customer)
            onDone()
        }
    }

    fun delete(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.deleteCustomer(customer)
            onDone()
        }
    }
}
