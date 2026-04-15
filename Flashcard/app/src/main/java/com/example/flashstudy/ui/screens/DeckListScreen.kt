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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Background
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
import kotlinx.coroutines.launch

// Deck jagsaaltiin undsen delgets
// Mend chimee, odriin zorilgo, dasgaliin gorim, hailt, deck kartuuruud haruulna
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    repository: DeckRepository,
    onCreateDeck: () -> Unit,
    onDeckClick: (String) -> Unit
) {
    // Deck jagsaalt state - repository-aas tataj avna
    var decks by remember { mutableStateOf(repository.getDecks()) }
    var searchQuery by remember { mutableStateOf("") }

    // Hailtaar shuursen deck jagsaalt
    val filtered = decks.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // ModalBottomSheet state - FAB darahad haruulna
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }

    // Stats card-d haruulah bodolt togtoosoh urt
    val totalCards = decks.sumOf { it.cards.size }
    val masteredCards = decks.sumOf { deck -> deck.cards.count { it.leitnerBox == 5 } }
    val reviewCards = decks.sumOf { deck -> deck.cards.count { it.needsReview } }

    // Delgets ankhnaas achaalagdahad deck-uudiig dakhij tatna
    LaunchedEffect(Unit) {
        decks = repository.getDecks()
    }

    // Lifecycle resume bolgond deck-uudiig shinechlene (busad delgetsees butsah yed)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                decks = repository.getDecks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Deck burt unique ongo todorhoiloh jagsaalt
    val deckColors = listOf(
        Primary, PrimaryVariant, Success, Warning, Danger,
        Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF06B6D4)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            // FAB zovhon decks baidag uyed haruulna, hooson uyed gradient button l hangana
            if (decks.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        nameInput = ""
                        descInput = ""
                        showBottomSheet = true
                    },
                    containerColor = Primary,
                    contentColor = White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Shine bagts nemeh")
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        GradientBackground {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // === Top bar: mend chimee ===
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Сайн байна уу! 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Өнөөдөр юу судлах вэ?",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                // === Odriin zorilgiin card - bodolt togtoson utguudiig haruulna ===
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Shine kart uzeh - ezemshsen / niit
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Шинэ карт үзэх",
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "$masteredCards / $totalCards",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = {
                                    if (totalCards > 0) masteredCards.toFloat() / totalCards else 0f
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Primary,
                                trackColor = Primary.copy(alpha = 0.15f),
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Davtah dasgal - davtah bish kartiin haritsaa
                            val reviewedCards = totalCards - reviewCards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Давтах дасгал",
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "$reviewedCards / $totalCards",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Success
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = {
                                        if (totalCards > 0)
                                            reviewedCards.toFloat() / totalCards else 0f
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Success,
                                    trackColor = Success.copy(alpha = 0.15f),
                                    strokeCap = StrokeCap.Round
                                )
                                // Check duur zarim yed l haruulna
                                if (reviewCards == 0 && totalCards > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Buren guitsetgesen",
                                        tint = Success,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // === Shine bagts uusgeh button ===
                item {
                    PrimaryGradientButton(
                        text = "+ Шинэ багц үүсгэх",
                        onClick = onCreateDeck
                    )
                }

                // === Dasgal gorimuud section ===
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Дасгал горимууд",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Өөрийн багцаар дасгал хийх",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // === 5 dasgaliin gorim ===
                item {
                    // 1. Flashcard gorim
                    PracticeModeRow(
                        icon = Icons.Default.Style,
                        iconBgColor = Primary.copy(alpha = 0.10f),
                        iconTint = Primary,
                        title = "Flashcard горим",
                        subtitle = "Карт тогшиж давтах",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Эхлээд багцаа сонгоно уу")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Learn gorim
                    PracticeModeRow(
                        icon = Icons.Default.Edit,
                        iconBgColor = PrimaryVariant.copy(alpha = 0.10f),
                        iconTint = PrimaryVariant,
                        title = "Learn горим",
                        subtitle = "Хариулт бичих",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Эхлээд багцаа сонгоно уу")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Statistik
                    PracticeModeRow(
                        icon = Icons.Default.BarChart,
                        iconBgColor = Success.copy(alpha = 0.10f),
                        iconTint = Success,
                        title = "Статистик",
                        subtitle = "Leitner хайрцаг · ахиц",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Эхлээд багцаа сонгоно уу")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Minii bagtsuuruud - deck jagsaalt section ruu scroll hiine
                    PracticeModeRow(
                        icon = Icons.Default.Layers,
                        iconBgColor = Warning.copy(alpha = 0.10f),
                        iconTint = Warning,
                        title = "Миний багцууд",
                        subtitle = "Үүсгэх · засах · устгах",
                        onClick = {
                            // Deck jagsaalt section ruu scroll hiine
                            scope.launch {
                                listState.animateScrollToItem(7)
                            }
                        }
                    )
                }

                // === Minii bagtsuuruud section header ===
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Миний багцууд",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${filtered.size} багц",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // === Hailtiin bar ===
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            placeholder = {
                                Text(
                                    text = "Багц хайх...",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Хайлт",
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        )
                    }
                }

                // === Deck card-uud ===
                items(filtered) { deck ->
                    val deckIndex = decks.indexOf(deck)
                    val stripeColor = deckColors[deckIndex % deckColors.size]

                    // Ezemshsen ba davtah kartiin too tootsooloh
                    val masteredCount = deck.cards.count { it.leitnerBox >= 5 }
                    val reviewCount = deck.cards.count { it.needsReview }
                    val totalCards = deck.cards.size
                    val progress = if (totalCards > 0) masteredCount.toFloat() / totalCards else 0f

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeckClick(deck.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Zuun taliin ongo zuuras
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(120.dp)
                                    .background(stripeColor)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(14.dp)
                            ) {
                                // Deck ner ba kartiin too
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = deck.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Kartiin too tag
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Primary.copy(alpha = 0.10f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$totalCards карт",
                                            fontSize = 11.sp,
                                            color = Primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Tailbar
                                if (deck.description.isNotEmpty()) {
                                    Text(
                                        text = deck.description,
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }

                                // Suuld uzsen tsag
                                Text(
                                    text = if (deck.lastStudied > 0L) {
                                        "Сүүлд: ${formatTimeAgo(deck.lastStudied)}"
                                    } else {
                                        "Хараахан судлаагүй"
                                    },
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Ahitsiin bar
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = Primary,
                                    trackColor = Primary.copy(alpha = 0.10f),
                                    strokeCap = StrokeCap.Round
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Ezemshsen ba davtah pill-uuruud
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Ezemshsen pill
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Success.copy(alpha = 0.12f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "$masteredCount эзэмшсэн",
                                                fontSize = 11.sp,
                                                color = Success,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Davtah pill
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Warning.copy(alpha = 0.12f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "$reviewCount давтах",
                                                fontSize = 11.sp,
                                                color = Warning,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    // Zasah icon - deck detail ruu ochiod edit hiine
                                    IconButton(
                                        onClick = { onDeckClick(deck.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Багц засах",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Doodu zadgai zai - FAB-d haalt uildluulehgui
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // ModalBottomSheet - FAB darahad shine bagts uusgeh form haruulna
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Шинэ багц үүсгэх",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Bagtsiin ner - shaardilatai
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Багцын нэр *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        cursorColor = Primary,
                        unfocusedBorderColor = TextMuted.copy(alpha = 0.4f)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Tailbar - songolt
                OutlinedTextField(
                    value = descInput,
                    onValueChange = { descInput = it },
                    label = { Text("Тайлбар") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        cursorColor = Primary,
                        unfocusedBorderColor = TextMuted.copy(alpha = 0.4f)
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Uusgeh gradient button - ner hooson bol guildehgui
                PrimaryGradientButton(
                    text = "Үүсгэх",
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            val newDeck = com.example.flashstudy.data.Deck(
                                name = nameInput.trim(),
                                description = descInput.trim()
                            )
                            repository.saveDeck(newDeck)
                            decks = repository.getDecks()
                            showBottomSheet = false
                            onDeckClick(newDeck.id)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Tsutslah text button - sheet hadana
                TextButton(
                    onClick = { showBottomSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Цуцлах", color = TextMuted)
                }
            }
        }
    }
}

// Dasgaliin gorim burt haruulah niitleg muriig composable
@Composable
private fun PracticeModeRow(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon dugui
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Epoch millisecond-iig "X odriin omno" gesen tekst bolgoh tuslah function
internal fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Дөнгөж сая"
        minutes < 60 -> "$minutes мин өмнө"
        hours < 24 -> "$hours цагийн өмнө"
        days < 7 -> "$days өдрийн өмнө"
        else -> "${days / 7} долоо хоногийн өмнө"
    }
}
