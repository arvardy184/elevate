package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.ProfileEntity

@Dao
interface ProfileDao {
    
    @Query("SELECT * FROM profile WHERE id = :userId")
    suspend fun getProfile(userId: Int): ProfileEntity?
    
    @Query("SELECT * FROM profile WHERE id = :userId")
    fun getProfileFlow(userId: Int): Flow<ProfileEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
    
    @Update
    suspend fun updateProfile(profile: ProfileEntity)
    
    @Query("DELETE FROM profile WHERE id = :userId")
    suspend fun deleteProfile(userId: Int)
    
    // Untuk sync operations
    @Query("SELECT * FROM profile WHERE isSynced = 0")
    suspend fun getUnsyncedProfiles(): List<ProfileEntity>
    
    @Query("UPDATE profile SET isSynced = 1, syncError = NULL WHERE id = :userId")
    suspend fun markAsSynced(userId: Int)
    
    @Query("UPDATE profile SET isSynced = 0, syncError = :error WHERE id = :userId")
    suspend fun markAsUnsynced(userId: Int, error: String?)
    
    @Query("SELECT COUNT(*) FROM profile")
    suspend fun getProfileCount(): Int
    
    @Query("DELETE FROM profile")
    suspend fun deleteAllProfiles()
} 