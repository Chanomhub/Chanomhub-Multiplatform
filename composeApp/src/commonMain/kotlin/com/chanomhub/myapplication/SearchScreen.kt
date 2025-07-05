package com.chanomhub.myapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit = {} // Changed from Int to String for slug
) {
    var searchQuery by remember { mutableStateOf("") }
    var articles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasSearched by remember { mutableStateOf(false) }
    val httpClient = remember { createHttpClient() }

    // Function to perform search
    suspend fun performSearch(query: String) {
        if (query.trim().isEmpty()) return

        try {
            isLoading = true
            errorMessage = null
            hasSearched = true

            val response = withContext(Dispatchers.IO) {
                httpClient.post("https://search.chanomhub.online/indexes/article/search") {
                    contentType(ContentType.Application.Json)
                    headers {
                        append("Authorization", "Bearer d7fc12050f11a0b6a069ec2b40570e83fca767d76a72a05e5bb3c0378fab2355")
                    }
                    setBody("""{"limit":21,"offset":0,"q":"$query","sort":["updatedAt:desc"]}""")
                }
            }

            if (response.status.isSuccess()) {
                articles = response.body<ApiResponse>().hits
            } else {
                throw Exception("HTTP Error: ${response.status}")
            }
        } catch (e: Exception) {
            errorMessage = "เกิดข้อผิดพลาด: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { runCatching { httpClient.close() } }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🔍 ค้นหา - Chanomhub",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    if (hasSearched) "พบ ${articles.size} ผลลัพธ์" else "ค้นหาเกมที่คุณสนใจ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("ค้นหาเกม...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "ค้นหา")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Button(
                        onClick = {
                            if (searchQuery.trim().isNotEmpty()) {
                                kotlinx.coroutines.GlobalScope.launch {
                                    performSearch(searchQuery.trim())
                                }
                            }
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("ค้นหา")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Results Section
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("กำลังค้นหา...")
                    }
                }
            }
            errorMessage != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("❌ เกิดข้อผิดพลาด", style = MaterialTheme.typography.titleMedium)
                        Text(errorMessage!!, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            errorMessage = null
                            if (searchQuery.trim().isNotEmpty()) {
                                kotlinx.coroutines.GlobalScope.launch {
                                    performSearch(searchQuery.trim())
                                }
                            }
                        }) {
                            Text("ลองใหม่")
                        }
                    }
                }
            }
            !hasSearched -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", style = MaterialTheme.typography.headlineLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("กรอกคำค้นหาเพื่อเริ่มต้น", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            articles.isEmpty() -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("😕", style = MaterialTheme.typography.headlineLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ไม่พบผลลัพธ์สำหรับ \"$searchQuery\"", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(articles) { article ->
                        SearchResultCard(
                            article = article,
                            onArticleClick = onArticleClick,
                            searchQuery = searchQuery
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    article: Article,
    onArticleClick: (String) -> Unit, // Changed from Int to String for slug
    searchQuery: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArticleClick(article.slug) }, // Use slug instead of id
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row {
            AsyncImage(
                model = article.mainImage,
                contentDescription = article.title,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Engine: ${article.engine}", style = MaterialTheme.typography.bodySmall)
                        Text("Code: ${article.sequentialCode}", style = MaterialTheme.typography.bodySmall)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        article.platforms.take(2).forEach { platform ->
                            Badge { Text(platform.name.uppercase(), fontSize = 8.sp) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (article.tags.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(article.tags.take(3)) { tag ->
                            AssistChip(
                                onClick = { },
                                label = { Text(tag.name, fontSize = 8.sp) },
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}