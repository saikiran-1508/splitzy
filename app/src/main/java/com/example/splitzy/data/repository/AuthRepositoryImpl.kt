package com.example.splitzy.data.repository

import com.example.splitzy.domain.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

// Bridges Firebase's Task-based API into a suspend function with one small
// helper, instead of pulling in the kotlinx-coroutines-play-services artifact
// just for its .await() extension — one less dependency to version-match.
private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            cont.resume(task.result)
        } else {
            cont.resumeWithException(task.exception ?: IllegalStateException("Firebase task failed with no exception"))
        }
    }
}

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    // One button, not a separate sign-up screen: try to sign in, and if no
    // account exists yet with this email, create one on the spot.
    override suspend fun continueWithEmail(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).awaitResult()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).awaitResult()
                Result.success(Unit)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun continueWithGoogleIdToken(idToken: String): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).awaitResult()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
