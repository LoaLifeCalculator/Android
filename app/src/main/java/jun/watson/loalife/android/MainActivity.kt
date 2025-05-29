package jun.watson.loalife.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import jun.watson.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태바와 네비게이션바를 투명하게 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val errorMessage = intent.getStringExtra("error")

        setContent {
            MaterialTheme {
                MainScreen(errorMessage) { nickname ->
                    val intent = Intent(this, ResultActivity::class.java).apply {
                        putExtra("nickname", nickname)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    errorMessage: String?,
    onSearch: (String) -> Unit
) {
    val context = LocalContext.current
    
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "로생 계산기",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.primary,
                    shadow = Shadow(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        blurRadius = 3f
                    )
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )

            var nickname by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("닉네임을 입력하세요") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (nickname.isNotBlank()) {
                                onSearch(nickname)
                            }
                        }
                    )
                )

                Button(
                    onClick = {
                        if (nickname.isNotBlank()) {
                            onSearch(nickname)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("검색")
                }
            }

            Button(
                onClick = {
                    val intent = Intent(context, ContentRewardActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reward),
                        contentDescription = "컨텐츠 보상",
                        modifier = Modifier.size(32.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "컨텐츠\n보상 보기",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
