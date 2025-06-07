//package com.application.elevate.data.api
//
//import android.util.Log
//import com.application.elevate.data.repository.UserRepository
//import kotlinx.coroutines.runBlocking
//import okhttp3.Interceptor
//import okhttp3.Response
//import javax.inject.Inject
//
//class AuthInterceptor @Inject constructor(
//    private val userRepository: UserRepository
//) : Interceptor {
//    private val TAG = "AuthInterceptor"
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val originalRequest = chain.request()
//
//        // Skip auth untuk endpoint login dan register
//        if (originalRequest.url.toString().contains("/auth/login") ||
//            originalRequest.url.toString().contains("/auth/register")) {
//            return chain.proceed(originalRequest)
//        }
//
//        // Dapatkan token dari repository
//        val token = runBlocking {
//            try {
//                userRepository.getAuthToken()
//            } catch (e: Exception) {
//                Log.e(TAG, "Error getting auth token: ${e.message}")
//                null
//            }
//        }
//
//        // Jika tidak ada token, lanjutkan request tanpa header auth
//        if (token == null) {
//            Log.w(TAG, "No auth token found, proceeding without auth header")
//            return chain.proceed(originalRequest)
//        }
//
//        // Tambahkan token ke header
//        val newRequest = originalRequest.newBuilder()
//            .header("Authorization", token)
//            .build()
//
//        Log.d(TAG, "Adding auth header to request: ${originalRequest.url}")
//        return chain.proceed(newRequest)
//    }
//}