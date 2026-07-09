package com.example.splitzy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.splitzy.data.local.Converters

@Entity(tableName = "groups")
@TypeConverters(Converters::class)
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val memberIds: List<String>
)