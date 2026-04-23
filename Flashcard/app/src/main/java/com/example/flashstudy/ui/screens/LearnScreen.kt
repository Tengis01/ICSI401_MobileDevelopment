package com.example.flashstudy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.PrimaryGradientButton
import com.example.flashstudy.ui.theme.Success
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.Warning
import com.example.flashstudy.ui.theme.White

// Bichgeer hariltuu surah gorim - needsReview kartuuruudiig ehleed haruulna
// Hariultiig lowercase + trim-eer haritsuulj Leitner system shinechlene
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    repository: DeckRepository,
    deckId: String,
    onNavigateBack: () -> Unit
) {
    // Deck state - repository-aas tataj avna
    var deck by remember { mutableStateOf(repository.getDeckById(deckId)) }

    // needsReview==true kartuuruudiig ehleed erembelne
    val studyCards = remember(deck) {
        val cards = deck?.cards ?: emptyList()
        cards.sortedByDescending { it.needsReview }
    }

    // Dasgaliin togtolts uud
    var currentIndex by remember { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    val currentCard = studyCards.getOrNull(currentIndex)

    // Hariultiig haritsuulj Leitner-iig shinechleh function
    fun markWrong() {
        currentCard?.let { card ->
            repository.updateCard(
                deckId,
                card.copy(leitnerBox = 1, needsReview = true)
            )
            wrongCount++
            deck = repository.getDeckById(deckId)
        }
    }

    // Daraagiin asugalt ruu shiljih function - state-uudig resetten
    fun nextQuestion() {
        userAnswer = ""
        isChecked = false
        isCorrect = false
        if (currentIndex < studyCards.size - 1) {
            currentIndex++
        } else {
            isCompleted = true
        }
    }

    // Hariultiig shalagah - case insensitive, trimmed
    fun checkAnswer() {
        val card = currentCard ?: return
        isCorrect = userAnswer.trim().lowercase() ==
                card.definition.trim().lowercase()
        isChecked = true
        if (isCorrect) {
            val newBox = minOf(5, card.leitnerBox + 1)
            repository.updateCard(
                deckId,
                card.copy(leitnerBox = newBox, needsReview = false)
            )
            correctCount++
        } else {
            markWrong()
        }
        deck = repository.getDeckById(deckId)
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
                        text = "Learn горим",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                actions = {
                    // Odoogiin / niit toloolog haruulna
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

                    // === Durgen irgelten togtolts: buh asugalt duussan ===
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
                                        text = "Сурах горим дууслаа!",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "$correctCount зөв · $wrongCount буруу",
                                        fontSize = 14.sp,
                                        color = TextMuted,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(28.dp))

                                    // Dakhij ehleh button
                                    OutlinedButton(
                                        onClick = {
                                            currentIndex = 0
                                            userAnswer = ""
                                            isChecked = false
                                            isCorrect = false
                                            correctCount = 0
                                            wrongCount = 0
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
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // === Segment progress: min(8, size) segment, color by state ===
                            val segmentCount = minOf(8, studyCards.size)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (0 until segmentCount).forEach { segIndex ->
                                    // Toloolog-d suurtsan real index maaplah
                                    val mappedIndex = if (studyCards.size <= 8) {
                                        segIndex
                                    } else {
                                        (segIndex.toFloat() /
                                                (segmentCount - 1) * (studyCards.size - 1)).toInt()
                                    }
                                    val segColor = when {
                                        mappedIndex < currentIndex -> Primary
                                        mappedIndex == currentIndex -> Primary.copy(alpha = 0.6f)
                                        else -> Color(0xFFE2E8F0) // surface gray
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(segColor)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Segment-iin doorkhi tov tekst
                            Text(
                                text = "$correctCount зөв · $wrongCount буруу · " +
                                        "${studyCards.size - currentIndex - 1} үлдсэн",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // "HARIULT BICH" label
                            Text(
                                text = "ХАРИУЛТ БИЧ",
                                fontSize = 10.sp,
                                color = TextMuted,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // === Term card: zuun border 3dp Primary ===
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    // Zuun Primary border
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(140.dp)
                                            .background(Primary)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // "NER TOMYOO" dээд label
                                        Text(
                                            text = "НЭР ТОМЬЁО",
                                            fontSize = 10.sp,
                                            color = TextMuted,
                                            letterSpacing = 1.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Term undsen tekst
                                        Text(
                                            text = currentCard?.term ?: "",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))



                                        // needsReview flag haruulah
                                        currentCard?.let { card ->
                                            if (card.needsReview) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(Danger)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Leitner ${card.leitnerBox} · needs review",
                                                        fontSize = 11.sp,
                                                        color = TextMuted
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // === Input talbar: isChecked-d harildahgui bytes ===
                            if (!isChecked) {
                                OutlinedTextField(
                                    value = userAnswer,
                                    onValueChange = { userAnswer = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            text = "Энд хариултаа бич...",
                                            color = TextMuted
                                        )
                                    },
                                    minLines = 2,
                                    maxLines = 4,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Primary,
                                        cursorColor = Primary,
                                        unfocusedBorderColor = TextMuted.copy(alpha = 0.3f)
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Shalagah gradient button
                                PrimaryGradientButton(
                                    text = "Шалгах ✓",
                                    onClick = { checkAnswer() }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Algasah text button - buruugaar temdeglej daraagiinkhruugaa
                                TextButton(
                                    onClick = {
                                        markWrong()
                                        nextQuestion()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Алгасах →",
                                        color = TextMuted,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // === Hariu kart: AnimatedVisibility ===
                            AnimatedVisibility(
                                visible = isChecked,
                                enter = fadeIn(animationSpec = tween(300)) +
                                        slideInVertically(
                                            animationSpec = tween(300)
                                        ) { it / 3 },
                                exit = fadeOut() + slideOutVertically { it / 3 }
                            ) {
                                Column {
                                    // Bichsen hariultiig haruulah
                                    Text(
                                        text = "Таны хариулт: \"${userAnswer.trim()}\"",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    if (isCorrect) {
                                        // === Zov hariult card ===
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    width = 1.5.dp,
                                                    color = Success,
                                                    shape = RoundedCornerShape(16.dp)
                                                ),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Success.copy(alpha = 0.10f)
                                            ),
                                            elevation = CardDefaults.cardElevation(0.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Зөв",
                                                        tint = Success,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Зөв! Leitner хайрцаг ахина →",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Success
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Зөв хариулт: ${currentCard?.definition}",
                                                    fontSize = 13.sp,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    } else {
                                        // === Buruu hariult card ===
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    width = 1.5.dp,
                                                    color = Danger,
                                                    shape = RoundedCornerShape(16.dp)
                                                ),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Danger.copy(alpha = 0.10f)
                                            ),
                                            elevation = CardDefaults.cardElevation(0.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Буруу",
                                                        tint = Danger,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Буруу — Хайрцаг 1 буцна",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Danger
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Зөв хариулт: ${currentCard?.definition}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "→ To review жагсаалтад нэмэгдлээ",
                                                    fontSize = 12.sp,
                                                    color = Warning
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Daraagiin asugalt gradient button
                                    PrimaryGradientButton(
                                        text = "Дараагийнх →",
                                        onClick = { nextQuestion() }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}
