package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.database.WebAppEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebAppSettingsSheet(
    webApp: WebAppEntity,
    onDismiss: () -> Unit,
    onSaveSettings: (updatedWebApp: WebAppEntity) -> Unit
) {
    var jsEnabled by remember { mutableStateOf(webApp.enableJavaScript) }
    var domStorageEnabled by remember { mutableStateOf(webApp.enableDomStorage) }
    var databaseEnabled by remember { mutableStateOf(webApp.enableDatabase) }
    var fileAccessAllowed by remember { mutableStateOf(webApp.allowFileAccess) }
    var zoomEnabled by remember { mutableStateOf(webApp.enableZoom) }
    var desktopMode by remember { mutableStateOf(webApp.desktopMode) }
    var forceDarkMode by remember { mutableStateOf(webApp.forceDarkMode) }
    var allowLocation by remember { mutableStateOf(webApp.allowGeolocation) }
    var allowCamera by remember { mutableStateOf(webApp.allowCamera) }
    var allowMicrophone by remember { mutableStateOf(webApp.allowMicrophone) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("webapp_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "دسترسی‌ها و قابلیت‌های WebView",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "بستن")
                }
            }

            Text(
                text = "تنظیمات اختصاصی برای برنامه '${webApp.title}' در محیط ایزوله:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Core Web Engine Toggles
            Text(
                text = "موتور وب و ذخیره‌سازی:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingToggleItem(
                title = "فعال بودن جاواتسکریپت (JavaScript)",
                subtitle = "برای اجرای اسکریپت‌ها و انیمیشن‌های تعاملی وب",
                checked = jsEnabled,
                onCheckedChange = { jsEnabled = it }
            )

            SettingToggleItem(
                title = "ذخیره‌سازی محلی (DOM Storage / LocalStorage)",
                subtitle = "ذخیره داده‌های کاربر و وضعیت برنامه به‌صورت آفلاین",
                checked = domStorageEnabled,
                onCheckedChange = { domStorageEnabled = it }
            )

            SettingToggleItem(
                title = "پایگاه‌داده وب (IndexedDB)",
                subtitle = "دسترسی به دیتابیس‌های مرورگر وب",
                checked = databaseEnabled,
                onCheckedChange = { databaseEnabled = it }
            )

            SettingToggleItem(
                title = "دسترسی به فایل‌های محلی ایزوله",
                subtitle = "اجازه خواندن تصاویر، استایل‌ها و فونت‌های پوشه برنامه",
                checked = fileAccessAllowed,
                onCheckedChange = { fileAccessAllowed = it }
            )

            HorizontalDivider()

            // Display & UX Options
            Text(
                text = "نمایش و حالت مرورگر:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingToggleItem(
                title = "امکان بزرگ‌نمایی (Zoom)",
                subtitle = "امکان زوم کردن صفحه با دو انگشت",
                checked = zoomEnabled,
                onCheckedChange = { zoomEnabled = it }
            )

            SettingToggleItem(
                title = "حالت دسکتاپ (Desktop Mode)",
                subtitle = "ارسال User-Agent مرورگر سیستم‌عامل دسکتاپ",
                checked = desktopMode,
                onCheckedChange = { desktopMode = it }
            )

            SettingToggleItem(
                title = "حالت تاریک اجباری (Force Dark Mode)",
                subtitle = "اعمال تم تاریک بر روی صفحات وب روشن",
                checked = forceDarkMode,
                onCheckedChange = { forceDarkMode = it }
            )

            HorizontalDivider()

            // Web Permissions
            Text(
                text = "مجوزها و دسترسی‌های حسگرها:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingToggleItem(
                title = "دسترسی به موقعیت مکانی (Geolocation)",
                subtitle = "اجازه به وب‌سایت برای دریافت موقعیت جغرافیایی",
                checked = allowLocation,
                onCheckedChange = { allowLocation = it }
            )

            SettingToggleItem(
                title = "دسترسی به دوربین (Camera)",
                subtitle = "اجازه به برنامه وب برای ضبط تصویر و اسکن",
                checked = allowCamera,
                onCheckedChange = { allowCamera = it }
            )

            SettingToggleItem(
                title = "دسترسی به میکروفون (Microphone)",
                subtitle = "اجازه به برنامه وب برای ضبط صدا",
                checked = allowMicrophone,
                onCheckedChange = { allowMicrophone = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onSaveSettings(
                        webApp.copy(
                            enableJavaScript = jsEnabled,
                            enableDomStorage = domStorageEnabled,
                            enableDatabase = databaseEnabled,
                            allowFileAccess = fileAccessAllowed,
                            enableZoom = zoomEnabled,
                            desktopMode = desktopMode,
                            forceDarkMode = forceDarkMode,
                            allowGeolocation = allowLocation,
                            allowCamera = allowCamera,
                            allowMicrophone = allowMicrophone
                        )
                    )
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_settings_btn")
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ذخیره تنظیمات دسترسی",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
