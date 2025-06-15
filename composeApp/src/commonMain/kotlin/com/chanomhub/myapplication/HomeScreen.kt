package com.chanomhub.myapplication.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ch_multiplatform.composeapp.generated.resources.Res
import ch_multiplatform.composeapp.generated.resources.compose_multiplatform
import com.chanomhub.myapplication.Greeting

@Composable
fun HomeScreen() {
    var showContent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏠 หน้าแรก",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { showContent = !showContent },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(if (showContent) "ซ่อน" else "แสดงเนื้อหา")
                }

                AnimatedVisibility(showContent) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Image(
                            painter = painterResource(Res.drawable.compose_multiplatform),
                            contentDescription = "Compose Multiplatform Logo",
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val greeting = remember { Greeting().greet() }
                        Text(
                            text = "Compose: $greeting",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // เพิ่มข้อมูลอื่นๆ ที่ต้องการในหน้าแรก
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📊 สถิติวันนี้",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("• ผู้ใช้งาน: 1,234 คน")
                Text("• เนื้อหาใหม่: 56 รายการ")
                Text("• อัปเดตล่าสุด: วันนี้")
            }
        }
    }
}