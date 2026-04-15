package com.example.flashstudy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.example.flashstudy.ui.theme.White

// Flashcard flip dasgaliin delgets - Leitner system-eer kart zagwarjuulna
// Kart togshihod 3D flip animation haruulna, ard ni unelgee songono
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

    // 3D flip animatsiin erguulelt - 0..180 degree
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_flip_rotation"
    )

    // Daraa kartyruu oroh tuslah function
    // isFlipped-iig false bolgoj, daraagiin kart esvel durgen irgelten haruulna
    fun nextCard() {
        isFlipped = false
        if (currentIndex < studyCards.size - 1) {
            currentIndex++
        } else {
            isCompleted = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Буцах",
                            tint = TextPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = "Flashcard горим",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                actions = {
                    // Odoogiin / niit kart toloolog haruulna
                    if (!isCompleted && studyCards.isNotEmpty()) {
                        Text(
                            text = "${currentIndex + 1}/${studyCards.size}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
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
                                colors = CardDefaults.cardColors(containerColor = White),
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
                            // Ahitsiin progress tuuzny deer
                            LinearProgressIndicator(
                                progress = {
                                    (currentIndex + 1).toFloat() / studyCards.size
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = Primary,
                                trackColor = Primary.copy(alpha = 0.12f),
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Flip zaavarlah tekst
                            Text(
                                text = if (!isFlipped) {
                                    "Картыг тогшоод нөгөө талыг харна уу"
                                } else {
                                    "Та мэдэж байсан уу?"
                                },
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // === 3D flip kart ===
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(220.dp)
                                    .clickable { isFlipped = !isFlipped }
                                    .graphicsLayer {
                                        rotationY = rotation
                                        // CameraDistance 3D effektiin gurvan dornotoo shaardlaga
                                        cameraDistance = 12f * density
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (rotation <= 90f) {
                                    // === Unuun tal - term haruulna ===
                                    Card(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(
                                                width = 1.dp,
                                                color = Primary,
                                                shape = RoundedCornerShape(24.dp)
                                            ),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = White
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 6.dp
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            // "NER TOMYOO" label
                                            Text(
                                                text = "НЭР ТОМЬЁО",
                                                fontSize = 10.sp,
                                                color = TextMuted,
                                                letterSpacing = 1.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Undsen term tekst
                                            Text(
                                                text = currentCard?.term ?: "",
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(16.dp))

                                            // Leitner dot ba box dugaar
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val leitnerBox =
                                                    currentCard?.leitnerBox ?: 1
                                                val dotColor = leitnerBoxColor(leitnerBox)
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(
                                                            androidx.compose.foundation.shape.CircleShape
                                                        )
                                                        .background(dotColor)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Leitner хайрцаг $leitnerBox",
                                                    fontSize = 11.sp,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // === Ard tal - definition haruulna ===
                                    // rotationY 180 - flip hiigdsen kartiin ard medeelel
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer { rotationY = 180f }
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .border(
                                                    width = 1.dp,
                                                    color = Primary.copy(alpha = 0.30f),
                                                    shape = RoundedCornerShape(24.dp)
                                                ),
                                            shape = RoundedCornerShape(24.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Surface
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = 6.dp
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                // "TAILBAR" label
                                                Text(
                                                    text = "ТАЙЛБАР",
                                                    fontSize = 10.sp,
                                                    color = TextMuted,
                                                    letterSpacing = 1.5.sp,
                                                    fontWeight = FontWeight.Medium
                                                )

                                                Spacer(modifier = Modifier.height(12.dp))

                                                // Definitsiin tekst - Primary ongoor
                                                Text(
                                                    text = currentCard?.definition ?: "",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Primary,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // === Unelgeenii tovch: AnimatedVisibility flip hiisen yed l haruulna ===
                            AnimatedVisibility(
                                visible = isFlipped,
                                enter = fadeIn() + slideInVertically { it / 2 },
                                exit = fadeOut() + slideOutVertically { it / 2 }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // "To review" - buruugaar medeegui, leitner box 1-d butsaana
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Warning.copy(alpha = 0.10f))
                                            .border(
                                                width = 2.dp,
                                                color = Warning,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable {
                                                currentCard?.let { card ->
                                                    // Box 1 ruu butsaaj, davtah flag togloono
                                                    val updated = card.copy(
                                                        leitnerBox = 1,
                                                        needsReview = true
                                                    )
                                                    repository.updateCard(deckId, updated)
                                                    deck = repository.getDeckById(deckId)
                                                    reviewCount++
                                                    nextCard()
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "To review",
                                                tint = Warning,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "To review",
                                                color = Warning,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // "I knew it!" - zov medesn, leitner box++
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Success.copy(alpha = 0.10f))
                                            .border(
                                                width = 2.dp,
                                                color = Success,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable {
                                                currentCard?.let { card ->
                                                    // Leitner box-iig 1-eer nemiiye, max 5
                                                    val newBox = minOf(5, card.leitnerBox + 1)
                                                    val updated = card.copy(
                                                        leitnerBox = newBox,
                                                        needsReview = newBox < 5
                                                    )
                                                    repository.updateCard(deckId, updated)
                                                    deck = repository.getDeckById(deckId)
                                                    correctCount++
                                                    nextCard()
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "I knew it",
                                                tint = Success,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "I knew it!",
                                                color = Success,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // === Leitner 5 haiirtsuugiin vizual tuuz ===
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val boxColors = listOf(
                                        Danger, Warning, Primary, PrimaryVariant, Success
                                    )
                                    val currentBox = currentCard?.leitnerBox ?: 1

                                    boxColors.forEachIndexed { index, color ->
                                        val boxNumber = index + 1
                                        val isActive = boxNumber == currentBox
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(
                                                    color.copy(
                                                        alpha = if (isActive) 1f else 0.30f
                                                    )
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Leitner strip-iin tailbar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "← Буруу: Хайрцаг 1",
                                        fontSize = 10.sp,
                                        color = Danger
                                    )
                                    Text(
                                        text = "Зөв: Ахина →",
                                        fontSize = 10.sp,
                                        color = Success
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // === Navigatsiin muriig: omnoh/algasah ===
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Omnoh kart ruu harj bolno
                                TextButton(
                                    onClick = {
                                        if (currentIndex > 0) {
                                            currentIndex--
                                            isFlipped = false
                                        }
                                    },
                                    enabled = currentIndex > 0
                                ) {
                                    Text(
                                        text = "← Өмнөх",
                                        color = if (currentIndex > 0) TextMuted
                                        else TextMuted.copy(alpha = 0.3f),
                                        fontSize = 14.sp
                                    )
                                }

                                // Leitner-gue algasna
                                TextButton(
                                    onClick = { nextCard() }
                                ) {
                                    Text(
                                        text = "Алгасах →",
                                        color = TextMuted,
                                        fontSize = 14.sp
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

// Leitner box-iin togolsor onguug butsaana - kartaas haramtsaanaa
private fun leitnerBoxColor(box: Int): Color = when (box) {
    1 -> Danger
    2 -> Warning
    3 -> Primary
    4 -> PrimaryVariant
    5 -> Success
    else -> TextMuted
}
