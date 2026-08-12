package com.example.data.model

data class ConsoleLog(
    val level: LogLevel,
    val message: String,
    val sourceId: String,
    val lineNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class LogLevel {
        LOG, WARNING, ERROR, TIP, DEBUG
    }
}
