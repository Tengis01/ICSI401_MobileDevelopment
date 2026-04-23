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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.flashstudy.data.Deck
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.data.Folder
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.Secondary
import com.example.flashstudy.ui.theme.Surface
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.TextSecondary
import com.example.flashstudy.ui.theme.White

sealed class LibraryItem {
    data class SetItem(val deck: Deck) : LibraryItem()
    data class FolderItem(val folder: Folder) : LibraryItem()
}

enum class LibraryFilter {
    ALL, SET, FOLDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    repository: DeckRepository,
    onDeckClick: (String) -> Unit,
    onFolderClick: (String) -> Unit,
    onCreateShowSheet: () -> Unit
) {
    var decks by remember { mutableStateOf(repository.getDecks()) }
    var folders by remember { mutableStateOf(repository.getFolders()) }

    // Refresh on resume
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                decks = repository.getDecks()
                folders = repository.getFolders()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(LibraryFilter.ALL) }

    val allItems = remember(decks, folders) {
        val folderItems = folders.map { LibraryItem.FolderItem(it) }
        val setItems = decks.map { LibraryItem.SetItem(it) }
        folderItems + setItems
    }

    val filteredItems = allItems.filter { item ->
        val name = when (item) {
            is LibraryItem.FolderItem -> item.folder.name
            is LibraryItem.SetItem -> item.deck.name
        }
        val matchesSearch = name.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            LibraryFilter.ALL -> true
            LibraryFilter.SET -> item is LibraryItem.SetItem
            LibraryFilter.FOLDER -> item is LibraryItem.FolderItem
        }
        matchesSearch && matchesFilter
    }

    val amberColor = Color(0xFFFB923C)
    val tealColor = Color(0xFF2DD4BF)

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        GradientBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Title
                item {
                    Text(
                        text = "Багцууд",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Search Bar
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
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
                                    text = "Хайх...",
                                    fontSize = 14.sp,
                                    color = TextMuted
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
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

                // Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedFilter == LibraryFilter.ALL,
                                onClick = { selectedFilter = LibraryFilter.ALL },
                                label = { Text("Бүгд", fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Secondary,
                                    selectedLabelColor = White,
                                    containerColor = Color.Transparent,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedFilter == LibraryFilter.ALL,
                                    borderColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedFilter == LibraryFilter.SET,
                                onClick = { selectedFilter = LibraryFilter.SET },
                                label = { Text("Set", fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Secondary,
                                    selectedLabelColor = White,
                                    containerColor = Color.Transparent,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedFilter == LibraryFilter.SET,
                                    borderColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedFilter == LibraryFilter.FOLDER,
                                onClick = { selectedFilter = LibraryFilter.FOLDER },
                                label = { Text("Folder", fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Secondary,
                                    selectedLabelColor = White,
                                    containerColor = Color.Transparent,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedFilter == LibraryFilter.FOLDER,
                                    borderColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }

                if (filteredItems.isEmpty()) {
                    item {
                        Text(
                            text = "Олдсонгүй",
                            fontSize = 14.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                } else {
                    items(filteredItems) { item ->
                        when (item) {
                            is LibraryItem.FolderItem -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onFolderClick(item.folder.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(72.dp)
                                                .background(amberColor)
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(amberColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Folder, contentDescription = null, tint = amberColor, modifier = Modifier.size(22.dp))
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.folder.name,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${item.folder.deckIds.size} багц",
                                                    fontSize = 13.sp,
                                                    color = TextMuted
                                                )
                                            }
                                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                                        }
                                    }
                                }
                            }
                            is LibraryItem.SetItem -> {
                                val deck = item.deck
                                val masteredCount = deck.cards.count { it.leitnerBox >= 5 }
                                val reviewCount = deck.cards.count { it.needsReview }
                                val totalCards = deck.cards.size
                                val progressPercent = if (totalCards > 0) (masteredCount * 100 / totalCards) else 0
                                val progressFloat = if (totalCards > 0) (masteredCount.toFloat() / totalCards) else 0f

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onDeckClick(deck.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(112.dp)
                                                .background(tealColor)
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(tealColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = tealColor, modifier = Modifier.size(22.dp))
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = deck.name,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "$totalCards карт",
                                                        fontSize = 13.sp,
                                                        color = TextMuted
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                androidx.compose.material3.LinearProgressIndicator(
                                                    progress = { progressFloat },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(6.dp)
                                                        .clip(RoundedCornerShape(3.dp)),
                                                    color = tealColor,
                                                    trackColor = com.example.flashstudy.ui.theme.SurfaceVariant,
                                                    strokeCap = StrokeCap.Round
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = "$progressPercent%",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = tealColor
                                                )
                                            }
                                            if (masteredCount > 0 || reviewCount > 0) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    if (masteredCount > 0) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(com.example.flashstudy.ui.theme.Success.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("Эзэмшсэн: $masteredCount", fontSize = 11.sp, color = com.example.flashstudy.ui.theme.Success)
                                                        }
                                                    }
                                                    if (reviewCount > 0) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(com.example.flashstudy.ui.theme.Warning.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("Давтах: $reviewCount", fontSize = 11.sp, color = com.example.flashstudy.ui.theme.Warning)
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
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
