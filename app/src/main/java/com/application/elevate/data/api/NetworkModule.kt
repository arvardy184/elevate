package com.application.elevate.data.api

import android.content.Context
import android.util.Log
import com.application.elevate.data.repository.AuthRepository
import com.application.elevate.data.repository.AuthRepositoryImpl
import com.application.elevate.data.repository.ProfileRepository
import com.application.elevate.data.repository.ProfileRepositoryImpl
import com.application.elevate.data.repository.UserRepository
import com.application.elevate.data.repository.AssessmentRepository
import com.application.elevate.data.repository.CourseRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TAG = "API_LOG"
    
    @Provides
    fun provideBaseUrl() = "http://test2.ciet.site/api/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor { message ->
        if (!message.contains("") && !message.contains("\\u0000")) {
            Log.d(TAG, "API Response: $message")
        }
    }.apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val startTime = System.nanoTime()
                
                Log.d(TAG, "Sending ${request.method} request to: ${request.url}")
                request.body?.contentType()?.let {
                    Log.d(TAG, "Content-Type: $it")
                }
                
                val response = try {
                    chain.proceed(request)
                } catch (e: Exception) {
                    Log.e(TAG, "Request failed: ${e.message}")
                    throw e
                }
                
                val duration = (System.nanoTime() - startTime) / 1_000_000 // Convert to milliseconds
                Log.d(TAG, "Response ${response.code} received in ${duration}ms")
                
                response
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Gunakan Gson yang sudah dikonfigurasi
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApiService): AuthRepository =
        AuthRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: ProfileApiService,
        userRepository: UserRepository,
        @ApplicationContext context: Context
    ): ProfileRepository {
        return ProfileRepositoryImpl(api, userRepository, context)
    }

    @Provides
    @Singleton
    fun provideAssessmentApiService(retrofit: Retrofit): AssessmentApiService =
        retrofit.create(AssessmentApiService::class.java)

    @Provides
    @Singleton
    fun provideAssessmentRepository(api: AssessmentApiService): AssessmentRepository =
        AssessmentRepository(api)

    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService =
        retrofit.create(CourseApiService::class.java)

    @Provides
    @Singleton
    fun provideCourseRepository(api: CourseApiService): CourseRepository =
        CourseRepository(api)
}