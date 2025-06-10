package com.application.elevate.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun edit(transform: suspend (MutablePreferences) -> Unit)
    fun getData(): Flow<Preferences>
} 