// ФАЙЛЫН БАЙРШИЛ: java/com/example/taskscheduler/MainActivity.kt
package com.example.taskscheduler

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.taskscheduler.notifications.NotificationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // Fragment hoorond shiljih uildliig uдirdah NavController object
    private lateinit var navController: NavController

    // Activity anh neegdeh ued ene function ajillana
    // End activity-iin undsen layout-iig tavij, navigation-iin tohirgoog hiine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        // activity_main.xml layout-iig ene activity deer tavij baina
        // Ene layout dotor nav host bolon bottom navigation baigaa
        setContentView(R.layout.activity_main)

        // Notification channel-iig uusgene - neg udaa duudagdah yostoi
        NotificationHelper.createChannel(this)

        // Layout dotor baigaa NavHostFragment-iig olj avch baina
        // NavHostFragment ni fragment-uudiig dotor ni haruulah sav yum
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // NavHostFragment dotorh navController-iig avch baina
        // Ene ni yamar fragment ruu shiljih, back stack yaj ajillahig udirdana
        navController = navHostFragment.navController

        // Bottom navigation view-g layout-aas oldoj avna
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Bottom navigation-g navController-tei holboj baina
        // Ingesneer tab deer darahad zohih fragment ruu avtomataar shiljine
        bottomNav.setupWithNavController(navController)
    }
}