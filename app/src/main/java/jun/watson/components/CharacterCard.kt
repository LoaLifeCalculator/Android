package jun.watson.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jun.watson.R
import jun.watson.model.data.Item
import jun.watson.model.dto.Resource
import jun.watson.model.dto.CharacterResponseDto
import jun.watson.model.data.ChaosDungeon
import jun.watson.model.data.Guardian
import jun.watson.model.data.Raid

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
            sum * (if (guardianOption == 1) 14.0/3.0 else 7.0)
        }
    }

    val availableRaids = remember(character.level) {
        Raid.getAvailableRaids(character.level, 6)
    }
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