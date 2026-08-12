package com.example.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.util.zip.ZipInputStream

object FileImporter {

    fun getWebAppsDir(context: Context): File {
        val dir = File(context.filesDir, "webapps")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getAppDir(context: Context, folderPath: String): File {
        val appDir = File(getWebAppsDir(context), folderPath)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }

    fun deleteAppDir(context: Context, folderPath: String) {
        val appDir = File(getWebAppsDir(context), folderPath)
        if (appDir.exists()) {
            appDir.deleteRecursively()
        }
    }

    // Copy single file (HTML)
    fun copySingleFile(context: Context, sourceUri: Uri, targetDir: File): String {
        val targetFile = File(targetDir, "index.html")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        return "index.html"
    }

    // Copy folder recursively from SAF DocumentTree
    fun copyFolderFromUri(context: Context, treeUri: Uri, targetDir: File): List<String> {
        val rootDoc = DocumentFile.fromTreeUri(context, treeUri) ?: return emptyList()
        val createdFiles = mutableListOf<String>()
        copyDocumentFileRecursive(context, rootDoc, targetDir, "", createdFiles)
        return createdFiles
    }

    private fun copyDocumentFileRecursive(
        context: Context,
        doc: DocumentFile,
        targetDir: File,
        currentRelPath: String,
        outList: MutableList<String>
    ) {
        if (doc.isDirectory) {
            val subDirName = doc.name ?: return
            val newRelPath = if (currentRelPath.isEmpty()) subDirName else "$currentRelPath/$subDirName"
            val newTargetDir = File(targetDir, doc.name ?: "dir")
            newTargetDir.mkdirs()
            doc.listFiles().forEach { child ->
                copyDocumentFileRecursive(context, child, targetDir, currentRelPath, outList)
            }
        } else if (doc.isFile) {
            val fileName = doc.name ?: return
            val targetFile = if (currentRelPath.isEmpty()) {
                File(targetDir, fileName)
            } else {
                val parentDir = File(targetDir, currentRelPath)
                parentDir.mkdirs()
                File(parentDir, fileName)
            }

            try {
                context.contentResolver.openInputStream(doc.uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                val relativePath = if (currentRelPath.isEmpty()) fileName else "$currentRelPath/$fileName"
                outList.add(relativePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Unzip ZIP file containing HTML project
    fun unzipFile(context: Context, zipUri: Uri, targetDir: File): List<String> {
        val extractedFiles = mutableListOf<String>()
        context.contentResolver.openInputStream(zipUri)?.use { inputStream ->
            ZipInputStream(BufferedInputStream(inputStream)).use { zipInput ->
                var entry = zipInput.nextEntry
                while (entry != null) {
                    val file = File(targetDir, entry.name)
                    // Path traversal check
                    if (!file.canonicalPath.startsWith(targetDir.canonicalPath)) {
                        zipInput.closeEntry()
                        entry = zipInput.nextEntry
                        continue
                    }

                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { out ->
                            zipInput.copyTo(out)
                        }
                        extractedFiles.add(entry.name)
                    }
                    zipInput.closeEntry()
                    entry = zipInput.nextEntry
                }
            }
        }
        return extractedFiles
    }

    // List all HTML files found inside app folder
    fun findHtmlFiles(targetDir: File): List<String> {
        val result = mutableListOf<String>()
        scanHtmlFilesRecursive(targetDir, "", result)
        return if (result.isEmpty()) listOf("index.html") else result
    }

    private fun scanHtmlFilesRecursive(dir: File, relativePath: String, result: MutableList<String>) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            val rel = if (relativePath.isEmpty()) file.name else "$relativePath/${file.name}"
            if (file.isDirectory) {
                scanHtmlFilesRecursive(file, rel, result)
            } else if (file.name.endsWith(".html", ignoreCase = true) || file.name.endsWith(".htm", ignoreCase = true)) {
                result.add(rel)
            }
        }
    }
}
