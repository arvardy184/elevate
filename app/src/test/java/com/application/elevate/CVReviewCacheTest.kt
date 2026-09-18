package com.application.elevate

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.application.elevate.data.database.dao.CVReviewDao
import com.application.elevate.data.database.entity.CVReviewEntity
import com.application.elevate.data.repository.CVReviewCache
import com.application.elevate.data.repository.SessionChangedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CVReviewCacheTest {
    private val tokenKey = stringPreferencesKey("auth_token")
    private lateinit var cache: CVReviewCache
    private lateinit var store: DataStore<Preferences>
    private val dao = MemoryCVReviewDao()

    @Before fun setUp() {
        store = MemoryPreferencesStore()
        cache = CVReviewCache(dao, store)
    }

    private suspend fun login(token: String): String {
        cache.changeSession { store.edit { it[tokenKey] = token } }
        return cache.capture(token)
    }

    @Test fun accountSwitchRemovesPreviousReviews() = runBlocking {
        val first = login("Bearer account-a")
        cache.access(first) { it.insertCVReview(review("a")) }
        val second = login("Bearer account-b")
        assertTrue(cache.access(second) { it.getAllCVReviews().first() }.isEmpty())
    }

    @Test fun lateResponseCannotWriteAfterLogout() = runBlocking {
        val requestSession = login("Bearer account-a")
        cache.changeSession { store.edit { it.remove(tokenKey) } }
        try {
            cache.access(requestSession) { it.insertCVReview(review("late")) }
            fail("A response from a logged-out session must be rejected")
        } catch (_: SessionChangedException) { }
        assertTrue(dao.getAllCVReviews().first().isEmpty())
    }

    @Test fun lateDeleteCannotRemoveNewAccountsRecordWithSameId() = runBlocking {
        val oldSession = login("Bearer account-a")
        val currentSession = login("Bearer account-b")
        cache.access(currentSession) { it.insertCVReview(review("shared-id")) }
        try {
            cache.access(oldSession) { it.deleteCVReviewById("shared-id") }
            fail("A stale delete must be rejected")
        } catch (_: SessionChangedException) { }
        assertNotNull(cache.access(currentSession) { it.getCVReviewById("shared-id") })
    }

    @Test fun loginWithReusedTokenStillInvalidatesOldRequests() = runBlocking {
        val oldSession = login("Bearer same-token")
        val newSession = login("Bearer same-token")
        assertNotEquals(oldSession, newSession)
        try {
            cache.access(oldSession) { it.insertCVReview(review("old")) }
            fail("Session generation must change even when a token is reused")
        } catch (_: SessionChangedException) { }
    }

    @Test fun legacyRowsWithUnknownOwnershipAreDiscarded() = runBlocking {
        store.edit { it[tokenKey] = "Bearer current-account" }
        dao.insertCVReview(review("legacy"))
        val session = cache.capture()
        assertTrue(cache.access(session) { it.getAllCVReviews().first() }.isEmpty())
    }

    @Test fun cacheSurvivesRepositoryRecreationWithinSameSession() = runBlocking {
        val session = login("Bearer current-account")
        cache.access(session) { it.insertCVReview(review("saved")) }
        val recreated = CVReviewCache(dao, store)
        val restoredSession = recreated.capture()
        assertEquals(session, restoredSession)
        assertNotNull(recreated.access(restoredSession) { it.getCVReviewById("saved") })
    }

    @Test fun anonymousUserCannotReadPrivateCache() = runBlocking {
        try {
            cache.capture()
            fail("A signed-out user must not obtain cache access")
        } catch (_: IllegalStateException) { }
    }

    private fun review(id: String) = CVReviewEntity(
        id = id, fileName = "sample.pdf", careerField = "Android", uploadDate = 1,
        overallScore = 1f, technicalScore = 1f, softSkillScore = 1f, experienceScore = 1f,
        aiAnalysis = "Sample", suggestions = "Sample"
    )
}

/** Tests the coordinator independently of Android's on-disk DataStore implementation. */
private class MemoryPreferencesStore : DataStore<Preferences> {
    private val values = MutableStateFlow(emptyPreferences())
    private val mutex = Mutex()
    override val data: Flow<Preferences> = values
    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
        mutex.withLock {
            transform(values.value).toPreferences().also { values.value = it }
        }
}

private class MemoryCVReviewDao : CVReviewDao {
    private val rows = MutableStateFlow<List<CVReviewEntity>>(emptyList())
    override fun getAllCVReviews(): Flow<List<CVReviewEntity>> = rows
    override suspend fun getCVReviewById(id: String) = rows.value.find { it.id == id }
    override suspend fun insertCVReview(cvReview: CVReviewEntity) {
        rows.value = rows.value.filterNot { it.id == cvReview.id } + cvReview
    }
    override suspend fun insertAllCVReviews(cvReviews: List<CVReviewEntity>) {
        cvReviews.forEach { insertCVReview(it) }
    }
    override suspend fun updateCVReview(cvReview: CVReviewEntity) = insertCVReview(cvReview)
    override suspend fun deleteCVReview(cvReview: CVReviewEntity) = deleteCVReviewById(cvReview.id)
    override suspend fun deleteCVReviewById(id: String) { rows.value = rows.value.filterNot { it.id == id } }
    override suspend fun deleteAllCVReviews() { rows.value = emptyList() }
    override suspend fun getReviewCount() = rows.value.size
}
