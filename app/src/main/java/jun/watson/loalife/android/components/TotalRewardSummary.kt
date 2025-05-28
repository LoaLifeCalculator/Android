package jun.watson.loalife.android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jun.watson.R
import jun.watson.loalife.android.model.data.Item
import jun.watson.loalife.android.model.data.Raid
import jun.watson.loalife.android.model.data.RewardCalculator
import jun.watson.loalife.android.model.dto.CharacterResponseDto
import jun.watson.loalife.android.model.dto.Resource

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
    disabledServers: List<String>,
    showTradableOnly: Boolean
) {
    val totalReward = remember(
        expeditions,
        resourceMap,
        serverCheckedStates.toMap(),
        goldRewardStates.toMap(),
        excludedStates.toMap(),
        sortedServers,
        chaosOption,
        guardianOption,
        disabledServers.toList(),
        showTradableOnly
    ) {
        var tradableGold = 0.0
        var boundGold = 0.0
        
        expeditions.forEach { (server, characters) ->
            if (server !in disabledServers) {
                characters.forEach { character ->
                    val isExcluded = excludedStates[server]?.get(characters.indexOf(character)) ?: false
                    
                    if (!isExcluded) {
                        val calculator = RewardCalculator(resourceMap, chaosOption, guardianOption)
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
            }
        }
        
        if (showTradableOnly) {
            Pair(tradableGold, 0.0)
        } else {
            Pair(tradableGold, boundGold)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
                        text = "${"%,.0f".format(totalReward.first)}G",
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
                        text = "${"%,.0f".format(totalReward.second)}G",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
} 