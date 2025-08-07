package com.samueljuma.gmsmobile.data.network

import com.samueljuma.gmsmobile.data.models.CreateExpenseDto
import com.samueljuma.gmsmobile.data.models.CreateTrainerPaymentDto
import com.samueljuma.gmsmobile.data.models.CreateUpdatePlanRequest
import com.samueljuma.gmsmobile.data.models.DashboardRequestParams
import com.samueljuma.gmsmobile.data.models.GymUserEntryDto
import com.samueljuma.gmsmobile.data.models.LoginRequest
import com.samueljuma.gmsmobile.data.models.MarkAttendanceRequest
import com.samueljuma.gmsmobile.data.models.PaymentRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class APIService(
    private val client: HttpClient
) {

    suspend fun login(loginRequest: LoginRequest): HttpResponse{
        return client.post("/api/auth/login/"){
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }
    }

    suspend fun logout(){
        client.post("/api/auth/logout/")
    }


    suspend fun fetchDashboardSummary(params: DashboardRequestParams? = null): HttpResponse {
        return client.get("/api/reports/summary/") {
            params?.let { params ->
                url {
                    parameters.append("start_date", params.start_date)
                    parameters.append("end_date", params.end_date)
                }
            }
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun fetchGymUsers(userType: String): HttpResponse {
        return client.get("/api/users/") {
            url { parameters.append("role", userType) }
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun addGymUser(gymUserEntryDto: GymUserEntryDto): HttpResponse {
        return client.post("/api/users/") {
            contentType(ContentType.Application.Json)
            setBody(gymUserEntryDto)
        }
    }

    suspend fun deleteGymUser(userId: Int): HttpResponse {
        return client.delete("/api/users/$userId/")
    }

    suspend fun updateGymUser(userId: Int, gymUser: GymUserEntryDto): HttpResponse{
        return client.patch("/api/users/$userId/"){
            contentType(ContentType.Application.Json)
            setBody(gymUser)
        }
    }

    suspend fun processMemberPayment(paymentRequest: PaymentRequest): HttpResponse {
        return client.post("/api/payments/initiate-payment/") {
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }
    }

    suspend fun checkIfPaymentExists(reference: String): HttpResponse{
        return client.get("/api/payments/check/") {
            url { parameters.append("reference", reference) }
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun confirmMpesaPayment(reference: String): HttpResponse {
        return client.get("/api/payments/mpesa-confirm/") {
            url { parameters.append("reference", reference) }
            contentType(ContentType.Application.Json)
        }
    }
    suspend fun fetchMembersAttendanceList(date: String): HttpResponse {
        return client.get("/api/attendance/attendance-by-date/") {
            url { parameters.append("date", date) }
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun markMembersAttendance(request: MarkAttendanceRequest): HttpResponse {
        return client.post("/api/attendance/mark-bulk-attendance/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun fetchSubscriptionPlans(): HttpResponse {
        return client.get("/api/subscriptions/plans/")
    }

    suspend fun addSubscriptionPlan(request: CreateUpdatePlanRequest): HttpResponse {
        return client.post("/api/subscriptions/plans/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteSubscriptionPlan(planId: Int): HttpResponse {
        return client.delete("/api/subscriptions/plans/$planId/")
    }

    suspend fun updateSubscriptionPlan(planId: Int, request: CreateUpdatePlanRequest): HttpResponse {
        return client.patch("/api/subscriptions/plans/$planId/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun fetchTrainerPayments(): HttpResponse {
        return client.get("/api/trainer-payments/"){
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun createTrainerPayment(request: CreateTrainerPaymentDto): HttpResponse {
        return client.post("/api/trainer-payments/"){
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteTrainerPayment(recordID: Int): HttpResponse {
        return client.delete("/api/trainer-payments/$recordID/")
    }

    suspend fun updateTrainerPayment(recordID: Int, request: CreateTrainerPaymentDto): HttpResponse {
        return client.patch("/api/trainer-payments/$recordID/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun fetchExpenses(): HttpResponse {
        return client.get("/api/expenses/"){
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun createExpense(request: CreateExpenseDto): HttpResponse {
        return client.post("/api/expenses/"){
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteExpense(recordID: Int): HttpResponse {
        return client.delete("/api/expenses/$recordID/")
    }

    suspend fun updateExpense(recordID: Int, request: CreateExpenseDto): HttpResponse {
        return client.patch("/api/expenses/$recordID/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun fetchExpenseCategories(): HttpResponse {
        return client.get("/api/expense-categories/")
    }

}