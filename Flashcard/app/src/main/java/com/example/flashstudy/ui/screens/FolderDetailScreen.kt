package com.example.flashstudy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.flashstudy.data.Deck
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.ui.theme.Danger
import com.example.flashstudy.ui.theme.GradientBackground
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary
import com.example.flashstudy.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    navController: NavController,
    repository: DeckRepository,
    folderId: String
) {
    var folder by remember { mutableStateOf(repository.getFolderById(folderId)) }
    var decksInFolder by remember { mutableStateOf<List<Deck>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Reload function
    fun reloadFolder() {
        val f = repository.getFolderById(folderId)
        folder = f
        decksInFolder = f?.deckIds?.mapNotNull { repository.getDeckById(it) } ?: emptyList()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                reloadFolder()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (folder == null) {
        // Fallback or navigate back
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    // Colors
    val amberColor = Color(0xFFFB923C)
    val tealColor = Color(0xFF2DD4BF)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Буцах", tint = TextPrimary)
                    }
                },
                title = {
                    Text(
                        text = folder?.name ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("create_folder?folderId=$folderId") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Засах", tint = TextMuted)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Устгах", tint = Danger)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        GradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Hero card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(80.dp)
                                .background(amberColor)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(amberColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = amberColor, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = folder?.name ?: "",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${decksInFolder.size} багц",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                if (!folder?.description.isNullOrBlank()) {
                    Text(
                        text = folder?.description ?: "",
                        fontSize = 14.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Flashcard set-ууд",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = { showAddSheet = true }) {
                        Text("+ Нэмэх", color = amberColor, fontWeight = FontWeight.Bold)
                    }
                }

                if (decksInFolder.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🗂️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Энэ хавтас хоосон байна", fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            TextButton(onClick = { showAddSheet = true }) {
                                Text("+ Багц нэмэх", color = amberColor)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(decksInFolder) { deck ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate("deck_detail/${deck.id}") },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(64.dp) // Adjusted height
                                            .background(tealColor)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(tealColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = tealColor, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = deck.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${deck.cards.size} карт",
                                                fontSize = 12.sp,
                                                color = TextMuted
                                            )
                                        }
                                        IconButton(onClick = {
                                            repository.removeDeckFromFolder(folderId, deck.id)
                                            reloadFolder()
                                        }) {
                                            Icon(Icons.Default.Close, contentDescription = "Хасах", tint = TextMuted, modifier = Modifier.size(20.dp))
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Хавтас устгах уу?") },
            text = { Text("Хавтас устгагдах боловч доторх картын багцууд хэвээр үлдэнэ.") },
            confirmButton = {
                TextButton(onClick = {
                    repository.deleteFolder(folderId)
                    showDeleteDialog = false
                    navController.navigate("library") {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("Устгах", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Цуцлах", color = TextPrimary)
                }
            }
        )
    }

    if (showAddSheet) {
        val allDecks = repository.getDecks()
        val notInFolder = allDecks.filter { it.id !in (folder?.deckIds ?: emptyList()) }
        var selectedDeckIds by remember { mutableStateOf(setOf<String>()) }

        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState,
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Багц нэмэх",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Шинэ багц үүсгэх товч
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            navController.navigate("create_edit_deck?folderIdToLink=$folderId")
                            showAddSheet = false
                        }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(tealColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = tealColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Шинэ багц үүсгэх", fontWeight = FontWeight.Bold, color = tealColor, fontSize = 15.sp)
                }
                
                Divider(color = TextMuted.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                if (notInFolder.isEmpty()) {
                    Text("Нэмэх багц олдсонгүй.", color = TextMuted, modifier = Modifier.padding(vertical = 24.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(notInFolder) { deck ->
                            val isSelected = selectedDeckIds.contains(deck.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedDeckIds = if (isSelected) {
                                            selectedDeckIds - deck.id
                                        } else {
                                            selectedDeckIds + deck.id
                                        }
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(checkedColor = amberColor)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = deck.name, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text(text = "${deck.cards.size} карт", fontSize = 12.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedDeckIds.forEach { deckId ->
                            repository.addDeckToFolder(folderId, deckId)
                        }
                        reloadFolder()
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showAddSheet = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = amberColor),
                    enabled = selectedDeckIds.isNotEmpty()
                ) {
                    Text("Нэмэх (${selectedDeckIds.size})", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
