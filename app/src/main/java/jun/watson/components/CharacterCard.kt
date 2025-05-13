package jun.watson.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import jun.watson.model.data.RewardCalculator

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
    guardianOption: Int,
    index: Int
) {
    val calculator = remember(resourceMap, chaosOption, guardianOption) {
        RewardCalculator(resourceMap, chaosOption, guardianOption)
    }

    val chaosReward = remember(character, excludedState, calculator) {
        calculator.calculateChaosReward(character, excludedState)
    }

    val guardianReward = remember(character, excludedState, calculator) {
        calculator.calculateGuardianReward(character, excludedState)
    }

    val raidRewards = remember(character, checkedStates, goldRewardState, calculator) {
        val availableRaids = Raid.getAvailableRaids(character.level, 6)
        availableRaids.withIndex()
            .filter { checkedStates.getOrNull(it.index) == true }
            .map { (_, raid) -> calculator.calculateRaidReward(raid, goldRewardState) }
    }

    val totalTradableGold = chaosReward.tradableGold + guardianReward.tradableGold + raidRewards.sumOf { it.tradableGold }
    val totalBoundGold = chaosReward.boundGold + guardianReward.boundGold + raidRewards.sumOf { it.boundGold }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (index % 2 == 0) 
            MaterialTheme.colorScheme.surfaceVariant
        else 
            MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
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
                        contentScale = ContentScale.Fit,
                        colorFilter = if (index % 2 == 1) ColorFilter.tint(Color.DarkGray) else null
                    )
                }
                Text(
                    text = "레벨: ${character.level}",
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(end = 16.dp)
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
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
            
            if (excludedState) {
                Text(
                    text = "계산 대상에서 제외됐습니다.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gold),
                        contentDescription = "골드",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "총 보상: ${"%,.0f".format(totalTradableGold + totalBoundGold)}G",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (detailedViewState) {
                    if (chaosReward.tradableGold > 0) {
                        Text(
                            text = "카던 총 보상: ${"%,.0f".format(chaosReward.tradableGold + chaosReward.boundGold)}G",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (chaosReward.tradableGold > 0) {
                            Text(
                                text = "  - 거래 가능: ${"%,.0f".format(chaosReward.tradableGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (chaosReward.boundGold > 0) {
                            Text(
                                text = "  - 귀속: ${"%,.0f".format(chaosReward.boundGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (guardianReward.tradableGold > 0) {
                        Text(
                            text = "가토 총 보상: ${"%,.0f".format(guardianReward.tradableGold + guardianReward.boundGold)}G",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (guardianReward.tradableGold > 0) {
                            Text(
                                text = "  - 거래 가능: ${"%,.0f".format(guardianReward.tradableGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        if (guardianReward.boundGold > 0) {
                            Text(
                                text = "  - 귀속: ${"%,.0f".format(guardianReward.boundGold)}G",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    if (raidRewards.sumOf { it.tradableGold } > 0) {
                        Text(
                            text = "레이드 총 보상: ${"%,.0f".format(raidRewards.sumOf { it.tradableGold })}G",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        raidRewards.forEach { reward ->
                            if (reward.tradableGold > 0) {
                                Text(
                                    text = "  - 거래 가능: ${"%,.0f".format(reward.tradableGold)}G",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "입장 가능 레이드",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Column {
                        val availableRaids = Raid.getAvailableRaids(character.level, 6)
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