package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConsoleLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsoleLogsBottomSheet(
    logs: List<ConsoleLog>,
    onClearLogs: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf<ConsoleLog.LogLevel?>(null) }

    val filteredLogs = remember(logs, selectedFilter) {
        if (selectedFilter == null) logs else logs.filter { it.level == selectedFilter }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("console_logs_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .heightIn(max = 500.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageVector = Icons.Filled.Terminal,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "کنسول توسعه‌دهنده (Web Console Log)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onClearLogs) {
                        Icon(Icons.Filled.ClearAll, contentDescription = "پاکسازی کنسول")
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "بستن")
                    }
                }
            }

            // Log Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("همه (${logs.size})") }
                )
                FilterChip(
                    selected = selectedFilter == ConsoleLog.LogLevel.ERROR,
                    onClick = { selectedFilter = ConsoleLog.LogLevel.ERROR },
                    label = { Text("خطاها") }
                )
                FilterChip(
                    selected = selectedFilter == ConsoleLog.LogLevel.WARNING,
                    onClick = { selectedFilter = ConsoleLog.LogLevel.WARNING },
                    label = { Text("هشدارها") }
                )
                FilterChip(
                    selected = selectedFilter == ConsoleLog.LogLevel.LOG,
                    onClick = { selectedFilter = ConsoleLog.LogLevel.LOG },
                    label = { Text("لاگ‌ها") }
                )
            }

            HorizontalDivider()

            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ پیامی در کنسول ثبت نشده است",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredLogs) { log ->
                        val (bgColor, textColor, badgeText) = when (log.level) {
                            ConsoleLog.LogLevel.ERROR -> Triple(Color(0x22EF4444), Color(0xFFEF4444), "ERROR")
                            ConsoleLog.LogLevel.WARNING -> Triple(Color(0x22F59E0B), Color(0xFFF59E0B), "WARN")
                            else -> Triple(Color(0x2238BDF8), Color(0xFF38BDF8), "LOG")
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        color = textColor,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    if (log.sourceId.isNotBlank()) {
                                        Text(
                                            text = "${log.sourceId}:${log.lineNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = log.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
