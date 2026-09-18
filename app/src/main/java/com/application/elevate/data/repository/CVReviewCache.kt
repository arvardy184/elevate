package com.application.elevate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.application.elevate.data.database.dao.CVReviewDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID

/** Serializes private cache access with login/logout. Network calls never hold this lock. */
class CVReviewCache(
    private val dao: CVReviewDao,
    private val store: DataStore<Preferences>
) {
    private val mutex = Mutex()

    suspend fun changeSession(updatePreferences: suspend () -> Unit) = mutex.withLock {
        // Invalidate before clearing: an interrupted transition must never expose old rows.
        withContext(NonCancellable) {
            val session = UUID.randomUUID().toString()
            store.edit { it[SESSION] = session; it.remove(OWNER) }
            dao.deleteAllCVReviews()
            updatePreferences()
            store.edit { it[OWNER] = session }
        }
    }

    suspend fun capture(token: String? = null): String = mutex.withLock {
        val preferences = store.data.first()
        val activeToken = preferences[TOKEN]
        check(!activeToken.isNullOrBlank()) { "Session expired. Please login again." }
        if (token != null && activeToken != token) throw SessionChangedException()
        val session = preferences[SESSION] ?: UUID.randomUUID().toString()
        if (preferences[OWNER] != session) {
            // One-time upgrade cleanup of the legacy cache with unknown ownership.
            dao.deleteAllCVReviews()
            store.edit { it[SESSION] = session; it[OWNER] = session }
        }
        session
    }

    suspend fun <T> access(session: String, action: suspend (CVReviewDao) -> T): T = mutex.withLock {
        val preferences = store.data.first()
        if (preferences[TOKEN].isNullOrBlank() || preferences[SESSION] != session || preferences[OWNER] != session) {
            throw SessionChangedException()
        }
        action(dao)
    }

    private companion object {
        val TOKEN = stringPreferencesKey("auth_token")
        val SESSION = stringPreferencesKey("cv_session_id")
        val OWNER = stringPreferencesKey("cv_cache_session_id")
    }
}

class SessionChangedException : CancellationException("Authentication session changed")
