package jun.watson

import jun.watson.BuildConfig
import android.os.Bundle
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
                    val response = client.get(BuildConfig.SEARCH_URL) {
                        parameter("name", nickname)
                    }
                    if (response.status == HttpStatusCode.OK) {
                        searchResponse = json.decodeFromString(response.bodyAsText())
                    } else {
                        error = "검색에 실패했습니다. (${response.status})"
                    }
                } catch (e: Exception) {
                    error = "오류가 발생했습니다: ${e.message}"
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserTitle(nickname)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Switch(
                checked = showResources,
                onCheckedChange = { showResources = it }
            )
            Text(
                text = if (showResources) "재화 시세 숨기기" else "재화 시세 보기",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        if (showResources) {
            ResourceRow(
                resources = searchResponse?.resources?.map {
                    it.copy(avgPrice = resourceStates[it.item] ?: it.avgPrice)
                } ?: emptyList(),
                onPriceChange = { item, value -> resourceStates[item] = value },
                resourceStates = resourceStates,
                priceTexts = priceTexts
            )
        }
        // 모든 서버를 합친 총 보상 및 서버별 총 보상 표시
        if (searchResponse != null) {
            TotalRewardSummary(
                expeditions = searchResponse.expeditions.expeditions,
                resourceMap = resourceStates.mapValues { Resource(it.key, it.value) }
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
                CharacterList(
                    expeditions = searchResponse.expeditions.expeditions,
                    resourceMap = resourceStates.mapValues { Resource(it.key, it.value) }
                )
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
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(resources) { resource ->
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = resource.item.korean,
                            fontSize = 14.sp
                        )
                        OutlinedTextField(
                            value = priceTexts[resource.item] ?: "",
                            onValueChange = {
                                priceTexts[resource.item] = it
                                it.toDoubleOrNull()?.let { value ->
                                    onPriceChange(resource.item, value)
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .width(120.dp),
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
fun CharacterList(
    expeditions: Map<String, List<CharacterResponseDto>>,
    resourceMap: Map<Item, Resource>
) {
    // 서버별 펼침/접힘 상태 관리
    val expandedStates = remember(expeditions.keys) {
        mutableStateMapOf<String, Boolean>().apply {
            expeditions.keys.forEach { put(it, true) }
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        expeditions.forEach { (server, characters) ->
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
                items(characters) { character ->
                    CharacterCard(character, resourceMap)
                }
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: CharacterResponseDto,
    resourceMap: Map<Item, Resource>
) {
    val chaosReward = remember(character.level) {
        ChaosDungeon.getSuitableReward(character.level)
    }
    val chaosTotalGold = remember(chaosReward, resourceMap) {
        var sum = 0.0
        chaosReward.shards.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        chaosReward.weaponStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        chaosReward.armorStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        chaosReward.leapStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        sum
    }
    val guardianReward = remember(character.level) {
        Guardian.getSuitableReward(character.level)
    }
    val guardianTotalGold = remember(guardianReward, resourceMap) {
        var sum = 0.0
        guardianReward.shards.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        guardianReward.weaponStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        guardianReward.armorStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        guardianReward.leapStones.forEach { (item, count) ->
            val price = resourceMap[item]?.avgPrice ?: 0.0
            sum += count * price
        }
        sum
    }
    val availableRaids = remember(character.level) {
        Raid.getAvailableRaids(character.level, 6)
    }
    val checkedStates = remember { mutableStateListOf<Boolean>() }
    if (checkedStates.size != availableRaids.size) {
        checkedStates.clear()
        checkedStates.addAll(List(availableRaids.size) { it < 3 })
    }
    // 레이드 총 보상 계산 (체크된 레이드만)
    val raidTotalGold = remember(checkedStates.toList(), availableRaids, resourceMap) {
        availableRaids.withIndex().filter { checkedStates.getOrNull(it.index) == true }.sumOf { (idx, raid) ->
            val reward = raid.getReward(true)
            var sum = reward.gold.toDouble()
            reward.shards.forEach { (item, count) ->
                val price = resourceMap[item]?.avgPrice ?: 0.0
                sum += count * price
            }
            reward.weaponStones.forEach { (item, count) ->
                val price = resourceMap[item]?.avgPrice ?: 0.0
                sum += count * price
            }
            reward.armorStones.forEach { (item, count) ->
                val price = resourceMap[item]?.avgPrice ?: 0.0
                sum += count * price
            }
            reward.leapStones.forEach { (item, count) ->
                val price = resourceMap[item]?.avgPrice ?: 0.0
                sum += count * price
            }
            sum
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = character.characterName,
                fontSize = 16.sp
            )
            Text(
                text = "레벨: ${character.level}",
                fontSize = 14.sp
            )
            Text(
                text = "직업: ${character.className}",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "카던 총 보상: ${"%,.0f".format(chaosTotalGold)}G",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "가토 총 보상: ${"%,.0f".format(guardianTotalGold)}G",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "레이드 총 보상: ${"%,.0f".format(raidTotalGold)}G",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
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
                        Checkbox(
                            checked = checkedStates[idx],
                            onCheckedChange = { checked ->
                                checkedStates[idx] = checked
                            }
                        )
                        Text(
                            text = raid.korean,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TotalRewardSummary(
    expeditions: Map<String, List<CharacterResponseDto>>,
    resourceMap: Map<Item, Resource>
) {
    // 서버별, 전체 합산 보상 계산
    val serverGoldMap = expeditions.mapValues { (_, characters) ->
        characters.sumOf { character ->
            val chaosReward = ChaosDungeon.getSuitableReward(character.level)
            val chaosTotalGold = chaosReward.run {
                shards.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                weaponStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                armorStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                leapStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count }
            }
            val guardianReward = Guardian.getSuitableReward(character.level)
            val guardianTotalGold = guardianReward.run {
                shards.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                weaponStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                armorStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                leapStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count }
            }
            // 레이드(상위 3개만 체크된 것으로 가정, 실제 체크박스 상태 반영하려면 상태를 올려야 함)
            val availableRaids = Raid.getAvailableRaids(character.level, 6)
            val raidTotalGold = availableRaids.take(3).sumOf { raid ->
                val reward = raid.getReward(true)
                reward.gold.toDouble() +
                reward.shards.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                reward.weaponStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                reward.armorStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count } +
                reward.leapStones.entries.sumOf { (item, count) -> (resourceMap[item]?.avgPrice ?: 0.0) * count }
            }
            chaosTotalGold + guardianTotalGold + raidTotalGold
        }
    }
    val totalGold = serverGoldMap.values.sum()
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
            serverGoldMap.forEach { (server, gold) ->
                Text(
                    text = "$server: ${"%,.0f".format(gold)}G",
                    fontSize = 15.sp
                )
            }
        }
    }
}
