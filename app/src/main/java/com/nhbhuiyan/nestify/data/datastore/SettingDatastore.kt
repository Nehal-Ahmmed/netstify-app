package com.nhbhuiyan.nestify.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.settingsDatastore : DataStore<Preferences> by preferencesDataStore("settings")

class SettingDatastore @Inject constructor(
    private val context: Context
) {
    val TAG = "SettingDatastore"

    companion object{
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val BIOMETRIC_ENABLE_KEY = booleanPreferencesKey("biometric_enable")
        val SYNC_ENABLE_KEY= booleanPreferencesKey("sync_enable")
        val FONT_SIZE_KEY= stringPreferencesKey("font_size")
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        Log.d(TAG, "🚀 START setDarkTheme: $enabled")
        try {
            context.settingsDatastore.edit { preferences ->
                preferences[DARK_THEME_KEY] = enabled
                Log.d(TAG, "✅ INSIDE EDIT: Set dark_theme to $enabled")
            }
            Log.d(TAG, "🎉 SUCCESS: dark_theme saved: $enabled")

            // Verify by reading back immediately
            val currentValue = context.settingsDatastore.data.map { it[DARK_THEME_KEY] ?: false }.first()
            Log.d(TAG, "🔍 VERIFY: Immediately read back: $currentValue")

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR in setDarkTheme: ${e.message}", e)
        }
    }
    val isDarkTheme = context.settingsDatastore.data.map { preferences->
        preferences[DARK_THEME_KEY] ?: false
    }

    suspend fun setBiometricLock(isBiometricLockEnabled: Boolean){
        context.settingsDatastore.edit { preferences->
            preferences[BIOMETRIC_ENABLE_KEY] = isBiometricLockEnabled
        }
    }

    val isBiometricLockEnabled = context.settingsDatastore.data.map { preferences->
        preferences[BIOMETRIC_ENABLE_KEY] ?: false
    }

    suspend fun setFontSize(fontsize: String){
        context.settingsDatastore.edit { preferences->
            preferences[FONT_SIZE_KEY] = fontsize
        }
    }

    val fontsize = context.settingsDatastore.data.map { preferences->
        preferences[FONT_SIZE_KEY] ?: "MEDIUM"
    }

    suspend fun setSync(isSyncEnabled: Boolean) {
        context.settingsDatastore.edit { preferences->
            preferences[SYNC_ENABLE_KEY] = isSyncEnabled
        }
    }
    val isSyncEnabled = context.settingsDatastore.data.map { preferences->
        preferences[SYNC_ENABLE_KEY] ?: false
    }

}