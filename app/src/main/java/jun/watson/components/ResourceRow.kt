package jun.watson.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jun.watson.R
import jun.watson.model.data.Item
import jun.watson.model.dto.Resource

@Composable
fun ResourceRow(
    resources: List<Resource>,
    onPriceChange: (Item, Double) -> Unit,
    resourceStates: Map<Item, Double>,
    priceTexts: MutableMap<Item, String>
) {
    if (resources.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            resources.forEach { resource ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(
                                painter = painterResource(
                                    id = when (resource.item) {
                                        Item.DESTINY_DESTRUCTION_STONE -> R.drawable.destiny_destruction_stone
                                        Item.REFINED_OBLITERATION_STONE -> R.drawable.refined_obliteration_stone
                                        Item.OBLITERATION_STONE -> R.drawable.obliteration_stone
                                        Item.DESTRUCTION_STONE_CRYSTAL -> R.drawable.destruction_stone_crystal
                                        Item.DESTINY_GUARDIAN_STONE -> R.drawable.destiny_guardian_stone
                                        Item.REFINED_PROTECTION_STONE -> R.drawable.refined_protection_stone
                                        Item.PROTECTION_STONE -> R.drawable.protection_stone
                                        Item.GUARDIAN_STONE_CRYSTAL -> R.drawable.guardian_stone_crystal
                                        Item.DESTINY_SHARD -> R.drawable.destiny_shard
                                        Item.HONOR_SHARD -> R.drawable.honor_shard
                                        Item.DESTINY_LEAPSTONE -> R.drawable.destiny_leapstone
                                        Item.RADIANT_HONOR_LEAPSTONE -> R.drawable.radiant_honor_leapstone
                                        Item.MARVELOUS_HONOR_LEAPSTONE -> R.drawable.marvelous_honor_leapstone
                                        Item.GREAT_HONOR_LEAPSTONE -> R.drawable.great_honor_leapstone
                                    }
                                ),
                                contentDescription = resource.item.korean,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = resource.item.korean,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = priceTexts[resource.item] ?: "",
                            onValueChange = {
                                priceTexts[resource.item] = it
                                it.toDoubleOrNull()?.let { value ->
                                    onPriceChange(resource.item, value)
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            label = { Text("시세") }
                        )
                    }
                }
            }
        }
    }
} 