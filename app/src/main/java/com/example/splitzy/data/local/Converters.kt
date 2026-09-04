package com.example.splitzy.data.local

import androidx.room.TypeConverter
import com.example.splitzy.domain.model.GroupType

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isEmpty()) emptyList() else data.split(",")

    @TypeConverter
    fun fromGroupType(type: GroupType): String = type.name

    @TypeConverter
    fun toGroupType(value: String): GroupType = GroupType.valueOf(value)
}