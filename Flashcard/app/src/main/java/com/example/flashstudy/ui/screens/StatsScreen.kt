package com.example.flashstudy.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.LeitnerBadge
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.PrimaryVariant
import com.example.flashstudy.ui.theme.Success
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.Warning
import com.example.flashstudy.ui.theme.White
import com.example.flashstudy.ui.theme.leitnerColor

// Leitner box bur tugalbar haruulah stats delgets
// Summary grid, bar chart, kart bur baidal haruulna
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    repository: DeckRepository,
    deckId: String,
    onNavigateBack: () -> Unit
) {
    // Deck ba kartuuruudiig tatah - null safe
    val deck = remember(deckId) { repository.getDeckById(deckId) }
    val cards = deck?.cards ?: emptyList()

    // Tootsoolson todorhoilolt uud
    val total = cards.size
    val mastered = cards.count { it.leitnerBox == 5 }
    val toReview = cards.count { it.needsReview }
    val progress = if (total > 0) mastered * 100 / total else 0

    // Box bur kartiin too - index 0 = box 1
    val perBox = (1..5).map { box -> cards.count { it.leitnerBox == box } }
    val maxInBox = perBox.maxOrNull()?.takeIf { it > 0 } ?: 1

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
                        text = "Ахицын тайлан",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        GradientBackground {
            // === Hooson togtolts: kart baikhgui yed ===
            if (total == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Статистик харахад карт байхгүй байна",
                        fontSize = 15.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TextButton(onClick = onNavigateBack) {
                        Text("Буцах", color = Primary)
                    }
                }
                return@GradientBackground
            }

            // === Undsen agulga: stats jagsaalt ===
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // === 2x2 Summary grid ===
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Ezemshsen - Success
                            SummaryCell(
                                value = "$mastered",
                                label = "Эзэмшсэн",
                                valueColor = Success,
                                modifier = Modifier.weight(1f)
                            )
                            // Davtah - Warning
                            SummaryCell(
                                value = "$toReview",
                                label = "Давтах",
                                valueColor = Warning,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Niit kart - TextPrimary
                            SummaryCell(
                                value = "$total",
                                label = "Нийт карт",
                                valueColor = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            // Niit ahits - Primary
                            SummaryCell(
                                value = "$progress%",
                                label = "Нийт ахиц",
                                valueColor = Primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // === Leitner bar chart card ===
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Leitner System — Хайрцаг бүр",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 5 bagana bar chart - dood talruugaa erembelne
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                perBox.forEachIndexed { index, count ->
                                    val boxNumber = index + 1
                                    val barColor = leitnerColor(boxNumber)

                                    // Bar onchtoi uchraas 80dp max - dorooshoo erembelne
                                    val barHeightDp = if (maxInBox > 0) {
                                        (count.toFloat() / maxInBox * 80f)
                                            .coerceAtLeast(if (count > 0) 8f else 0f)
                                    } else 0f

                                    // Box label-uud
                                    val boxLabel = when (boxNumber) {
                                        1 -> "Хайрцаг 1\nШинэ"
                                        2 -> "Хайрцаг 2"
                                        3 -> "Хайрцаг 3"
                                        4 -> "Хайрцаг 4"
                                        5 -> "Хайрцаг 5\nЭзэмшсэн"
                                        else -> ""
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        // Togloo too bar-iin deeres
                                        Text(
                                            text = count.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = barColor,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        // Bar oo
                                        if (barHeightDp > 0f) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(barHeightDp.dp)
                                                    .clip(
                                                        RoundedCornerShape(
                                                            topStart = 6.dp,
                                                            topEnd = 6.dp
                                                        )
                                                    )
                                                    .background(barColor.copy(alpha = 0.20f))
                                                    .border(
                                                        width = 1.dp,
                                                        color = barColor,
                                                        shape = RoundedCornerShape(
                                                            topStart = 6.dp,
                                                            topEnd = 6.dp
                                                        )
                                                    )
                                            )
                                        } else {
                                            // Kart baikhgui bol jijig placeholder
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Box label door
                                        Text(
                                            text = boxLabel,
                                            fontSize = 9.sp,
                                            color = TextMuted,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = TextMuted.copy(alpha = 0.12f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Leitner durem tailbar
                            Text(
                                text = "Буруу → Хайрцаг 1 · Зөв → Дараагийн хайрцаг",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // === Kart bur baidal card ===
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Карт бүрийн байдал",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Kart jagsaalt - kart bur baidal row
                items(cards) { card ->
                    // Status tag todorhoiloh
                    val (statusText, statusColor) = when {
                        card.leitnerBox == 5 -> "Эзэмшсэн" to Success
                        card.needsReview -> "Давтах" to Warning
                        else -> "Суралцаж байна" to Primary
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Leitner badge zuun
                                LeitnerBadge(box = card.leitnerBox)

                                Spacer(modifier = Modifier.width(12.dp))

                                // Term ba definition dund
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = card.term,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = card.definition,
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Status tag baruun
                                Box(
                                    modifier = Modifier
                                        .background(
                                            statusColor.copy(alpha = 0.15f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        fontSize = 10.sp,
                                        color = statusColor,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

// Summary grid cell - 4 baidlaar ashiglana
@Composable
private fun SummaryCell(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
