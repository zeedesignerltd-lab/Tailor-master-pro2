package com.tailormaster.pro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.tailormaster.pro.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object CodeSent : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state

    var verificationId: String? = null
        private set

    fun isLoggedIn() = authRepo.isLoggedIn()
    fun currentUid() = authRepo.currentUser()?.uid

    fun signInWithGoogle(idToken: String) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepo.signInWithGoogleIdToken(idToken)
                .onSuccess { _state.value = AuthUiState.Success }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "Google sign-in failed") }
        }
    }

    fun startPhoneLogin(phoneNumber: String, activity: android.app.Activity) {
        _state.value = AuthUiState.Loading
        authRepo.startPhoneVerification(
            phoneNumber = phoneNumber,
            activity = activity,
            onCodeSent = { id ->
                verificationId = id
                _state.value = AuthUiState.CodeSent
            },
            onAutoVerified = { credential -> signInWithPhoneCredential(credential) },
            onError = { msg -> _state.value = AuthUiState.Error(msg) }
        )
    }

    fun verifyOtp(code: String) {
        val id = verificationId ?: run {
            _state.value = AuthUiState.Error("Please request the code again")
            return
        }
        val credential = authRepo.verifyOtpCode(id, code)
        signInWithPhoneCredential(credential)
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepo.signInWithPhoneCredential(credential)
                .onSuccess { _state.value = AuthUiState.Success }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "Invalid code") }
        }
    }

    fun resetState() {
        _state.value = AuthUiState.Idle
    }

    fun signOut() {
        authRepo.signOut()
    }
}
