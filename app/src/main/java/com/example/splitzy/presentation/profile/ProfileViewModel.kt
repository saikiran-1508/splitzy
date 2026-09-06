package com.example.splitzy.presentation.profile

import androidx.lifecycle.ViewModel
import com.example.splitzy.domain.usecase.GetCurrentUserEmailUseCase
import com.example.splitzy.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserEmail: GetCurrentUserEmailUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    fun currentUserEmail(): String? = getCurrentUserEmail()

    fun signOut() = signOutUseCase()
}
