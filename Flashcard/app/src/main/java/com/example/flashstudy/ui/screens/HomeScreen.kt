package com.example.flashstudy.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.OnPrimary
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.PrimaryVariant
import com.example.flashstudy.ui.theme.Success
import com.example.flashstudy.ui.theme.Surface
import com.example.flashstudy.ui.theme.SurfaceElevated
import com.example.flashstudy.ui.theme.SurfaceVariant
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.TextSecondary
import com.example.flashstudy.ui.theme.Warning
import com.example.flashstudy.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: DeckRepository,
    onDeckClick: (String) -> Unit,
    onCreateDeck: () -> Unit
) {
    var decks by remember { mutableStateOf(repository.getDecks()) }

    // Refresh on resume (after navigating back from study)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                decks = repository.getDecks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Most recently studied deck (lastStudied > 0 means the user has actually opened it)
    val lastStudiedDeck = decks.filter { !it.isFolder }.maxByOrNull { it.lastStudied }?.takeIf { it.lastStudied > 0L }
    var searchQuery by remember { mutableStateOf("") }

    val deckColors = listOf(
        Primary, PrimaryVariant, Success, Warning, Danger,
        Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF06B6D4)
    )
    val Cyan = Color(0xFF06B6D4)

    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        GradientBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ── Search bar ──────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxSize(),
                            placeholder = {
                                Text(
                                    text = "Багц хайх...",
                                    fontSize = 14.sp,
                                    color = TextMuted
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Search,
                                    contentDescription = "Хайх",
                                    tint = TextMuted,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Surface,
                                focusedContainerColor = Surface,
                                unfocusedTextColor = TextPrimary,
                                focusedTextColor = TextPrimary
                            )
                        )
                    }
                }

                // ── Сүүлд үзсэн title ───────────────────────────────────
                item {
                    Text(
                        text = "Сүүлд үзсэн",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // ── Сүүлд үзсэн card ────────────────────────────────────
                item {
                    if (lastStudiedDeck != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = com.example.flashstudy.ui.theme.PrimaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(Primary.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Public,
                                        contentDescription = "Deck Icon",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = if (lastStudiedDeck.isFolder) "Хавтас" else "Үгсийн багц",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lastStudiedDeck.name,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    val total = lastStudiedDeck.cards.size
                                    val masteredCount = lastStudiedDeck.cards.count { it.leitnerBox >= 5 }
                                    val progressPercent = if (total > 0) (masteredCount * 100 / total) else 0

                                    Text(
                                        text = "$progressPercent%",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = com.example.flashstudy.ui.theme.PrimaryContainer.copy(red = 0.1f, green = 0.4f, blue = 0.3f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val total = lastStudiedDeck.cards.size
                                val masteredCount = lastStudiedDeck.cards.count { it.leitnerBox >= 5 }
                                val progressFloat = if (total > 0) (masteredCount.toFloat() / total) else 0f

                                LinearProgressIndicator(
                                    progress = { progressFloat },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Primary,
                                    trackColor = White.copy(alpha = 0.5f),
                                    strokeCap = StrokeCap.Round
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$total картаас $masteredCount эзэмшсэн",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { onDeckClick(lastStudiedDeck.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    shape = RoundedCornerShape(28.dp)
                                ) {
                                    Text(
                                        "Үргэлжлүүлэх",
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty state
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 1.dp,
                                        color = TextMuted.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📚", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Багц байхгүй байна", fontSize = 14.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(onClick = onCreateDeck) {
                                        Text(
                                            "+ Шинэ багц",
                                            color = Cyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Миний багцууд title ──────────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Миний багцууд",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        TextButton(onClick = { /* Handle View All */ }) {
                            Text(
                                text = "Бүгдийг үзэх",
                                fontSize = 14.sp,
                                color = com.example.flashstudy.ui.theme.Secondary
                            )
                        }
                    }
                }

                // ── Deck list ────────────────────────────────────────────
                val displayDecks = if (searchQuery.isBlank()) decks
                else decks.filter { it.name.contains(searchQuery, ignoreCase = true) }

                if (displayDecks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 1.dp,
                                        color = TextMuted.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Үүсгэсэн багц байхгүй", fontSize = 14.sp, color = TextMuted)
                            }
                        }
                    }
                } else {
                    items(displayDecks) { deck ->
                        val deckIndex = decks.indexOf(deck)
                        val iconColor = deckColors[deckIndex % deckColors.size]
                        
                        val totalCards = deck.cards.size
                        val masteredCount = deck.cards.count { it.leitnerBox >= 5 }
                        val progressPercent = if (totalCards > 0) (masteredCount * 100 / totalCards) else 0
                        val progressFloat = if (totalCards > 0) (masteredCount.toFloat() / totalCards) else 0f

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDeckClick(deck.id) },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Circular Background Icon
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(
                                            if (deck.isFolder) Primary.copy(alpha = 0.2f)
                                            else iconColor.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (deck.isFolder) androidx.compose.material.icons.Icons.Default.Folder else androidx.compose.material.icons.Icons.Default.Style,
                                        contentDescription = if (deck.isFolder) "Хавтас" else "Багц",
                                        tint = if (deck.isFolder) Primary else iconColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = deck.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (deck.isFolder) "Хавтас"
                                               else "$totalCards карт",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                // Progress side
                                if (!deck.isFolder) {
                                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(60.dp)) {
                                        Text(
                                            text = "$progressPercent%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { progressFloat },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = Primary,
                                            trackColor = SurfaceVariant,
                                            strokeCap = StrokeCap.Round
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}
