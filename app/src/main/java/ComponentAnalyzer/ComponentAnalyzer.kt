package com.autolearn.ar.ai

import android.graphics.Bitmap
import android.util.Base64
import com.autolearn.ar.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class ComponentAnalyzer {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeImage(bitmap: Bitmap): Result<String> =
        withContext(Dispatchers.IO) {

            try {
                val resizedBitmap = resizeBitmap(bitmap)
                val imageBase64 = bitmapToBase64(resizedBitmap)

                val prompt = """
                    You are an automotive component recognition system.

                    Look carefully at the IMAGE.

                    Identify the most clearly visible automotive component.

                    Choose ONE component from this list only:

                    battery
                    engine_oil_cap
                    air_filter
                    radiator_cap
                    coolant_reservoir
                    brake_fluid_reservoir
                    fuse_box
                    washer_fluid_reservoir
                    alternator
                    serpentine_belt
                    engine_cover
                    intake_hose
                    unknown

                    Return ONLY the component ID.

                    Examples:
                    A car battery -> battery
                    Yellow engine oil cap -> engine_oil_cap
                    Air filter box -> air_filter
                    Green coolant tank -> coolant_reservoir

                    Do not explain.
                    Do not add punctuation.
                    Do not use markdown.
                """.trimIndent()

                val parts = JSONArray().apply {

                    put(
                        JSONObject().apply {
                            put("text", prompt)
                        }
                    )

                    put(
                        JSONObject().apply {
                            put(
                                "inline_data",
                                JSONObject().apply {
                                    put("mime_type", "image/jpeg")
                                    put("data", imageBase64)
                                }
                            )
                        }
                    )
                }

                val requestJson = JSONObject().apply {

                    put(
                        "contents",
                        JSONArray().put(
                            JSONObject().apply {
                                put("parts", parts)
                            }
                        )
                    )

                    put(
                        "generationConfig",
                        JSONObject().apply {
                            put("maxOutputTokens", 20)
                        }
                    )
                }

                val requestBody = requestJson
                    .toString()
                    .toRequestBody(
                        "application/json; charset=utf-8".toMediaType()
                    )

                val request = Request.Builder()
                    .url(
                        "https://generativelanguage.googleapis.com/" +
                                "v1beta/models/gemini-3.7-flash:generateContent"
                    )
                    .addHeader(
                        "x-goog-api-key",
                        BuildConfig.GEMINI_API_KEY
                    )
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->

                    val responseText =
                        response.body?.string().orEmpty()

                    if (!response.isSuccessful) {

                        val message = try {
                            JSONObject(responseText)
                                .optJSONObject("error")
                                ?.optString("message")
                        } catch (_: Exception) {
                            null
                        }

                        return@withContext Result.failure(
                            Exception(
                                message
                                    ?: "HTTP ${response.code}: $responseText"
                            )
                        )
                    }

                    val json = JSONObject(responseText)

                    val candidates =
                        json.optJSONArray("candidates")

                    if (
                        candidates == null ||
                        candidates.length() == 0
                    ) {
                        return@withContext Result.success("unknown")
                    }

                    val text = candidates
                        .optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")
                        ?.trim()
                        ?.lowercase()
                        .orEmpty()

                    val componentId =
                        normalizeComponent(text)

                    Result.success(componentId)
                }

            } catch (e: Exception) {

                Result.failure(
                    Exception(
                        e.message ?: "Analysis failed"
                    )
                )
            }
        }

    private fun normalizeComponent(text: String): String {

        return when {

            text.contains("battery") ->
                "battery"

            text.contains("engine_oil_cap") ||
                    text.contains("oil cap") ->
                "engine_oil_cap"

            text.contains("air_filter") ||
                    text.contains("air filter") ->
                "air_filter"

            text.contains("radiator_cap") ||
                    text.contains("radiator cap") ->
                "radiator_cap"

            text.contains("coolant_reservoir") ||
                    text.contains("coolant") ->
                "coolant_reservoir"

            text.contains("brake_fluid") ||
                    text.contains("brake fluid") ->
                "brake_fluid_reservoir"

            text.contains("fuse_box") ||
                    text.contains("fuse box") ->
                "fuse_box"

            text.contains("washer_fluid") ||
                    text.contains("washer fluid") ->
                "washer_fluid_reservoir"

            text.contains("alternator") ->
                "alternator"

            text.contains("serpentine_belt") ||
                    text.contains("serpentine belt") ->
                "serpentine_belt"

            text.contains("engine_cover") ||
                    text.contains("engine cover") ->
                "engine_cover"

            text.contains("intake_hose") ||
                    text.contains("intake hose") ->
                "intake_hose"

            else ->
                "unknown"
        }
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {

        val maxSize = 1024

        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val ratio = minOf(
            maxSize.toFloat() / width.toFloat(),
            maxSize.toFloat() / height.toFloat()
        )

        return Bitmap.createScaledBitmap(
            bitmap,
            (width * ratio).toInt(),
            (height * ratio).toInt(),
            true
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {

        val outputStream = ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            80,
            outputStream
        )

        return Base64.encodeToString(
            outputStream.toByteArray(),
            Base64.NO_WRAP
        )
    }
}