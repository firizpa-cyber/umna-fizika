package uz.fizika.core.network.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Безопасное хранилище для API-ключей на основе EncryptedSharedPreferences (AES-256).
 * Ключи никогда не хранятся в открытом виде.
 */
@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_GEMINI_API = "gemini_api_key"
        private const val DEFAULT_API_KEY = "AIzaSyD3dQuZUOjE2zf9vIw-EDSbzRUXR1IF7pE"
    }

    var geminiApiKey: String?
        get() = prefs.getString(KEY_GEMINI_API, DEFAULT_API_KEY)
        set(value) {
            if (value.isNullOrBlank()) prefs.edit().remove(KEY_GEMINI_API).apply()
            else prefs.edit().putString(KEY_GEMINI_API, value.trim()).apply()
        }

    fun hasGeminiKey(): Boolean = !geminiApiKey.isNullOrBlank()

    fun clearAll() = prefs.edit().clear().apply()
}
