package com.example.splitzy.domain.repository

interface AuthRepository {
    val currentUserId: String?
    val currentUserEmail: String?
    suspend fun continueWithEmail(email: String, password: String): Result<Unit>
    suspend fun continueWithGoogleIdToken(idToken: String): Result<Unit>
    fun signOut()
}
