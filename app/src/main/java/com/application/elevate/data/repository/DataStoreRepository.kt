package com.application.elevate.data.repository

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.application.elevate.data.datastore.DataStoreManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun edit(transform: suspend (MutablePreferences) -> Unit) {
        dataStoreManager.dataStore.edit(transform)
    }
} 