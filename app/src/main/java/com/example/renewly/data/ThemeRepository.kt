package com.example.renewly.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class ThemeRepository(private val context: Context) {
    private val KEY_DARK = booleanPreferencesKey("dark_theme")

    val darkFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK] ?: false }

    suspend fun setDark(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK] = enabled }
    }
}