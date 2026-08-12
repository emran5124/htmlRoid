package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.WebAppEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WebAppLauncherIcon(
    webApp: WebAppEntity,
    columnsCount: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamic size of circular icon based on column count
    val iconSize = when (columnsCount) {
        3 -> 68.dp
        4 -> 58.dp
        5 -> 48.dp
        else -> 42.dp
    }
    
    val emojiSize = when (columnsCount) {
        3 -> 32.sp
        4 -> 28.sp
        5 -> 24.sp
        else -> 20.sp
    }

    val titleFontSize = when (columnsCount) {
        3 -> 14.sp
        4 -> 12.sp
        5 -> 11.sp
        else -> 10.sp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        // Round launcher icon container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = webApp.iconValue.ifBlank { "🌐" },
                fontSize = emojiSize
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // App Label
        Text(
            text = webApp.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = titleFontSize,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
        )
    }
}
