package com.example.splitzy.di

import android.content.Context
import androidx.room.Room
import com.example.splitzy.data.local.SplitzyDatabase
import com.example.splitzy.data.local.dao.ExpenseDao
import com.example.splitzy.data.local.dao.GroupDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SplitzyDatabase {
        return Room.databaseBuilder(
            context,
            SplitzyDatabase::class.java,
            "splitzy.db"
        ).build()
    }

    @Provides
    fun provideExpenseDao(db: SplitzyDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideGroupDao(db: SplitzyDatabase): GroupDao = db.groupDao()
}