package uz.fizika.aichat.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.fizika.core.network.security.SecureStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val secureStorage: SecureStorage
) {
    private var _generativeModel: GenerativeModel? = null

    private fun getModel(): GenerativeModel? {
        val apiKey = secureStorage.geminiApiKey ?: return null
        if (_generativeModel == null) {
            _generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = apiKey,
                systemInstruction = content { text("Ты — эксперт-физик и помощник в приложении «Умная Физика». Твоя задача — помогать студентам понимать сложные концепции, решать задачи и объяснять формулы. Твои ответы должны быть точными, научными, но понятными. Используй LaTeX для формул.") }
            )
        }
        return _generativeModel
    }

    fun sendMessageStream(message: String): Flow<String>? {
        val model = getModel() ?: return null
        return model.generateContentStream(message).map { it.text ?: "" }
    }

    fun hasApiKey(): Boolean = secureStorage.hasGeminiKey()
    
    fun saveApiKey(key: String) {
        secureStorage.geminiApiKey = key
    }
}
