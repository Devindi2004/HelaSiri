package com.example.ui.tools

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Moshi Compatible Models for Gemini REST API ---

data class Part(
    val text: String? = null
)

data class Content(
    val parts: List<Part>
)

data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

data class Candidate(
    val content: Content? = null
)

data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

// --- Retrofit Interface ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

// --- Client Instance ---

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }

    /**
     * Call the Gemini API to generate a creative viral Sinhala caption or status.
     * Falls back to a local template if API key is missing or request fails.
     */
    suspend fun generateSinhalaCaption(category: String, isSinglish: Boolean): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        // Check if key is unconfigured (empty or placeholder)
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Logically return a local fallback
            return@withContext getLocalFallbackCaption(category, isSinglish)
        }

        val prompt = if (isSinglish) {
            "Generate a highly engaging, creative, trending, and funny social media caption in Sri Lankan Singlish (Sinhala language written using English letters) about '$category'. It must be colloquial, match Sri Lankan culture and memes, and use some popular local slang (like 'ada', 'bro', 'patta', 'bokka', 'athel'). Keep it brief, under 2 lines. Do not return any other text, just return the caption itself."
        } else {
            "Generate a highly engaging, creative, trending, and emotional social media caption in clean grammatical Sinhala characters (සිංහල අකුරු) about '$category'. It must resonate with Sri Lankan folks on Facebook, Instagram or TikTok. You can use some modern emoji appropriate for Colombo/Sri Lankan street culture. Keep it brief, under 2 lines. Do not return any other text, just return the caption itself."
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are a professional, viral social media influencer from Sri Lanka who knows all hot local trends, memes, and cultural contexts.")))
        )

        try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: getLocalFallbackCaption(category, isSinglish)
        } catch (e: Exception) {
            // Under any connection issue, gracefully fallback
            getLocalFallbackCaption(category, isSinglish)
        }
    }

    private fun getLocalFallbackCaption(category: String, isSinglish: Boolean): String {
        val templates = ToolsData.CAPTION_TEMPLATES[category] ?: ToolsData.CAPTION_TEMPLATES["Life"]!!
        val selected = templates.random()
        return if (isSinglish) selected.singlish else selected.text
    }
}
