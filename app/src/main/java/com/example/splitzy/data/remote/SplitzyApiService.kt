package com.example.splitzy.data.remote

import com.example.splitzy.data.remote.dto.ExpenseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SplitzyApiService {

    @GET("groups/{groupId}/expenses")
    suspend fun getExpenses(@Path("groupId") groupId: String): List<ExpenseDto>

    @POST("expenses")
    suspend fun createExpense(@Body expense: ExpenseDto): ExpenseDto
}