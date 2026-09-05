package com.example.splitzy.di

import com.example.splitzy.data.repository.AuthRepositoryImpl
import com.example.splitzy.data.repository.CategoryRepositoryImpl
import com.example.splitzy.data.repository.ExpenseRepositoryImpl
import com.example.splitzy.data.repository.GroupRepositoryImpl
import com.example.splitzy.domain.repository.AuthRepository
import com.example.splitzy.domain.repository.CategoryRepository
import com.example.splitzy.domain.repository.ExpenseRepository
import com.example.splitzy.domain.repository.GroupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        impl: GroupRepositoryImpl
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}