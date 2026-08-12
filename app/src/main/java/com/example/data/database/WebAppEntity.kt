package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "web_apps")
data class WebAppEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val iconType: String = "EMOJI", // EMOJI, DEFAULT, IMAGE
    val iconValue: String = "🌐",
    val folderPath: String, // Path relative to internal files/webapps dir
    val entryPoint: String = "index.html",
    val category: String = "عمومی",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastOpenedAt: Long = System.currentTimeMillis(),

    // Persistent state
    val lastVisitedUrl: String = "",
    val scrollX: Int = 0,
    val scrollY: Int = 0,

    // WebView Settings & Access Permissions
    val enableJavaScript: Boolean = true,
    val enableDomStorage: Boolean = true,
    val enableDatabase: Boolean = true,
    val allowFileAccess: Boolean = true,
    val enableZoom: Boolean = true,
    val customUserAgent: String = "",
    val desktopMode: Boolean = false,
    val forceDarkMode: Boolean = false,
    val allowGeolocation: Boolean = false,
    val allowCamera: Boolean = false,
    val allowMicrophone: Boolean = false
)
