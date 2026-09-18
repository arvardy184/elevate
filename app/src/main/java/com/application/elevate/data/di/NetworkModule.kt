package com.application.elevate.di

import android.content.Context
import android.util.Log
import com.application.elevate.BuildConfig
import com.application.elevate.api.CVReviewApiService
import com.application.elevate.data.api.AuthApiService
import com.application.elevate.data.api.CounselingApiService
import com.application.elevate.data.api.CourseApiService
import com.application.elevate.data.datastore.DataStoreManager
import com.application.elevate.data.repository.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import com.application.elevate.data.api.ProfileApiService
import com.application.elevate.data.database.dao.ConsultantDao
import com.application.elevate.data.database.dao.CounselingCategoryDao
import com.application.elevate.data.database.dao.SearchHistoryDao
import com.application.elevate.api.JobMatchingApiService
import com.application.elevate.data.repository.jobmatching.JobMatchingRepository
import com.application.elevate.data.repository.jobmatching.JobMatchingRepositoryImpl
import com.application.elevate.util.NetworkUtil
import com.application.elevate.data.database.dao.CourseDao

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
    fun provideBaseUrl() = BuildConfig.API_BASE_URL

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                if (BuildConfig.DEBUG) {
                    // Do not log URLs, headers, bodies, or backend error messages.
                    Log.d(TAG, "HTTP ${request.method}: ${response.code}")
                }
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
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @CvReviewRetrofit
    @Singleton
    fun provideCvReviewRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
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
    fun provideAuthRepository(api: AuthApiService): AuthRepository =
        AuthRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideCVReviewRepository(
        api: CVReviewApiService,
        cache: CVReviewCache
    ): CVReviewRepository =
        CVReviewRepositoryImpl(api, cache)

    @Provides
    @Singleton
    fun provideJobMatchingRepository(
        api: JobMatchingApiService,
        userRepository: UserRepository
    ): JobMatchingRepository =
        JobMatchingRepositoryImpl(api, userRepository)

    @Provides
    @Singleton
    fun provideCounselingRepository(
        api: CounselingApiService,
        consultantDao: ConsultantDao,
        categoryDao: CounselingCategoryDao,
        searchHistoryDao: SearchHistoryDao
    ): CounselingRepository =
        CounselingRepositoryImpl(api, consultantDao, categoryDao, searchHistoryDao)

    @Provides
    @Singleton
    fun provideDataStoreRepository(dataStoreManager: DataStoreManager): DataStoreRepository =
        DataStoreRepository(dataStoreManager)

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
    fun provideNetworkUtil(@ApplicationContext context: Context): NetworkUtil =
        NetworkUtil(context)

    @Provides
    @Singleton
    fun provideSearchRepository(
        courseApiService: CourseApiService,
        courseDao: CourseDao,
        consultantDao: ConsultantDao,
        searchHistoryDao: SearchHistoryDao,
        counselingRepository: CounselingRepository
    ): SearchRepository =
        SearchRepositoryImpl(
            courseApiService = courseApiService,
            courseDao = courseDao,
            consultantDao = consultantDao,
            searchHistoryDao = searchHistoryDao,
            counselingRepository = counselingRepository
        )
}
