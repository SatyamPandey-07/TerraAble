package com.terraable.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Backgrounds & Surfaces (Premium Deep Charcoal Dark Mode)
val BgDark = Color(0xFF0C0F14)
val SurfaceDark = Color(0xFF131922)
val SurfaceElevated = Color(0xFF1A222E)
val SurfaceHighlight = Color(0xFF222B3A)
val SurfaceBorder = Color(0xFF2A374A)
val SurfaceBorderSubtle = Color(0x33475569)

// Purpose-based Accents
val EcoGreen = Color(0xFF22C55E)
val EcoGreenLight = Color(0xFF4ADE80)
val EcoGreenDark = Color(0xFF15803D)
val EcoGreenGlow = Color(0x3322C55E)

val RouteBlue = Color(0xFF38BDF8)
val RouteBlueLight = Color(0xFF7DD3FC)
val RouteBlueDark = Color(0xFF0284C7)
val RouteBlueGlow = Color(0x3338BDF8)

val AccessPurple = Color(0xFFA855F7)
val AccessPurpleLight = Color(0xFFC084FC)
val AccessPurpleDark = Color(0xFF7E22CE)
val AccessPurpleGlow = Color(0x33A855F7)

val WarningAmber = Color(0xFFF59E0B)
val WarningOrange = Color(0xFFF97316)
val WarningGlow = Color(0x33F59E0B)

val SosRed = Color(0xFFEF4444)
val SosRedDark = Color(0xFF991B1B)
val SosRedGlow = Color(0x44EF4444)

// Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)
val TextDisabled = Color(0xFF475569)

// Gradients
val HeroGreenGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF064E3B), Color(0xFF0F766E), Color(0xFF134E4A))
)

val HeroBlueGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0C4A6E), Color(0xFF0369A1), Color(0xFF1E3A8A))
)

val HeroPurpleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF4C1D95), Color(0xFF581C87), Color(0xFF3B0764))
)

val SosGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFEF4444), Color(0xFFB91C1C), Color(0xFF7F1D1D))
)

val CardGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0x1A38BDF8), Color(0x000C0F14))
)

val GreenGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0x1A22C55E), Color(0x000C0F14))
)
