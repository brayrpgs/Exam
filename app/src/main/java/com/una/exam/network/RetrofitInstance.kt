package com.una.exam.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.una.exam.common.Constants.API_URL
import com.una.exam.utils.NetworkUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@SuppressLint("StaticFieldLeak")
object RetrofitInstance {
    private const val CACHE_SIZE = 10 * 1024 * 1024 // 10 MB
    private lateinit var cache: Cache
    private lateinit var context: Context

    fun initCache(appContext: Context) {
        context = appContext
        val cacheDir = File(appContext.cacheDir, "http_cache")
        cache = Cache(cacheDir, CACHE_SIZE.toLong())
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(cache)

            .addInterceptor { chain ->
                val request = if (NetworkUtils.isNetworkAvailable(context)) {

                    Log.d("NETWORK", "You're connected - using internet")
                    chain.request().newBuilder()
                        .header("Cache-Control", "no-cache")
                        .build()
                } else {

                    Log.d("NETWORK", "You're disconnected - using caché")
                    chain.request().newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=604800")
                        .build()
                }

                chain.proceed(request)
            }

            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=60")
                    .build()
            }
            .build()
    }

    val api: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)

    }

}