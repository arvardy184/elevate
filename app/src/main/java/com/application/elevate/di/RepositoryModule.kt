package com.application.elevate.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // Removed HomeRepository provider since it's now handled by data.repository.RepositoryModule
} 