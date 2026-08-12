package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.database.WebAppEntity
import com.example.util.FileImporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWebAppDialog(
    webApp: WebAppEntity,
    onDismiss: () -> Unit,
    onSave: (updatedWebApp: WebAppEntity) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(webApp.title) }
    var description by remember { mutableStateOf(webApp.description) }
    var emojiIcon by remember { mutableStateOf(webApp.iconValue.ifBlank { "🌐" }) }
    var category by remember { mutableStateOf(webApp.category) }
    var selectedEntryPoint by remember { mutableStateOf(webApp.entryPoint) }

    // Scan for available HTML files in app's internal folder
    val candidateHtmlFiles by remember {
        mutableStateOf(
            try {
                val appDir = FileImporter.getAppDir(context, webApp.folderPath)
                FileImporter.findHtmlFiles(appDir)
            } catch (e: Exception) {
                listOf(webApp.entryPoint)
            }
        )
    }

    val presetEmojis = listOf("🌐", "📁", "🧮", "📝", "🐍", "📋", "⏱️", "🎮", "📊", "⚙️", "🚀", "💡")
    val presetCategories = listOf("عمومی", "بازی", "ابزار", "کاربردی", "آموزشی", "پروژه‌ها")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_webapp_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ویرایش مشخصات برنامه",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "بستن")
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان برنامه") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیحات کوتاه") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Entry Point Selector
                if (candidateHtmlFiles.size > 1) {
                    Text(
                        text = "فایل شروع اصلی (Entry Point):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(candidateHtmlFiles) { htmlPath ->
                            FilterChip(
                                selected = selectedEntryPoint == htmlPath,
                                onClick = { selectedEntryPoint = htmlPath },
                                label = { Text(htmlPath) }
                            )
                        }
                    }
                }

                // Emoji Icon Selector
                Text(
                    text = "آیکون برنامه:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emojiIcon, fontSize = 28.sp)
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(presetEmojis) { em ->
                            FilterChip(
                                selected = emojiIcon == em,
                                onClick = { emojiIcon = em },
                                label = { Text(em, fontSize = 18.sp) }
                            )
                        }
                    }
                }

                // Category Selector
                Text(
                    text = "دسته‌بندی:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetCategories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSave(
                            webApp.copy(
                                title = title.ifBlank { webApp.title },
                                description = description,
                                iconValue = emojiIcon,
                                category = category,
                                entryPoint = selectedEntryPoint
                            )
                        )
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ذخیره تغییرات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
