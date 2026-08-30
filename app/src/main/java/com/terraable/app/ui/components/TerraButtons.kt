package com.terraable.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.terraable.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush = Brush.horizontalGradient(listOf(EcoGreen, Color(0xFF0D9488))),
    icon: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = enabled) { onClick() },
        color = Color.Transparent,
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (enabled) gradient else Brush.linearGradient(listOf(SurfaceElevated, SurfaceDark))),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    color = if (enabled) Color.White else TextDisabled
                )
            }
        }
    }
}

@Composable
fun PromptChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    accentColor: Color = EcoGreen
) {
    Surface(
        modifier = modifier
            .clip(PillShape)
            .clickable { onClick() },
        shape = PillShape,
        color = if (isSelected) accentColor.copy(alpha = 0.2f) else SurfaceElevated,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.2.dp, accentColor) else androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderSubtle)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) TextPrimary else TextSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun HoldToConfirmSosButton(
    onTriggered: () -> Unit,
    modifier: Modifier = Modifier,
    holdDurationMillis: Long = 3000L
) {
    var isPressed by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "sosScale"
    )

    Box(
        modifier = modifier
            .size(190.dp)
            .scale(scale)
            .clip(RoundedCornerShape(95.dp))
            .background(SosGradient)
            .border(4.dp, if (isPressed) SosRed else Color(0x66EF4444), RoundedCornerShape(95.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        val startTime = System.currentTimeMillis()
                        val job = scope.launch {
                            while (isPressed && (System.currentTimeMillis() - startTime) < holdDurationMillis) {
                                val elapsed = System.currentTimeMillis() - startTime
                                progress = (elapsed.toFloat() / holdDurationMillis).coerceIn(0f, 1f)
                                delay(30)
                            }
                            if (isPressed && progress >= 0.98f) {
                                progress = 1f
                                onTriggered()
                            }
                        }
                        tryAwaitRelease()
                        isPressed = false
                        progress = 0f
                        job.cancel()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer progress ring indicator
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize().padding(6.dp),
            color = Color.White,
            strokeWidth = 6.dp,
            trackColor = Color(0x33FFFFFF)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SOS",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isPressed) "HOLD (${((1f - progress) * 3).toInt() + 1}s)..." else "HOLD 3 SEC",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}
