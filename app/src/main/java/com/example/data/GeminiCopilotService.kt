package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiCopilotService {
    private const val TAG = "GeminiCopilotService"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(prompt: String, systemInstruction: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            "MY_GEMINI_API_KEY"
        }
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Error: Gemini API Key is missing or offline. Please add your real GEMINI_API_KEY via the Secrets panel in AI Studio to activate the live assistant."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        val mediaType = "application/json; charset=utf-8".toMediaType()
        
        try {
            // Build direct JSON
            val requestJson = JSONObject()
            
            // Contents
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestJson.put("contents", contentsArray)

            // System Instruction
            if (systemInstruction.isNotEmpty()) {
                val systemInstructionObj = JSONObject()
                val siPartsArray = JSONArray()
                val siPartObj = JSONObject()
                siPartObj.put("text", systemInstruction)
                siPartsArray.put(siPartObj)
                systemInstructionObj.put("parts", siPartsArray)
                requestJson.put("systemInstruction", systemInstructionObj)
            }
            
            val requestBody = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
                
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val code = response.code
                    val errorString = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful response: $code, $errorString")
                    return@withContext "API Call Failed (Code $code): ${parseErrorMessage(errorString)}"
                }
                
                val responseBodyStr = response.body?.string() ?: return@withContext "Error: Empty response body"
                val jsonResponse = JSONObject(responseBodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext "The assistant couldn't generate a safe response. Please try rephrasing your request."
                }
                
                val firstCandidateObj = candidates.getJSONObject(0)
                val contentObj = firstCandidateObj.optJSONObject("content") ?: return@withContext "Error: Missing content object"
                val partsArray = contentObj.optJSONArray("parts") ?: return@withContext "Error: Missing parts array"
                if (partsArray.length() == 0) {
                     return@withContext "Error: No text parts returned"
                }
                
                return@withContext partsArray.getJSONObject(0).optString("text", "No text content found.")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network exception during Gemini API call", e)
            return@withContext "Network Error: Unable to reach Gemini server. Please check your internet connectivity."
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception during Gemini API call", e)
            return@withContext "Unexpected Error: ${e.localizedMessage ?: "Unknown error occurred"}"
        }
    }
    
    private fun parseErrorMessage(errorString: String): String {
        return try {
            val json = JSONObject(errorString)
            val error = json.getJSONObject("error")
            error.getString("message")
        } catch (e: Exception) {
            "An error occurred while calling the Gemini API."
        }
    }
}
