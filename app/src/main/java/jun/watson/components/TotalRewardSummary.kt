package jun.watson.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jun.watson.model.data.Item
import jun.watson.model.dto.Resource
import jun.watson.model.dto.CharacterResponseDto
import jun.watson.model.data.ChaosDungeon
import jun.watson.model.data.Guardian
import jun.watson.model.data.Raid

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

                    val weeklyMultiplier = if (chaosOption == 1) 14.0/3.0 else 7.0
                    rewards = rewards.copy(
                        chaosTradableGold = rewards.chaosTradableGold + tradableGold * weeklyMultiplier,
                        chaosBoundGold = rewards.chaosBoundGold + boundGold * weeklyMultiplier
                    )
                }

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

                    val weeklyMultiplier = if (guardianOption == 1) 14.0/3.0 else 7.0
                    rewards = rewards.copy(
                        guardianTradableGold = rewards.guardianTradableGold + guardianTradableGold * weeklyMultiplier,
                        guardianBoundGold = rewards.guardianBoundGold + guardianBoundGold * weeklyMultiplier
                    )
                }

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