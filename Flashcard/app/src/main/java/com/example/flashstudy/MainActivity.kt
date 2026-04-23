package com.example.flashstudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.flashstudy.data.DeckRepository
import com.example.flashstudy.navigation.AppNavigation
import com.example.flashstudy.ui.theme.FlashStudyTheme
import com.example.flashstudy.ui.theme.GradientBackground

import androidx.room.Room
import com.example.flashstudy.data.local.FlashStudyDatabase

// FlashStudy app-iin gantskhaan Activity
// Buh delgetsuud Compose composable-iin togtoltsood ajillana
class MainActivity : ComponentActivity() {

    // Repository-g activity level-d ekhluulne - context shaardagdana
    private lateinit var repository: DeckRepository
    private lateinit var database: FlashStudyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge display idewhjuulne
        enableEdgeToEdge()

        database = Room.databaseBuilder(
            applicationContext,
            FlashStudyDatabase::class.java, "flashstudy_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build() // For simple migration
        
        repository = DeckRepository(database.deckDao())

        setContent {
            FlashStudyTheme {
                GradientBackground(modifier = Modifier.fillMaxSize()) {
                    // navController-iig remember-eer uusgene
                    val navController = rememberNavController()

                    // AppNavigation - buh route-uud ba screen-uud ene dotor
                    AppNavigation(
                        navController = navController,
                        repository = repository
                    )
                }
            }
        }
    }
}
