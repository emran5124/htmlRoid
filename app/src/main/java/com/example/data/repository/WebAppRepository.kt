package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.database.WebAppDao
import com.example.data.database.WebAppEntity
import com.example.data.sample.SampleWebApps
import com.example.util.FileImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class WebAppRepository(
    private val context: Context,
    private val webAppDao: WebAppDao
) {
    val allWebApps: Flow<List<WebAppEntity>> = webAppDao.getAllWebApps()

    fun getWebAppByIdFlow(id: String): Flow<WebAppEntity?> = webAppDao.getWebAppByIdFlow(id)

    suspend fun getWebAppById(id: String): WebAppEntity? = webAppDao.getWebAppById(id)

    suspend fun insertWebApp(webApp: WebAppEntity) = webAppDao.insertWebApp(webApp)

    suspend fun updateWebApp(webApp: WebAppEntity) = webAppDao.updateWebApp(webApp)

    suspend fun deleteWebApp(id: String, folderPath: String) = withContext(Dispatchers.IO) {
        FileImporter.deleteAppDir(context, folderPath)
        webAppDao.deleteWebAppById(id)
    }

    suspend fun updateState(id: String, url: String, scrollX: Int, scrollY: Int) {
        webAppDao.updateState(id, url, scrollX, scrollY)
    }

    suspend fun updateFavorite(id: String, isFav: Boolean) {
        webAppDao.updateFavorite(id, isFav)
    }

    suspend fun incrementLaunchCount(id: String) {
        webAppDao.incrementLaunchCount(id)
    }

    suspend fun checkAndCreateDefaultSamples() = withContext(Dispatchers.IO) {
        if (webAppDao.getCount() == 0) {
            val defaultSamples = SampleWebApps.createDefaultSamples(context)
            defaultSamples.forEach { sample ->
                webAppDao.insertWebApp(sample)
            }
        }
    }

    suspend fun importSingleHtml(
        title: String,
        uri: Uri,
        emojiIcon: String,
        category: String
    ): WebAppEntity = withContext(Dispatchers.IO) {
        val id = "app_" + UUID.randomUUID().toString().take(8)
        val appDir = FileImporter.getAppDir(context, id)
        val entryPoint = FileImporter.copySingleFile(context, uri, appDir)

        val newApp = WebAppEntity(
            id = id,
            title = if (title.isBlank()) "پروژه وب تک فایلی" else title,
            description = "فایل HTML وارد شده به صورت محلی و آفلاین",
            iconType = "EMOJI",
            iconValue = if (emojiIcon.isBlank()) "🌐" else emojiIcon,
            folderPath = id,
            entryPoint = entryPoint,
            category = if (category.isBlank()) "عمومی" else category,
            createdAt = System.currentTimeMillis(),
            lastOpenedAt = System.currentTimeMillis()
        )
        webAppDao.insertWebApp(newApp)
        newApp
    }

    suspend fun importFolderTree(
        title: String,
        treeUri: Uri,
        emojiIcon: String,
        category: String
    ): WebAppEntity = withContext(Dispatchers.IO) {
        val id = "app_" + UUID.randomUUID().toString().take(8)
        val appDir = FileImporter.getAppDir(context, id)
        FileImporter.copyFolderFromUri(context, treeUri, appDir)

        val candidateHtmls = FileImporter.findHtmlFiles(appDir)
        val entryPoint = candidateHtmls.firstOrNull { it.contains("index.html", ignoreCase = true) }
            ?: candidateHtmls.firstOrNull()
            ?: "index.html"

        val newApp = WebAppEntity(
            id = id,
            title = if (title.isBlank()) "پروژه پوشه‌ای HTML" else title,
            description = "پروژه کامل وب با منابع HTML/CSS/JS مستقل",
            iconType = "EMOJI",
            iconValue = if (emojiIcon.isBlank()) "📁" else emojiIcon,
            folderPath = id,
            entryPoint = entryPoint,
            category = if (category.isBlank()) "پروژه‌ها" else category,
            createdAt = System.currentTimeMillis(),
            lastOpenedAt = System.currentTimeMillis()
        )
        webAppDao.insertWebApp(newApp)
        newApp
    }

    suspend fun importZipFile(
        title: String,
        zipUri: Uri,
        emojiIcon: String,
        category: String
    ): WebAppEntity = withContext(Dispatchers.IO) {
        val id = "app_" + UUID.randomUUID().toString().take(8)
        val appDir = FileImporter.getAppDir(context, id)
        FileImporter.unzipFile(context, zipUri, appDir)

        val candidateHtmls = FileImporter.findHtmlFiles(appDir)
        val entryPoint = candidateHtmls.firstOrNull { it.contains("index.html", ignoreCase = true) }
            ?: candidateHtmls.firstOrNull()
            ?: "index.html"

        val newApp = WebAppEntity(
            id = id,
            title = if (title.isBlank()) "پروژه آرشیو زیپ" else title,
            description = "استخراج شده از فایل آرشیو ZIP",
            iconType = "EMOJI",
            iconValue = if (emojiIcon.isBlank()) "📦" else emojiIcon,
            folderPath = id,
            entryPoint = entryPoint,
            category = if (category.isBlank()) "آرشیو" else category,
            createdAt = System.currentTimeMillis(),
            lastOpenedAt = System.currentTimeMillis()
        )
        webAppDao.insertWebApp(newApp)
        newApp
    }

    suspend fun exportBackupJson(apps: List<WebAppEntity>): String = withContext(Dispatchers.Default) {
        val jsonArray = JSONArray()
        apps.forEach { app ->
            val obj = JSONObject()
            obj.put("id", app.id)
            obj.put("title", app.title)
            obj.put("description", app.description)
            obj.put("iconType", app.iconType)
            obj.put("iconValue", app.iconValue)
            obj.put("folderPath", app.folderPath)
            obj.put("entryPoint", app.entryPoint)
            obj.put("category", app.category)
            obj.put("isFavorite", app.isFavorite)
            obj.put("enableJavaScript", app.enableJavaScript)
            obj.put("enableDomStorage", app.enableDomStorage)
            obj.put("enableDatabase", app.enableDatabase)
            obj.put("allowFileAccess", app.allowFileAccess)
            obj.put("enableZoom", app.enableZoom)
            obj.put("customUserAgent", app.customUserAgent)
            obj.put("desktopMode", app.desktopMode)
            obj.put("forceDarkMode", app.forceDarkMode)
            obj.put("allowGeolocation", app.allowGeolocation)
            obj.put("allowCamera", app.allowCamera)
            obj.put("allowMicrophone", app.allowMicrophone)
            jsonArray.put(obj)
        }
        jsonArray.toString(2)
    }
}
