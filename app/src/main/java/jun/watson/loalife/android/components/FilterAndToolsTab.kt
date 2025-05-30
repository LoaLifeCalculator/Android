package jun.watson.loalife.android.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FilterAndToolsTab(
    chaosOption: Int,
    onChaosOptionChange: (Int) -> Unit,
    guardianOption: Int,
    onGuardianOptionChange: (Int) -> Unit,
    batchExcludeLevel: String,
    onBatchExcludeLevelChange: (String) -> Unit,
    onBatchExcludeByLevel: (Int) -> Unit,
    sortedServers: List<String>,
    disabledServers: List<String>,
    onDisabledServerChange: (String, Boolean) -> Unit,
    showTradableOnly: Boolean,
    onShowTradableOnlyChange: (Boolean) -> Unit,
    onClose: () -> Unit
) {
    var isButtonAnimating by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf("확인") }
    var buttonColor by remember { mutableStateOf<Color?>(null) }
    var isClosing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val slideInState = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        slideInState.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(isClosing) {
        if (isClosing) {
            slideInState.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
            onClose()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .offset(x = with(LocalDensity.current) { (slideInState.value * 1000).toInt().dp }),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 1. 거래 가능만 보기
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "거래 가능만 보기",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Checkbox(
                            checked = showTradableOnly,
                            onCheckedChange = onShowTradableOnlyChange
                        )
                    }
                }

                // 2. 카오스 던전, 가디언 토벌
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "카오스 던전",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                RadioButton(
                                    selected = chaosOption == 0,
                                    onClick = { onChaosOptionChange(0) }
                                )
                                Text(
                                    text = "매일",
                                    fontSize = 14.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                RadioButton(
                                    selected = chaosOption == 1,
                                    onClick = { onChaosOptionChange(1) }
                                )
                                Text(
                                    text = "휴게만",
                                    fontSize = 14.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                RadioButton(
                                    selected = chaosOption == 2,
                                    onClick = { onChaosOptionChange(2) }
                                )
                                Text(
                                    text = "계산 X",
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "가디언 토벌",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                RadioButton(
                                    selected = guardianOption == 0,
                                    onClick = { onGuardianOptionChange(0) }
                                )
                                Text(
                                    text = "매일",
                                    fontSize = 14.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                RadioButton(
                                    selected = guardianOption == 1,
                                    onClick = { onGuardianOptionChange(1) }
                                )
                                Text(
                                    text = "휴게만",
                                    fontSize = 14.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                RadioButton(
                                    selected = guardianOption == 2,
                                    onClick = { onGuardianOptionChange(2) }
                                )
                                Text(
                                    text = "계산 X",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // 3. 해당 레벨 미만 캐릭터 제외
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "해당 레벨 미만 캐릭터 제외",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = batchExcludeLevel,
                                onValueChange = { 
                                    if (it.isEmpty() || it.toIntOrNull() != null) {
                                        onBatchExcludeLevelChange(it)
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .onKeyEvent { event ->
                                        if (event.key == Key.Enter) {
                                            batchExcludeLevel.toIntOrNull()?.let { level ->
                                                onBatchExcludeByLevel(level)
                                                isButtonAnimating = true
                                                buttonText = "완료"
                                                buttonColor = Color.Green
                                                focusManager.clearFocus()
                                                
                                                MainScope().launch {
                                                    delay(1000)
                                                    isButtonAnimating = false
                                                    buttonText = "확인"
                                                    buttonColor = null
                                                }
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                label = { Text("레벨") }
                            )
                            Button(
                                onClick = {
                                    batchExcludeLevel.toIntOrNull()?.let { level ->
                                        onBatchExcludeByLevel(level)
                                        isButtonAnimating = true
                                        buttonText = "완료"
                                        buttonColor = Color.Green
                                        focusManager.clearFocus()
                                        
                                        MainScope().launch {
                                            delay(1000)
                                            isButtonAnimating = false
                                            buttonText = "확인"
                                            buttonColor = null
                                        }
                                    }
                                },
                                enabled = batchExcludeLevel.isNotEmpty() && !isButtonAnimating,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = buttonColor ?: MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(buttonText)
                            }
                        }
                    }
                }

                // 4. 서버 비활성화
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "서버 비활성화",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 0.dp)
                        ) {
                            sortedServers.forEach { server ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = server in disabledServers,
                                        onCheckedChange = { checked ->
                                            onDisabledServerChange(server, checked)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = server,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { isClosing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("닫기")
            }
        }
    }
} 