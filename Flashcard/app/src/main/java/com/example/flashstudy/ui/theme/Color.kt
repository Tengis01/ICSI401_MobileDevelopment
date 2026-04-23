package com.example.flashstudy.ui.theme

import androidx.compose.ui.graphics.Color

// Light palette based on prototypes
val Primary = Color(0xFF2DD4BF) // Teal
val PrimaryContainer = Color(0xFFE2FAF7) // Light Teal Background
val Secondary = Color(0xFF627C77) // Muted Teal
val Tertiary = Color(0xFF4C7E90) // Blue Grey
val OnPrimary = Color(0xFFFFFFFF) // White text on Primary

val Background = Color(0xFFF0F9FF) // Very light blue/grey
val Surface = Color(0xFFFFFFFF) // White cards
val SurfaceElevated = Color(0xFFF8FAFC) // Slightly off-white for elevated elements
val SurfaceVariant = Color(0xFFE2E8F0) // Borders, dividers

val Success = Color(0xFF34D399)
val Warning = Color(0xFFFBBF24)
val Danger = Color(0xFFF87171)
val Info = Color(0xFF38BDF8)
val PrimaryVariant = Secondary

val TextPrimary = Color(0xFF0F172A) // Dark slate for primary text
val TextSecondary = Color(0xFF475569) // Slate for secondary text
val TextMuted = Color(0xFF94A3B8) // Light slate for muted text

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// Leitner haiirtsag tus buriin ongoo butsaana (1=shine, 5=ezemshsen)
fun leitnerColor(box: Int): Color = when (box) {
    1 -> Danger
    2 -> Warning
    3 -> Primary
    4 -> Info
    5 -> Success
    else -> TextMuted
}
