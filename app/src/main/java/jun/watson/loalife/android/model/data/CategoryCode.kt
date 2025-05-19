package jun.watson.loalife.android.model.data

import kotlinx.serialization.Serializable

@Serializable
enum class CategoryCode(val value: Int, val codeName: String) {

    EQUIPMENT_BOX(10100, "장비 상자"),

    AVATAR(20000, "아바타"),
    AVATAR_WEAPON(20005, "무기"),
    AVATAR_HEAD(20010, "머리"),
    AVATAR_FACE1(20020, "얼굴1"),
    AVATAR_FACE2(20030, "얼굴2"),
    AVATAR_TOP(20050, "상의"),
    AVATAR_BOTTOM(20060, "하의"),
    AVATAR_FULL_SET(20070, "상하의 세트"),
    AVATAR_INSTRUMENT(21400, "악기"),
    AVATAR_BOX(21500, "아바타 상자"),
    AVATAR_MOVE_EFFECT(21600, "이동 효과"),

    ENGRAVING_BOOK(40000, "각인서"),

    ENHANCEMENT_MATERIAL(50000, "강화 재료"),
    REFINING_MATERIAL(50010, "재련 재료"),
    REFINING_ADDITIONAL_MATERIAL(50020, "재련 추가 재료"),
    OTHER_MATERIAL(51000, "기타 재료"),
    WEAPON_EVOLUTION_MATERIAL(51100, "무기 진화 재료"),

    BATTLE_ITEM(60000, "전투 용품"),
    BATTLE_ITEM_HEALING(60200, "배틀 아이템 -회복형"),
    BATTLE_ITEM_ATTACK(60300, "배틀 아이템 -공격형"),
    BATTLE_ITEM_FUNCTIONAL(60400, "배틀 아이템 -기능성"),
    BATTLE_ITEM_BUFF(60500, "배틀 아이템 -버프형"),

    COOKING(70000, "요리"),

    LIFE(90000, "생활"),
    HERB_LOOT(90200, "식물채집 전리품"),
    LOGGING_LOOT(90300, "벌목 전리품"),
    MINING_LOOT(90400, "채광 전리품"),
    HUNTING_LOOT(90500, "수렵 전리품"),
    FISHING_LOOT(90600, "낚시 전리품"),
    ARCHAEOLOGY_LOOT(90700, "고고학 전리품"),
    LIFE_OTHER(90800, "기타"),

    ADVENTURE_BOOK(100000, "모험의 서"),

    SAILING(110000, "항해"),
    SHIP_MATERIAL(110100, "선박 재료"),
    SHIP_SKIN(110110, "선박 스킨"),
    SHIP_MATERIAL_BOX(111900, "선박 재료 상자"),

    PET(140000, "펫"),
    PET_ITEM(140100, "펫"),
    PET_BOX(140200, "펫 상자"),

    MOUNT(160000, "탈것"),
    MOUNT_ITEM(160100, "탈것"),
    MOUNT_BOX(160200, "탈것 상자"),

    ETC(170000, "기타"),

    GEM(210000, "보석"),
    GEM_BOX(220000, "보석 상자")

}
