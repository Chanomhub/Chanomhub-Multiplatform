package com.chanomhub.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // ข้อมูลตัวอย่างสำหรับการค้นหา
    val sampleData = listOf(
        "Kotlin Multiplatform",
        "Compose UI",
        "Android Development",
        "iOS Development",
        "Cross Platform",
        "Mobile App",
        "UI/UX Design",
        "Programming",
        "Software Development",
        "Technology"
    )

    val filteredData = remember(searchText) {
        if (searchText.isEmpty()) {
            emptyList()
        } else {
            sampleData.filter {
                it.contains(searchText, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "🔍 ค้นหา",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                isSearching = it.isNotEmpty()
            },
            label = { Text("ค้นหา...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Results
        if (isSearching) {
            if (filteredData.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ไม่พบผลการค้นหา",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredData) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                // Handle item click
                            }
                        ) {
                            Text(
                                text = item,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        } else {
            // Popular searches or suggestions
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔥 ยอดนิยม",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    sampleData.take(5).forEach { item ->
                        TextButton(
                            onClick = { searchText = item },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(item)
                            }
                        }
                    }
                }
            }
        }
    }
}