package com.example.util

import android.content.Context
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object IsolatedAssetLoader {
    const val VIRTUAL_DOMAIN = "applocal"
    const val BASE_URL_PREFIX = "https://$VIRTUAL_DOMAIN/"

    fun getAppBaseUrl(appId: String): String {
        return "$BASE_URL_PREFIX$appId/"
    }

    fun handleInterceptRequest(
        context: Context,
        request: WebResourceRequest,
        appId: String,
        appDir: File
    ): WebResourceResponse? {
        val url = request.url ?: return null
        if (url.scheme != "https" || url.host != VIRTUAL_DOMAIN) {
            return null // Pass through to standard network handler
        }

        val path = url.path ?: return null // e.g. "/appId/sub/file.js"
        val prefix = "/$appId"
        
        if (!path.startsWith(prefix)) {
            // Access denied - attempt to access another app's isolated domain
            return WebResourceResponse("text/plain", "UTF-8", 403, "Forbidden", mapOf(), null)
        }

        var relativePath = path.removePrefix(prefix).removePrefix("/")
        if (relativePath.isEmpty()) {
            relativePath = "index.html"
        }

        val targetFile = File(appDir, relativePath)

        // Security check against path traversal attacks (e.g. "../")
        val canonicalAppDir = appDir.canonicalPath
        val canonicalTarget = targetFile.canonicalPath
        if (!canonicalTarget.startsWith(canonicalAppDir)) {
            return WebResourceResponse("text/plain", "UTF-8", 403, "Path Traversal Blocked", mapOf(), null)
        }

        return if (targetFile.exists() && targetFile.isFile) {
            try {
                val mimeType = getMimeType(targetFile.name)
                val inputStream: InputStream = FileInputStream(targetFile)
                val headers = mapOf(
                    "Access-Control-Allow-Origin" to "*",
                    "Access-Control-Allow-Methods" to "GET, POST, OPTIONS",
                    "Access-Control-Allow-Headers" to "*",
                    "Cache-Control" to "no-cache"
                )
                WebResourceResponse(mimeType, "UTF-8", 200, "OK", headers, inputStream)
            } catch (e: Exception) {
                WebResourceResponse("text/plain", "UTF-8", 500, "Internal Error", mapOf(), null)
            }
        } else {
            WebResourceResponse("text/html", "UTF-8", 404, "Not Found", mapOf(), null)
        }
    }

    private fun getMimeType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "js", "mjs" -> "text/javascript"
            "json" -> "application/json"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "svg" -> "image/svg+xml"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "ico" -> "image/x-icon"
            "woff" -> "font/woff"
            "woff2" -> "font/woff2"
            "ttf" -> "font/ttf"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "mp4" -> "video/mp4"
            "wasm" -> "application/wasm"
            else -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
        }
    }
}
