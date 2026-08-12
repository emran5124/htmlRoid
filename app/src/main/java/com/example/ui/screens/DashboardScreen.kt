package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.WebAppEntity
import com.example.ui.components.AddWebAppDialog
import com.example.ui.components.EditWebAppDialog
import com.example.ui.components.WebAppCard
import com.example.ui.components.WebAppLauncherIcon
import com.example.ui.components.WebAppSettingsSheet
import com.example.ui.viewmodel.WebAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WebAppViewModel,
    onLaunchApp: (appId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val webApps by viewModel.filteredWebApps.collectAsState()
    val suggestedApps by viewModel.suggestedWebApps.collectAsState()
    val gridColumns by viewModel.gridColumns.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingWebApp by remember { mutableStateOf<WebAppEntity?>(null) }
    var settingsWebApp by remember { mutableStateOf<WebAppEntity?>(null) }
    var activeOptionsApp by remember { mutableStateOf<WebAppEntity?>(null) }

    val categories = listOf("همه", "محبوب‌ها", "ابزار", "بازی", "کاربردی", "پروژه‌ها")

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.testTag("dashboard_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "اجراکننده وب ایزوله",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "محیط آفلاین و بدون مصرف پس‌زمینه",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "افزودن برنامه") },
                text = { Text("افزودن برنامه وب", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_app")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("جستجو در برنامه‌ها...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "پاک‌کردن")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_field")
            )

            // Categories Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { viewModel.selectedCategory.value = cat },
                        label = { Text(cat, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) },
                        leadingIcon = if (cat == "محبوب‌ها") {
                            { Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFB800)) }
                        } else null,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (isImporting) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Installed Web Apps List
            if (webApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("📁", fontSize = 56.sp)
                        Text(
                            text = "هیچ برنامه‌ای یافت نشد",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "با زدن دکمه پایین، پوشه یا فایل HTML پروژه جدیدی اضافه کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // 1. Suggestions Row (busiest apps)
                    if (suggestedApps.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                Text(
                                    text = "پیشنهادها (پر استفاده‌ترین)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(suggestedApps, key = { "suggested_" + it.id }) { app ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(64.dp)
                                                .clickable { onLaunchApp(app.id) }
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                            ) {
                                                Text(
                                                    text = app.iconValue.ifBlank { "🌐" },
                                                    fontSize = 26.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = app.title,
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${app.launchCount} بار اجرا",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                            }
                        }
                    }

                    // 2. Layout Configuration Row
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "تعداد آیکون‌ها در ردیف:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(3, 4, 5).forEach { cols ->
                                    val isSelected = gridColumns == cols
                                    IconButton(
                                        onClick = { viewModel.setGridColumns(cols) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                    ) {
                                        Text(
                                            text = cols.toString(),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                    }

                    // 3. Grid of all apps
                    val chunkedApps = webApps.chunked(gridColumns)
                    items(chunkedApps) { rowApps ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            for (app in rowApps) {
                                WebAppLauncherIcon(
                                    webApp = app,
                                    columnsCount = gridColumns,
                                    onClick = { onLaunchApp(app.id) },
                                    onLongClick = { activeOptionsApp = app },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining spaces in the row so everything aligns beautifully!
                            val emptySlots = gridColumns - rowApps.size
                            if (emptySlots > 0) {
                                Spacer(modifier = Modifier.weight(emptySlots.toFloat()))
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddWebAppDialog(
            onDismiss = { showAddDialog = false },
            onImportSingleHtml = { title, uri, icon, cat ->
                viewModel.importSingleHtml(title, uri, icon, cat)
            },
            onImportFolder = { title, treeUri, icon, cat ->
                viewModel.importFolderTree(title, treeUri, icon, cat)
            },
            onImportZip = { title, zipUri, icon, cat ->
                viewModel.importZipFile(title, zipUri, icon, cat)
            }
        )
    }

    editingWebApp?.let { app ->
        EditWebAppDialog(
            webApp = app,
            onDismiss = { editingWebApp = null },
            onSave = { updated ->
                viewModel.updateWebApp(updated)
                editingWebApp = null
            }
        )
    }

    settingsWebApp?.let { app ->
        WebAppSettingsSheet(
            webApp = app,
            onDismiss = { settingsWebApp = null },
            onSaveSettings = { updated ->
                viewModel.updateWebApp(updated)
                settingsWebApp = null
            }
        )
    }

    // Long Press Options Bottom Sheet
    activeOptionsApp?.let { app ->
        ModalBottomSheet(
            onDismissRequest = { activeOptionsApp = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header of Bottom Sheet
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(text = app.iconValue.ifBlank { "🌐" }, fontSize = 28.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${app.category} • ${app.launchCount} بار اجرا شده",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                // Actions List
                ListItem(
                    headlineContent = { Text("اجرای برنامه", fontWeight = FontWeight.SemiBold) },
                    leadingContent = { Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.clickable {
                        activeOptionsApp = null
                        onLaunchApp(app.id)
                    }
                )

                ListItem(
                    headlineContent = { Text(if (app.isFavorite) "حذف از علاقه‌مندی‌ها" else "افزودن به علاقه‌مندی‌ها") },
                    leadingContent = { 
                        Icon(
                            imageVector = if (app.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder, 
                            contentDescription = null, 
                            tint = Color(0xFFFFB800)
                        ) 
                    },
                    modifier = Modifier.clickable {
                        viewModel.toggleFavorite(app)
                        activeOptionsApp = null
                    }
                )

                ListItem(
                    headlineContent = { Text("دسترسی‌ها و تنظیمات وب") },
                    leadingContent = { Icon(Icons.Filled.Tune, contentDescription = null) },
                    modifier = Modifier.clickable {
                        settingsWebApp = app
                        activeOptionsApp = null
                    }
                )

                ListItem(
                    headlineContent = { Text("ویرایش اطلاعات") },
                    leadingContent = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    modifier = Modifier.clickable {
                        editingWebApp = app
                        activeOptionsApp = null
                    }
                )

                ListItem(
                    headlineContent = { Text("حذف برنامه", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) },
                    leadingContent = { Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        viewModel.deleteWebApp(app)
                        activeOptionsApp = null
                    }
                )
            }
        }
    }
}
