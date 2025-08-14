package com.company.npw.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.company.npw.core.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_USER_ID = stringPreferencesKey(Constants.KEY_USER_ID)
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey(Constants.KEY_IS_LOGGED_IN)
        private val KEY_DARK_MODE = booleanPreferencesKey(Constants.KEY_DARK_MODE)
        private val KEY_LANGUAGE = stringPreferencesKey(Constants.KEY_LANGUAGE)
        private val KEY_FIRST_TIME = booleanPreferencesKey(Constants.KEY_FIRST_TIME)
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey(Constants.KEY_NOTIFICATIONS_ENABLED)
    }

    // User ID
    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }

    suspend fun setUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }

    suspend fun clearUserId() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
        }
    }

    // Login Status
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }

    // Dark Mode
    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: false
    }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = isDarkMode
        }
    }

    // Language
    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_LANGUAGE] ?: Constants.LANGUAGE_ENGLISH
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language
        }
    }

    // First Time
    val isFirstTime: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_FIRST_TIME] ?: true
    }

    suspend fun setFirstTime(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_FIRST_TIME] = isFirstTime
        }
    }

    // Notifications
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // Clear all preferences (for logout)
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
