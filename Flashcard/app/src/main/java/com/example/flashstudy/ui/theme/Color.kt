package com.example.flashstudy.ui.theme

import androidx.compose.ui.graphics.Color

// App-iin undsen onguunuud - mint/teal color scheme
val Primary = Color(0xFF2DD4BF)
val PrimaryVariant = Color(0xFF38BDF8)
val Success = Color(0xFF4ADE80)
val Warning = Color(0xFFFB923C)
val Danger = Color(0xFFF87171)
val Background = Color(0xFFF0FFFE)
val Surface = Color(0xFFFAFDFF)
val TextPrimary = Color(0xFF1E293B)
val TextMuted = Color(0xFF94A3B8)

// Niilmel onguunuud - Material theme system-d ashiglana
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// Leitner haiirtsag tus buriin ongoo butsaana (1=shine, 5=ezemshsen)
fun leitnerColor(box: Int): Color = when (box) {
    1 -> Danger
    2 -> Warning
    3 -> Primary
    4 -> PrimaryVariant
    5 -> Success
    else -> TextMuted
}
