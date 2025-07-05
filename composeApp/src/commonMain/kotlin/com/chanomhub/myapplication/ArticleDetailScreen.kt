package com.chanomhub.myapplication

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import com.chanomhub.myapplication.models.Article
import com.chanomhub.myapplication.models.Author
import com.chanomhub.myapplication.models.ArticleDetailResponse
import com.chanomhub.myapplication.screens.createHttpClient

private const val TAG = "ArticleDetailScreen"

// Placeholder and error image URLs (replace with your actual URLs or assets)
private const val PLACEHOLDER_IMAGE_URL = "https://via.placeholder.com/150" // Example placeholder
private const val ERROR_IMAGE_URL = "https://via.placeholder.com/150/FF0000/FFFFFF?text=Error" // Example error image

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ArticleDetailScreen(
    articleSlug: String,
    onBackClick: () -> Unit
) {
    var article by remember { mutableStateOf<Article?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFavorited by remember { mutableStateOf(false) }
    val httpClient = remember { createHttpClient() }

    LaunchedEffect(articleSlug) {
        try {
            Log.d(TAG, "Starting API call for article slug: $articleSlug")
            isLoading = true
            errorMessage = null

            val apiUrl = "https://api.chanomhub.online/api/articles/$articleSlug"
            val response = withContext(Dispatchers.IO) {
                httpClient.get(apiUrl) {
                    headers { append("accept", "application/json") }
                }
            }

            if (response.status.isSuccess()) {
                val articleDetailResponse = response.body<ArticleDetailResponse>()
                article = articleDetailResponse.article
            } else {
                throw Exception("HTTP Error: ${response.status.value} - ${response.status.description}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in API call", e)
            errorMessage = "เกิดข้อผิดพลาด: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { runCatching { httpClient.close() } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        article?.title ?: "รายละเอียด",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "ย้อนกลับ")
                    }
                },
                actions = {
                    AnimatedContent(
                        targetState = isFavorited,
                        transitionSpec = {
                            scaleIn(tween(200)) + fadeIn(tween(200)) with
                                    scaleOut(tween(200)) + fadeOut(tween(200))
                        }
                    ) { favorited ->
                        IconButton(onClick = { isFavorited = !isFavorited }) {
                            Icon(
                                if (favorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "ถูกใจ",
                                tint = if (favorited) Color(0xFFE91E63) else LocalContentColor.current
                            )
                        }
                    }
                    IconButton(onClick = { /* Handle share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "แชร์")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        AnimatedContent(
            targetState = isLoading to errorMessage,
            transitionSpec = { fadeIn(tween(300)) with fadeOut(tween(300)) }
        ) { (loading, error) ->
            when {
                loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "กำลังโหลดข้อมูล...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Article Slug: $articleSlug",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "❌ เกิดข้อผิดพลาด",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Article Slug: $articleSlug",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        isLoading = true
                                        errorMessage = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                ) {
                                    Text("ลองใหม่", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
                article != null -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Hero Image Section
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                                    .shadow(8.dp)
                            ) {
                                AsyncImage(
                                    model = article!!.mainImage,
                                    contentDescription = article!!.title,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset(y = (-16).dp), // Parallax effect
                                    contentScale = ContentScale.Crop,
                                    placeholder = rememberAsyncImagePainter(PLACEHOLDER_IMAGE_URL),
                                    error = rememberAsyncImagePainter(ERROR_IMAGE_URL)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                                                )
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = article!!.title,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            shadow = Shadow(
                                                color = Color.Black.copy(alpha = 0.4f),
                                                offset = Offset(2f, 2f),
                                                blurRadius = 4f
                                            )
                                        ),
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                        ) {
                                            Text(
                                                article!!.sequentialCode,
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Text(
                                            "Version ${article!!.ver}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            // Author Info Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12
                                        .dp)
                                ) {
                                    AsyncImage(
                                        model = article!!.author.image,
                                        contentDescription = article!!.author.username,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(28.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentScale = ContentScale.Crop,
                                        placeholder = rememberAsyncImagePainter(PLACEHOLDER_IMAGE_URL),
                                        error = rememberAsyncImagePainter(ERROR_IMAGE_URL)
                                    )
                                    Column {
                                        Text(
                                            article!!.author.username,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            article!!.author.bio,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            // Stats Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = "เวอร์ชั่น",
                                    value = article!!.version.toString(),
                                    icon = "📋"
                                )
                                StatItem(
                                    label = "ถูกใจ",
                                    value = article!!.favoritesCount.toString(),
                                    icon = "❤️"
                                )
                                StatItem(
                                    label = "สถานะ",
                                    value = article!!.status,
                                    icon = "📊"
                                )
                            }
                        }

                        item {
                            // Tags Section
                            if (article!!.tagList.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        "แท็ก",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(article!!.tagList) { tag ->
                                            SuggestionChip(
                                                onClick = { Log.d(TAG, "Tag clicked: $tag") },
                                                label = {
                                                    Text(
                                                        tag,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                },
                                                modifier = Modifier.animateContentSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // Technical Info Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        "ข้อมูลเทคนิค",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    TechInfoRow(
                                        label = "Engine",
                                        value = article!!.engine
                                    )
                                    TechInfoRow(
                                        label = "Platform",
                                        value = article!!.platformList.joinToString(", ")
                                    )
                                    TechInfoRow(
                                        label = "Category",
                                        value = article!!.categoryList.joinToString(", ")
                                    )
                                    TechInfoRow(
                                        label = "สถานะ",
                                        value = article!!.status
                                    )
                                }
                            }
                        }

                        item {
                            // Description Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "คำอธิบาย",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        article!!.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        item {
                            // Date Info Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        "ข้อมูลวันที่",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "วันที่เผยแพร่:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            formatDateString(article!!.createdAt),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "อัพเดตล่าสุด:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            formatDateString(article!!.updatedAt),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            // Debug Section
                            if (true) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            "🐛 Debug Info",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Article Slug: $articleSlug",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Article ID: ${article!!.id}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Sequential Code: ${article!!.sequentialCode}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Version: ${article!!.ver}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Main Image URL: ${article!!.mainImage}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Author Image URL: ${article!!.author.image}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Created: ${article!!.createdAt}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            "Updated: ${article!!.updatedAt}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: String
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TechInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun formatDateString(isoString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        Log.e(TAG, "Error formatting date: $isoString", e)
        "Invalid date"
    }
}