package com.example.splitzy.presentation.auth

enum class AuthMode { SIGN_IN, SIGN_UP }

data class AuthUiState(
    val mode: AuthMode = AuthMode.SIGN_IN,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
