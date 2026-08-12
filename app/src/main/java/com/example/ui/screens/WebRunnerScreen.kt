package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.database.WebAppEntity
import com.example.data.model.ConsoleLog
import com.example.ui.components.ConsoleLogsBottomSheet
import com.example.ui.components.WebAppSettingsSheet
import com.example.ui.viewmodel.WebAppViewModel
import com.example.util.FileImporter
import com.example.util.IsolatedAssetLoader
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebRunnerScreen(
    appId: String,
    viewModel: WebAppViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webAppFlow = remember(appId) { viewModel.getWebAppByIdFlow(appId) }
    val webApp by webAppFlow.collectAsState(initial = null)
    val consoleLogs by viewModel.activeConsoleLogs.collectAsState()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf("") }
    var pageProgress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    var showConsoleSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val currentWebApp = webApp ?: return

    val initialUrl = remember(currentWebApp) {
        val baseUrl = IsolatedAssetLoader.getAppBaseUrl(currentWebApp.id)
        if (currentWebApp.lastVisitedUrl.isNotBlank() && currentWebApp.lastVisitedUrl.startsWith(baseUrl)) {
            currentWebApp.lastVisitedUrl
        } else {
            "$baseUrl${currentWebApp.entryPoint}"
        }
    }

    // Handle Hardware Back Button inside WebView
    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onBack()
        }
    }

    // Cleanup WebView on Dispose (Zero background CPU/RAM consumption!)
    DisposableEffect(appId) {
        onDispose {
            webViewInstance?.let { wv ->
                wv.stopLoading()
                wv.onPause()
                wv.removeAllViews()
                wv.destroy()
            }
            webViewInstance = null
            viewModel.clearConsoleLogs()
        }
    }

    Scaffold(
        modifier = modifier.testTag("webrunner_screen"),
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentWebApp.iconValue.ifBlank { "🌐" },
                                    fontSize = 18.sp
                                )
                            }
                            Column {
                                Text(
                                    text = if (pageTitle.isNotBlank()) pageTitle else currentWebApp.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "محیط ایزوله آفلاین",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (webViewInstance?.canGoBack() == true) {
                                webViewInstance?.goBack()
                            } else {
                                onBack()
                            }
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "بازگشت")
                        }
                    },
                    actions = {
                        // Console Badge Button
                        BadgedBox(
                            badge = {
                                if (consoleLogs.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = Color.White
                                    ) {
                                        Text("${consoleLogs.size}")
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = { showConsoleSheet = true }) {
                                Icon(Icons.Filled.Terminal, contentDescription = "کنسول توسعه‌دهنده")
                            }
                        }

                        // Reload Button
                        IconButton(onClick = { webViewInstance?.reload() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "بارگذاری مجدد")
                        }

                        // Home Button
                        IconButton(onClick = {
                            val homeUrl = "${IsolatedAssetLoader.getAppBaseUrl(currentWebApp.id)}${currentWebApp.entryPoint}"
                            webViewInstance?.loadUrl(homeUrl)
                        }) {
                            Icon(Icons.Filled.Home, contentDescription = "صفحه اصلی")
                        }

                        // Settings Button
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Filled.Tune, contentDescription = "تنظیمات دسترسی")
                        }

                        // Fullscreen Toggle Button
                        IconButton(onClick = { isFullscreen = !isFullscreen }) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                contentDescription = "تمام صفحه"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullscreen) PaddingValues(0.dp) else innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isLoading && pageProgress in 1..99) {
                    LinearProgressIndicator(
                        progress = { pageProgress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            // Isolated WebView Settings
                            settings.apply {
                                javaScriptEnabled = currentWebApp.enableJavaScript
                                domStorageEnabled = currentWebApp.enableDomStorage
                                databaseEnabled = currentWebApp.enableDatabase
                                allowFileAccess = currentWebApp.allowFileAccess
                                setSupportZoom(currentWebApp.enableZoom)
                                builtInZoomControls = currentWebApp.enableZoom
                                displayZoomControls = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                mediaPlaybackRequiresUserGesture = false

                                if (currentWebApp.desktopMode) {
                                    userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                }
                            }

                            val appDir = FileImporter.getAppDir(context, currentWebApp.folderPath)

                            // Intercept requests for isolated offline local file serving
                            webViewClient = object : WebViewClient() {
                                override fun shouldInterceptRequest(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): WebResourceResponse? {
                                    if (request != null) {
                                        val intercepted = IsolatedAssetLoader.handleInterceptRequest(
                                            context,
                                            request,
                                            currentWebApp.id,
                                            appDir
                                        )
                                        if (intercepted != null) return intercepted
                                    }
                                    return super.shouldInterceptRequest(view, request)
                                }

                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                    super.onPageStarted(view, url, favicon)
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                    view?.title?.let { pageTitle = it }
                                    if (url != null) {
                                        viewModel.saveState(currentWebApp.id, url, view?.scrollX ?: 0, view?.scrollY ?: 0)
                                    }
                                    super.onPageFinished(view, url)
                                }
                            }

                            // Console Log & Web Chrome Client
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    pageProgress = newProgress
                                    super.onProgressChanged(view, newProgress)
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (!title.isNullOrBlank()) pageTitle = title
                                    super.onReceivedTitle(view, title)
                                }

                                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                    consoleMessage?.let { msg ->
                                        val level = when (msg.messageLevel()) {
                                            ConsoleMessage.MessageLevel.ERROR -> ConsoleLog.LogLevel.ERROR
                                            ConsoleMessage.MessageLevel.WARNING -> ConsoleLog.LogLevel.WARNING
                                            ConsoleMessage.MessageLevel.TIP -> ConsoleLog.LogLevel.TIP
                                            ConsoleMessage.MessageLevel.DEBUG -> ConsoleLog.LogLevel.DEBUG
                                            else -> ConsoleLog.LogLevel.LOG
                                        }
                                        viewModel.addConsoleLog(
                                            ConsoleLog(
                                                level = level,
                                                message = msg.message(),
                                                sourceId = msg.sourceId().substringAfterLast('/'),
                                                lineNumber = msg.lineNumber()
                                            )
                                        )
                                    }
                                    return super.onConsoleMessage(consoleMessage)
                                }

                                override fun onPermissionRequest(request: PermissionRequest?) {
                                    request?.let { req ->
                                        val requestedResources = req.resources
                                        val allowedList = mutableListOf<String>()

                                        if (currentWebApp.allowCamera && requestedResources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                                            allowedList.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                                        }
                                        if (currentWebApp.allowMicrophone && requestedResources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                                            allowedList.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                                        }

                                        if (allowedList.isNotEmpty()) {
                                            req.grant(allowedList.toTypedArray())
                                        } else {
                                            req.deny()
                                        }
                                    }
                                }

                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: GeolocationPermissions.Callback?
                                ) {
                                    callback?.invoke(origin, currentWebApp.allowGeolocation, false)
                                }
                            }

                            loadUrl(initialUrl)
                            webViewInstance = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Floating exit fullscreen button
            if (isFullscreen) {
                SmallFloatingActionButton(
                    onClick = { isFullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    Icon(Icons.Filled.FullscreenExit, contentDescription = "خروج از تمام صفحه")
                }
            }
        }
    }

    // Sheets
    if (showConsoleSheet) {
        ConsoleLogsBottomSheet(
            logs = consoleLogs,
            onClearLogs = { viewModel.clearConsoleLogs() },
            onDismiss = { showConsoleSheet = false }
        )
    }

    if (showSettingsSheet) {
        WebAppSettingsSheet(
            webApp = currentWebApp,
            onDismiss = { showSettingsSheet = false },
            onSaveSettings = { updated ->
                viewModel.updateWebApp(updated)
                showSettingsSheet = false
            }
        )
    }
}
