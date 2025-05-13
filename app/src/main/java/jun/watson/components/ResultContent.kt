package jun.watson.components

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import jun.watson.model.data.RewardCalculator

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
    
    val disabledServers = remember { mutableStateListOf<String>() }

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
        mutableStateMapOf<String, Boolean>().apply {
            searchResponse?.expeditions?.expeditions?.forEach { (server, characters) ->
                characters.forEach { character ->
                    put("$server:${character.characterName}", false)
                }
            }
        }
    }

    val updateGoldRewardState = { server: String, index: Int, checked: Boolean ->
        val currentStates = goldRewardStates[server]
        if (currentStates != null) {
            goldRewardStates[server] = currentStates.toMutableList().apply {
                this[index] = checked
            }
        }
    }

    val updateExcludedState = { server: String, index: Int, checked: Boolean ->
        val currentStates = excludedStates[server]
        if (currentStates != null) {
            excludedStates[server] = currentStates.toMutableList().apply {
                this[index] = checked
            }
        }
    }

    val updateDetailedViewState = { server: String, characterName: String, checked: Boolean ->
        detailedViewStates["$server:$characterName"] = checked
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

    val serverGoldInfo = remember(searchResponse?.expeditions?.expeditions) {
        derivedStateOf {
            if (searchResponse == null) emptyMap()
            else {
                val calculator = RewardCalculator(resourceStates.mapValues { Resource(it.key, it.value) }, chaosOption, guardianOption)
                searchResponse.expeditions.expeditions.mapValues { (server, characters) ->
                    var tradableGold = 0.0
                    var boundGold = 0.0
                    
                    characters.forEach { character ->
                        val isExcluded = excludedStates[server]?.get(characters.indexOf(character)) ?: false
                        
                        if (!isExcluded) {
                            val chaosReward = calculator.calculateChaosReward(character, isExcluded)
                            val guardianReward = calculator.calculateGuardianReward(character, isExcluded)
                            
                            tradableGold += chaosReward.tradableGold + guardianReward.tradableGold
                            boundGold += chaosReward.boundGold + guardianReward.boundGold
                            
                            val availableRaids = Raid.getAvailableRaids(character.level, 6)
                            val checkedStates = serverCheckedStates["$server:${character.characterName}"] ?: emptyList()
                            val isGoldReward = goldRewardStates[server]?.get(characters.indexOf(character)) ?: false
                            
                            availableRaids.withIndex()
                                .filter { checkedStates.getOrNull(it.index) == true }
                                .forEach { (_, raid) ->
                                    val raidReward = calculator.calculateRaidReward(raid, isGoldReward)
                                    tradableGold += raidReward.tradableGold
                                    boundGold += raidReward.boundGold
                                }
                        }
                    }
                    Pair(tradableGold, boundGold)
                }
            }
        }
    }

    val sortedServers = remember(searchResponse?.expeditions?.expeditions) {
        derivedStateOf {
            if (searchResponse == null) emptyList()
            else {
                val serverGoldMap = searchResponse.expeditions.expeditions.mapValues { (server, characters) ->
                    var tradableGold = 0.0
                    var boundGold = 0.0
                    
                    characters.forEach { character ->
                        val chaosReward = ChaosDungeon.getSuitableReward(character.level)
                        val guardianReward = Guardian.getSuitableReward(character.level)
                        val availableRaids = Raid.getAvailableRaids(character.level, 6)
                        
                        // 카오스 던전 보상
                        val chaosTradableReward = chaosReward.getChaosTradableReward()
                        val chaosBoundReward = chaosReward.getChaosBoundReward()
                        
                        // 가디언 토벌 보상
                        val guardianTradableReward = guardianReward.getGuardianTradableReward()
                        val guardianBoundReward = guardianReward.getGuardianBoundReward()
                        
                        // 기본 보상 계산
                        tradableGold += chaosTradableReward.gold.toDouble() + guardianTradableReward.gold.toDouble()
                        boundGold += chaosBoundReward.gold.toDouble() + guardianBoundReward.gold.toDouble()
                        
                        // 아이템 보상 계산
                        chaosTradableReward.weaponStones.forEach { (item, count) ->
                            tradableGold += count * (resourceStates[item] ?: 0.0)
                        }
                        chaosTradableReward.armorStones.forEach { (item, count) ->
                            tradableGold += count * (resourceStates[item] ?: 0.0)
                        }
                        chaosTradableReward.jewelries.forEach { (_, count) ->
                            tradableGold += count
                        }
                        
                        chaosBoundReward.shards.forEach { (item, count) ->
                            boundGold += count * (resourceStates[item] ?: 0.0)
                        }
                        chaosBoundReward.leapStones.forEach { (item, count) ->
                            boundGold += count * (resourceStates[item] ?: 0.0)
                        }
                        
                        guardianTradableReward.weaponStones.forEach { (item, count) ->
                            tradableGold += count * (resourceStates[item] ?: 0.0)
                        }
                        guardianTradableReward.armorStones.forEach { (item, count) ->
                            tradableGold += count * (resourceStates[item] ?: 0.0)
                        }
                        guardianTradableReward.leapStones.forEach { (item, count) ->
                            tradableGold += count * (resourceStates[item] ?: 0.0)
                        }
                        guardianTradableReward.jewelries.forEach { (_, count) ->
                            tradableGold += count
                        }
                        
                        guardianBoundReward.shards.forEach { (item, count) ->
                            boundGold += count * (resourceStates[item] ?: 0.0)
                        }
                        
                        // 레이드 보상 (기본적으로 3개 선택)
                        availableRaids.take(3).forEach { raid ->
                            val reward = raid.getReward(true)
                            val tradableReward = reward.getRaidTradableReward()
                            val boundReward = reward.getRaidBoundReward()
                            
                            tradableGold += tradableReward.gold.toDouble()
                            tradableReward.shards.forEach { (item, count) ->
                                tradableGold += count * (resourceStates[item] ?: 0.0)
                            }
                            tradableReward.weaponStones.forEach { (item, count) ->
                                tradableGold += count * (resourceStates[item] ?: 0.0)
                            }
                            tradableReward.armorStones.forEach { (item, count) ->
                                tradableGold += count * (resourceStates[item] ?: 0.0)
                            }
                            tradableReward.leapStones.forEach { (item, count) ->
                                tradableGold += count * (resourceStates[item] ?: 0.0)
                            }
                            
                            boundReward.shards.forEach { (item, count) ->
                                boundGold += count * (resourceStates[item] ?: 0.0)
                            }
                            boundReward.weaponStones.forEach { (item, count) ->
                                boundGold += count * (resourceStates[item] ?: 0.0)
                            }
                            boundReward.armorStones.forEach { (item, count) ->
                                boundGold += count * (resourceStates[item] ?: 0.0)
                            }
                            boundReward.leapStones.forEach { (item, count) ->
                                boundGold += count * (resourceStates[item] ?: 0.0)
                            }
                        }
                    }
                    Pair(tradableGold, boundGold)
                }
                
                serverGoldMap.entries
                    .filter { (_, gold) -> gold.first + gold.second > 0 }
                    .sortedByDescending { (_, gold) -> gold.first + gold.second }
                    .map { it.key }
            }
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
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            if (searchResponse.expeditions.expeditions != null) {
                                TotalRewardSummary(
                                    expeditions = searchResponse.expeditions.expeditions,
                                    resourceMap = resourceStates.mapValues { Resource(it.key, it.value) },
                                    serverCheckedStates = serverCheckedStates,
                                    goldRewardStates = goldRewardStates,
                                    excludedStates = excludedStates,
                                    sortedServers = sortedServers.value,
                                    chaosOption = chaosOption,
                                    guardianOption = guardianOption,
                                    disabledServers = disabledServers
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    sortedServers.value.forEach { server ->
                                        val characters = searchResponse.expeditions.expeditions[server] ?: return@forEach
                                        val isExpanded = if (server in disabledServers) false else (expandedStates[server] ?: false)
                                        
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = server,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    val goldInfo = serverGoldInfo.value[server]
                                                    if (goldInfo != null) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Text(
                                                                text = "거래 가능: ${"%,.0f".format(goldInfo.first)}G",
                                                                fontSize = 14.sp,
                                                                color = MaterialTheme.colorScheme.primary,
                                                                textDecoration = if (server in disabledServers) TextDecoration.LineThrough else TextDecoration.None
                                                            )
                                                            Text(
                                                                text = "귀속: ${"%,.0f".format(goldInfo.second)}G",
                                                                fontSize = 14.sp,
                                                                color = MaterialTheme.colorScheme.secondary,
                                                                textDecoration = if (server in disabledServers) TextDecoration.LineThrough else TextDecoration.None
                                                            )
                                                        }
                                                    }
                                                }
                                                if (server !in disabledServers) {
                                                    IconButton(onClick = {
                                                        expandedStates[server] = !isExpanded
                                                    }) {
                                                        Icon(
                                                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                            contentDescription = if (isExpanded) "접기" else "펼치기",
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (isExpanded) {
                                            itemsIndexed(characters) { index, character ->
                                                CharacterCard(
                                                    character = character,
                                                    resourceMap = resourceStates.mapValues { Resource(it.key, it.value) },
                                                    checkedStates = serverCheckedStates["$server:${character.characterName}"] ?: emptyList(),
                                                    goldRewardState = goldRewardStates[server]?.get(index) ?: false,
                                                    excludedState = excludedStates[server]?.get(index) ?: false,
                                                    detailedViewState = detailedViewStates["$server:${character.characterName}"] ?: false,
                                                    onCheckedChange = { idx, checked -> updateCheckedState(server, character.characterName, idx, checked) },
                                                    onGoldRewardChange = { checked -> updateGoldRewardState(server, index, checked) },
                                                    onExcludedChange = { checked -> updateExcludedState(server, index, checked) },
                                                    onDetailedViewChange = { checked -> updateDetailedViewState(server, character.characterName, checked) },
                                                    chaosOption = chaosOption,
                                                    guardianOption = guardianOption,
                                                    index = index
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "서버 비활성화",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            sortedServers.value.forEach { server ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = server in disabledServers,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                disabledServers.add(server)
                                            } else {
                                                disabledServers.remove(server)
                                            }
                                        }
                                    )
                                    Text(
                                        text = server,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
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