package com.autolearn.ar.ai

import com.autolearn.ar.data.TaskTheory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service that wraps the Google Gemini API.
 *
 * Answers are grounded in the TaskTheory content
 * for the current component — not open-domain chat.
 */
class ChatbotService(
    private val apiKey: String
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    /**
     * Build the system instruction that grounds the
     * chatbot in the specific task's theory content.
     */
    private fun buildSystemInstruction(theory: TaskTheory): String {
        return buildString {
            appendLine("You are the AutoLearn AR AI assistant — an educational chatbot embedded in an automotive training app.")
            appendLine("You are currently helping a student study the following maintenance task:")
            appendLine()
            appendLine("TASK: ${theory.title}")
            appendLine("SUBTITLE: ${theory.subtitle}")
            appendLine()
            appendLine("OVERVIEW:")
            appendLine(theory.overview)
            appendLine()
            appendLine("WHY IT MATTERS:")
            appendLine(theory.whyItMatters)
            appendLine()
            appendLine("LEARNING OBJECTIVES:")
            theory.learningObjectives.forEachIndexed { i, obj ->
                appendLine("${i + 1}. $obj")
            }
            appendLine()
            appendLine("SAFETY WARNINGS:")
            theory.safetyWarnings.forEach { warning ->
                appendLine("⚠️ $warning")
            }
            appendLine()
            appendLine("PROCEDURE STEPS:")
            theory.procedureSteps.forEachIndexed { i, step ->
                appendLine("${i + 1}. $step")
            }
            appendLine()
            appendLine("PRO TIPS:")
            theory.proTips.forEach { tip ->
                appendLine("• $tip")
            }
            appendLine()
            appendLine("IMPORTANT RULES:")
            appendLine("1. Only answer questions related to this automotive maintenance task and general automotive knowledge.")
            appendLine("2. Always prioritize SAFETY in your answers. If a student asks about skipping a safety step, firmly explain why it must not be skipped.")
            appendLine("3. Keep answers concise, practical, and suitable for a vocational student.")
            appendLine("4. If you are unsure about something, say so rather than guessing — incorrect safety advice in an automotive context has real consequences.")
            appendLine("5. Do NOT answer questions unrelated to automotive maintenance or the current task.")
            appendLine("6. Use simple, clear language. Avoid unnecessary jargon.")
        }
    }

    /**
     * Send a user message and get a grounded response.
     *
     * @param theory  The theory content for the current component
     * @param conversationHistory  List of pairs (role, message) representing the chat history
     * @param userMessage  The new message from the user
     * @return The assistant's reply text
     */
    suspend fun sendMessage(
        theory: TaskTheory,
        conversationHistory: List<Pair<String, String>>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {

        if (apiKey.isBlank()) {
            return@withContext "⚠️ AI Chatbot is not configured. Please add your GEMINI_API_KEY to local.properties to enable this feature."
        }

        try {

            // Build the conversation contents array
            val contentsArray = JSONArray()

            // Add conversation history
            for ((role, text) in conversationHistory) {
                val partObj = JSONObject()
                    .put("text", text)

                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))

                contentsArray.put(contentObj)
            }

            // Add the new user message
            val userPart = JSONObject().put("text", userMessage)
            val userContent = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(userPart))
            contentsArray.put(userContent)

            // Build the system instruction
            val systemInstruction = JSONObject()
                .put("parts", JSONArray().put(
                    JSONObject().put("text", buildSystemInstruction(theory))
                ))

            // Build the generation config
            val generationConfig = JSONObject()
                .put("temperature", 0.7)
                .put("topK", 40)
                .put("topP", 0.95)
                .put("maxOutputTokens", 1024)

            // Build full request body
            val requestBody = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstruction)
                .put("generationConfig", generationConfig)

            val request = Request.Builder()
                .url("$apiUrl?key=$apiKey")
                .post(
                    requestBody.toString()
                        .toRequestBody("application/json".toMediaType())
                )
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorJson = try {
                    JSONObject(responseBody)
                } catch (_: Exception) { null }

                val errorMessage = errorJson
                    ?.optJSONObject("error")
                    ?.optString("message")
                    ?: "Unknown error"

                return@withContext "⚠️ API Error (${response.code}): $errorMessage"
            }

            // Parse response
            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")

            if (candidates == null || candidates.length() == 0) {
                return@withContext "I couldn't generate a response. Please try rephrasing your question."
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            if (parts == null || parts.length() == 0) {
                return@withContext "I couldn't generate a response. Please try again."
            }

            parts.getJSONObject(0).optString("text", "No response generated.")

        } catch (e: java.net.UnknownHostException) {
            "⚠️ No internet connection. The AI Chatbot requires an active internet connection to work."
        } catch (e: java.net.SocketTimeoutException) {
            "⚠️ Request timed out. Please check your connection and try again."
        } catch (e: Exception) {
            "⚠️ Error: ${e.message ?: "Something went wrong. Please try again."}"
        }
    }
}
