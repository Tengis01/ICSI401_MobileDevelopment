package com.example.flashstudy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.data.FlashCard
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.LeitnerBadge
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.White

// Kart uusgeh ba zasvarlahin delgets
// cardId null bol shine kart, ugui bol odoo baiigaa kartiin tailbar shine bolno
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorScreen(
    repository: DeckRepository,
    deckId: String,
    cardId: String?,
    onNavigateBack: () -> Unit
) {
    // Deck ba card-iig tatah - null safe
    val deck = remember(deckId) { repository.getDeckById(deckId) }
    val card = remember(cardId, deck) {
        cardId?.let { id -> deck?.cards?.firstOrNull { it.id == id } }
    }

    // Form state-uuruud
    var term by remember { mutableStateOf(card?.term ?: "") }
    var definition by remember { mutableStateOf(card?.definition ?: "") }
    var termError by remember { mutableStateOf(false) }
    var defError by remember { mutableStateOf(false) }

    // Hadgalah logic - validate, deck update, navigate back
    fun saveCard() {
        // Buh talbariin validate
        termError = term.isBlank()
        defError = definition.isBlank()
        if (termError || defError) return

        // Kartiin nutgiin index
        val currentDeck = repository.getDeckById(deckId) ?: return

        // Shine esvel zassan kartaig bossoo
        val newCard = card?.copy(
            term = term.trim(),
            definition = definition.trim()
        ) ?: FlashCard(
            term = term.trim(),
            definition = definition.trim()
        )

        // Deck-iin kartiin jagsaalyg shinechlene
        val updatedCards = if (cardId == null) {
            // Shine kart - jagsaaltiin sulruud nemne
            currentDeck.cards + newCard
        } else {
            // Odoo baiigaa kartiin tailbar shinechlene
            currentDeck.cards.map { if (it.id == cardId) newCard else it }
        }

        repository.saveDeck(currentDeck.copy(cards = updatedCards))
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    // Tsutslah - butsah
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            text = "Цуцлах",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                },
                title = {
                    Text(
                        text = if (cardId == null) "Карт нэмэх" else "Карт засварлах",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                actions = {
                    // Hadgalah button - teal ongtoi
                    TextButton(onClick = { saveCard() }) {
                        Text(
                            text = "Хадгалах",
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = Color(0xFFF0FFFE)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Ner tomyoo oruulah talbar (term, zaavaltai)
            OutlinedTextField(
                value = term,
                onValueChange = {
                    term = it
                    if (it.isNotBlank()) termError = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Нэр томьёо *") },
                placeholder = { Text("Жишээ: apple, 苹果, яблоко...") },
                isError = termError,
                supportingText = if (termError) {
                    { Text("Нэр томьёо заавал оруулна", color = Danger) }
                } else {
                    { Text("Карт дээр нүүр талд харагдана") }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    cursorColor = Primary,
                    focusedLabelColor = Primary,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                    errorBorderColor = Danger,
                    errorLabelColor = Danger
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tailbar / orchuulga oruulah talbar (definition, zaavaltai)
            OutlinedTextField(
                value = definition,
                onValueChange = {
                    definition = it
                    if (it.isNotBlank()) defError = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Тайлбар / Хариулт *") },
                placeholder = { Text("Жишээ: алим, жимс...") },
                isError = defError,
                supportingText = if (defError) {
                    { Text("Тайлбар заавал оруулна", color = Danger) }
                } else {
                    { Text("Карт дээр арын талд харагдана") }
                },
                minLines = 3,
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    cursorColor = Primary,
                    focusedLabelColor = Primary,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                    errorBorderColor = Danger,
                    errorLabelColor = Danger
                )
            )

            // Leitner box medeelel - zasah modet haruulna
            if (cardId != null && card != null) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LeitnerBadge(box = card.leitnerBox)

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Leitner хайрцаг ${card.leitnerBox}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = leitnerBoxLabel(card.leitnerBox),
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Leitner box-iin tuvshinii tailbar mongol heleer
private fun leitnerBoxLabel(box: Int): String = when (box) {
    1 -> "Шинэ карт — өдөр бүр давтана"
    2 -> "Анхан шат — 2 хоногт нэг"
    3 -> "Дунд шат — 4 хоногт нэг"
    4 -> "Дэвшилтэт шат — долоо хоногт нэг"
    5 -> "Эзэмшсэн — сард нэг давтана"
    else -> "Тодорхойгүй"
}
