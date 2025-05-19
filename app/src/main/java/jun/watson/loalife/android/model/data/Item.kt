package jun.watson.loalife.android.model.data

import jun.watson.loalife.android.model.data.CategoryCode.*
import kotlinx.serialization.Serializable

const val DEFAULT_ID = 0

@Serializable
enum class Item(
    val korean: String,
    val id: Int,
    val categoryCode: CategoryCode,
    val bundleCount: Int
) {

    DESTINY_DESTRUCTION_STONE(
        "운명의 파괴석",
        66102006,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),
    REFINED_OBLITERATION_STONE(
        "정제된 파괴강석",
        66102005,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),
    OBLITERATION_STONE(
        "파괴강석",
        66102004,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),
    DESTRUCTION_STONE_CRYSTAL(
        "파괴석 결정",
        66102003,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),

    DESTINY_GUARDIAN_STONE(
        "운명의 수호석",
        66102106,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),
    REFINED_PROTECTION_STONE(
        "정제된 수호강석",
        66102105,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),
    PROTECTION_STONE(
        "수호강석",
        66102104,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),
    GUARDIAN_STONE_CRYSTAL(
        "수호석 결정",
        66102103,
        REFINING_ADDITIONAL_MATERIAL,
        10
    ),

    DESTINY_SHARD(
        "운명의 파편 주머니(소)",
        66130141,
        REFINING_ADDITIONAL_MATERIAL,
        1000
    ),
    HONOR_SHARD(
        "명예의 파편 주머니(소)",
        66130131,
        REFINING_ADDITIONAL_MATERIAL,
        1000
    ),

    DESTINY_LEAPSTONE(
        "운명의 돌파석",
        66110225,
        REFINING_ADDITIONAL_MATERIAL,
        1
    ),
    RADIANT_HONOR_LEAPSTONE(
        "찬란한 명예의 돌파석",
        66110224,
        REFINING_ADDITIONAL_MATERIAL,
        1
    ),
    MARVELOUS_HONOR_LEAPSTONE(
        "경이로운 명예의 돌파석",
        66110223,
        REFINING_ADDITIONAL_MATERIAL,
        1
    ),
    GREAT_HONOR_LEAPSTONE(
        "위대한 명예의 돌파석",
        66110222,
        REFINING_ADDITIONAL_MATERIAL,
        1
    ),

    GEM_TIER_3(
        "1레벨 멸화의 보석",
        DEFAULT_ID,
        GEM,
        1,
    ),
    GEM_TIER_4(
        "1레벨 겁화의 보석",
        DEFAULT_ID,
        GEM,
        1,
    );

}
