package jun.watson.loalife.android.model.data

import jun.watson.R
import jun.watson.loalife.android.model.data.CategoryCode.*
import kotlinx.serialization.Serializable

const val DEFAULT_ID = 0

@Serializable
enum class Item(
    val korean: String,
    val id: Int,
    val categoryCode: CategoryCode,
    val bundleCount: Int,
    val image: Int
) {

    DESTINY_DESTRUCTION_STONE(
        "운명의 파괴석",
        66102006,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.destiny_destruction_stone
    ),
    REFINED_OBLITERATION_STONE(
        "정제된 파괴강석",
        66102005,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.refined_obliteration_stone
    ),
    OBLITERATION_STONE(
        "파괴강석",
        66102004,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.obliteration_stone
    ),
    DESTRUCTION_STONE_CRYSTAL(
        "파괴석 결정",
        66102003,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.destruction_stone_crystal
    ),

    DESTINY_GUARDIAN_STONE(
        "운명의 수호석",
        66102106,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.destiny_guardian_stone
    ),
    REFINED_PROTECTION_STONE(
        "정제된 수호강석",
        66102105,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.refined_protection_stone
    ),
    PROTECTION_STONE(
        "수호강석",
        66102104,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.protection_stone
    ),
    GUARDIAN_STONE_CRYSTAL(
        "수호석 결정",
        66102103,
        REFINING_ADDITIONAL_MATERIAL,
        10,
        R.drawable.guardian_stone_crystal
    ),

    DESTINY_SHARD(
        "운명의 파편",
        66130141,
        REFINING_ADDITIONAL_MATERIAL,
        1000,
        R.drawable.destiny_shard
    ),
    HONOR_SHARD(
        "명예의 파편",
        66130131,
        REFINING_ADDITIONAL_MATERIAL,
        1000,
        R.drawable.honor_shard
    ),

    DESTINY_LEAPSTONE(
        "운명의 돌파석",
        66110225,
        REFINING_ADDITIONAL_MATERIAL,
        1,
        R.drawable.destiny_leapstone
    ),
    RADIANT_HONOR_LEAPSTONE(
        "찬란한 명예의 돌파석",
        66110224,
        REFINING_ADDITIONAL_MATERIAL,
        1,
        R.drawable.radiant_honor_leapstone
    ),
    MARVELOUS_HONOR_LEAPSTONE(
        "경이로운 명예의 돌파석",
        66110223,
        REFINING_ADDITIONAL_MATERIAL,
        1,
        R.drawable.marvelous_honor_leapstone
    ),
    GREAT_HONOR_LEAPSTONE(
        "위대한 명예의 돌파석",
        66110222,
        REFINING_ADDITIONAL_MATERIAL,
        1,
        R.drawable.great_honor_leapstone
    ),

    GEM_TIER_3(
        "1레벨 멸화의 보석",
        DEFAULT_ID,
        GEM,
        1,
        R.drawable.gem_tier_3
    ),
    GEM_TIER_4(
        "1레벨 겁화의 보석",
        DEFAULT_ID,
        GEM,
        1,
        R.drawable.gem_tier_4
    );

}
