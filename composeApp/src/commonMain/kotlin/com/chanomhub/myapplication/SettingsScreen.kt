package com.chanomhub.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class SettingItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val type: SettingType = SettingType.NAVIGATION,
    val isEnabled: Boolean = true,
    val onClick: () -> Unit = {}
)

enum class SettingType {
    NAVIGATION,
    SWITCH,
    DROPDOWN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var isDarkMode by remember { mutableStateOf(false) }
    var isNotificationEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("ไทย") }
    var isExpanded by remember { mutableStateOf(false) }

    val languages = listOf("ไทย", "English", "日本語", "中文")

    val settingGroups = listOf(
        "การแสดงผล" to listOf(
            SettingItem(
                "โหมดกลางคืน",
                "เปลี่ยนธีมเป็นโทนสีเข้ม",
                Icons.Default.DarkMode,
                SettingType.SWITCH
            ),
            SettingItem(
                "ภาษา",
                "เลือกภาษาที่ใช้แสดงผล",
                Icons.Default.Language,
                SettingType.DROPDOWN
            ),
            SettingItem(
                "ขนาดตัวอักษร",
                "ปรับขนาดตัวอักษรในแอป",
                Icons.Default.FormatSize
            )
        ),
        "การแจ้งเตือน" to listOf(
            SettingItem(
                "การแจ้งเตือน",
                "เปิด/ปิดการแจ้งเตือน",
                Icons.Default.Notifications,
                SettingType.SWITCH
            ),
            SettingItem(
                "เสียงแจ้งเตือน",
                "ตั้งค่าเสียงการแจ้งเตือน",
                Icons.Default.VolumeUp
            ),
            SettingItem(
                "การสั่น",
                "เปิด/ปิดการสั่นเมื่อมีแจ้งเตือน",
                Icons.Default.Vibration,
                SettingType.SWITCH
            )
        ),
        "ความปลอดภัย" to listOf(
            SettingItem(
                "เปลี่ยนรหัสผ่าน",
                "อัปเดตรหัสผ่านของคุณ",
                Icons.Default.Lock
            ),
            SettingItem(
                "การตรวจสอบตัวตน",
                "ตั้งค่าการยืนยันตัวตน",
                Icons.Default.Fingerprint
            ),
            SettingItem(
                "การเข้าถึงข้อมูล",
                "จัดการสิทธิ์การเข้าถึง",
                Icons.Default.Security
            )
        ),
        "เกี่ยวกับ" to listOf(
            SettingItem(
                "เงื่อนไขการใช้งาน",
                "อ่านเงื่อนไขและข้อตกลง",
                Icons.Default.Description
            ),
            SettingItem(
                "นโยบายความเป็นส่วนตัว",
                "ข้อมูลเกี่ยวกับความเป็นส่วนตัว",
                Icons.Default.PrivacyTip
            ),
            SettingItem(
                "เวอร์ชัน",
                "v1.0.0 (Build 001)",
                Icons.Default.Info
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "⚙️ ตั้งค่า",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        settingGroups.forEach { (groupTitle, items) ->
            // Group Title
            Text(
                text = groupTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
            )

            // Group Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column {
                    items.forEachIndexed { index, item ->
                        when (item.type) {
                            SettingType.SWITCH -> {
                                val isChecked = when (item.title) {
                                    "โหมดกลางคืน" -> isDarkMode
                                    "การแจ้งเตือน" -> isNotificationEnabled
                                    else -> false
                                }

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
                                        Switch(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                when (item.title) {
                                                    "โหมดกลางคืน" -> isDarkMode = checked
                                                    "การแจ้งเตือน" -> isNotificationEnabled = checked
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            SettingType.DROPDOWN -> {
                                ListItem(
                                    headlineContent = { Text(item.title) },
                                    supportingContent = { Text(selectedLanguage) },
                                    leadingContent = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title
                                        )
                                    },
                                    trailingContent = {
                                        ExposedDropdownMenuBox(
                                            expanded = isExpanded,
                                            onExpandedChange = { isExpanded = !isExpanded }
                                        ) {
                                            IconButton(
                                                onClick = { isExpanded = true },
                                                modifier = Modifier.menuAnchor()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDropDown,
                                                    contentDescription = "Dropdown"
                                                )
                                            }
                                            ExposedDropdownMenu(
                                                expanded = isExpanded,
                                                onDismissRequest = { isExpanded = false }
                                            ) {
                                                languages.forEach { language ->
                                                    DropdownMenuItem(
                                                        text = { Text(language) },
                                                        onClick = {
                                                            selectedLanguage = language
                                                            isExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                            SettingType.NAVIGATION -> {
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
                            }
                        }

                        if (index < items.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chanomhub App",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Made with ❤️ using Kotlin Multiplatform",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}