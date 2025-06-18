package com.application.elevate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "counseling_category")
data class CounselingCategoryEntity(
  @PrimaryKey 
  val id: String,
  
  val name: String,
  
  @ColumnInfo(name = "icon_resource")
  val iconResource: Int,
  
  @ColumnInfo(name = "cached_at")
  val cachedAt: Long = System.currentTimeMillis()
) 