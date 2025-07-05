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

// Navigation state to handle article details
sealed class NavigationState {
    object Home : NavigationState()
    object Search : NavigationState()
    object Profile : NavigationState()
    object Settings : NavigationState()
    data class ArticleDetail(val articleSlug: String) : NavigationState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var selectedScreen by remember { mutableStateOf(Screen.HOME) }
    var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.Home) }

    // Function to navigate to article detail using slug
    val navigateToArticle = { articleSlug: String ->
        navigationState = NavigationState.ArticleDetail(articleSlug)
    }

    // Function to navigate back
    val navigateBack = {
        navigationState = when (selectedScreen) {
            Screen.HOME -> NavigationState.Home
            Screen.SEARCH -> NavigationState.Search
            Screen.PROFILE -> NavigationState.Profile
            Screen.SETTINGS -> NavigationState.Settings
        }
    }

    // Update navigation state when screen changes
    LaunchedEffect(selectedScreen) {
        navigationState = when (selectedScreen) {
            Screen.HOME -> NavigationState.Home
            Screen.SEARCH -> NavigationState.Search
            Screen.PROFILE -> NavigationState.Profile
            Screen.SETTINGS -> NavigationState.Settings
        }
    }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                // Hide bottom navigation when viewing article details
                if (navigationState !is NavigationState.ArticleDetail) {
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
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val currentNavState = navigationState
                when (currentNavState) {
                    is NavigationState.Home -> {
                        HomeScreen(onArticleClick = navigateToArticle)
                    }
                    is NavigationState.Search -> {
                        SearchScreen(onArticleClick = navigateToArticle)
                    }
                    is NavigationState.Profile -> {
                        ProfileScreen()
                    }
                    is NavigationState.Settings -> {
                        SettingsScreen()
                    }
                    is NavigationState.ArticleDetail -> {
                        ArticleDetailScreen(
                            articleSlug = currentNavState.articleSlug,
                            onBackClick = navigateBack
                        )
                    }
                }
            }
        }
    }
}