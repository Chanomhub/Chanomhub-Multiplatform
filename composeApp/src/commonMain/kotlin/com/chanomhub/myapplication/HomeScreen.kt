package com.chanomhub.myapplication.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Data classes for API response
@Serializable
data class Author(
    val id: Int,
    val name: String,
    val bio: String,
    val image: String
)

@Serializable
data class Platform(
    val id: Int,
    val name: String
)

@Serializable
data class Tag(
    val id: Int,
    val name: String
)

@Serializable
data class Category(
    val id: Int,
    val name: String
)

@Serializable
data class Article(
    val id: Int,
    val author: Author,
    val platforms: List<Platform>,
    val tags: List<Tag>,
    val categories: List<Category>,
    val title: String,
    val slug: String,
    val description: String,
    val body: String,
    val version: Int,
    val ver: String,
    val createdAt: Long,
    val updatedAt: Long,
    val mainImage: String,
    val backgroundImage: String?,
    val coverImage: String?,
    val status: String,
    val points: Int,
    val engine: String,
    val sequentialCode: String,
    val favoriteCount: Int,
    val commentCount: Int
)

@Serializable
data class ApiResponse(
    val hits: List<Article>,
    val query: String,
    val processingTimeMs: Int,
    val limit: Int,
    val offset: Int,
    val estimatedTotalHits: Int
)

// Expect/actual function for platform-specific HTTP client
expect fun createHttpClient(): HttpClient

@Composable
fun HomeScreen() {
    var articles by remember { mutableStateOf<List<Article>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Use platform-specific HTTP Client
    val httpClient = remember { createHttpClient() }

    LaunchedEffect(Unit) {
        try {
            val response = httpClient.post("https://search.chanomhub.online/indexes/article/search") {
                contentType(ContentType.Application.Json)
                headers {
                    append("Authorization", "Bearer d7fc12050f11a0b6a069ec2b40570e83fca767d76a72a05e5bb3c0378fab2355")
                }
                setBody("""{"limit":21,"offset":0,"q":"*","sort":["updatedAt:desc"]}""")
            }

            val apiResponse: ApiResponse = response.body()
            articles = apiResponse.hits
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "เกิดข้อผิดพลาดในการโหลดข้อมูล: ${e.message}"
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            httpClient.close()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏠 หน้าแรก - Chanomhub",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = if (isLoading) "กำลังโหลด..."
                    else "พบ ${articles.size} เกม",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("กำลังโหลดข้อมูล...")
                    }
                }
            }

            errorMessage != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "❌ เกิดข้อผิดพลาด",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            articles.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ไม่พบข้อมูล",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(articles) { article ->
                        ArticleCard(article = article)
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Main Image
            AsyncImage(
                model = article.mainImage,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tags
                if (article.tags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(article.tags.take(5)) { tag ->
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = tag.name,
                                        fontSize = 10.sp
                                    )
                                },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Engine and Code
                    Column {
                        Text(
                            text = "Engine: ${article.engine}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Code: ${article.sequentialCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Platforms
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        article.platforms.forEach { platform ->
                            Badge {
                                Text(
                                    text = platform.name.uppercase(),
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Author info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = article.author.image,
                        contentDescription = article.author.name,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "โดย ${article.author.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}