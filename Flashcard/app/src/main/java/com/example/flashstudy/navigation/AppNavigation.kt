package com.example.flashstudy.navigation

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.flashstudy.data.DeckRepository
import kotlinx.coroutines.launch
import com.example.flashstudy.ui.screens.CardEditorScreen
import com.example.flashstudy.ui.screens.CreateEditDeckScreen
import com.example.flashstudy.ui.screens.CreateFolderScreen
import com.example.flashstudy.ui.screens.DeckDetailScreen
import com.example.flashstudy.ui.screens.FolderDetailScreen

import com.example.flashstudy.ui.screens.FlashcardScreen
import com.example.flashstudy.ui.screens.HomeScreen
import com.example.flashstudy.ui.screens.LearnScreen
import com.example.flashstudy.ui.screens.LibraryScreen
import com.example.flashstudy.ui.screens.StatsScreen
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.Surface
import com.example.flashstudy.ui.theme.SurfaceElevated
import com.example.flashstudy.ui.theme.TextMuted
import com.example.flashstudy.ui.theme.TextPrimary

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Нүүр", Icons.Default.Home, "home"),
    BottomNavItem("Шинэ", Icons.Default.Add, ""), // Handled specially
    BottomNavItem("Багцууд", Icons.Default.Folder, "library")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: DeckRepository
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar only on home and library top-level routes
    val showBottomBar = currentRoute == "home" ||
            currentRoute == "library" ||
            currentRoute == null

    var showCreateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Sync selected tab with current route
    LaunchedEffect(currentRoute) {
        when {
            currentRoute?.startsWith("home") == true -> selectedTabIndex = 0
            currentRoute?.startsWith("library") == true -> selectedTabIndex = 2
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                FloatingBottomNavBar(
                    items = bottomNavItems,
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { index ->
                        if (index == 1) {
                            // "Шинэ" opens bottom sheet
                            showCreateSheet = true
                        } else {
                            val route = bottomNavItems[index].route
                            selectedTabIndex = index
                            navController.navigate(route) {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home", // Changed to home
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    repository = repository,
                    onDeckClick = { deckId -> navController.navigate("deck_detail/$deckId") },
                    onCreateDeck = { showCreateSheet = true }
                )
            }

            composable("library") {
                LibraryScreen(
                    repository = repository,
                    onDeckClick = { deckId -> navController.navigate("deck_detail/$deckId") },
                    onFolderClick = { folderId -> navController.navigate("folder_detail/$folderId") },
                    onCreateShowSheet = { showCreateSheet = true }
                )
            }

            composable(
                route = "create_folder?folderId={folderId}",
                arguments = listOf(
                    navArgument("folderId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getString("folderId")
                CreateFolderScreen(
                    navController = navController,
                    repository = repository,
                    folderId = folderId
                )
            }

            composable(
                route = "folder_detail/{folderId}",
                arguments = listOf(
                    navArgument("folderId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getString("folderId") ?: return@composable
                FolderDetailScreen(
                    navController = navController,
                    repository = repository,
                    folderId = folderId
                )
            }


            composable(
                route = "create_edit_deck?deckId={deckId}&isFolder={isFolder}&folderIdToLink={folderIdToLink}",
                arguments = listOf(
                    navArgument("deckId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("isFolder") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("folderIdToLink") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")
                val isFolder = backStackEntry.arguments?.getBoolean("isFolder") ?: false
                val folderIdToLink = backStackEntry.arguments?.getString("folderIdToLink")
                CreateEditDeckScreen(
                    repository = repository,
                    deckId = deckId,
                    isFolder = isFolder,
                    folderIdToLink = folderIdToLink,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCardEditor = { dId, cardId ->
                        val route = if (cardId != null) {
                            "card_editor/$dId?cardId=$cardId"
                        } else {
                            "card_editor/$dId"
                        }
                        navController.navigate(route)
                    }
                )
            }

            composable(
                route = "deck_detail/{deckId}",
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                DeckDetailScreen(
                    repository = repository,
                    deckId = deckId,
                    onNavigateBack = { navController.popBackStack() },
                // Editing deck still uses old route format (no isFolder needed when editing)
                    onEditDeck = {
                        navController.navigate("create_edit_deck?deckId=$deckId")
                    },
                    onNavigateToFlashcard = {
                        navController.navigate("flashcard/$deckId")
                    },
                    onNavigateToLearn = {
                        navController.navigate("learn/$deckId")
                    },
                    onNavigateToStats = {
                        navController.navigate("stats/$deckId")
                    },
                    onNavigateToCardEditor = { cardId ->
                        val route = if (cardId != null) {
                            "card_editor/$deckId?cardId=$cardId"
                        } else {
                            "card_editor/$deckId"
                        }
                        navController.navigate(route)
                    }
                )
            }

            composable(
                route = "card_editor/{deckId}?cardId={cardId}",
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType },
                    navArgument("cardId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                val cardId = backStackEntry.arguments?.getString("cardId")
                CardEditorScreen(
                    repository = repository,
                    deckId = deckId,
                    cardId = cardId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "flashcard/{deckId}",
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                FlashcardScreen(
                    repository = repository,
                    deckId = deckId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "learn/{deckId}",
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                LearnScreen(
                    repository = repository,
                    deckId = deckId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "stats/{deckId}",
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
                StatsScreen(
                    repository = repository,
                    deckId = deckId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    // ModalBottomSheet - "Шинэ"
    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState,
            containerColor = Surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Юу үүсгэх вэ?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Карт үүсгэх
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            navController.navigate("create_edit_deck")
                            showCreateSheet = false
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF2DD4BF).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.LibraryBooks, contentDescription = null, tint = Color(0xFF2DD4BF), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Картын багц үүсгэх", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Картуудаа нэг багцад нэгтгэх", fontSize = 12.sp, color = TextMuted)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Хавтас үүсгэх
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            navController.navigate("create_folder")
                            showCreateSheet = false
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFB923C).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = Color(0xFFFB923C), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Хавтас", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Хэд хэдэн flashcard set агуулах", fontSize = 12.sp, color = TextMuted)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FloatingBottomNavBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .width(300.dp)
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            color = SurfaceElevated,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { onItemSelected(0) },
                    icon = { Icon(imageVector = items[0].icon, contentDescription = items[0].label, modifier = Modifier.size(24.dp)) },
                    label = { Text(text = items[0].label, fontSize = 11.sp, fontWeight = if(selectedIndex==0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Primary, selectedTextColor = Primary,
                        unselectedIconColor = TextMuted, unselectedTextColor = TextMuted,
                        indicatorColor = Primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Add button (Center)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Box(
                            modifier = Modifier
                                .size(48.dp) // Large button
                                .clip(CircleShape)
                                .background(SurfaceElevated)
                                .clickable { onItemSelected(1) }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = items[1].icon,
                                    contentDescription = "Шинэ",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                // Library
                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = { onItemSelected(2) },
                    icon = { Icon(imageVector = items[2].icon, contentDescription = items[2].label, modifier = Modifier.size(24.dp)) },
                    label = { Text(text = items[2].label, fontSize = 11.sp, fontWeight = if(selectedIndex==2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Primary, selectedTextColor = Primary,
                        unselectedIconColor = TextMuted, unselectedTextColor = TextMuted,
                        indicatorColor = Primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
