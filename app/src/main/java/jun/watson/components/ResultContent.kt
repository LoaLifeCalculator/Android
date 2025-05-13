package jun.watson.components

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jun.watson.R
import jun.watson.ResultActivity
import jun.watson.model.data.ChaosDungeon
import jun.watson.model.data.Guardian
import jun.watson.model.data.Item
import jun.watson.model.data.Raid
import jun.watson.model.dto.Resource
import jun.watson.model.dto.SearchResponseDto
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultContent(
    nickname: String,
    searchResponse: SearchResponseDto?,
    isLoading: Boolean,
    error: String?
) {
    val resourceStates = remember(searchResponse) {
        mutableStateMapOf<Item, Double>().apply {
            searchResponse?.resources?.forEach { put(it.item, it.avgPrice) }
        }
    }
    val priceTexts = remember(resourceStates.keys.toList()) {
        mutableStateMapOf<Item, String>().apply {
            resourceStates.forEach { (item, value) -> put(item, value.toString()) }
        }
    }
    var showResources by remember { mutableStateOf(true) }
    
    var chaosOption by remember { mutableStateOf(0) }
    var guardianOption by remember { mutableStateOf(0) }
    
    var selectedTab by remember { mutableStateOf(-1) }
    var showGuide by remember { mutableStateOf(false) }
    
    val serverCheckedStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                characters.forEach { character ->
                    val availableRaids = Raid.getAvailableRaids(character.level, 6)
                    val checkedStates = List(availableRaids.size) { it < 3 }
                    put("$server:${character.characterName}", checkedStates)
                }
            }
        }
    }

    val expandedStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, Boolean>().apply {
            searchResponse?.expeditions?.expeditions?.keys?.forEach { put(it, true) }
        }
    }

    val updateCheckedState = { server: String, characterName: String, index: Int, checked: Boolean ->
        val key = "$server:$characterName"
        val currentStates = serverCheckedStates[key]
        if (currentStates != null) {
            serverCheckedStates[key] = currentStates.toMutableList().apply {
                this[index] = checked
            }
        }
    }

    val goldRewardStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                val sortedCharacters = characters.sortedByDescending { it.level }
                val initialStates = characters.map { character ->
                    sortedCharacters.indexOf(character) < 6
                }
                put(server, initialStates)
            }
        }
    }

    val excludedStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                put(server, List(characters.size) { false })
            }
        }
    }

    val detailedViewStates = remember(searchResponse?.expeditions?.expeditions) {
        mutableStateMapOf<String, List<Boolean>>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                put(server, List(characters.size) { false })
            }
        }
    }

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

    var batchExcludeLevel by remember { mutableStateOf("") }
    
    var isButtonAnimating by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf("확인") }
    var buttonColor by remember { mutableStateOf<Color?>(null) }

    val batchExcludeByLevel = { level: Int ->
        searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
            val currentStates = excludedStates[server]
            if (currentStates != null) {
                excludedStates[server] = characters.mapIndexed { index, character ->
                    character.level < level
                }
            }
        }
        isButtonAnimating = true
        buttonText = "완료"
        buttonColor = Color.Green
        
        MainScope().launch {
            delay(1000)
            isButtonAnimating = false
            buttonText = "확인"
            buttonColor = null
        }
    }

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