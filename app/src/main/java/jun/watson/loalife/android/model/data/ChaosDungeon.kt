package jun.watson.loalife.android.model.data

import jun.watson.loalife.android.model.data.Item.*
import jun.watson.loalife.android.model.dto.ContentReward

/**
 * 참고자료 1: https://www.inven.co.kr/board/lostark/4821/104225
 * 참고자료 2: https://www.inven.co.kr/board/lostark/4821/93807
 * 참고자료 3: https://jungbla.tistory.com/118
 */
enum class ChaosDungeon(
    val minimumLevel: Int,
    val reward: ContentReward
) {

    C1700(
        1700,
        ContentReward(
            shards = mapOf(DESTINY_SHARD to 34000),
            leapStones = mapOf(DESTINY_LEAPSTONE to 23),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 280),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 860),
            gems = mapOf(4 to 4.8)
        )
    ),
    C1680(
        1680,
        ContentReward(
            shards = mapOf(DESTINY_SHARD to 31000),
            leapStones = mapOf(DESTINY_LEAPSTONE to 19),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 222),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 670),
            gems = mapOf(4 to 4.6)
        )
    ),
    C1660(
        1660,
        ContentReward(
            shards = mapOf(DESTINY_SHARD to 25800),
            leapStones = mapOf(DESTINY_LEAPSTONE to 15),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 163),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 514),
            gems = mapOf(4 to 4.0)
        )
    ),
    C1640(
        1640,
        ContentReward(
            shards = mapOf(DESTINY_SHARD to 21500),
            leapStones = mapOf(DESTINY_LEAPSTONE to 11),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 155),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 483),
            gems = mapOf(4 to 3.2)
        )
    ),
    C1610(
        1610,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 23400),
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 10),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 116),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 334),
            gems = mapOf(3 to 20.0)
        )
    ),
    C1600(
        1600,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 22700),
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 8),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 85),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 260),
            gems = mapOf(3 to 20.0)
        )
    ),
    C1580(
        1580,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 20700),
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 6),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 80),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 240),
            gems = mapOf(3 to 20.0)
        )
    ),
    C1560(
        1560,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 20000),
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 12),
            weaponStones = mapOf(OBLITERATION_STONE to 190),
            armorStones = mapOf(PROTECTION_STONE to 560),
            gems = mapOf(3 to 22.0)
        )
    ),
    C1540(
        1540,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 16000),
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 10),
            weaponStones = mapOf(OBLITERATION_STONE to 150),
            armorStones = mapOf(PROTECTION_STONE to 480),
            gems = mapOf(3 to 22.0)
        )
    ),
    C1520(
        1520,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 13000),
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 9),
            weaponStones = mapOf(OBLITERATION_STONE to 130),
            armorStones = mapOf(PROTECTION_STONE to 400),
            gems = mapOf(3 to 22.0)
        )
    ),
    C1490(
        1490,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 11000),
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 6),
            weaponStones = mapOf(OBLITERATION_STONE to 100),
            armorStones = mapOf(PROTECTION_STONE to 300),
            gems = mapOf(3 to 16.0)
        )
    ),
    C1475(
        1475,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 6000),
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 12),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 200),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 600),
            gems = mapOf(3 to 14.0)
        )
    ),
    C1445(
        1445,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 5000),
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 11),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 180),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 570),
            gems = mapOf(3 to 13.0)
        )
    ),
    C1415(
        1415,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 4600),
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 10),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 160),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 480),
            gems = mapOf(3 to 12.0)
        )
    ),
    C1400(
        1400,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 4200),
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 9),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 140),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 420),
            gems = mapOf(3 to 11.0)
        )
    ),
    C1385(
        1385,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 4000),
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 7),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 125),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 390),
            gems = mapOf(3 to 10.0)
        )
    ),
    C1370(
        1370,
        ContentReward(
            shards = mapOf(HONOR_SHARD to 3500),
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 6),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 110),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 360),
            gems = mapOf(3 to 9.0)
        )
    ),
    NULL(0, ContentReward());

    companion object {
        fun getSuitableReward(level: Double): ContentReward {
            return ChaosDungeon.entries
                .filter { guardian -> guardian.minimumLevel <= level }
                .maxBy { it.minimumLevel }
                .reward
        }
    }
}
