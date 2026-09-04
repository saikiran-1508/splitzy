package com.example.splitzy.data.mapper

import com.example.splitzy.data.local.entity.CategoryEntity
import com.example.splitzy.domain.model.Category

fun CategoryEntity.toDomain() = Category(id = id, groupId = groupId, name = name)

fun Category.toEntity() = CategoryEntity(id = id, groupId = groupId, name = name)
