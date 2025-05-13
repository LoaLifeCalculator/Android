package jun.watson.model.dto

import jun.watson.model.data.Item
import kotlinx.serialization.Serializable

@Serializable
data class ContentReward(
    val gold: Int = 0, // 골드
    val leapStones: Map<Item, Int> = emptyMap(), // 돌파석 <이름, 수량>
    val weaponStones: Map<Item, Int> = emptyMap(), // 파강
    val armorStones: Map<Item, Int> = emptyMap(), // 수강
    val shards: Map<Item, Int> = emptyMap(), // 명파
    val jewelries: Map<Int, Double> = emptyMap(), // 보석 <티어, 수량>
) {

    operator fun plus(other: ContentReward): ContentReward {
        return ContentReward(
            gold = gold + other.gold,
            leapStones = mergeMapsInt(leapStones, other.leapStones),
            weaponStones = mergeMapsInt(weaponStones, other.weaponStones),
            armorStones = mergeMapsInt(armorStones, other.armorStones),
            shards = mergeMapsInt(shards, other.shards),
            jewelries = mergeMapsDouble(jewelries, other.jewelries)
        )
    }

    private fun <K> mergeMapsInt(
        first: Map<K, Int>,
        second: Map<K, Int>
    ): Map<K, Int> {
        return (first.keys + second.keys).associateWith { key ->
            (first[key] ?: 0) + (second[key] ?: 0)
        }
    }

    private fun mergeMapsDouble(
        first: Map<Int, Double>,
        second: Map<Int, Double>
    ): Map<Int, Double> {
        return (first.keys + second.keys).associateWith { key ->
            (first[key] ?: 0.0) + (second[key] ?: 0.0)
        }
    }

    // 레이드의 거래 가능 재화
    fun getRaidTradableReward(): ContentReward {
        return ContentReward(
            gold = gold,
            jewelries = jewelries
        )
    }

    // 레이드의 귀속 재화
    fun getRaidBoundReward(): ContentReward {
        return ContentReward(
            leapStones = leapStones,
            weaponStones = weaponStones,
            armorStones = armorStones,
            shards = shards
        )
    }

    // 카오스 던전의 거래 가능 재화
    fun getChaosTradableReward(): ContentReward {
        return ContentReward(
            gold = gold,
            weaponStones = weaponStones,
            armorStones = armorStones,
            jewelries = jewelries
        )
    }

    // 카오스 던전의 귀속 재화
    fun getChaosBoundReward(): ContentReward {
        return ContentReward(
            leapStones = leapStones,
            shards = shards
        )
    }

    // 가디언 토벌의 거래 가능 재화
    fun getGuardianTradableReward(): ContentReward {
        return ContentReward(
            gold = gold,
            weaponStones = weaponStones,
            armorStones = armorStones,
            leapStones = leapStones,
            jewelries = jewelries
        )
    }

    // 가디언 토벌의 귀속 재화
    fun getGuardianBoundReward(): ContentReward {
        return ContentReward(
            shards = shards
        )
    }

}
