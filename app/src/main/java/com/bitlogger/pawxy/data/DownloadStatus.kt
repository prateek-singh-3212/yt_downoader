package com.bitlogger.pawxy.data

sealed class DownloadStatus {
    data class Success(val uri: String) : DownloadStatus()

    data class Error(val message: String, val cause: Exception? = null) : DownloadStatus()

    data class Progress(val progress: Long, val totalSize: Int): DownloadStatus()

    data class Save(val progress: Long, val totalSize: Int): DownloadStatus()

    data class Convert(val progress: Long, val totalTime: Int): DownloadStatus()
}
