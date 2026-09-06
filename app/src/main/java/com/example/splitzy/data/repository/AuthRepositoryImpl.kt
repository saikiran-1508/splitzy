package com.example.splitzy.data.repository

import com.example.splitzy.domain.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
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

    override val currentUserEmail: String?
        get() = firebaseAuth.currentUser?.email

    override val currentUserName: String?
        get() = firebaseAuth.currentUser?.displayName?.takeIf { it.isNotBlank() }

    // Sign-in and sign-up are separate on purpose. The old "try to sign in and
    // create the account if the user doesn't exist" trick relied on catching
    // FirebaseAuthInvalidUserException, but Firebase's email enumeration
    // protection (on by default) reports an unknown email as
    // INVALID_LOGIN_CREDENTIALS — indistinguishable from a wrong password — so
    // that fallback never fired and new users could never register.
    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).awaitResult()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).awaitResult()
            Result.success(Unit)
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

    override suspend fun updateDisplayName(name: String): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(IllegalStateException("Not signed in"))
        return try {
            user.updateProfile(userProfileChangeRequest { displayName = name }).awaitResult()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
