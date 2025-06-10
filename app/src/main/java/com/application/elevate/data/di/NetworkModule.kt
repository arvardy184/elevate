package com.application.elevate.di

import android.content.Context
import android.util.Log
import com.application.elevate.data.api.CVReviewApiService
import com.application.elevate.data.api.AuthApiService
import com.application.elevate.data.api.CounselingApiService
import com.application.elevate.data.api.CourseApiService
import com.application.elevate.data.api.AssessmentApiService
import com.application.elevate.data.datastore.DataStoreManager
import com.application.elevate.data.repository.*
import com.application.elevate.data.repository.DataStoreRepositoryImpl
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
import javax.inject.Qualifier
import javax.inject.Singleton
import androidx.room.Room
import com.application.elevate.data.api.ProfileApiService
import com.application.elevate.data.database.AppDatabase
import com.application.elevate.data.database.dao.CVReviewDao
import com.application.elevate.api.JobMatchingApiService
import com.application.elevate.data.repository.jobmatching.JobMatchingRepository
import com.application.elevate.data.repository.jobmatching.JobMatchingRepositoryImpl
import com.application.elevate.util.NetworkUtil

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProdRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CvReviewRetrofit

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
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @ProdRetrofit
    @Singleton
    fun provideProdRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://test2.ciet.site/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @CvReviewRetrofit
    @Singleton
    fun provideCvReviewRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://test2.ciet.site/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(@ProdRetrofit retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideCVReviewApiService(@CvReviewRetrofit retrofit: Retrofit): CVReviewApiService =
        retrofit.create(CVReviewApiService::class.java)

    @Provides
    @Singleton
    fun provideJobMatchingApiService(@ProdRetrofit retrofit: Retrofit): JobMatchingApiService =
        retrofit.create(JobMatchingApiService::class.java)

    @Provides
    @Singleton
    fun provideCounselingApiService(@ProdRetrofit retrofit: Retrofit): CounselingApiService =
        retrofit.create(CounselingApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileApiService(@ProdRetrofit retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideCourseApiService(@ProdRetrofit retrofit: Retrofit): CourseApiService =
        retrofit.create(CourseApiService::class.java)

    @Provides
    @Singleton
    fun provideAssessmentApiService(@ProdRetrofit retrofit: Retrofit): AssessmentApiService =
        retrofit.create(AssessmentApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApiService): AuthRepository =
        AuthRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideCVReviewRepository(
        api: CVReviewApiService,
        dao: CVReviewDao
    ): CVReviewRepository =
        CVReviewRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideJobMatchingRepository(
        api: JobMatchingApiService,
        userRepository: UserRepository
    ): JobMatchingRepository =
        JobMatchingRepositoryImpl(api, userRepository)

    @Provides
    @Singleton
    fun provideCounselingRepository(api: CounselingApiService): CounselingRepository =
        CounselingRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: ProfileApiService,
        userRepository: UserRepository,
        @ApplicationContext context: Context
    ): ProfileRepository =
        ProfileRepositoryImpl(api, userRepository, context)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "elevate_database"
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideCVReviewDao(database: AppDatabase): CVReviewDao =
        database.cvReviewDao()
        
    @Provides
    @Singleton
    fun provideNetworkUtil(@ApplicationContext context: Context): NetworkUtil =
        NetworkUtil(context)
} 