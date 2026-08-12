package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.WebAppEntity
import com.example.ui.components.AddWebAppDialog
import com.example.ui.components.EditWebAppDialog
import com.example.ui.components.WebAppCard
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingWebApp by remember { mutableStateOf<WebAppEntity?>(null) }
    var settingsWebApp by remember { mutableStateOf<WebAppEntity?>(null) }

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
                placeholder = { Text("جستجو در برنامه‌ها و پروژه...)") },
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
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(webApps, key = { it.id }) { app ->
                        WebAppCard(
                            webApp = app,
                            onLaunch = { onLaunchApp(app.id) },
                            onSettings = { settingsWebApp = app },
                            onEdit = { editingWebApp = app },
                            onDelete = { viewModel.deleteWebApp(app) },
                            onToggleFavorite = { viewModel.toggleFavorite(app) }
                        )
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
}
