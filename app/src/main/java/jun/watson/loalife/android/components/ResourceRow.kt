package jun.watson.loalife.android.components

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
import jun.watson.loalife.android.model.data.Item
import jun.watson.loalife.android.model.dto.Resource
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

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
            resources.forEachIndexed { idx, resource ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (idx % 2 == 0) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        Item.GEM_TIER_3 -> R.drawable.gem_tier_3
                                        Item.GEM_TIER_4 -> R.drawable.gem_tier_4
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "시세",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val isFocused = remember { mutableStateOf(false) }
                            val hasError = remember { mutableStateOf(false) }
                            val borderColor = when {
                                hasError.value -> Color.Red
                                isFocused.value -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outline
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .border(
                                        width = 1.dp,
                                        color = borderColor,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = priceTexts[resource.item] ?: "",
                                    onValueChange = { text ->
                                        if (text.isEmpty() || text.matches(Regex("^\\d*\\.?\\d*$"))) {
                                            priceTexts[resource.item] = text
                                            hasError.value = false
                                            text.toDoubleOrNull()?.let { value ->
                                                if (value >= 0) {
                                                    onPriceChange(resource.item, value)
                                                } else {
                                                    hasError.value = true
                                                }
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 16.sp,
                                        color = if (hasError.value) Color.Red else MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { focusState ->
                                            isFocused.value = focusState.isFocused
                                        },
                                    decorationBox = { innerTextField ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                            if (hasError.value) {
                                Text(
                                    text = "올바른 가격을 입력하세요",
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 