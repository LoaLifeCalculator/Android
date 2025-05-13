package jun.watson

import jun.watson.BuildConfig
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import jun.watson.model.data.ChaosDungeon
import jun.watson.model.data.Guardian
import jun.watson.model.data.Item
import jun.watson.model.data.Raid
import jun.watson.model.dto.CharacterResponseDto
import jun.watson.model.dto.SearchResponseDto
import kotlinx.serialization.json.Json
import jun.watson.model.dto.Resource
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

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

        // 상태바와 네비게이션바를 투명하게 설정
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
                            
                            // 캐릭터 정보가 없는 경우 처리
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
                        NavigationBar {
                            NavigationBarItem(
                                selected = false,
                                onClick = {
                                    finish()
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = "홈") },
                                label = { Text("홈") }
                            )
                        }
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

@Composable
fun ResultContent(
    nickname: String,
    searchResponse: SearchResponseDto?,
    isLoading: Boolean,
    error: String?
) {
    // 재화 상태를 remember로 관리
    val resourceStates = remember(searchResponse) {
        mutableStateMapOf<Item, Double>().apply {
            searchResponse?.resources?.forEach { put(it.item, it.avgPrice) }
        }
    }
    // 재화별 입력값을 String으로 별도 관리
    val priceTexts = remember(resourceStates.keys.toList()) {
        mutableStateMapOf<Item, String>().apply {
            resourceStates.forEach { (item, value) -> put(item, value.toString()) }
        }
    }
    var showResources by remember { mutableStateOf(true) }
    
    // 휴게 사용 여부 상태 관리
    var chaosOption by remember { mutableStateOf(0) } // 0: 휴게 미사용, 1: 휴게 사용, 2: 안보기
    var guardianOption by remember { mutableStateOf(0) } // 0: 휴게 미사용, 1: 휴게 사용, 2: 안보기
    
    // 탭 상태 관리
    var selectedTab by remember { mutableStateOf(-1) } // -1: 닫힘, 0: 필터, 1: 시세

    // 안내 문구 표시 상태
    var showGuide by remember { mutableStateOf(false) }
    
    // 서버별 체크박스 상태를 관리하는 Map
    val serverCheckedStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                // 각 캐릭터별로 체크박스 상태 리스트 생성
                characters.forEach { character ->
                    val availableRaids = Raid.getAvailableRaids(character.level, 6)
                    val checkedStates = List(availableRaids.size) { it < 3 }
                    put("$server:${character.characterName}", checkedStates)
                }
            }
        }
    }

    // 서버별 펼침/접힘 상태 관리
    val expandedStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, Boolean>().apply {
            searchResponse?.expeditions?.expeditions?.keys?.forEach { put(it, true) }
        }
    }

    // 체크박스 상태 변경을 위한 함수
    val updateCheckedState = { server: String, characterName: String, index: Int, checked: Boolean ->
        val key = "$server:$characterName"
        val currentStates = serverCheckedStates[key]
        if (currentStates != null) {
            serverCheckedStates[key] = currentStates.toMutableList().apply {
                this[index] = checked
            }
        }
    }

    // 서버별 골드 획득 캐릭터 상태를 관리하는 Map
    val goldRewardStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                // 레벨 기준으로 정렬하여 상위 6개 캐릭터를 골드 획득 캐릭터로 지정
                val sortedCharacters = characters.sortedByDescending { it.level }
                val initialStates = characters.map { character ->
                    sortedCharacters.indexOf(character) < 6
                }
                put(server, initialStates)
            }
        }
    }

    // 서버별 계산 제외 캐릭터 상태를 관리하는 Map
    val excludedStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                put(server, List(characters.size) { false })
            }
        }
    }

    // 서버별 상세보기 상태를 관리하는 Map
    val detailedViewStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                put(server, List(characters.size) { false })
            }
        }
    }

    // 상세보기 상태 변경을 위한 함수
    val updateDetailedViewState = { server: String, characterName: String, checked: Boolean ->
        val currentStates = detailedViewStates[server]
        if (currentStates != null) {
            val characterIndex = searchResponse?.expeditions?.expeditions?.get(server)?.indexOfFirst { it.characterName == characterName }
            if (characterIndex != null && characterIndex >= 0) {
                detailedViewStates[server] = currentStates.toMutableList().apply {
                    this[characterIndex] = checked
                }
            }
        }
    }

    // 일괄 제외 레벨 입력값
    var batchExcludeLevel by remember { mutableStateOf("") }
    
    // 버튼 상태 관리를 위한 변수들
    var isButtonAnimating by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf("확인") }
    var buttonColor by remember { mutableStateOf<androidx.compose.ui.graphics.Color?>(null) }

    // 일괄 제외 함수
    val batchExcludeByLevel = { level: Int ->
        searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
            val currentStates = excludedStates[server]
            if (currentStates != null) {
                excludedStates[server] = characters.mapIndexed { index, character ->
                    character.level < level
                }
            }
        }
        // 버튼 애니메이션 시작
        isButtonAnimating = true
        buttonText = "완료"
        buttonColor = androidx.compose.ui.graphics.Color.Green
        
        // 1초 후 원래 상태로 복귀
        kotlinx.coroutines.MainScope().launch {
            kotlinx.coroutines.delay(1000)
            isButtonAnimating = false
            buttonText = "확인"
            buttonColor = null
        }
    }

    // 골드 획득 캐릭터 상태 변경을 위한 함수
    val updateGoldRewardState = { server: String, characterName: String, checked: Boolean ->
        val currentStates = goldRewardStates[server]
        if (currentStates != null) {
            val characterIndex = searchResponse?.expeditions?.expeditions?.get(server)?.indexOfFirst { it.characterName == characterName }
            if (characterIndex != null && characterIndex >= 0) {
                goldRewardStates[server] = currentStates.toMutableList().apply {
                    this[characterIndex] = checked
                }
            }
        }
    }

    // 계산 제외 상태 변경을 위한 함수
    val updateExcludedState = { server: String, characterName: String, checked: Boolean ->
        val currentStates = excludedStates[server]
        if (currentStates != null) {
            val characterIndex = searchResponse?.expeditions?.expeditions?.get(server)?.indexOfFirst { it.characterName == characterName }
            if (characterIndex != null && characterIndex >= 0) {
                excludedStates[server] = currentStates.toMutableList().apply {
                    this[characterIndex] = checked
                }
            }
        }
    }

    // 초기 서버 정렬 순서 계산
    val sortedServers = remember(searchResponse?.expeditions?.expeditions) {
        if (searchResponse == null) emptyList()
        else {
            val serverGoldMap = searchResponse.expeditions.expeditions.mapValues { (server, characters) ->
                characters.sumOf { character ->
                    val chaosReward = ChaosDungeon.getSuitableReward(character.level)
                    val chaosTotalGold = chaosReward.run {
                        shards.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                        weaponStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                        armorStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                        leapStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count }
                    }
                    val guardianReward = Guardian.getSuitableReward(character.level)
                    val guardianTotalGold = guardianReward.run {
                        shards.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                        weaponStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                        armorStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                        leapStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count }
                    }
                    val availableRaids = Raid.getAvailableRaids(character.level, 6)
                    val checkedStates = serverCheckedStates["$server:${character.characterName}"] ?: emptyList()
                    val isGoldReward = goldRewardStates[server]?.get(characters.indexOf(character)) ?: false
                    val isExcluded = excludedStates[server]?.get(characters.indexOf(character)) ?: false
                    val raidTotalGold = availableRaids.withIndex()
                        .filter { checkedStates.getOrNull(it.index) == true }
                        .sumOf { (_, raid) ->
                            val reward = if (isGoldReward) raid.getReward(true) else raid.getReward(false)
                            val tradableReward = reward.getRaidTradableReward()
                            val boundReward = reward.getRaidBoundReward()
                            
                            tradableReward.gold.toDouble() +
                            tradableReward.shards.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                            tradableReward.weaponStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                            tradableReward.armorStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count } +
                            tradableReward.leapStones.entries.sumOf { (item, count) -> (resourceStates[item] ?: 0.0) * count }
                        }
                    chaosTotalGold + guardianTotalGold + raidTotalGold
                }
            }
            serverGoldMap
                .toList()
                .sortedByDescending { (_, gold) -> gold }
                .map { it.first }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showGuide = true },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "도움말",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                var searchText by remember { mutableStateOf(nickname) }
                val context = LocalContext.current
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    singleLine = true,
                    placeholder = { Text("닉네임을 입력하세요") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val intent = Intent(context, ResultActivity::class.java).apply {
                                    putExtra("nickname", searchText)
                                }
                                context.startActivity(intent)
                                (context as? ResultActivity)?.finish()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "검색",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            // 탭 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(
                    text = "필터 및 도구",
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "시세",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
                error != null -> {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                searchResponse != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            TotalRewardSummary(
                                expeditions = searchResponse.expeditions.expeditions,
                                resourceMap = resourceStates.mapValues { Resource(it.key, it.value) },
                                serverCheckedStates = serverCheckedStates,
                                goldRewardStates = goldRewardStates,
                                excludedStates = excludedStates,
                                sortedServers = sortedServers,
                                chaosOption = chaosOption,
                                guardianOption = guardianOption
                            )
                        }
                        sortedServers.forEach { server ->
                            val characters = searchResponse.expeditions.expeditions[server] ?: emptyList()
                            val hasCalculableCharacters = characters.any { character ->
                                Raid.getAvailableRaids(character.level, 6).isNotEmpty()
                            }
                            
                            if (hasCalculableCharacters) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = server,
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(vertical = 8.dp).weight(1f)
                                        )
                                        TextButton(onClick = {
                                            expandedStates[server] = !(expandedStates[server] ?: true)
                                        }) {
                                            Text(if (expandedStates[server] == true) "숨기기" else "펼치기")
                                        }
                                    }
                                }
                                if (expandedStates[server] == true) {
                                    characters.forEach { character ->
                                        item {
                                            CharacterCard(
                                                character = character,
                                                resourceMap = resourceStates.mapValues { Resource(it.key, it.value) },
                                                checkedStates = serverCheckedStates["$server:${character.characterName}"] ?: emptyList(),
                                                goldRewardState = goldRewardStates[server]?.get(characters.indexOf(character)) ?: false,
                                                excludedState = excludedStates[server]?.get(characters.indexOf(character)) ?: false,
                                                detailedViewState = detailedViewStates[server]?.get(characters.indexOf(character)) ?: false,
                                                onCheckedChange = { index, checked -> updateCheckedState(server, character.characterName, index, checked) },
                                                onGoldRewardChange = { checked -> updateGoldRewardState(server, character.characterName, checked) },
                                                onExcludedChange = { checked -> updateExcludedState(server, character.characterName, checked) },
                                                onDetailedViewChange = { checked -> updateDetailedViewState(server, character.characterName, checked) },
                                                chaosOption = chaosOption,
                                                guardianOption = guardianOption
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showGuide) {
            val alpha = remember { Animatable(0f) }
            
            LaunchedEffect(Unit) {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
                delay(5000)
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
                showGuide = false
            }
            
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                        .wrapContentSize()
                        .graphicsLayer(alpha = alpha.value),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "캐릭터 카드를 클릭하면 각 캐릭터의 상세 수급량을 확인할 수 있습니다.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "다양한 검색 설정은 '필터 및 도구' 탭에서 적용할 수 있습니다.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "레벨이 일정 이하인 캐릭터는 목록에서 제외됩니다.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 필터 탭 오버레이
        if (selectedTab == 0) {
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

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .offset(x = with(LocalDensity.current) { (slideInState.value * 1000).toInt().dp }),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                        Text(
                            text = "카오스 던전",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                                RadioButton(
                                    selected = chaosOption == 0,
                                    onClick = { chaosOption = 0 }
                )
                Text(
                                    text = "휴게 X",
                    fontSize = 14.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                                RadioButton(
                                    selected = chaosOption == 1,
                                    onClick = { chaosOption = 1 }
                )
                Text(
                                    text = "휴게 O",
                                    fontSize = 14.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = chaosOption == 2,
                                    onClick = { chaosOption = 2 }
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
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = guardianOption == 0,
                                    onClick = { guardianOption = 0 }
            )
            Text(
                                    text = "휴게 X",
                                    fontSize = 14.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = guardianOption == 1,
                                    onClick = { guardianOption = 1 }
                                )
                                Text(
                                    text = "휴게 O",
                                    fontSize = 14.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = guardianOption == 2,
                                    onClick = { guardianOption = 2 }
                                )
                                Text(
                                    text = "계산 X",
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "캐릭터 일괄 제외",
                            fontSize = 16.sp,
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
                                        batchExcludeLevel = it
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                label = { Text("레벨 미만 캐릭터 제외") }
                            )
                            Button(
                                onClick = {
                                    batchExcludeLevel.toIntOrNull()?.let { level ->
                                        batchExcludeByLevel(level)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { selectedTab = -1 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("닫기")
                    }
                }
            }
        }

        // 시세 탭 오버레이
        if (selectedTab == 1) {
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

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .offset(x = with(LocalDensity.current) { (slideInState.value * 1000).toInt().dp }),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            ResourceRow(
                resources = searchResponse?.resources?.map {
                    it.copy(avgPrice = resourceStates[it.item] ?: it.avgPrice)
                } ?: emptyList(),
                onPriceChange = { item, value -> resourceStates[item] = value },
                resourceStates = resourceStates,
                priceTexts = priceTexts
            )
        }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { selectedTab = -1 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}

@Composable
fun UserTitle(nickname: String) {
    Text(
        text = "$nickname 님의 로생 정보",
        fontSize = 24.sp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun ResourceRow(
    resources: List<Resource>,
    onPriceChange: (Item, Double) -> Unit,
    resourceStates: Map<Item, Double>,
    priceTexts: MutableMap<Item, String>
) {
    if (resources.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            resources.forEach { resource ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(
                                painter = painterResource(
                                    id = when (resource.item) {
                                        Item.DESTINY_DESTRUCTION_STONE -> R.drawable.destiny_destruction_stone
                                        Item.REFINED_OBLITERATION_STONE -> R.drawable.refined_obliteration_stone
                                        Item.OBLITERATION_STONE -> R.drawable.obliteration_stone
                                        Item.DESTRUCTION_STONE_CRYSTAL -> R.drawable.destruction_stone_crystal
                                        Item.DESTINY_GUARDIAN_STONE -> R.drawable.destiny_guardian_stone
                                        Item.REFINED_PROTECTION_STONE -> R.drawable.refined_protection_stone
                                        Item.PROTECTION_STONE -> R.drawable.protection_stone
                                        Item.GUARDIAN_STONE_CRYSTAL -> R.drawable.guardian_stone_crystal
                                        Item.DESTINY_SHARD -> R.drawable.destiny_shard
                                        Item.HONOR_SHARD -> R.drawable.honor_shard
                                        Item.DESTINY_LEAPSTONE -> R.drawable.destiny_leapstone
                                        Item.RADIANT_HONOR_LEAPSTONE -> R.drawable.radiant_honor_leapstone
                                        Item.MARVELOUS_HONOR_LEAPSTONE -> R.drawable.marvelous_honor_leapstone
                                        Item.GREAT_HONOR_LEAPSTONE -> R.drawable.great_honor_leapstone
                                    }
                                ),
                                contentDescription = resource.item.korean,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = resource.item.korean,
                                fontSize = 16.sp
                        )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = priceTexts[resource.item] ?: "",
                            onValueChange = {
                                priceTexts[resource.item] = it
                                it.toDoubleOrNull()?.let { value ->
                                    onPriceChange(resource.item, value)
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            label = { Text("시세") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: CharacterResponseDto,
    resourceMap: Map<Item, Resource>,
    checkedStates: List<Boolean>,
    goldRewardState: Boolean,
    excludedState: Boolean,
    detailedViewState: Boolean,
    onCheckedChange: (Int, Boolean) -> Unit,
    onGoldRewardChange: (Boolean) -> Unit,
    onExcludedChange: (Boolean) -> Unit,
    onDetailedViewChange: (Boolean) -> Unit,
    chaosOption: Int,
    guardianOption: Int
) {
    val chaosReward = remember(character.level) {
        ChaosDungeon.getSuitableReward(character.level)
    }
    val chaosTradableGold = remember(chaosReward, resourceMap, chaosOption) {
        if (chaosOption == 2 || excludedState) 0.0 else {
            val tradableReward = chaosReward.getChaosTradableReward()
            var sum = tradableReward.gold.toDouble()
            tradableReward.weaponStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            tradableReward.armorStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            tradableReward.jewelries.forEach { (_, count) ->
                sum += count
            }
            // 일주일 보상 계산 (휴게 사용 시 14/3, 미사용 시 7)
            sum * (if (chaosOption == 1) 14.0/3.0 else 7.0)
        }
    }
    val chaosBoundGold = remember(chaosReward, resourceMap, chaosOption) {
        if (chaosOption == 2 || excludedState) 0.0 else {
            val boundReward = chaosReward.getChaosBoundReward()
            var sum = 0.0
            boundReward.shards.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            boundReward.leapStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            // 일주일 보상 계산 (휴게 사용 시 14/3, 미사용 시 7)
            sum * (if (chaosOption == 1) 14.0/3.0 else 7.0)
    }
    }

    val guardianReward = remember(character.level) {
        Guardian.getSuitableReward(character.level)
    }
    val guardianTradableGold = remember(guardianReward, resourceMap, guardianOption) {
        if (guardianOption == 2 || excludedState) 0.0 else {
            val tradableReward = guardianReward.getGuardianTradableReward()
            var sum = tradableReward.gold.toDouble()
            tradableReward.weaponStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            tradableReward.armorStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            tradableReward.leapStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            tradableReward.jewelries.forEach { (_, count) ->
                sum += count
            }
            // 일주일 보상 계산 (휴게 사용 시 14/3, 미사용 시 7)
            sum * (if (guardianOption == 1) 14.0/3.0 else 7.0)
        }
    }
    val guardianBoundGold = remember(guardianReward, resourceMap, guardianOption) {
        if (guardianOption == 2 || excludedState) 0.0 else {
            val boundReward = guardianReward.getGuardianBoundReward()
            var sum = 0.0
            boundReward.shards.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
            // 일주일 보상 계산 (휴게 사용 시 14/3, 미사용 시 7)
            sum * (if (guardianOption == 1) 14.0/3.0 else 7.0)
    }
    }

    val availableRaids = remember(character.level) {
        Raid.getAvailableRaids(character.level, 6)
    }
    // 레이드 총 보상 계산 (체크된 레이드만)
    val raidRewards = remember(checkedStates, availableRaids, resourceMap, goldRewardState) {
        if (excludedState) Pair(0.0, 0.0) else {
            var tradableGold = 0.0
            var boundGold = 0.0
        availableRaids.withIndex()
            .filter { checkedStates.getOrNull(it.index) == true }
            .forEach { (_, raid) ->
                    val reward = if (goldRewardState) raid.getReward(true) else raid.getReward(false)
                    val tradableReward = reward.getRaidTradableReward()
                    val boundReward = reward.getRaidBoundReward()
                    
                    tradableGold += tradableReward.gold.toDouble()
                    tradableReward.jewelries.forEach { (_, count) ->
                        tradableGold += count
                    }

                    boundReward.shards.forEach { (item: Item, count: Int) ->
                    val price = resourceMap[item]?.avgPrice ?: 0.0
                        boundGold += count * price
                }
                    boundReward.weaponStones.forEach { (item: Item, count: Int) ->
                    val price = resourceMap[item]?.avgPrice ?: 0.0
                        boundGold += count * price
                }
                    boundReward.armorStones.forEach { (item: Item, count: Int) ->
                    val price = resourceMap[item]?.avgPrice ?: 0.0
                        boundGold += count * price
                }
                    boundReward.leapStones.forEach { (item: Item, count: Int) ->
                    val price = resourceMap[item]?.avgPrice ?: 0.0
                        boundGold += count * price
                }
            }
            Pair(tradableGold, boundGold)
    }
    }

    val totalReward = chaosTradableGold + chaosBoundGold + guardianTradableGold + guardianBoundGold + raidRewards.first + raidRewards.second

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onDetailedViewChange(!detailedViewState) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = character.characterName,
                fontSize = 16.sp
            )
                        Image(
                            painter = painterResource(
                                id = when (character.className) {
                                    "버서커" -> R.drawable.berserker
                                    "디스트로이어" -> R.drawable.destroyer
                                    "워로드" -> R.drawable.gunlancer
                                    "홀리나이트" -> R.drawable.paladin
                                    "슬레이어" -> R.drawable.slayer
                                    "배틀마스터" -> R.drawable.wardancer
                                    "인파이터" -> R.drawable.scrapper
                                    "기공사" -> R.drawable.soulfist
                                    "창술사" -> R.drawable.glaivier
                                    "스트라이커" -> R.drawable.striker
                                    "브레이커" -> R.drawable.breaker
                                    "건슬링어" -> R.drawable.gunslinger
                                    "데빌헌터" -> R.drawable.deadeye
                                    "블래스터" -> R.drawable.artillerist
                                    "호크아이" -> R.drawable.sharpshooter
                                    "스카우터" -> R.drawable.machinist
                                    "바드" -> R.drawable.bard
                                    "서머너" -> R.drawable.summoner
                                    "아르카나" -> R.drawable.arcanist
                                    "소서리스" -> R.drawable.sorceress
                                    "블레이드" -> R.drawable.deathblade
                                    "데모닉" -> R.drawable.shadowhunter
                                    "리퍼" -> R.drawable.reaper
                                    "소울이터" -> R.drawable.souleater
                                    "도화가" -> R.drawable.artist
                                    "기상술사" -> R.drawable.aeromancer
                                    "환수사" -> R.drawable.wildsoul
                                    else -> R.drawable.ic_launcher_foreground
                                }
                            ),
                            contentDescription = character.className,
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
            Text(
                text = "레벨: ${character.level}",
                fontSize = 14.sp
            )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
            Text(
                            text = "골드 획득",
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onGoldRewardChange(!goldRewardState) }
                        )
                        Checkbox(
                            checked = goldRewardState,
                            onCheckedChange = onGoldRewardChange,
                            enabled = !excludedState
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "계산 제외",
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onExcludedChange(!excludedState) }
                        )
                        Checkbox(
                            checked = excludedState,
                            onCheckedChange = onExcludedChange
                        )
                    }
                }
            }
            
            if (availableRaids.isEmpty() || excludedState) {
                Text(
                    text = "계산 대상에서 제외됐습니다.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
            Spacer(modifier = Modifier.height(8.dp))
                
            Text(
                    text = "총 보상: ${"%,.0f".format(totalReward)}G",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

                if (detailedViewState) {
                    // 카던 보상 표시
                    if (chaosTradableGold + chaosBoundGold > 0) {
            Text(
                            text = "카던 총 보상: ${"%,.0f".format(chaosTradableGold + chaosBoundGold)}G",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (chaosTradableGold > 0) {
                            Text(
                                text = "  - 거래 가능: ${"%,.0f".format(chaosTradableGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (chaosBoundGold > 0) {
                            Text(
                                text = "  - 귀속: ${"%,.0f".format(chaosBoundGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 가토 보상 표시
                    if (guardianTradableGold + guardianBoundGold > 0) {
                        Text(
                            text = "가토 총 보상: ${"%,.0f".format(guardianTradableGold + guardianBoundGold)}G",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
                        if (guardianTradableGold > 0) {
                            Text(
                                text = "  - 거래 가능: ${"%,.0f".format(guardianTradableGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (guardianBoundGold > 0) {
                            Text(
                                text = "  - 귀속: ${"%,.0f".format(guardianBoundGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // 레이드 보상 표시
                    if (raidRewards.first + raidRewards.second > 0) {
            Text(
                text = "레이드 총 보상: ${"%,.0f".format(raidRewards.first + raidRewards.second)}G",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
                        if (raidRewards.first > 0) {
            Text(
                                text = "  - 거래 가능: ${"%,.0f".format(raidRewards.first)}G",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
                        }
                        if (raidRewards.second > 0) {
            Text(
                                text = "  - 귀속: ${"%,.0f".format(raidRewards.second)}G",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
                        }
                    }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "입장 가능 레이드",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
            Column {
                availableRaids.forEachIndexed { idx, raid ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                                Text(
                                    text = raid.korean,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable { onCheckedChange(idx, !(checkedStates.getOrNull(idx) ?: false)) }
                                )
                        Checkbox(
                            checked = checkedStates.getOrNull(idx) ?: false,
                            onCheckedChange = { checked -> onCheckedChange(idx, checked) }
                        )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TotalRewardSummary(
    expeditions: Map<String, List<CharacterResponseDto>>,
    resourceMap: Map<Item, Resource>,
    serverCheckedStates: Map<String, List<Boolean>>,
    goldRewardStates: Map<String, List<Boolean>>,
    excludedStates: Map<String, List<Boolean>>,
    sortedServers: List<String>,
    chaosOption: Int,
    guardianOption: Int
) {
    // 서버별, 전체 합산 보상 계산
    data class ServerRewards(
        val chaosTradableGold: Double = 0.0,
        val chaosBoundGold: Double = 0.0,
        val guardianTradableGold: Double = 0.0,
        val guardianBoundGold: Double = 0.0,
        val raidTradableGold: Double = 0.0,
        val raidBoundGold: Double = 0.0
    )

    val serverGoldMap = expeditions.mapValues { (server, characters) ->
        var rewards = ServerRewards()
        characters.forEachIndexed { index, character ->
            val isExcluded = excludedStates[server]?.get(index) ?: false
            if (!isExcluded) {
            // 카던 보상
                if (chaosOption != 2) {
            val chaosReward = ChaosDungeon.getSuitableReward(character.level)
                    val tradableReward = chaosReward.getChaosTradableReward()
                    val boundReward = chaosReward.getChaosBoundReward()
                    
                    var tradableGold = tradableReward.gold.toDouble()
                    tradableReward.weaponStones.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        tradableGold += count * price
                    }
                    tradableReward.armorStones.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        tradableGold += count * price
                    }
                    tradableReward.jewelries.forEach { (_, count) ->
                        tradableGold += count
                    }

                    var boundGold = 0.0
                    boundReward.shards.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        boundGold += count * price
                    }
                    boundReward.leapStones.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        boundGold += count * price
                    }

                    // 일주일 보상 계산 (휴게 사용 시 14/3, 미사용 시 7)
                    val weeklyMultiplier = if (chaosOption == 1) 14.0/3.0 else 7.0
                    rewards = rewards.copy(
                        chaosTradableGold = rewards.chaosTradableGold + tradableGold * weeklyMultiplier,
                        chaosBoundGold = rewards.chaosBoundGold + boundGold * weeklyMultiplier
                    )
                }

            // 가토 보상
                if (guardianOption != 2) {
            val guardianReward = Guardian.getSuitableReward(character.level)
                    val guardianTradableReward = guardianReward.getGuardianTradableReward()
                    val guardianBoundReward = guardianReward.getGuardianBoundReward()

                    var guardianTradableGold = guardianTradableReward.gold.toDouble()
                    guardianTradableReward.weaponStones.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        guardianTradableGold += count * price
                    }
                    guardianTradableReward.armorStones.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        guardianTradableGold += count * price
                    }
                    guardianTradableReward.leapStones.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        guardianTradableGold += count * price
                    }
                    guardianTradableReward.jewelries.forEach { (_, count) ->
                        guardianTradableGold += count
                    }

                    var guardianBoundGold = 0.0
                    guardianBoundReward.shards.forEach { (item, count) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                        guardianBoundGold += count * price
                    }

                    // 일주일 보상 계산 (휴게 사용 시 14/3, 미사용 시 7)
                    val weeklyMultiplier = if (guardianOption == 1) 14.0/3.0 else 7.0
                    rewards = rewards.copy(
                        guardianTradableGold = rewards.guardianTradableGold + guardianTradableGold * weeklyMultiplier,
                        guardianBoundGold = rewards.guardianBoundGold + guardianBoundGold * weeklyMultiplier
                    )
                }

            // 레이드 보상
            val availableRaids = Raid.getAvailableRaids(character.level, 6)
            val checkedStates = serverCheckedStates["$server:${character.characterName}"] ?: emptyList()
                val isGoldReward = goldRewardStates[server]?.get(index) ?: false
            availableRaids.withIndex()
                .filter { checkedStates.getOrNull(it.index) == true }
                .forEach { (_, raid) ->
                        val reward = if (isGoldReward) raid.getReward(true) else raid.getReward(false)
                        val raidTradableReward = reward.getRaidTradableReward()
                        val raidBoundReward = reward.getRaidBoundReward()

                        var raidTradableGold = raidTradableReward.gold.toDouble()
                        raidTradableReward.jewelries.forEach { (_, count) ->
                            raidTradableGold += count
                        }

                        var raidBoundGold = 0.0
                        raidBoundReward.shards.forEach { (item: Item, count: Int) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                            raidBoundGold += count * price
                    }
                        raidBoundReward.weaponStones.forEach { (item: Item, count: Int) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                            raidBoundGold += count * price
                    }
                        raidBoundReward.armorStones.forEach { (item: Item, count: Int) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                            raidBoundGold += count * price
                    }
                        raidBoundReward.leapStones.forEach { (item: Item, count: Int) ->
                        val price = resourceMap[item]?.avgPrice ?: 0.0
                            raidBoundGold += count * price
                        }

                        rewards = rewards.copy(
                            raidTradableGold = rewards.raidTradableGold + raidTradableGold,
                            raidBoundGold = rewards.raidBoundGold + raidBoundGold
                        )
                    }
                }
        }
        rewards
    }

    val totalGold = serverGoldMap.values.sumOf { rewards ->
        rewards.chaosTradableGold + rewards.chaosBoundGold +
        rewards.guardianTradableGold + rewards.guardianBoundGold +
        rewards.raidTradableGold + rewards.raidBoundGold
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "로생 요약: ${"%,.0f".format(totalGold)}G",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            sortedServers
                .filter { server -> 
                    val rewards = serverGoldMap[server] ?: ServerRewards()
                    rewards.chaosTradableGold + rewards.chaosBoundGold +
                    rewards.guardianTradableGold + rewards.guardianBoundGold +
                    rewards.raidTradableGold + rewards.raidBoundGold > 0 
                }
                .forEach { server ->
                    val rewards = serverGoldMap[server] ?: ServerRewards()
                    val serverTotal = rewards.chaosTradableGold + rewards.chaosBoundGold +
                                     rewards.guardianTradableGold + rewards.guardianBoundGold +
                                     rewards.raidTradableGold + rewards.raidBoundGold
                    
                    val serverTradableGold = rewards.chaosTradableGold + 
                                            rewards.guardianTradableGold + 
                                            rewards.raidTradableGold
                    
                    val serverBoundGold = rewards.chaosBoundGold + 
                                         rewards.guardianBoundGold + 
                                         rewards.raidBoundGold
                    
                    Text(
                        text = "$server: ${"%,.0f".format(serverTotal)}G",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "  - 거래 가능: ${"%,.0f".format(serverTradableGold)}G",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "  - 귀속: ${"%,.0f".format(serverBoundGold)}G",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun DestroyerImage() {
    Image(
        painter = painterResource(id = R.drawable.destroyer),
        contentDescription = "디스트로이어",
        modifier = Modifier.size(100.dp)
    )
}
