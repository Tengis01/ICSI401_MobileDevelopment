package com.example.flashstudy.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.PrimaryGradientButton
import com.example.flashstudy.ui.theme.PrimaryVariant
import com.example.flashstudy.ui.theme.Success
import com.example.flashstudy.ui.theme.Surface
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.Warning
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Flashcard flip dasgaliin delgets - Leitner system-eer kart zagwarjuulna
// Swipe (Quizlet-style) mechanism
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    repository: DeckRepository,
    deckId: String,
    onNavigateBack: () -> Unit
) {
    // Deck ba kart state
    var deck by remember { mutableStateOf(repository.getDeckById(deckId)) }

    // Leitner box dorogshoo erembelj - shine kartuuruud ehleed gardag
    val studyCards = deck?.cards?.sortedBy { it.leitnerBox } ?: emptyList()

    // Dasgaliin togtolts
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var reviewCount by remember { mutableIntStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    val currentCard = studyCards.getOrNull(currentIndex)

    // lastStudied-iig shinechlene - RecentlyViewed section ajillana
    LaunchedEffect(Unit) {
        val currentDeck = repository.getDeckById(deckId)
        if (currentDeck != null) {
            repository.saveDeck(currentDeck.copy(lastStudied = System.currentTimeMillis()))
        }
    }

    // 3D flip animatsiin erguulelt - 0..180 degree
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_flip_rotation"
    )

    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val densityValue = density.density
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val threshold = 120f * densityValue

    fun nextCard() {
        isFlipped = false
        if (currentIndex < studyCards.size - 1) {
            currentIndex++
        } else {
            isCompleted = true
        }
    }

    fun swipeCard(direction: Int) { // 1 for right, -1 for left
        scope.launch {
            offsetX.animateTo(direction * screenWidth * 1.5f, tween(300))
            
            if (direction > 0) {
                // Correct / Know
                currentCard?.let { card ->
                    val newBox = minOf(5, card.leitnerBox + 1)
                    val updated = card.copy(leitnerBox = newBox, needsReview = false)
                    repository.updateCard(deckId, updated)
                    deck = repository.getDeckById(deckId)
                    correctCount++
                }
            } else {
                // Review
                currentCard?.let { card ->
                    val updated = card.copy(leitnerBox = 1, needsReview = true)
                    repository.updateCard(deckId, updated)
                    deck = repository.getDeckById(deckId)
                    reviewCount++
                }
            }
            
            nextCard()
            
            // Snap back silently for the next card
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = "Карт тоглуулагч",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        GradientBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    // === Hooson togtolts: deck-d kart baikhgui ===
                    studyCards.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🗂️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Карт байхгүй байна",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Эхлээд карт нэмнэ үү",
                                fontSize = 14.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            TextButton(onClick = onNavigateBack) {
                                Text("Буцах", color = Primary)
                            }
                        }
                    }

                    // === Durgen irgelten: buh kartuuruud duussan ===
                    isCompleted -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "🎉", fontSize = 48.sp)

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Амжилттай дуусгалаа!",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "$correctCount зөв · $reviewCount давтах",
                                        fontSize = 14.sp,
                                        color = TextMuted,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(28.dp))

                                    // Dakhij ehleh button
                                    OutlinedButton(
                                        onClick = {
                                            // Buh state-iig ankhny baidald oruulna
                                            currentIndex = 0
                                            isFlipped = false
                                            correctCount = 0
                                            reviewCount = 0
                                            isCompleted = false
                                            deck = repository.getDeckById(deckId)
                                            scope.launch {
                                                offsetX.snapTo(0f)
                                                offsetY.snapTo(0f)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        border = androidx.compose.foundation.BorderStroke(
                                            2.dp, Primary
                                        ),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Primary
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text(
                                            text = "Дахин эхлэх",
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Butsah gradient button
                                    PrimaryGradientButton(
                                        text = "Буцах",
                                        onClick = onNavigateBack
                                    )
                                }
                            }
                        }
                    }

                    // === Undsen dasgaliin UI ===
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Counters (Left and Right edges)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Review counter (Left)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            com.example.flashstudy.ui.theme.Warning.copy(alpha = 0.15f),
                                            RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                        )
                                        .border(
                                            2.dp,
                                            com.example.flashstudy.ui.theme.Warning.copy(alpha = 0.5f),
                                            RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                        )
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = reviewCount.toString(), color = com.example.flashstudy.ui.theme.Warning, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                // Known counter (Right)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            com.example.flashstudy.ui.theme.Success.copy(alpha = 0.15f),
                                            RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                        )
                                        .border(
                                            2.dp,
                                            com.example.flashstudy.ui.theme.Success.copy(alpha = 0.5f),
                                            RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                        )
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = correctCount.toString(), color = com.example.flashstudy.ui.theme.Success, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            // Progress bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = deck?.name ?: "Багц",
                                    fontSize = 14.sp,
                                    color = com.example.flashstudy.ui.theme.Secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${currentIndex + 1} / ${studyCards.size}",
                                    fontSize = 14.sp,
                                    color = com.example.flashstudy.ui.theme.Secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = {
                                    (currentIndex + 1).toFloat() / studyCards.size
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = com.example.flashstudy.ui.theme.Secondary,
                                trackColor = com.example.flashstudy.ui.theme.SurfaceVariant,
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            val knowAlpha = (offsetX.value / threshold).coerceIn(0f, 1f)
                            val reviewAlpha = (-offsetX.value / threshold).coerceIn(0f, 1f)
                            val rotationZValue = (offsetX.value / screenWidth) * 15f
                            val borderColor = when {
                                knowAlpha > 0f -> Success.copy(alpha = knowAlpha)
                                reviewAlpha > 0f -> Warning.copy(alpha = reviewAlpha)
                                else -> Color.Transparent
                            }

                            // === Swipe-able Card Container ===
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f) // Fill remaining space
                                    .padding(horizontal = 24.dp)
                                    .padding(bottom = 48.dp) // Leave some space at bottom
                                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                                    .graphicsLayer {
                                        rotationZ = rotationZValue
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragEnd = {
                                                if (offsetX.value > threshold) {
                                                    swipeCard(1)
                                                } else if (offsetX.value < -threshold) {
                                                    swipeCard(-1)
                                                } else {
                                                    scope.launch {
                                                        offsetX.animateTo(0f)
                                                    }
                                                    scope.launch {
                                                        offsetY.animateTo(0f)
                                                    }
                                                }
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                scope.launch {
                                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                                    offsetY.snapTo(offsetY.value + dragAmount.y)
                                                }
                                            }
                                        )
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { isFlipped = !isFlipped }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Face & Back
                                Box(modifier = Modifier.fillMaxSize()) {
                                    if (rotation <= 90f) {
                                        Card(
                                            modifier = Modifier.fillMaxSize()
                                                .graphicsLayer {
                                                    rotationY = rotation
                                                    cameraDistance = 12f * densityValue
                                                }
                                                .border(
                                                    width = 4.dp,
                                                    color = borderColor,
                                                    shape = RoundedCornerShape(32.dp)
                                                ),
                                            shape = RoundedCornerShape(32.dp),
                                            colors = CardDefaults.cardColors(containerColor = Surface),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize().padding(32.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(com.example.flashstudy.ui.theme.PrimaryContainer, RoundedCornerShape(12.dp))
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "НЭР ТОМЬЁО",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = com.example.flashstudy.ui.theme.Secondary
                                                    )
                                                }
                                                
                                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = currentCard?.term ?: "",
                                                        fontSize = 38.sp, // Made larger
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                                
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.TouchApp,
                                                        contentDescription = null,
                                                        tint = TextMuted,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Тогшоод эргүүлэх",
                                                        fontSize = 14.sp,
                                                        color = TextMuted
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Card(
                                            modifier = Modifier.fillMaxSize()
                                                .graphicsLayer { 
                                                    rotationY = rotation 
                                                    cameraDistance = 12f * densityValue
                                                }
                                                .border(
                                                    width = 4.dp,
                                                    color = borderColor,
                                                    shape = RoundedCornerShape(32.dp)
                                                ),
                                            shape = RoundedCornerShape(32.dp),
                                            colors = CardDefaults.cardColors(containerColor = Surface),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize().padding(32.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(com.example.flashstudy.ui.theme.PrimaryContainer, RoundedCornerShape(12.dp))
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "ТАЙЛБАР",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = com.example.flashstudy.ui.theme.Secondary
                                                    )
                                                }
                                                
                                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = currentCard?.definition ?: "",
                                                        fontSize = 28.sp, // Made larger
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextPrimary,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                                
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.TouchApp,
                                                        contentDescription = null,
                                                        tint = TextMuted,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Тогшоод эргүүлэх",
                                                        fontSize = 14.sp,
                                                        color = TextMuted
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Overlays for visual feedback
                                if (knowAlpha > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Surface.copy(alpha = knowAlpha * 0.95f), RoundedCornerShape(32.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Мэдэж байна",
                                            color = Success,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Black,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.alpha(knowAlpha)
                                        )
                                    }
                                }

                                if (reviewAlpha > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Surface.copy(alpha = reviewAlpha * 0.95f), RoundedCornerShape(32.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Давтах",
                                            color = Warning,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Black,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.alpha(reviewAlpha)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Leitner box-iin togolsor onguug butsaana - kartaas haramtsaanaa
private fun leitnerBoxColor(box: Int): Color = when (box) {
    1 -> Danger
    2 -> Warning
    3 -> Primary
    4 -> PrimaryVariant
    5 -> Success
    else -> TextMuted
}
