package jun.watson.loalife.android.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var isClosing by remember { mutableStateOf(false) }
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

    LaunchedEffect(isClosing) {
        if (isClosing) {
            slideInState.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
            onClose()
        }
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
                onClick = { isClosing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("닫기")
            }
        }
    }
} 