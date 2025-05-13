package jun.watson.model.dto

import jun.watson.model.data.Item
import kotlinx.serialization.Serializable

@Serializable
data class Resource(
    var item: Item,
    var avgPrice: Double = 0.0,
)
