package com.bitlogger.pawxy.ui

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import com.bitlogger.pawxy.data.DownloadStatus
import com.bitlogger.pawxy.data.YoutubeData
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.ByteBuffer
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

class MainViewModel : ViewModel() {

    var ytData by mutableStateOf<YoutubeData?>(null)

    var downloadStates by mutableStateOf<DownloadStatus>(DownloadStatus.Progress(0, 0))

    var downloadJob by mutableStateOf<Job?>(null)

    var saveLocation by mutableStateOf<String?>(null)

    /**
     * Add the yt data.
     * */
    fun addData(data: YoutubeData) {
        ytData = data
    }

    /**
     * Starts the process of downloading, converting and saving audio file.
     * */
    fun startAudioDownloadProcess(context: Context, coroutineContext: CoroutineContext) {
        val outputFileName = ytData!!.title + "-audio.mp3"
        downloadVideo(context, coroutineContext)
        if (!coroutineContext.isActive) return
        convertVideoToAudio(context, outputFileName, coroutineContext)
        if (!coroutineContext.isActive) return
        saveAudio(context, outputFileName, coroutineContext)
        downloadStates = DownloadStatus.Success("")
    }

    /**
     * Download the video.
     * */
    private fun downloadVideo(context: Context, coroutineContext: CoroutineContext) {
        downloadStates = DownloadStatus.Progress(0, ytData?.downloadSize ?: -1)
        URL(ytData!!.downloadURL).openStream().use { input ->
            context.openFileOutput(ytData!!.title, Context.MODE_PRIVATE).use { output ->
                input.available()
                var bytesCopied: Long = 0
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytes = input.read(buffer)
                while (bytes >= 0) {
                    if (!coroutineContext.isActive)
                        return

                    output!!.write(buffer, 0, bytes)
                    bytesCopied += bytes
                    // Updating Download Progress
                    downloadStates =
                        DownloadStatus.Progress(bytesCopied, ytData?.downloadSize ?: -1)
                    bytes = input.read(buffer)
                }
            }
        }
    }

    /**
     * Save the converted audio to the user accessible storage.
     * */
    private fun saveAudio(
        context: Context,
        outputFileName: String,
        coroutineContext: CoroutineContext
    ) {
        downloadStates = DownloadStatus.Save(0, ytData?.downloadSize ?: -1)

        val resolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val docFile = DocumentFile.fromTreeUri(context, saveLocation!!.toUri())
            val newFileUri = docFile?.createFile("audio/mpeg", ytData!!.title)?.uri
                ?: buildFileURI(resolver)

            context.openFileInput(outputFileName).use { input ->
                resolver.openOutputStream(newFileUri).use { output ->
                    input.available()
                    var bytesCopied: Long = 0
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output!!.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        downloadStates =
                            DownloadStatus.Save(bytesCopied, ytData?.downloadSize ?: -1)
                        bytes = input.read(buffer)
                    }
                }
            }
        } else {
            val target = saveLocation?.let {
                File(it, ytData!!.title)
            } ?: File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                ytData!!.title
            )
            context.openFileInput(outputFileName).use { input ->
                FileOutputStream(target).use { output ->
                    input.available()
                    var bytesCopied: Long = 0
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        downloadStates =
                            DownloadStatus.Save(bytesCopied, ytData?.downloadSize ?: -1)
                        bytes = input.read(buffer)
                    }
                }
            }
        }

        context.deleteFile(outputFileName)
        context.deleteFile(ytData!!.title)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buildFileURI(resolver: ContentResolver): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, ytData!!.title)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/Test")
        }
        return resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)!!
    }

    /**
     * convert video to audio.
     * */
    private fun convertVideoToAudio(
        context: Context,
        outputFileName: String,
        coroutineContext: CoroutineContext
    ) {
        downloadStates = DownloadStatus.Convert(0, 0)
        val fileList = context.filesDir.absolutePath + "/${ytData!!.title}"
        val outputFile = File(context.filesDir.absolutePath, outputFileName).absolutePath

        // Set up MediaExtractor to read from the source.
        val extractor = MediaExtractor()
        extractor.setDataSource(fileList)
        val trackCount = extractor.trackCount

        // Set up MediaMuxer for the destination.
        val muxer = MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        // Set up the tracks and retrieve the max buffer size for selected tracks
        val indexMap = HashMap<Int, Int>(trackCount)
        var bufferSize = -1
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            val selectCurrentTrack = mime!!.startsWith("audio/")
            if (selectCurrentTrack) {
                extractor.selectTrack(i)
                val dstIndex = muxer.addTrack(format)
                indexMap[i] = dstIndex
                if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                    val newSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
                    bufferSize = max(newSize, bufferSize)
                }
            }
        }
        if (bufferSize < 0) {
            bufferSize = DEFAULT_BUFFER_SIZE
        }

        // Set up the orientation and starting time for extractor.
        val retrieverSrc = MediaMetadataRetriever()
        retrieverSrc.setDataSource(fileList)

        // Copy the samples from MediaExtractor to MediaMuxer. We will loop for copying each sample and stop when we get to the end of the source file or exceed the end time of the trimming.
        val offset = 0
        var trackIndex = -1
        val dstBuf = ByteBuffer.allocate(bufferSize)
        val bufferInfo = MediaCodec.BufferInfo()
        muxer.start()
        while (true) {
            bufferInfo.offset = offset
            bufferInfo.size = extractor.readSampleData(dstBuf, offset)
            if (bufferInfo.size < 0) {
                Log.d("worker", "Saw input EOS.")
                bufferInfo.size = 0
                break
            } else {
                bufferInfo.presentationTimeUs = extractor.sampleTime
                downloadStates =
                    DownloadStatus.Convert(bufferInfo.presentationTimeUs, ytData?.duration ?: -1)
                bufferInfo.flags = extractor.sampleFlags
                trackIndex = extractor.sampleTrackIndex
                muxer.writeSampleData(indexMap[trackIndex]!!, dstBuf, bufferInfo)
                extractor.advance()
            }
        }
        muxer.stop()
        muxer.release()
    }
}