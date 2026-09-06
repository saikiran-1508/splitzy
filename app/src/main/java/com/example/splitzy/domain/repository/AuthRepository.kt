package com.example.splitzy.domain.repository

interface AuthRepository {
    val currentUserId: String?
    val currentUserEmail: String?
    val currentUserName: String?
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun continueWithGoogleIdToken(idToken: String): Result<Unit>
    suspend fun updateDisplayName(name: String): Result<Unit>
    fun signOut()
}
