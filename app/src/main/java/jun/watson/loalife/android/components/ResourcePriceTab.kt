package jun.watson.loalife.android.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import jun.watson.loalife.android.model.data.Item
import jun.watson.loalife.android.model.dto.Resource

@Composable
fun ResourcePriceTab(
    resources: List<Resource>,
    onPriceChange: (Item, Double) -> Unit,
    resourceStates: Map<Item, Double>,
    priceTexts: MutableMap<Item, String>,
    onClose: () -> Unit
) {
    val slideInState = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        slideInState.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .offset(x = with(LocalDensity.current) { (slideInState.value * 1000).toInt().dp }),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                ResourceRow(
                    resources = resources,
                    onPriceChange = onPriceChange,
                    resourceStates = resourceStates,
                    priceTexts = priceTexts
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("닫기")
            }
        }
    }
} 