package jun.watson.loalife.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import jun.watson.BuildConfig
import jun.watson.loalife.android.components.ResultContent
import jun.watson.loalife.android.model.dto.SearchResponseDto
import kotlinx.serialization.json.Json

class ResultActivity : ComponentActivity() {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }
    private val json = Json { 
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    private suspend fun fetchCharacterData(nickname: String): Result<SearchResponseDto> {
        return try {
            android.util.Log.d("ResultActivity", "Starting API call for nickname: $nickname")
            val response = client.get(BuildConfig.SEARCH_URL) {
                url {
                    parameters.append("name", nickname)
                }
                headers {
                    append("accept", "application/json")
                }
            }
            
            android.util.Log.d("ResultActivity", "API call completed with status: ${response.status}")
            
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.bodyAsText()
                android.util.Log.d("ResultActivity", "API Response body: $responseBody")
                
                try {
                    val searchResponse = json.decodeFromString<SearchResponseDto>(responseBody)
                    validateSearchResponse(searchResponse)
                    Result.success(searchResponse)
                } catch (e: Exception) {
                    android.util.Log.e("ResultActivity", "Failed to parse response: ${e.message}", e)
                    android.util.Log.e("ResultActivity", "Response body was: $responseBody")
                    Result.failure(e)
                }
            } else {
                android.util.Log.e("ResultActivity", "API call failed with status: ${response.status}")
                android.util.Log.e("ResultActivity", "Response body: ${response.bodyAsText()}")
                Result.failure(Exception("API call failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ResultActivity", "Exception during API call: ${e.message}", e)
            android.util.Log.e("ResultActivity", "Stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }

    private fun validateSearchResponse(response: SearchResponseDto): Boolean {
        if (response.expeditions?.expeditions == null) {
            android.util.Log.e("ResultActivity", "Expeditions is null in response")
            return false
        }
        if (response.expeditions.expeditions.isEmpty()) {
            android.util.Log.e("ResultActivity", "Expeditions map is empty")
            return false
        }
        val serverCount = response.expeditions.expeditions.size
        val characterCount = response.expeditions.expeditions.values.sumOf { it.size }
        android.util.Log.d("ResultActivity", "Found $serverCount servers with $characterCount characters")
        return true
    }

    private fun handleError(error: Throwable) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("error", "캐릭터 조회에 실패했습니다")
        }
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nickname = intent.getStringExtra("nickname") ?: ""

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            var searchResponse by remember { mutableStateOf<SearchResponseDto?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(nickname) {
                try {
                    fetchCharacterData(nickname)
                        .onSuccess { response ->
                            searchResponse = response
                        }
                        .onFailure { e ->
                            handleError(e)
                        }
                } finally {
                    isLoading = false
                }
            }

            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    primary = MaterialTheme.colorScheme.primary,
                    secondary = MaterialTheme.colorScheme.secondary,
                    background = MaterialTheme.colorScheme.background
                )
            ) {
                Scaffold(
                    // bottomBar = {
                    //     Footer(
                    //         onHomeClick = { finish() }
                    //     )
                    // },
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ResultContent(
                            nickname = nickname,
                            searchResponse = searchResponse,
                            isLoading = isLoading,
                            error = error
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }
}