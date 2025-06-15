package com.chanomhub.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class ProfileItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
fun ProfileScreen() {
    val profileItems = listOf(
        ProfileItem("แก้ไขโปรไฟล์", "อัปเดตข้อมูลส่วนตัว", Icons.Default.Edit),
        ProfileItem("การแจ้งเตือน", "ตั้งค่าการแจ้งเตือน", Icons.Default.Notifications),
        ProfileItem("ความปลอดภัย", "รหัสผ่านและความปลอดภัย", Icons.Default.Lock),
        ProfileItem("ประวัติการใช้งาน", "ดูกิจกรรมของคุณ", Icons.Default.History),
        ProfileItem("ความช่วยเหลือ", "คำถามที่พบบ่อย", Icons.Default.Help),
        ProfileItem("เกี่ยวกับเรา", "ข้อมูลแอปพลิเคชัน", Icons.Default.Info)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "👤 โปรไฟล์",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar placeholder
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ชื่อผู้ใช้",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "user@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileStatCard("123", "โพสต์")
                    ProfileStatCard("456", "ผู้ติดตาม")
                    ProfileStatCard("789", "กำลังติดตาม")
                }
            }
        }

        // Profile Options
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                profileItems.forEachIndexed { index, item ->
                    ListItem(
                        headlineContent = { Text(item.title) },
                        supportingContent = { Text(item.subtitle) },
                        leadingContent = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Next"
                            )
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (index < profileItems.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        OutlinedButton(
            onClick = { /* Handle logout */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("ออกจากระบบ")
        }
    }
}

@Composable
private fun ProfileStatCard(
    number: String,
    label: String
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}