package jun.watson.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val expeditions: Expeditions,
    val resources: List<Resource>
)
