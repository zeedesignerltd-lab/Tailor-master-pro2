package com.tailormaster.pro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tailormaster.pro.data.AuthRepository
import com.tailormaster.pro.data.Repository

class ViewModelFactory(
    private val repo: Repository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            CustomerViewModel::class.java -> CustomerViewModel(repo) as T
            OrderViewModel::class.java -> OrderViewModel(repo) as T
            AuthViewModel::class.java -> AuthViewModel(authRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
