package com.application.elevate.data.mapper

import com.application.elevate.data.database.entity.CounselingCategoryEntity
import com.application.elevate.model.CounselingCategory

object CounselingCategoryMapper {

  fun toEntity(category: CounselingCategory): CounselingCategoryEntity {
    return CounselingCategoryEntity(
      id = category.id,
      name = category.name,
      iconResource = category.iconResId
    )
  }

  fun fromEntity(entity: CounselingCategoryEntity): CounselingCategory {
    return CounselingCategory(
      id = entity.id,
      name = entity.name,
      iconResId = entity.iconResource
    )
  }

  fun toEntityList(categories: List<CounselingCategory>): List<CounselingCategoryEntity> {
    return categories.map { toEntity(it) }
  }

  fun fromEntityList(entities: List<CounselingCategoryEntity>): List<CounselingCategory> {
    return entities.map { fromEntity(it) }
  }
} 