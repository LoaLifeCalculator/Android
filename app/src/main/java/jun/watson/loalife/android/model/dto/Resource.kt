package jun.watson.loalife.android.model.dto

import jun.watson.loalife.android.model.data.Item
import kotlinx.serialization.Serializable

@Serializable
data class Resource(
    var item: Item,
    var avgPrice: Double = 0.0,
)
