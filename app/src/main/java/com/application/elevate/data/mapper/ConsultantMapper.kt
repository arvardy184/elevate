package com.application.elevate.data.mapper

import com.application.elevate.data.database.entity.ConsultantEntity
import com.application.elevate.model.Consultant
import com.application.elevate.model.CounselorUser
import com.application.elevate.model.SessionCount

object ConsultantMapper {

  fun toEntity(consultant: Consultant): ConsultantEntity {
    return ConsultantEntity(
      id = consultant.id,
      userId = consultant.userId,
      specialization = consultant.specialization,
      bio = consultant.bio,
      verified = consultant.verified,
      firstName = consultant.users.firstName,
      lastName = consultant.users.lastName,
      email = consultant.users.email,
      totalSessions = consultant.totalSessions,
      averageRating = consultant.averageRating,
      sessionCount = consultant.count.counselingsession
    )
  }

  fun fromEntity(entity: ConsultantEntity): Consultant {
    return Consultant(
      id = entity.id,
      userId = entity.userId,
      specialization = entity.specialization,
      bio = entity.bio,
      verified = entity.verified,
      users = CounselorUser(
        firstName = entity.firstName,
        lastName = entity.lastName,
        email = entity.email
      ),
      count = SessionCount(
        counselingsession = entity.sessionCount
      ),
      averageRating = entity.averageRating,
      totalSessions = entity.totalSessions
    )
  }

  fun toEntityList(consultants: List<Consultant>): List<ConsultantEntity> {
    return consultants.map { toEntity(it) }
  }

  fun fromEntityList(entities: List<ConsultantEntity>): List<Consultant> {
    return entities.map { fromEntity(it) }
  }
} 