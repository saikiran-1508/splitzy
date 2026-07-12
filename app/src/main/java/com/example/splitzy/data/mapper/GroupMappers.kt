package com.example.splitzy.data.mapper

import com.example.splitzy.data.local.entity.GroupEntity
import com.example.splitzy.domain.model.Group

fun GroupEntity.toDomain() = Group(id = id, name = name, memberIds = memberIds)

fun Group.toEntity() = GroupEntity(id = id, name = name, memberIds = memberIds)
