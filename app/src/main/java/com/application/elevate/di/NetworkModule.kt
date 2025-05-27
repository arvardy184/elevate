package com.application.elevate.di

import android.util.Log
import com.application.elevate.api.AuthApiService
import com.application.elevate.data.repository.AuthRepository
import com.application.elevate.data.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okio.Buffer

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TAG = "API_LOG"
    
    @Provides
    fun provideBaseUrl() = "http://test2.ciet.site/api/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = HttpLoggingInterceptor { message ->
        Log.d(TAG, "API Response: $message")
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d(TAG, "Sending request: ${request.method} ${request.url}")
                Log.d(TAG, "Request headers: ${request.headers}")
                
                // Log request body jika ada
                request.body?.let { body ->
                    val buffer = Buffer()
                    body.writeTo(buffer)
                    Log.d(TAG, "Request body: ${buffer.readUtf8()}")
                }
                
                val response = chain.proceed(request)
                Log.d(TAG, "Received response: ${response.code} ${response.message}")
                Log.d(TAG, "Response headers: ${response.headers}")
                
                // Log response body
                val responseBody = response.body?.string()
                Log.d(TAG, "Response body: $responseBody")
                
                // Rebuild response karena body sudah dibaca
                response.newBuilder()
                    .body(responseBody?.toResponseBody(response.body?.contentType()))
                    .build()
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApiService): AuthRepository =
        AuthRepositoryImpl(api)
}