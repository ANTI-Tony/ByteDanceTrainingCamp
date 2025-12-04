package com.example.douyinclone.data.remote

import com.example.douyinclone.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface DoubaoApiService {
    @POST("api/v3/chat/completions")
    suspend fun chat(@Body body: ChatRequest): ChatResponse
}

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessageReq>
)

data class ChatMessageReq(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<Choice> = emptyList()
) {
    data class Choice(
        val message: Message
    ) {
        data class Message(
            val content: String
        )
    }
}

object DoubaoApiClient {
    // 豆包 API 的完整 endpoint（火山引擎）
    private const val BASE_URL = "https://ark.cn-beijing.volces.com/"
    const val DEFAULT_MODEL = "doubao-pro-4k"
    
    fun create(apiKey: String): DoubaoApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY  // 改为 BODY 以便查看详细错误
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DoubaoApiService::class.java)
    }
}
