package com.application.elevate.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.application.elevate.data.database.entity.ConsultantEntity

@Dao
interface ConsultantDao {

  @Query("SELECT * FROM consultant ORDER BY average_rating DESC, total_sessions DESC")
  fun getAllConsultants(): Flow<List<ConsultantEntity>>
  
  @Query("SELECT * FROM consultant WHERE specialization = :specialization ORDER BY average_rating DESC")
  fun getConsultantsBySpecialization(specialization: String): Flow<List<ConsultantEntity>>
  
  @Query("SELECT * FROM consultant WHERE id = :consultantId")
  suspend fun getConsultantById(consultantId: Int): ConsultantEntity?
  
  @Query("SELECT * FROM consultant WHERE first_name LIKE '%' || :query || '%' OR last_name LIKE '%' || :query || '%' OR specialization LIKE '%' || :query || '%'")
  fun searchConsultants(query: String): Flow<List<ConsultantEntity>>
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConsultants(consultants: List<ConsultantEntity>)
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConsultant(consultant: ConsultantEntity)
  
  @Update
  suspend fun updateConsultant(consultant: ConsultantEntity)
  
  @Delete
  suspend fun deleteConsultant(consultant: ConsultantEntity)
  
  @Query("DELETE FROM consultant")
  suspend fun deleteAllConsultants()
  
  // Cache management
  @Query("SELECT COUNT(*) FROM consultant")
  suspend fun getConsultantCount(): Int
  
  @Query("SELECT * FROM consultant WHERE cached_at < :timestamp")
  suspend fun getStaleConsultants(timestamp: Long): List<ConsultantEntity>
  
  @Query("DELETE FROM consultant WHERE cached_at < :timestamp")
  suspend fun deleteStaleConsultants(timestamp: Long): Int
  
  // Get recently viewed consultants
  @Query("SELECT * FROM consultant ORDER BY last_updated DESC LIMIT :limit")
  suspend fun getRecentConsultants(limit: Int = 5): List<ConsultantEntity>
} 