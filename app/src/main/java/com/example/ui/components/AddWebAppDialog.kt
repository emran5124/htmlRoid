package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

enum class ImportMode {
    SINGLE_FILE, FOLDER, ZIP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWebAppDialog(
    onDismiss: () -> Unit,
    onImportSingleHtml: (title: String, uri: Uri, emojiIcon: String, category: String) -> Unit,
    onImportFolder: (title: String, treeUri: Uri, emojiIcon: String, category: String) -> Unit,
    onImportZip: (title: String, zipUri: Uri, emojiIcon: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var emojiIcon by remember { mutableStateOf("🌐") }
    var category by remember { mutableStateOf("عمومی") }
    var importMode by remember { mutableStateOf(ImportMode.FOLDER) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val presetEmojis = listOf("🌐", "📁", "🧮", "📝", "🐍", "📋", "⏱️", "🎮", "📊", "⚙️", "🚀", "💡")
    val presetCategories = listOf("عمومی", "بازی", "ابزار", "کاربردی", "آموزشی", "پروژه‌ها")

    // File pickers
    val singleHtmlPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            selectedUri = it
            selectedFileName = "فایل HTML انتخاب شد"
        }
    }

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            selectedUri = it
            selectedFileName = "پوشه پروژه انتخاب شد"
        }
    }

    val zipPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            selectedUri = it
            selectedFileName = "آرشیو ZIP انتخاب شد"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_webapp_dialog")
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
                        text = "افزودن برنامه وب جدید",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "بستن")
                    }
                }

                // Import Mode Selector
                Text(
                    text = "نوع ورود پروژه:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = importMode == ImportMode.FOLDER,
                        onClick = {
                            importMode = ImportMode.FOLDER
                            selectedUri = null
                            selectedFileName = null
                        },
                        label = { Text("انتخاب پوشه") },
                        leadingIcon = { Icon(Icons.Filled.Folder, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = importMode == ImportMode.SINGLE_FILE,
                        onClick = {
                            importMode = ImportMode.SINGLE_FILE
                            selectedUri = null
                            selectedFileName = null
                        },
                        label = { Text("تک فایل HTML") },
                        leadingIcon = { Icon(Icons.Filled.Code, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = importMode == ImportMode.ZIP,
                        onClick = {
                            importMode = ImportMode.ZIP
                            selectedUri = null
                            selectedFileName = null
                        },
                        label = { Text("فایل ZIP") },
                        leadingIcon = { Icon(Icons.Filled.Archive, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // File/Folder Select Button
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (importMode) {
                                ImportMode.FOLDER -> folderPicker.launch(null)
                                ImportMode.SINGLE_FILE -> singleHtmlPicker.launch(arrayOf("text/html", "text/plain"))
                                ImportMode.ZIP -> zipPicker.launch(arrayOf("application/zip", "application/x-zip-compressed"))
                            }
                        }
                        .testTag("select_source_btn"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (importMode) {
                                    ImportMode.FOLDER -> Icons.Filled.FolderOpen
                                    ImportMode.SINGLE_FILE -> Icons.Filled.InsertDriveFile
                                    ImportMode.ZIP -> Icons.Filled.FolderZip
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedFileName ?: when (importMode) {
                                    ImportMode.FOLDER -> "برای انتخاب پوشه پروژه کلیک کنید"
                                    ImportMode.SINGLE_FILE -> "برای انتخاب فایل HTML کلیک کنید"
                                    ImportMode.ZIP -> "برای انتخاب فایل زیپ کلیک کنید"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedUri != null) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (selectedUri == null) {
                                Text(
                                    text = "منابع و فایل‌ها به حافظه ایزوله برنامه منتقل می‌شوند",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان برنامه (اختیاری)") },
                    placeholder = { Text("مثلاً: ماشین حساب آنلاین") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("app_title_input")
                )

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

                // Submit Button
                Button(
                    onClick = {
                        val uri = selectedUri ?: return@Button
                        when (importMode) {
                            ImportMode.SINGLE_FILE -> onImportSingleHtml(title, uri, emojiIcon, category)
                            ImportMode.FOLDER -> onImportFolder(title, uri, emojiIcon, category)
                            ImportMode.ZIP -> onImportZip(title, uri, emojiIcon, category)
                        }
                        onDismiss()
                    },
                    enabled = selectedUri != null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_import_btn")
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "افزودن و ذخیره برنامه",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
