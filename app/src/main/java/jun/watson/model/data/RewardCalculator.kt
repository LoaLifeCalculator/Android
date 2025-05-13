package jun.watson.model.data

import jun.watson.model.dto.CharacterResponseDto
import jun.watson.model.dto.ContentReward
import jun.watson.model.dto.Resource

class RewardCalculator(
    private val resourceMap: Map<Item, Resource>,
    private val chaosOption: Int,
    private val guardianOption: Int
) {
    data class RewardResult(
        val tradableGold: Double,
        val boundGold: Double
    )

    fun calculateChaosReward(character: CharacterResponseDto, isExcluded: Boolean): RewardResult {
        if (chaosOption == 2 || isExcluded) {
            return RewardResult(0.0, 0.0)
        }

        val chaosReward = ChaosDungeon.getSuitableReward(character.level)
        val tradableReward = chaosReward.getChaosTradableReward()
        val boundReward = chaosReward.getChaosBoundReward()

        val tradableGold = calculateTradableGold(tradableReward) * getChaosMultiplier()
        val boundGold = calculateBoundGold(boundReward) * getChaosMultiplier()

        return RewardResult(tradableGold, boundGold)
    }

    fun calculateGuardianReward(character: CharacterResponseDto, isExcluded: Boolean): RewardResult {
        if (guardianOption == 2 || isExcluded) {
            return RewardResult(0.0, 0.0)
        }

        val guardianReward = Guardian.getSuitableReward(character.level)
        val tradableReward = guardianReward.getGuardianTradableReward()
        val boundReward = guardianReward.getGuardianBoundReward()

        val tradableGold = calculateTradableGold(tradableReward) * getGuardianMultiplier()
        val boundGold = calculateBoundGold(boundReward) * getGuardianMultiplier()

        return RewardResult(tradableGold, boundGold)
    }

    fun calculateRaidReward(raid: Raid, isGoldReward: Boolean): RewardResult {
        val reward = if (isGoldReward) raid.getReward(true) else raid.getReward(false)
        val tradableReward = reward.getRaidTradableReward()
        val boundReward = reward.getRaidBoundReward()

        val tradableGold = calculateTradableGold(tradableReward)
        val boundGold = calculateBoundGold(boundReward)

        return RewardResult(tradableGold, boundGold)
    }

    private fun calculateTradableGold(reward: ContentReward): Double {
        var sum = reward.gold.toDouble()
        
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
        
        reward.jewelries.forEach { (_, count) ->
            sum += count
        }

        return sum
    }

    private fun calculateBoundGold(reward: ContentReward): Double {
        var sum = 0.0
        
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

        return sum
    }

    private fun getChaosMultiplier(): Double = when (chaosOption) {
        1 -> 14.0/3.0
        else -> 7.0
    }

    private fun getGuardianMultiplier(): Double = when (guardianOption) {
        1 -> 14.0/3.0
        else -> 7.0
    }
} 