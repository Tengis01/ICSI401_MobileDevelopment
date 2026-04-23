package com.example.flashstudy.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.LeitnerBadge
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.PrimaryGradientButton
import com.example.flashstudy.ui.theme.Success
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.Warning
import com.example.flashstudy.ui.theme.White

// Deck-iin delgerengui medeelel haruulah delgets
// Stat chip, mode grid, kart jagsaalt, hooson togtolts
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    repository: DeckRepository,
    deckId: String,
    onNavigateBack: () -> Unit,
    onEditDeck: () -> Unit,
    onNavigateToFlashcard: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToCardEditor: (String?) -> Unit
) {
    // Deck state - id-aar tataj avna, null bol hooson togtolts haruulna
    var deck by remember { mutableStateOf(repository.getDeckById(deckId)) }

    // Ustgah baiguullagiig haruulah state
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Lifecycle resume bolgond deck-iig dakhij tatna (kart nemeh/zassan yed)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                deck = repository.getDeckById(deckId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Tootsoolson utguud - deck null bol 0 bolgono
    val total = deck?.cards?.size ?: 0
    val mastered = deck?.cards?.count { it.leitnerBox == 5 } ?: 0
    val toReview = deck?.cards?.count { it.needsReview && it.leitnerBox < 5 } ?: 0
    // Weighted Leitner progress: box1=0%, box2=25%, box3=50%, box4=75%, box5=100%
    val progress = if (total > 0) {
        val weightedSum = deck!!.cards.sumOf { ((it.leitnerBox - 1) * 25) }
        weightedSum / total
    } else 0

    // Ustgah baiguullag - deck ustgaj deck_list ruu butsana
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Устгах уу?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Энэ үйлдлийг буцаах боломжгүй. Багц болон бүх картууд устагдана.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.deleteDeck(deckId)
                        showDeleteDialog = false
                        // Butsaj deck_list delgets ruu - back stack-iin deeguur deck_list baidag
                        onNavigateBack()
                    }
                ) {
                    Text(
                        text = "Устгах",
                        color = Danger,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Цуцлах", color = TextMuted)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    // Butsah arrow
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
                        text = deck?.name ?: "Багц",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    // Zasah icon
                    IconButton(onClick = onEditDeck) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Багц засах",
                            tint = Primary
                        )
                    }
                    // Ustgah icon - ulsaan ongotoi
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Багц устгах",
                            tint = Danger
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    scrolledContainerColor = White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        GradientBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // === Hero card: avatar + ner + tailbar + suuld uzsen ===
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Zuun taliin teal border 4dp
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(100.dp)
                                    .background(Primary)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar dugui - deck neriin UrDur 2 temdeget
                                    val avatarText = (deck?.name ?: "?")
                                        .take(2)
                                        .uppercase()
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = avatarText,
                                            color = White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = deck?.name ?: "",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (!deck?.description.isNullOrBlank()) {
                                            Text(
                                                text = deck?.description ?: "",
                                                fontSize = 12.sp,
                                                color = TextMuted,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Suuld uzsen tsag
                                val lastStudied = deck?.lastStudied ?: 0L
                                Text(
                                    text = if (lastStudied > 0L) {
                                        "Сүүлд үзсэн: ${formatTimeAgo(lastStudied)}"
                                    } else {
                                        "Үзэж байгаагүй"
                                    },
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                // === 4 stat chip - niit, ezemshsen, davtah, ahits ===
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatChip(
                            value = "$total",
                            label = "Нийт",
                            valueColor = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            value = "$mastered",
                            label = "Эзэмшсэн",
                            valueColor = Success,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            value = "$toReview",
                            label = "Давтах",
                            valueColor = Warning,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            value = "$progress%",
                            label = "Ахиц",
                            valueColor = Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // === Ahitsiin progress bar ===
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ерөнхий ахиц",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "$mastered / $total",
                                    fontSize = 13.sp,
                                    color = Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = {
                                    if (total > 0) mastered.toFloat() / total else 0f
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Primary,
                                trackColor = Primary.copy(alpha = 0.12f),
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }

                // === 2x2 dasgaliin gorim grid ===
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Flashcard gorim
                        ModeButton(
                            label = "Flashcard горим",
                            icon = Icons.Default.Style,
                            iconTint = Primary,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToFlashcard
                        )
                        // Learn gorim
                        ModeButton(
                            label = "Learn горим",
                            icon = Icons.Default.School,
                            iconTint = com.example.flashstudy.ui.theme.PrimaryVariant,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToLearn
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Statistik
                        ModeButton(
                            label = "Статистик",
                            icon = Icons.Default.BarChart,
                            iconTint = Success,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToStats
                        )
                        // Kart nemeh
                        ModeButton(
                            label = "Карт нэмэх",
                            icon = Icons.Default.CreditCard,
                            iconTint = Warning,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToCardEditor(null) }
                        )
                    }
                }

                // === Buh kartuuruud header ===
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Бүх картууд",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                // === Hooson togtolts: kart baikhgui yed ===
                if (total == 0) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🗂️",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Карт байхгүй байна",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Эхний картаа нэмэхэд бэлэн үү?",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            PrimaryGradientButton(
                                text = "+ Карт нэмэх",
                                onClick = { onNavigateToCardEditor(null) },
                                modifier = Modifier.padding(horizontal = 40.dp)
                            )
                        }
                    }
                }

                // === Kart jagsaalt ===
                val cardList = deck?.cards ?: emptyList()
                items(cardList) { card ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Leitner badge - box tuvshing dugui tootsoor haruulna
                            LeitnerBadge(box = card.leitnerBox)

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.term,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = card.definition,
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Status dot - needsReview esehiig haruulna
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (card.needsReview) Warning else Success
                                    )
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

// Niitleg stat chip composable - 4 baidlaar ашиглана
@Composable
private fun StatChip(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

// Dasgaliin gorim button - 2x2 grid-d ashiglana
@Composable
private fun ModeButton(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Epoch millisecond-iig "X odriin omno" gesen tekst bolgoh tuslah function
private fun formatTimeAgo(timestamp: Long): String {
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
