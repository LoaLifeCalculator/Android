package jun.watson.loalife.android.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponseDto(
    val characterName: String,
    val serverName: String,
    val level: Double,
    val className: String,
)
