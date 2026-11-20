package com.cleverkeyboard.app.network

import com.google.gson.annotations.SerializedName

data class GenerateContentRequest(
    @SerializedName("contents")
    val contents: List<Content>,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig? = null
)

data class Content(
    @SerializedName("parts")
    val parts: List<Part>,
    @SerializedName("role")
    val role: String? = null
)

data class Part(
    @SerializedName("text")
    val text: String
)

data class GenerationConfig(
    @SerializedName("temperature")
    val temperature: Float? = 0.4f,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int? = 8192,
    @SerializedName("responseMimeType")
    val responseMimeType: String? = null
)

data class GenerateContentResponse(
    @SerializedName("candidates")
    val candidates: List<Candidate>?,
    @SerializedName("error")
    val error: GeminiError? = null
)

data class Candidate(
    @SerializedName("content")
    val content: ContentResponse?,
    @SerializedName("finishReason")
    val finishReason: String? = null
)

data class ContentResponse(
    @SerializedName("parts")
    val parts: List<PartResponse>?,
    @SerializedName("role")
    val role: String? = null
)

data class PartResponse(
    @SerializedName("text")
    val text: String? = null
)

data class GeminiError(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

