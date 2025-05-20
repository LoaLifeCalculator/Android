package jun.watson.loalife.android.model.data

import jun.watson.loalife.android.model.data.Item.*
import jun.watson.loalife.android.model.dto.ContentReward

enum class Guardian(
    val minimumLevel: Int,
    val reward: ContentReward
) {

    DRECTALAS(
        1700,
        ContentReward(
            leapStones = mapOf(DESTINY_LEAPSTONE to 21),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 186),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 550)
        )
    ),
    SKOLAKIA(
        1680,
        ContentReward(
            leapStones = mapOf(DESTINY_LEAPSTONE to 17),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 196),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 438)
        )
    ),
    ARGEOS(
        1640,
        ContentReward(
            leapStones = mapOf(DESTINY_LEAPSTONE to 12),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 92),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 281)
        )
    ),
    VESKAL(
        1630,
        ContentReward(
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 24),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 165),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 445)
        )
    ),
    GARGADETH(
        1610,
        ContentReward(
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 12),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 103),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 301)
        )
    ),
    SONAVEL(
        1580,
        ContentReward(
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 8),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 68),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 204)
        )
    ),
    HANUMATAN(
        1540,
        ContentReward(
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 14),
            weaponStones = mapOf(OBLITERATION_STONE to 101),
            armorStones = mapOf(PROTECTION_STONE to 306)
        )
    ),
    CALILIGOS(
        1490,
        ContentReward(
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 10),
            weaponStones = mapOf(OBLITERATION_STONE to 75),
            armorStones = mapOf(PROTECTION_STONE to 226)
        )
    ),
    KUNGELANIUM(
        1460,
        ContentReward(
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 16),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 133),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 408)
        )
    ),
    DESKALUDA(
        1415,
        ContentReward(
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 11),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 101),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 315)
        )
    ),
    NULL(0, ContentReward());

    companion object {
        fun getSuitableReward(level: Double): ContentReward {
            return Guardian.entries
                .filter { guardian -> guardian.minimumLevel <= level }
                .maxBy { it.minimumLevel }
                .reward
        }
    }

}
