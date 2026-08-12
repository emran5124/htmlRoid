package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.WebAppEntity
import com.example.data.model.ConsoleLog
import com.example.data.repository.WebAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WebAppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WebAppRepository
    private val prefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    val gridColumns = MutableStateFlow(prefs.getInt("grid_columns", 4))

    fun setGridColumns(cols: Int) {
        gridColumns.value = cols
        prefs.edit().putInt("grid_columns", cols).apply()
    }

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("همه")
    val isImporting = MutableStateFlow(false)
    val userMessage = MutableStateFlow<String?>(null)

    val activeConsoleLogs = MutableStateFlow<List<ConsoleLog>>(emptyList())

    val allWebApps: StateFlow<List<WebAppEntity>>

    val filteredWebApps: StateFlow<List<WebAppEntity>>

    val suggestedWebApps: StateFlow<List<WebAppEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = WebAppRepository(application, database.webAppDao())

        allWebApps = repository.allWebApps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        suggestedWebApps = allWebApps.map { apps ->
            apps.filter { it.launchCount > 0 }
                .sortedByDescending { it.launchCount }
                .take(6)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        filteredWebApps = combine(allWebApps, searchQuery, selectedCategory) { apps, query, category ->
            apps.filter { app ->
                val matchesQuery = query.isBlank() ||
                        app.title.contains(query, ignoreCase = true) ||
                        app.description.contains(query, ignoreCase = true)

                val matchesCategory = when (category) {
                    "همه" -> true
                    "محبوب‌ها" -> app.isFavorite
                    else -> app.category == category
                }

                matchesQuery && matchesCategory
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.checkAndCreateDefaultSamples()
        }
    }

    fun importSingleHtml(title: String, uri: Uri, emojiIcon: String, category: String) {
        viewModelScope.launch {
            isImporting.value = true
            try {
                val app = repository.importSingleHtml(title, uri, emojiIcon, category)
                userMessage.value = "برنامه '${app.title}' با موفقیت افزوده‌شد"
            } catch (e: Exception) {
                userMessage.value = "خطا در افزودن فایل: ${e.localizedMessage}"
            } finally {
                isImporting.value = false
            }
        }
    }

    fun importFolderTree(title: String, treeUri: Uri, emojiIcon: String, category: String) {
        viewModelScope.launch {
            isImporting.value = true
            try {
                val app = repository.importFolderTree(title, treeUri, emojiIcon, category)
                userMessage.value = "پروژه '${app.title}' با موفقیت وارد شد"
            } catch (e: Exception) {
                userMessage.value = "خطا در وارد کردن پوشه: ${e.localizedMessage}"
            } finally {
                isImporting.value = false
            }
        }
    }

    fun importZipFile(title: String, zipUri: Uri, emojiIcon: String, category: String) {
        viewModelScope.launch {
            isImporting.value = true
            try {
                val app = repository.importZipFile(title, zipUri, emojiIcon, category)
                userMessage.value = "آرشیو '${app.title}' با موفقیت استخراج شد"
            } catch (e: Exception) {
                userMessage.value = "خطا در استخراج آرشیو زیپ: ${e.localizedMessage}"
            } finally {
                isImporting.value = false
            }
        }
    }

    fun toggleFavorite(app: WebAppEntity) {
        viewModelScope.launch {
            repository.updateFavorite(app.id, !app.isFavorite)
        }
    }

    fun incrementLaunchCount(appId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.incrementLaunchCount(appId)
        }
    }

    fun deleteWebApp(app: WebAppEntity) {
        viewModelScope.launch {
            repository.deleteWebApp(app.id, app.folderPath)
            userMessage.value = "برنامه '${app.title}' حذف شد"
        }
    }

    fun updateWebApp(app: WebAppEntity) {
        viewModelScope.launch {
            repository.updateWebApp(app)
            userMessage.value = "تنظیمات '${app.title}' به‌روزرسانی شد"
        }
    }

    fun saveState(id: String, url: String, scrollX: Int, scrollY: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateState(id, url, scrollX, scrollY)
        }
    }

    fun addConsoleLog(log: ConsoleLog) {
        activeConsoleLogs.value = (activeConsoleLogs.value + log).takeLast(100)
    }

    fun clearConsoleLogs() {
        activeConsoleLogs.value = emptyList()
    }

    fun clearUserMessage() {
        userMessage.value = null
    }

    fun getWebAppByIdFlow(id: String): Flow<WebAppEntity?> {
        return repository.getWebAppByIdFlow(id)
    }
}
