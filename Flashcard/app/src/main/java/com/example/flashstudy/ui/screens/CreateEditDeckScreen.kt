package com.example.flashstudy.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.example.flashstudy.data.Deck
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.LeitnerBadge
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.White

// Deck uusgeh ba zasvarlahin delgets
// deckId null bol shine uusgene, ugui bol odoo baiigaagiig zasna
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditDeckScreen(
    repository: DeckRepository,
    deckId: String?,
    onNavigateBack: () -> Unit,
    onNavigateToCardEditor: (String, String?) -> Unit
) {
    // Odoo baigaa deck-iig tatah, deckId null bol null
    val existingDeck = remember(deckId) {
        deckId?.let { repository.getDeckById(it) }
    }

    // Form state-uuruud
    var name by remember { mutableStateOf(existingDeck?.name ?: "") }
    var description by remember { mutableStateOf(existingDeck?.description ?: "") }
    var cards by remember { mutableStateOf(existingDeck?.cards ?: emptyList()) }
    var nameError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (deckId == null) "Багц үүсгэх" else "Багц засварлах",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    // Tsutslah button - butsah
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            text = "Цуцлах",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                },
                actions = {
                    // Hadgalah button
                    TextButton(
                        onClick = {
                            // Ner hooson baival aldaag haruulna
                            if (name.isBlank()) {
                                nameError = true
                                return@TextButton
                            }

                            // Shine esvel odoo baiigaa deck-iig hadgalna
                            val newDeck = existingDeck?.copy(
                                name = name.trim(),
                                description = description.trim(),
                                cards = cards
                            ) ?: Deck(
                                name = name.trim(),
                                description = description.trim()
                            )
                            repository.saveDeck(newDeck)
                            onNavigateBack()
                        }
                    ) {
                        Text(
                            text = "Хадгалах",
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
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
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Bagtsiin ner oruulah talbar
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) nameError = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Багцын нэр *") },
                placeholder = { Text("Жишээ: Англи хэл - Үндсэн үгс") },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Багцын нэр заавал оруулна", color = Danger) }
                } else null,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    cursorColor = Primary,
                    focusedLabelColor = Primary,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tailbar oruulah talbar
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Тайлбар (заавал биш)") },
                placeholder = { Text("Багцын тухай товч тайлбар...") },
                minLines = 2,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    cursorColor = Primary,
                    focusedLabelColor = Primary,
                    unfocusedBorderColor = TextMuted.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = TextMuted.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))

            // Kartuuruud header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Картууд",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${cards.size} карт",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kartiin jagsaalt
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Leitner badge - kartiin tuvshin haruulna
                            LeitnerBadge(box = card.leitnerBox)

                            Spacer(modifier = Modifier.width(12.dp))

                            // Term ba definition
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.term,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = card.definition,
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    maxLines = 1
                                )
                            }

                            // Zasah button - card editor ruu shiljine
                            IconButton(
                                onClick = {
                                    // Ehleed deck-iig hadgalj daraa card editor ruu
                                    val savedDeck = existingDeck?.copy(
                                        name = name.trim(),
                                        description = description.trim(),
                                        cards = cards
                                    ) ?: Deck(
                                        name = if (name.isBlank()) "Нэргүй багц" else name.trim(),
                                        description = description.trim(),
                                        cards = cards
                                    )
                                    repository.saveDeck(savedDeck)
                                    onNavigateToCardEditor(savedDeck.id, card.id)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Карт засах",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Ustgah button - jagsaaltaas hasna
                            IconButton(
                                onClick = {
                                    cards = cards.toMutableList().apply {
                                        removeAt(index)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Карт устгах",
                                    tint = Danger.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // === Shine kart nemeh button (dashed border) ===
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(
                                width = 1.5.dp,
                                color = Primary.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                // Deck-iig ehleed hadgalj daraa card editor ruu shiljine
                                val savedDeck = existingDeck?.copy(
                                    name = name.trim(),
                                    description = description.trim(),
                                    cards = cards
                                ) ?: Deck(
                                    name = if (name.isBlank()) "Нэргүй багц" else name.trim(),
                                    description = description.trim(),
                                    cards = cards
                                )
                                repository.saveDeck(savedDeck)
                                onNavigateToCardEditor(savedDeck.id, null)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Primary.copy(alpha = 0.04f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Шинэ карт нэмэх",
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Шинэ карт нэмэх",
                                color = Primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
