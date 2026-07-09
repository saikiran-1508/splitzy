package com.example.splitzy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.splitzy.data.local.dao.ExpenseDao
import com.example.splitzy.data.local.dao.GroupDao
import com.example.splitzy.data.local.entity.ExpenseEntity
import com.example.splitzy.data.local.entity.GroupEntity

@Database(
    entities = [ExpenseEntity::class, GroupEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SplitzyDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun groupDao(): GroupDao
}