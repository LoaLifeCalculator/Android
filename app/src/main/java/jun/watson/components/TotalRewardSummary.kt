package jun.watson.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jun.watson.R
import jun.watson.model.data.Item
import jun.watson.model.dto.Resource
import jun.watson.model.dto.CharacterResponseDto
import jun.watson.model.data.ChaosDungeon
import jun.watson.model.data.Guardian
import jun.watson.model.data.Raid
import jun.watson.model.data.RewardCalculator

@Composable
fun TotalRewardSummary(
    expeditions: Map<String, List<CharacterResponseDto>>,
    resourceMap: Map<Item, Resource>,
    serverCheckedStates: Map<String, List<Boolean>>,
    goldRewardStates: Map<String, List<Boolean>>,
    excludedStates: Map<String, List<Boolean>>,
    sortedServers: List<String>,
    chaosOption: Int,
    guardianOption: Int,
    disabledServers: List<String>
) {
    val calculator = remember(resourceMap, chaosOption, guardianOption) {
        RewardCalculator(resourceMap, chaosOption, guardianOption)
    }

    val totalGold = remember(expeditions, resourceMap, serverCheckedStates, goldRewardStates, excludedStates, sortedServers, chaosOption, guardianOption, disabledServers) {
        derivedStateOf {
            var totalTradableGold = 0.0
            var totalBoundGold = 0.0
            
            sortedServers.filter { it !in disabledServers }.forEach { server ->
                val characters = expeditions[server] ?: return@forEach
                
                characters.forEach { character ->
                    val isExcluded = excludedStates[server]?.get(characters.indexOf(character)) ?: false
                    
                    if (!isExcluded) {
                        val chaosReward = calculator.calculateChaosReward(character, isExcluded)
                        val guardianReward = calculator.calculateGuardianReward(character, isExcluded)
                        
                        totalTradableGold += chaosReward.tradableGold + guardianReward.tradableGold
                        totalBoundGold += chaosReward.boundGold + guardianReward.boundGold
                        
                        val availableRaids = Raid.getAvailableRaids(character.level, 6)
                        val checkedStates = serverCheckedStates["$server:${character.characterName}"] ?: emptyList()
                        val isGoldReward = goldRewardStates[server]?.get(characters.indexOf(character)) ?: false
                        
                        availableRaids.withIndex()
                            .filter { checkedStates.getOrNull(it.index) == true }
                            .forEach { (_, raid) ->
                                val raidReward = calculator.calculateRaidReward(raid, isGoldReward)
                                totalTradableGold += raidReward.tradableGold
                                totalBoundGold += raidReward.boundGold
                            }
                    }
                }
            }
            Pair(totalTradableGold, totalBoundGold)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gold),
                        contentDescription = "골드",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "총합",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "거래 가능",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${"%,.0f".format(totalGold.value.first)}G",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "귀속",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${"%,.0f".format(totalGold.value.second)}G",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
} 