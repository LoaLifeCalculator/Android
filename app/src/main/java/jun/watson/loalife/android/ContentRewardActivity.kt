package jun.watson.loalife.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import jun.watson.R
import jun.watson.loalife.android.components.Footer
import jun.watson.loalife.android.model.data.ChaosDungeon
import jun.watson.loalife.android.model.data.Guardian
import jun.watson.loalife.android.model.data.Item
import jun.watson.loalife.android.model.data.Raid
import jun.watson.loalife.android.model.dto.ContentReward
import jun.watson.loalife.android.model.dto.Resource
import kotlinx.serialization.json.Json

class ContentRewardActivity : ComponentActivity() {
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
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentRewardScreen(
                        client = client,
                        json = json,
                        onFinish = { finish() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentRewardScreen(
    client: HttpClient,
    json: Json,
    onFinish: () -> Unit
) {
    var resources by remember { mutableStateOf<List<Resource>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isRaidExpanded by remember { mutableStateOf(false) }
    var isChaosExpanded by remember { mutableStateOf(false) }
    var isGuardianExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = client.get(BuildConfig.RESOURCE_URL) {
                headers {
                    append("accept", "application/json")
                }
            }
            
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.bodyAsText()
                resources = json.decodeFromString(responseBody)
            } else {
                error = "서버 응답 오류: ${response.status}"
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            Footer(
                onHomeClick = onFinish
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn {
                // 레이드 보상 정보
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isRaidExpanded = !isRaidExpanded }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "레이드 보상 정보",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = if (isRaidExpanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = if (isRaidExpanded) "접기" else "펼치기",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                if (isRaidExpanded) {
                    itemsIndexed(Raid.entries.sortedByDescending { it.minimumLevel }) { index, raid ->
                        val cardColor = if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            RaidRewardItem(raid, resources, cardColor)
                        }
                    }
                }

                // 카오스 던전 보상 정보
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isChaosExpanded = !isChaosExpanded }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "카오스 던전 보상 정보",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = if (isChaosExpanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = if (isChaosExpanded) "접기" else "펼치기",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                if (isChaosExpanded) {
                    itemsIndexed(ChaosDungeon.entries.sortedByDescending { it.minimumLevel }.filter { it.minimumLevel > 0 }) { index, chaos ->
                        val cardColor = if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ChaosDungeonRewardItem(chaos, resources, cardColor)
                        }
                    }
                }

                // 가디언 토벌 보상 정보
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isGuardianExpanded = !isGuardianExpanded }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "가디언 토벌 보상 정보",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = if (isGuardianExpanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = if (isGuardianExpanded) "접기" else "펼치기",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                if (isGuardianExpanded) {
                    itemsIndexed(Guardian.entries.sortedByDescending { it.minimumLevel }.filter { it.minimumLevel > 0 }) { index, guardian ->
                        val cardColor = if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            GuardianRewardItem(guardian, resources, cardColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RaidRewardItem(raid: Raid, resources: List<Resource>, cardColor: Color) {
    val resourceMap = resources.associate { it.item to it.avgPrice }
    var isExpanded by remember { mutableStateOf(false) }
    
    // 총 보상 계산 (기본 보상만)
    val goldReward = raid.getReward(true)
    val totalGold = goldReward.gold
    val totalLeapStoneGold = goldReward.leapStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalWeaponStoneGold = goldReward.weaponStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalArmorStoneGold = goldReward.armorStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalShardGold = goldReward.shards.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalGoldValue = totalGold + totalLeapStoneGold + totalWeaponStoneGold + totalArmorStoneGold + totalShardGold
    
    Card(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth(0.96f)
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = raid.korean,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gold),
                        contentDescription = "골드",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${totalGoldValue.toInt()} 골드",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // 골드 보상
                Text(
                    text = "기본 보상",
                    style = MaterialTheme.typography.titleSmall
                )
                RewardContent(goldReward, resourceMap)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 재화 보상
                val nonGoldReward = raid.getReward(false)
                Text(
                    text = "더보기 포함 보상(골드 제외)",
                    style = MaterialTheme.typography.titleSmall
                )
                RewardContent(nonGoldReward, resourceMap)
            }
        }
    }
}

@Composable
fun ChaosDungeonRewardItem(chaos: ChaosDungeon, resources: List<Resource>, cardColor: Color) {
    val resourceMap = resources.associate { it.item to it.avgPrice }
    var isExpanded by remember { mutableStateOf(false) }
    
    // 총 보상 계산 (기본 보상만)
    val reward = chaos.reward
    val totalGold = reward.gold
    val totalLeapStoneGold = reward.leapStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalWeaponStoneGold = reward.weaponStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalArmorStoneGold = reward.armorStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalShardGold = reward.shards.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalGoldValue = totalGold + totalLeapStoneGold + totalWeaponStoneGold + totalArmorStoneGold + totalShardGold
    
    Card(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth(0.92f)
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chaos.minimumLevel.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gold),
                        contentDescription = "골드",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${totalGoldValue.toInt()} 골드",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                RewardContent(reward, resourceMap)
            }
        }
    }
}

@Composable
fun GuardianRewardItem(guardian: Guardian, resources: List<Resource>, cardColor: Color) {
    val resourceMap = resources.associate { it.item to it.avgPrice }
    var isExpanded by remember { mutableStateOf(false) }
    
    // 총 보상 계산 (기본 보상만)
    val reward = guardian.reward
    val totalGold = reward.gold
    val totalLeapStoneGold = reward.leapStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalWeaponStoneGold = reward.weaponStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalArmorStoneGold = reward.armorStones.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalShardGold = reward.shards.entries.sumOf { (item, count) -> count * (resourceMap[item] ?: 0.0) }
    val totalGoldValue = totalGold + totalLeapStoneGold + totalWeaponStoneGold + totalArmorStoneGold + totalShardGold
    
    Card(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth(0.92f)
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guardian.minimumLevel.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gold),
                        contentDescription = "골드",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${totalGoldValue.toInt()} 골드",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                RewardContent(reward, resourceMap)
            }
        }
    }
}

@Composable
fun RewardContent(reward: ContentReward, resourceMap: Map<Item, Double>) {
    Column(
        modifier = Modifier.padding(start = 8.dp)
    ) {
        if (reward.gold > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gold),
                    contentDescription = "골드",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("골드: ${reward.gold}")
            }
        }
        
        reward.leapStones.forEach { (item, count) ->
            val price = resourceMap[item] ?: 0.0
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = item.korean,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${item.korean}: $count (${(count * price).toInt()} 골드)")
            }
        }
        
        reward.weaponStones.forEach { (item, count) ->
            val price = resourceMap[item] ?: 0.0
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = item.korean,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${item.korean}: $count (${(count * price).toInt()} 골드)")
            }
        }
        
        reward.armorStones.forEach { (item, count) ->
            val price = resourceMap[item] ?: 0.0
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = item.korean,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${item.korean}: $count (${(count * price).toInt()} 골드)")
            }
        }
        
        reward.shards.forEach { (item, count) ->
            val price = resourceMap[item] ?: 0.0
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = item.image),
                    contentDescription = item.korean,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${item.korean}: $count (${(count * price).toInt()} 골드)")
            }
        }
        
        reward.gems.forEach { (tier, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = if (tier == 3) R.drawable.gem_tier_3 else R.drawable.gem_tier_4),
                    contentDescription = "$tier 티어 보석",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("$tier 티어 보석: $count")
            }
        }
    }
} 