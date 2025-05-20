package jun.watson.loalife.android

import jun.watson.BuildConfig
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import jun.watson.loalife.android.model.dto.SearchResponseDto
import kotlinx.serialization.json.Json
import jun.watson.loalife.android.components.ResultContent
import jun.watson.loalife.android.components.Footer

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
                            searchResponse = json.decodeFromString(responseBody)
                            android.util.Log.d("ResultActivity", "Successfully parsed response")
                            
                            if (searchResponse?.expeditions?.expeditions == null) {
                                android.util.Log.e("ResultActivity", "Expeditions is null in response")
                                val intent = Intent(this@ResultActivity, MainActivity::class.java).apply {
                                    putExtra("error", "캐릭터 조회에 실패했습니다")
                                }
                                startActivity(intent)
                                finish()
                            } else if (searchResponse?.expeditions?.expeditions?.isEmpty() == true) {
                                android.util.Log.e("ResultActivity", "Expeditions map is empty")
                                val intent = Intent(this@ResultActivity, MainActivity::class.java).apply {
                                    putExtra("error", "캐릭터 조회에 실패했습니다")
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                val serverCount = searchResponse?.expeditions?.expeditions?.size ?: 0
                                val characterCount = searchResponse?.expeditions?.expeditions?.values?.sumOf { it.size } ?: 0
                                android.util.Log.d("ResultActivity", "Found $serverCount servers with $characterCount characters")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ResultActivity", "Failed to parse response: ${e.message}", e)
                            android.util.Log.e("ResultActivity", "Response body was: $responseBody")
                            val intent = Intent(this@ResultActivity, MainActivity::class.java).apply {
                                putExtra("error", "캐릭터 조회에 실패했습니다")
                            }
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        android.util.Log.e("ResultActivity", "API call failed with status: ${response.status}")
                        android.util.Log.e("ResultActivity", "Response body: ${response.bodyAsText()}")
                        val intent = Intent(this@ResultActivity, MainActivity::class.java).apply {
                            putExtra("error", "캐릭터 조회에 실패했습니다")
                        }
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ResultActivity", "Exception during API call: ${e.message}", e)
                    android.util.Log.e("ResultActivity", "Stack trace: ${e.stackTraceToString()}")
                    val intent = Intent(this@ResultActivity, MainActivity::class.java).apply {
                        putExtra("error", "캐릭터 조회에 실패했습니다")
                    }
                    startActivity(intent)
                    finish()
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
                    bottomBar = {
                        Footer(
                            onHomeClick = { finish() }
                        )
                    }
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