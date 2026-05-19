package com.gramakhata.data.repository

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// ── Gemini API Models ──────────────────────────────────────────────────────────

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// ── Retrofit Interface ─────────────────────────────────────────────────────────

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// ── AI Repository ──────────────────────────────────────────────────────────────

class AiRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geminiService = retrofit.create(GeminiApiService::class.java)

    // Replace with your actual Gemini API key from Google AI Studio
    private val API_KEY = "YOUR_GEMINI_API_KEY_HERE"

    suspend fun generateDailyCollectionReport(
        shopName: String,
        customerSummaries: List<String>,
        totalOutstanding: Double
    ): Result<String> {
        return try {
            val prompt = buildPrompt(shopName, customerSummaries, totalOutstanding)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )
            val response = geminiService.generateContent(API_KEY, request)
            val text = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: "Unable to generate report"
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateWhatsAppMessage(
        customerName: String,
        shopName: String,
        amountDue: Double
    ): Result<String> {
        return try {
            val prompt = """
                Write a polite WhatsApp reminder message in simple English for a village shopkeeper.
                Customer name: $customerName
                Shop name: $shopName
                Amount due: ₹${"%.2f".format(amountDue)}
                
                Make it friendly, short (2-3 lines), and respectful. Add a namaste greeting.
                Do not add any formatting symbols.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
            )
            val response = geminiService.generateContent(API_KEY, request)
            val text = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: "Namaskara ${customerName}, your due at ${shopName} is ₹${"%.2f".format(amountDue)}. Please pay when convenient."
            Result.success(text)
        } catch (e: Exception) {
            // Fallback message if AI fails
            Result.success("Namaskara $customerName, your due at $shopName is ₹${"%.2f".format(amountDue)}. Please pay when convenient.")
        }
    }

    private fun buildPrompt(
        shopName: String,
        customerSummaries: List<String>,
        totalOutstanding: Double
    ): String {
        return """
            Generate a brief Daily Collection Report for a village grocery shop.
            Shop: $shopName
            Date: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}
            Total Outstanding: ₹${"%.2f".format(totalOutstanding)}
            
            Customer-wise dues:
            ${customerSummaries.joinToString("\n")}
            
            Write a short, clear report in plain English. Include:
            - Total amount pending
            - Top 3 customers with highest dues
            - A simple action recommendation
            Keep it under 150 words. No markdown formatting.
        """.trimIndent()
    }
}
