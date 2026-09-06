package com.example.splitzy.domain.usecase

import com.example.splitzy.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): String? = repository.currentUserEmail
}
