package com.application.elevate.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.application.elevate.data.datastore.DataStoreManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DataStoreRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : DataStoreRepository {
    
    override suspend fun edit(transform: suspend (MutablePreferences) -> Unit) {
        dataStoreManager.dataStore.edit(transform)
    }
    
    override fun getData(): Flow<Preferences> {
        return dataStoreManager.dataStore.data
    }
} 