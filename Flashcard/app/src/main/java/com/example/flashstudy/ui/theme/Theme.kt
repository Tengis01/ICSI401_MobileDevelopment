package com.example.flashstudy.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Light color scheme - Material3 color system
private val FlashStudyColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE8F4FF),
    onSurfaceVariant = TextMuted,
    error = Danger,
    onError = White
)

// Buh card-iin shape - 20dp rounded corner
private val FlashStudyShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// App-iin undsen theme composable
@Composable
fun FlashStudyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FlashStudyColorScheme,
        typography = FlashStudyTypography,
        shapes = FlashStudyShapes,
        content = content
    )
}

// ============================================================
// Dahin ashiglagdah uildiin composable-uud
// ============================================================

// Deeguurees dooshoo gradient aryn orc - buh delgetsuudiin daraa ashiglana
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF0FFFE),
            Color(0xFFE8F4FF)
        )
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradient)
    ) {
        content()
    }
}

// Niitleg tsagaan card composable - delgetsuud dotor ashiglana
@Composable
fun StudyCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

// Leitner haiirtsagiin dugui badge - ont onor ongotoi
// box 1 = ulaan (shine), 5 = nogoon (ezemshsen)
@Composable
fun LeitnerBadge(
    box: Int,
    modifier: Modifier = Modifier
) {
    val color = leitnerColor(box)
    Box(
        modifier = modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.20f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = box.toString(),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// Gradient teal tomor button - undsen CTA-d ashiglana
@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Primary, PrimaryVariant)
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Primary.copy(alpha = 0.3f),
                spotColor = Primary.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(brush = gradientBrush)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
    }
}
