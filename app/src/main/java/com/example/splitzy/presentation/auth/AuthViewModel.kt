package com.example.splitzy.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitzy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setMode(mode: AuthMode) {
        _uiState.update { it.copy(mode = mode, errorMessage = null) }
    }

    fun submit(email: String, password: String, onSuccess: () -> Unit) {
        val mode = _uiState.value.mode
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter an email and password") }
            return
        }
        if (mode == AuthMode.SIGN_UP && password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = when (mode) {
                AuthMode.SIGN_IN -> authRepository.signIn(email.trim(), password)
                AuthMode.SIGN_UP -> authRepository.signUp(email.trim(), password)
            }
            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.readableMessage(mode))
                    }
                }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.continueWithGoogleIdToken(idToken)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Couldn't sign in with Google")
                    }
                }
        }
    }

    fun googleSignInFailed(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
}

// Firebase's raw messages are long and mention internal error codes; these are
// the cases a user can actually act on.
private fun Throwable.readableMessage(mode: AuthMode): String {
    val raw = message.orEmpty()
    return when {
        raw.contains("already in use", ignoreCase = true) ->
            "That email already has an account — switch to Sign In."
        raw.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
            raw.contains("credential is incorrect", ignoreCase = true) ||
            raw.contains("password is invalid", ignoreCase = true) ->
            if (mode == AuthMode.SIGN_IN) "Wrong email or password. New here? Switch to Sign Up."
            else "Couldn't create that account. Try a different email."
        raw.contains("badly formatted", ignoreCase = true) -> "That doesn't look like a valid email."
        raw.contains("network", ignoreCase = true) -> "No connection. Check your internet and try again."
        else -> raw.ifBlank { "Something went wrong. Try again." }
    }
}
