package com.example.splitzy.data.remote.dto

import com.squareup.moshi.Json

data class ExpenseDto(
    val id: String,
    @Json(name = "group_id") val groupId: String,
    val description: String,
    val amount: Double,
    @Json(name = "paid_by") val paidByUserId: String,
    @Json(name = "split_between") val splitBetween: List<String>,
    @Json(name = "created_at") val createdAt: Long
)