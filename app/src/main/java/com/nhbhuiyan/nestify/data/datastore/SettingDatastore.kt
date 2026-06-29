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
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode") // "system" | "light" | "dark"
        val SYNC_ENABLE_KEY= booleanPreferencesKey("sync_enable")
        val DEFAULT_LEVEL_KEY = androidx.datastore.preferences.core.intPreferencesKey("default_level")
        val DEFAULT_TERM_KEY = androidx.datastore.preferences.core.intPreferencesKey("default_term")
        
        val USER_ROLE_KEY = stringPreferencesKey("user_role")
        val CLASS_GROUP_ID_KEY = stringPreferencesKey("class_group_id")
        val STUDENT_ID_KEY = stringPreferencesKey("student_id")
        val DEPT_CODE_KEY = stringPreferencesKey("dept_code")
    }

    suspend fun setUserSession(role: String, classGroupId: String, studentId: String, deptCode: String) {
        context.settingsDatastore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role
            preferences[CLASS_GROUP_ID_KEY] = classGroupId
            preferences[STUDENT_ID_KEY] = studentId
            preferences[DEPT_CODE_KEY] = deptCode
        }
    }

    suspend fun clearUserSession() {
        context.settingsDatastore.edit { preferences ->
            preferences.remove(USER_ROLE_KEY)
            preferences.remove(CLASS_GROUP_ID_KEY)
            preferences.remove(STUDENT_ID_KEY)
            preferences.remove(DEPT_CODE_KEY)
        }
    }

    val userRole = context.settingsDatastore.data.map { preferences ->
        preferences[USER_ROLE_KEY] ?: "student"
    }

    val classGroupId = context.settingsDatastore.data.map { preferences ->
        preferences[CLASS_GROUP_ID_KEY] ?: ""
    }

    val studentId = context.settingsDatastore.data.map { preferences ->
        preferences[STUDENT_ID_KEY] ?: ""
    }

    val deptCode = context.settingsDatastore.data.map { preferences ->
        preferences[DEPT_CODE_KEY] ?: ""
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

    /** 3-way theme mode: "system" (default), "light", or "dark". */
    suspend fun setThemeMode(mode: String) {
        context.settingsDatastore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }
    val themeMode = context.settingsDatastore.data.map { preferences ->
        preferences[THEME_MODE_KEY] ?: "system"
    }

    suspend fun setSync(isSyncEnabled: Boolean) {
        context.settingsDatastore.edit { preferences->
            preferences[SYNC_ENABLE_KEY] = isSyncEnabled
        }
    }
    val isSyncEnabled = context.settingsDatastore.data.map { preferences->
        preferences[SYNC_ENABLE_KEY] ?: false
    }

    suspend fun setDefaultLevel(level: Int) {
        Log.d(TAG, "🚀 START setDefaultLevel: $level")
        try {
            context.settingsDatastore.edit { preferences ->
                preferences[DEFAULT_LEVEL_KEY] = level
                Log.d(TAG, "✅ INSIDE EDIT: Set default_level to $level")
            }
            Log.d(TAG, "🎉 SUCCESS: default_level saved: $level")
            val currentVal = context.settingsDatastore.data.map { it[DEFAULT_LEVEL_KEY] ?: 2 }.first()
            Log.d(TAG, "🔍 VERIFY: default_level read back: $currentVal")
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR in setDefaultLevel: ${e.message}", e)
        }
    }

    val defaultLevel = context.settingsDatastore.data.map { preferences ->
        val level = preferences[DEFAULT_LEVEL_KEY] ?: 2
        Log.d(TAG, "📖 READ defaultLevel from Datastore: $level")
        level
    }

    suspend fun setDefaultTerm(term: Int) {
        Log.d(TAG, "🚀 START setDefaultTerm: $term")
        try {
            context.settingsDatastore.edit { preferences ->
                preferences[DEFAULT_TERM_KEY] = term
                Log.d(TAG, "✅ INSIDE EDIT: Set default_term to $term")
            }
            Log.d(TAG, "🎉 SUCCESS: default_term saved: $term")
            val currentVal = context.settingsDatastore.data.map { it[DEFAULT_TERM_KEY] ?: 2 }.first()
            Log.d(TAG, "🔍 VERIFY: default_term read back: $currentVal")
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR in setDefaultTerm: ${e.message}", e)
        }
    }

    val defaultTerm = context.settingsDatastore.data.map { preferences ->
        val term = preferences[DEFAULT_TERM_KEY] ?: 2
        Log.d(TAG, "📖 READ defaultTerm from Datastore: $term")
        term
    }

}