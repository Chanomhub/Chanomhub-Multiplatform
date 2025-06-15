package com.chanomhub.myapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.chanomhub.myapplication.screens.HomeScreen
import com.chanomhub.myapplication.screens.ProfileScreen
import com.chanomhub.myapplication.screens.SearchScreen
import com.chanomhub.myapplication.screens.SettingsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

// Screen enum สำหรับแต่ละหน้า
enum class Screen(val title: String, val icon: ImageVector) {
    HOME("หน้าแรก", Icons.Filled.Home),
    SEARCH("ค้นหา", Icons.Filled.Search),
    PROFILE("โปรไฟล์", Icons.Filled.Person),
    SETTINGS("ตั้งค่า", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var selectedScreen by remember { mutableStateOf(Screen.HOME) }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Screen.entries.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = selectedScreen == screen,
                            onClick = { selectedScreen = screen }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedScreen) {
                    Screen.HOME -> HomeScreen()
                    Screen.SEARCH -> SearchScreen()
                    Screen.PROFILE -> ProfileScreen()
                    Screen.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}