package jun.watson.loalife.android.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val expeditions: Expeditions,
    val resources: List<Resource>
)
