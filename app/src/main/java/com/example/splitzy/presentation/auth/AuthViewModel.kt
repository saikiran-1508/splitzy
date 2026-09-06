package com.example.splitzy.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Tries to sign in; if no account exists yet for this email, the
    // repository creates one — so one "Continue" tap covers both cases.
    fun continueWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Enter an email and password")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            authRepository.continueWithEmail(email.trim(), password)
                .onSuccess {
                    _uiState.value = AuthUiState()
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState(errorMessage = e.message ?: "Couldn't sign in")
                }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            authRepository.continueWithGoogleIdToken(idToken)
                .onSuccess {
                    _uiState.value = AuthUiState()
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState(errorMessage = e.message ?: "Couldn't sign in with Google")
                }
        }
    }

    fun googleSignInFailed(message: String) {
        _uiState.value = AuthUiState(errorMessage = message)
    }
}
