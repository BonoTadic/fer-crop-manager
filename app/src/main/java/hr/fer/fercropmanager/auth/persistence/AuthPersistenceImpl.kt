package hr.fer.fercropmanager.auth.persistence

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import hr.fer.fercropmanager.auth.service.AuthState
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "token_prefs")
private val TOKEN_KEY = stringPreferencesKey("token_key")

class AuthPersistenceImpl(private val context: Context) : AuthPersistence {

    override val authStateFlow = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]?.let { Json.decodeFromString<AuthState>(it) } ?: AuthState.Idle
    }

    override suspend fun updateAuthState(authState: AuthState) {
        val jsonString = Json.encodeToString(authState)
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = jsonString
        }
    }
}
