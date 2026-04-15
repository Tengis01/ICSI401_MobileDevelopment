package com.example.flashstudy.navigation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.flashstudy.ui.screens.DeckDetailScreen
import com.example.flashstudy.ui.screens.DeckListScreen
import com.example.flashstudy.ui.screens.FlashcardScreen
import com.example.flashstudy.ui.screens.LearnScreen
import com.example.flashstudy.ui.screens.StatsScreen
import com.example.flashstudy.ui.theme.Primary
import com.example.flashstudy.ui.theme.Surface as AppSurface
import com.example.flashstudy.ui.theme.TextMuted

// Bottom nav tab-iin medeelliig hadgalah data class
private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// 4 tab-iin jagsaalt - odoogoor buh tab deck_list-ruu yvna
private val bottomNavItems = listOf(
    BottomNavItem("Нүүр", Icons.Default.Home, "deck_list"),
    BottomNavItem("Багцууд", Icons.Default.MenuBook, "deck_list"),
    BottomNavItem("Дасгал", Icons.Default.TrackChanges, "deck_list"),
    BottomNavItem("Профайл", Icons.Default.Person, "deck_list")
)

// App-iin undsen navigation composable
// navController ba repository-g avna
@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: DeckRepository
) {
    // odoogiin idewhtei tab-iin index
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // odoogiin route-iig harj tab songolt shinechleh
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom nav delgetsuudees gadna haruulah esehiig shalgana
    val showBottomBar = currentRoute?.startsWith("deck_list") == true ||
            currentRoute == null

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            // Floating pill nav - dooshoo unegui, tovchin haruulna
            if (showBottomBar) {
                FloatingBottomNavBar(
                    items = bottomNavItems,
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { index ->
                        // Profail tab (index 3) navigate hiihgui, snackbar haruulna
                        if (index == 3) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Тун удахгүй!")
                            }
                        } else {
                            selectedTabIndex = index
                            navController.navigate("deck_list") {
                                // back stack-iig tseverlen davhar navigate hiyehaas zailsna
                                popUpTo("deck_list") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        // NavHost - buh route-uudiig ene dotor todorhoilno
        NavHost(
            navController = navController,
            startDestination = "deck_list",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Deck jagsaaltiin delgets
            composable("deck_list") {
                DeckListScreen(
                    repository = repository,
                    onCreateDeck = {
                        navController.navigate("create_edit_deck")
                    },
                    onDeckClick = { deckId ->
                        navController.navigate("deck_detail/$deckId")
                    }
                )
            }

            // Deck uusgeh/zasvarlahin delgets - deckId optional parameter
            composable(
                route = "create_edit_deck?deckId={deckId}",
                arguments = listOf(
                    navArgument("deckId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")
                CreateEditDeckScreen(
                    repository = repository,
                    deckId = deckId,
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

            // Deck delgerengui delgets
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

            // Kart nemeh/zasvarlahin delgets - cardId optional
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

            // Flashcard dасгалын delgets
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

            // Learn gorimin delgets
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

            // Statistikiin delgets
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
}

// Floating pill bottom nav bar composable
// 320dp ongon, 28dp rounded, 8dp shadow, dooshoo 20dp zadgai
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
                .width(320.dp)
                .height(68.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onItemSelected(index) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = Primary.copy(alpha = 0.10f)
                        )
                    )
                }
            }
        }
    }
}
