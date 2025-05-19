package jun.watson.loalife.android.model.data

import jun.watson.loalife.android.model.data.Item.*
import jun.watson.loalife.android.model.dto.ContentReward

enum class Raid(
    val korean: String,
    private val minimumLevel: Int,
    private val raidName: String,
    private val goldReward: ContentReward,
    private val nonGoldReward: ContentReward
) {

    MORDUM_HARD(
        "모르둠 하드",
        1700,
        "Mordum",
        ContentReward(
            gold = 38000,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1600),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 3200),
            shards = mapOf(DESTINY_SHARD to 13000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 131),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 5650),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 11300),
            shards = mapOf(DESTINY_SHARD to 46700)
        )
    ),
    BRELSHAZA_KAZEROS_HARD(
        "아브렐슈드 하드",
        1690,
        "BrelshazaKazeros",
        ContentReward(
            gold = 30500,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1340),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 2680),
            shards = mapOf(DESTINY_SHARD to 10600)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 80),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 3590),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 7380),
            shards = mapOf(DESTINY_SHARD to 32600)
        )
    ),
    MORDUM_NORMAL(
        "모르둠 노말",
        1680,
        "Mordum",
        ContentReward(
            gold = 28000,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1240),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 2480),
            shards = mapOf(DESTINY_SHARD to 9800)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 64),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 3200),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 6400),
            shards = mapOf(DESTINY_SHARD to 27600)
        )
    ),
    AEGIR_HARD(
        "에기르 하드",
        1680,
        "Aegir",
        ContentReward(
            gold = 27500,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1240),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 2480),
            shards = mapOf(DESTINY_SHARD to 9600)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 66),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 3240),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 6480),
            shards = mapOf(DESTINY_SHARD to 28100)
        )
    ),
    BRELSHAZA_KAZEROS_NORMAL(
        "아브렐슈드 노말",
        1670,
        "BrelshazaKazeros",
        ContentReward(
            gold = 25000,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1180),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 2360),
            shards = mapOf(DESTINY_SHARD to 8600)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 48),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 3030),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 6060),
            shards = mapOf(DESTINY_SHARD to 26100)
        )
    ),
    AEGIR_NORMAL(
        "에기르 노말",
        1660,
        "Aegir",
        ContentReward(
            gold = 23000,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1060),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 2120),
            shards = mapOf(DESTINY_SHARD to 8000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 44),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 2760),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 5520),
            shards = mapOf(DESTINY_SHARD to 24000)
        )
    ),
    BEHEMOTH_NORMAL(
        "베히모스",
        1640,
        "Behemoth",
        ContentReward(
            gold = 11000,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 480),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 960),
            shards = mapOf(DESTINY_SHARD to 7000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 25),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1500),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 3000),
            shards = mapOf(DESTINY_SHARD to 13730)
        )
    ),
    ECHIDNA_HARD(
        "에키드나 하드",
        1640,
        "Echidna",
        ContentReward(
            gold = 11000,
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 460),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 920),
            shards = mapOf(DESTINY_SHARD to 6500)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(DESTINY_LEAPSTONE to 22),
            weaponStones = mapOf(DESTINY_DESTRUCTION_STONE to 1410),
            armorStones = mapOf(DESTINY_GUARDIAN_STONE to 2820),
            shards = mapOf(DESTINY_SHARD to 12960)
        )
    ),
    THAEMINE_HARD(
        "카멘 하드",
        1630,
        "Thaemine",
        ContentReward(
            gold = 15500,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 890),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 1780),
            shards = mapOf(HONOR_SHARD to 13500)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 68),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 2720),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 5440),
            shards = mapOf(HONOR_SHARD to 32350)
        )
    ),
    ECHIDNA_NORMAL(
        "에키드나 노말",
        1620,
        "Echidna",
        ContentReward(
            gold = 9500,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 380),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 760),
            shards = mapOf(HONOR_SHARD to 7800)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 30),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 1210),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 2420),
            shards = mapOf(HONOR_SHARD to 18430)
        )
    ),
    IVORY_TOWER_HARD(
        "상아탑 하드",
        1620,
        "IvoryTower",
        ContentReward(
            gold = 9000,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 440),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 880),
            shards = mapOf(HONOR_SHARD to 8400)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 14),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 1120),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 2240),
            shards = mapOf(HONOR_SHARD to 17500)
        )
    ),
    THAEMINE_NORMAL(
        "카멘 노말",
        1610,
        "Thaemine",
        ContentReward(
            gold = 8000,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 370),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 740),
            shards = mapOf(HONOR_SHARD to 7500)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 34),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 1350),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 2700),
            shards = mapOf(HONOR_SHARD to 16910)
        )
    ),
    IVORY_TOWER_NORMAL(
        "상아탑 노말",
        1600,
        "IvoryTower",
        ContentReward(
            gold = 6500,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 360),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 720),
            shards = mapOf(HONOR_SHARD to 7000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 10),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 920),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 1840),
            shards = mapOf(HONOR_SHARD to 21500)
        )
    ),
    ILLIAKAN_HARD(
        "일리아칸 하드",
        1600,
        "Illiakan",
        ContentReward(
            gold = 7500,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 700),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 1400),
            shards = mapOf(HONOR_SHARD to 8800)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 39),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 1500),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 3000),
            shards = mapOf(HONOR_SHARD to 22300)
        )
    ),
    ILLIAKAN_NORMAL(
        "일리아칸 노말",
        1580,
        "Illiakan",
        ContentReward(
            gold = 5400,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 520),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 1040),
            shards = mapOf(HONOR_SHARD to 5600)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 13),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 800),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 1600),
            shards = mapOf(HONOR_SHARD to 11600)
        )
    ),
    KAYANGEL_HARD(
        "카양겔 하드",
        1580,
        "Kayangel",
        ContentReward(
            gold = 4800,
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 350),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 700),
            shards = mapOf(HONOR_SHARD to 11000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(RADIANT_HONOR_LEAPSTONE to 25),
            weaponStones = mapOf(REFINED_OBLITERATION_STONE to 1040),
            armorStones = mapOf(REFINED_PROTECTION_STONE to 2080),
            shards = mapOf(HONOR_SHARD to 15800)
        )
    ),
    KAYANGEL_NORMAL(
        "카양겔 노말",
        1540,
        "Kayangel",
        ContentReward(
            gold = 3600,
            weaponStones = mapOf(OBLITERATION_STONE to 880),
            armorStones = mapOf(PROTECTION_STONE to 1760),
            shards = mapOf(HONOR_SHARD to 8500)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 19),
            weaponStones = mapOf(OBLITERATION_STONE to 1580),
            armorStones = mapOf(PROTECTION_STONE to 3160),
            shards = mapOf(HONOR_SHARD to 13000)
        )
    ),
    BRELSHAZA_LEGION_HARD(
        "아브렐슈드(3T) 하드",
        1540,
        "BrelshazaLegion",
        ContentReward(
            gold = 5600,
            weaponStones = mapOf(OBLITERATION_STONE to 1800),
            armorStones = mapOf(PROTECTION_STONE to 3600),
            shards = mapOf(HONOR_SHARD to 14000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 92),
            weaponStones = mapOf(OBLITERATION_STONE to 4120),
            armorStones = mapOf(PROTECTION_STONE to 8240),
            shards = mapOf(HONOR_SHARD to 22200)
        )
    ),
    BRELSHAZA_LEGION_NORMAL(
        "아브렐슈드(3T) 노말",
        1490,
        "BrelshazaLegion",
        ContentReward(
            gold = 4600,
            weaponStones = mapOf(OBLITERATION_STONE to 720),
            armorStones = mapOf(PROTECTION_STONE to 1440),
            shards = mapOf(HONOR_SHARD to 6700)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(MARVELOUS_HONOR_LEAPSTONE to 56),
            weaponStones = mapOf(OBLITERATION_STONE to 1920),
            armorStones = mapOf(PROTECTION_STONE to 3840),
            shards = mapOf(HONOR_SHARD to 21900)
        )
    ),
    KAKUL_SAYDON_NORMAL(
        "쿠크세이튼",
        1475,
        "KakulSaydon",
        ContentReward(
            gold = 3000,
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 680),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 1360),
            shards = mapOf(HONOR_SHARD to 2400)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 36),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 1940),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 3880),
            shards = mapOf(HONOR_SHARD to 6600)
        )
    ),
    VYKAS_HARD(
        "비아키스 하드",
        1460,
        "Vykas",
        ContentReward(
            gold = 2400,
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 520),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 1040),
            shards = mapOf(HONOR_SHARD to 1500)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 24),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 1490),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 2980),
            shards = mapOf(HONOR_SHARD to 5100)
        )
    ),
    VALTAN_HARD(
        "발탄 하드",
        1445,
        "Valtan",
        ContentReward(
            gold = 1800,
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 440),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 880),
            shards = mapOf(HONOR_SHARD to 1200)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 20),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 1280),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 2560),
            shards = mapOf(HONOR_SHARD to 3500)
        )
    ),
    VYKAS_NORMAL(
        "비아키스 노말",
        1430,
        "Vykas",
        ContentReward(
            gold = 1600,
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 350),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 700),
            shards = mapOf(HONOR_SHARD to 1200)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 16),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 1010),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 2020),
            shards = mapOf(HONOR_SHARD to 2900)
        )
    ),
    VALTAN_NORMAL(
        "발탄 노말",
        1415,
        "Valtan",
        ContentReward(
            gold = 1200,
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 350),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 700),
            shards = mapOf(HONOR_SHARD to 1000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 13),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 890),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 1780),
            shards = mapOf(HONOR_SHARD to 2100)
        )
    ),
    ARGOS_NORMAL(
        "아르고스",
        1370,
        "Argos",
        ContentReward(
            gold = 1000,
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 350),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 700),
            shards = mapOf(HONOR_SHARD to 1000)
        ),
        ContentReward(
            gold = 0,
            leapStones = mapOf(GREAT_HONOR_LEAPSTONE to 15),
            weaponStones = mapOf(DESTRUCTION_STONE_CRYSTAL to 620),
            armorStones = mapOf(GUARDIAN_STONE_CRYSTAL to 1240),
            shards = mapOf(HONOR_SHARD to 2920)
        )
    );

    companion object {
        fun getAvailableRaids(level: Double, limit: Int = Raid.entries.size): List<Raid> {
            return Raid.entries
                .filter { raid -> raid.minimumLevel <= level }
                .sortedByDescending { it.minimumLevel }
                .distinctBy { it.raidName }
                .take(limit)
        }
    }

    fun getReward(goldReceived: Boolean): ContentReward {
        return if (goldReceived) {
            goldReward
        } else {
            nonGoldReward
        }
    }

}
