package com.banglarbhumi.pro.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.banglarbhumi.pro.data.model.LandRecordResult
import com.banglarbhumi.pro.data.model.SearchType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SavedPdfItem(
    val file: File,
    val fileName: String,
    val mouzaName: String,
    val searchType: SearchType,
    val searchNumber: String,
    val districtName: String,
    val blockName: String,
    val sizeBytes: Long,
    val lastModified: Long,
    val isStarred: Boolean = false
) {
    fun getCleanMouza(isBengali: Boolean): String {
        val noJl = mouzaName.replace(Regex("\\s*\\[\\d+]"), "").trim()
        if (noJl.contains("(") && noJl.contains(")")) {
            val enPart = noJl.substringBefore("(").trim()
            val parenContent = noJl.substringAfter("(").substringBefore(")").trim()
            val hasBengaliInParen = parenContent.any { it in '\u0980'..'\u09FF' }
            return if (isBengali) {
                if (hasBengaliInParen) parenContent else noJl
            } else {
                if (enPart.isNotBlank()) enPart else noJl
            }
        }
        return noJl
    }

    fun getDisplayTitle(isBengali: Boolean): String {
        val cleanMouza = getCleanMouza(isBengali)
        val typeLabel = if (searchType == SearchType.PLOT) {
            if (isBengali) "দাগ নং" else "Plot No."
        } else {
            if (isBengali) "খতিয়ান নং" else "Khatian No."
        }
        return if (cleanMouza.isNotBlank()) {
            "$cleanMouza • $typeLabel $searchNumber"
        } else {
            "$typeLabel $searchNumber"
        }
    }

    val formattedSize: String
        get() {
            return when {
                sizeBytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", sizeBytes.toDouble() / (1024 * 1024))
                sizeBytes >= 1024 -> String.format(Locale.US, "%d KB", sizeBytes / 1024)
                else -> "$sizeBytes B"
            }
        }

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            return sdf.format(Date(lastModified))
        }
}

object PdfStorageManager {

    private const val FOLDER_RELATIVE_PATH = "SumanOnline Web Services/BanglarBhoomi Pro"
    private const val PREFS_NAME = "banglarbhumi_saved_pdfs_meta"
    private const val PREFS_STARRED_KEY = "starred_pdf_filenames"

    fun getTargetDirectory(): File {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val target = File(downloadDir, FOLDER_RELATIVE_PATH)
        if (!target.exists()) {
            target.mkdirs()
        }
        return target
    }

    suspend fun savePdf(
        context: Context,
        @Suppress("UNUSED_PARAMETER") webView: WebView?,
        result: LandRecordResult
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val targetDir = getTargetDirectory()
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val rawMouza = result.mouzaName
                .replace(Regex("\\s*\\[\\d+]"), "")
                .replace(Regex("[^a-zA-Z0-9\\u0980-\\u09FF]"), "_")
                .replace(Regex("_+"), "_")
                .trim('_')
            val safeMouza = if (rawMouza.isBlank()) "Record" else rawMouza
            val typeStr = if (result.searchType == SearchType.PLOT) "Dag" else "Khatian"
            val safeNumber = result.searchNumber.replace('/', '-').replace(Regex("[^a-zA-Z0-9\\-_]"), "")
            val fileName = "${safeMouza}_${typeStr}_${safeNumber}.pdf"
            val destinationFile = File(targetDir, fileName)

            val tempFile = File(context.cacheDir, "temp_render_${System.currentTimeMillis()}.pdf")

            PdfExporter.generateLandRecordPdf(context, result, tempFile)

            if (!tempFile.exists() || tempFile.length() == 0L) {
                return@withContext Result.failure(IOException("PDF generation failed"))
            }

            try {
                FileOutputStream(destinationFile).use { out ->
                    FileInputStream(tempFile).use { input ->
                        input.copyTo(out)
                    }
                }
            } catch (ioe: Exception) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveViaMediaStore(context, tempFile, fileName)
                } else {
                    throw ioe
                }
            }

            try { tempFile.delete() } catch (_: Exception) {}

            MediaScannerConnection.scanFile(
                context,
                arrayOf(destinationFile.absolutePath),
                arrayOf("application/pdf"),
                null
            )

            saveMetadata(
                context = context,
                fileName = fileName,
                mouzaName = result.mouzaName,
                searchType = result.searchType,
                searchNumber = result.searchNumber,
                districtName = result.districtName,
                blockName = result.blockName
            )

            Result.success(destinationFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveViaMediaStore(context: Context, sourceFile: File, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download/$FOLDER_RELATIVE_PATH")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IOException("Could not create MediaStore entry")

            context.contentResolver.openOutputStream(uri)?.use { out ->
                FileInputStream(sourceFile).use { input ->
                    input.copyTo(out)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            context.contentResolver.update(uri, contentValues, null, null)
        }
    }

    private fun saveMetadata(
        context: Context,
        fileName: String,
        mouzaName: String,
        searchType: SearchType,
        searchNumber: String,
        districtName: String,
        blockName: String
    ) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = JSONObject().apply {
                put("mouzaName", mouzaName)
                put("searchType", searchType.name)
                put("searchNumber", searchNumber)
                put("districtName", districtName)
                put("blockName", blockName)
                put("savedAt", System.currentTimeMillis())
            }
            prefs.edit().putString(fileName, json.toString()).apply()
        } catch (_: Exception) {}
    }

    fun toggleStarPdf(context: Context, fileName: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val oldSet = prefs.getStringSet(PREFS_STARRED_KEY, emptySet()) ?: emptySet()
        val newSet = HashSet(oldSet)
        val isNowStarred: Boolean
        if (newSet.contains(fileName)) {
            newSet.remove(fileName)
            isNowStarred = false
        } else {
            newSet.add(fileName)
            isNowStarred = true
        }
        prefs.edit().putStringSet(PREFS_STARRED_KEY, newSet).commit()
        return isNowStarred
    }

    fun getSavedPdfs(context: Context): List<SavedPdfItem> {
        val targetDir = getTargetDirectory()
        val files = targetDir.listFiles { file ->
            file.isFile && file.extension.equals("pdf", ignoreCase = true)
        } ?: emptyArray()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val starredSet = prefs.getStringSet(PREFS_STARRED_KEY, emptySet()) ?: emptySet()

        return files.map { file ->
            val isStarred = starredSet.contains(file.name)
            val metaJsonStr = prefs.getString(file.name, null)
            if (!metaJsonStr.isNullOrBlank()) {
                try {
                    val json = JSONObject(metaJsonStr)
                    val sType = if (json.optString("searchType") == SearchType.KHATIAN.name)
                        SearchType.KHATIAN else SearchType.PLOT
                    SavedPdfItem(
                        file = file,
                        fileName = file.name,
                        mouzaName = json.optString("mouzaName", ""),
                        searchType = sType,
                        searchNumber = json.optString("searchNumber", ""),
                        districtName = json.optString("districtName", ""),
                        blockName = json.optString("blockName", ""),
                        sizeBytes = file.length(),
                        lastModified = file.lastModified(),
                        isStarred = isStarred
                    )
                } catch (_: Exception) {
                    parseFromFileName(file, isStarred)
                }
            } else {
                parseFromFileName(file, isStarred)
            }
        }.sortedByDescending { it.lastModified }
    }

    private fun parseFromFileName(file: File, isStarred: Boolean = false): SavedPdfItem {

        val baseName = file.nameWithoutExtension
        val parts = baseName.split("_")
        val isDag = parts.any { it.equals("Dag", ignoreCase = true) || it.equals("Plot", ignoreCase = true) }
        val searchType = if (isDag) SearchType.PLOT else SearchType.KHATIAN

        var mouza = ""
        var number = ""

        if (parts.size >= 3) {
            mouza = parts[0]
            number = parts.drop(2).joinToString("_").replace("-", "/")
        } else if (parts.size == 2) {
            mouza = parts[0]
            number = parts[1].replace("-", "/")
        } else {
            mouza = baseName
        }

        return SavedPdfItem(
            file = file,
            fileName = file.name,
            mouzaName = mouza,
            searchType = searchType,
            searchNumber = number,
            districtName = "",
            blockName = "",
            sizeBytes = file.length(),
            lastModified = file.lastModified(),
            isStarred = isStarred
        )
    }

    fun deleteSavedPdf(context: Context, item: SavedPdfItem): Boolean {
        return try {
            val deleted = item.file.delete()
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val edit = prefs.edit()
            edit.remove(item.fileName)
            val starredSet = prefs.getStringSet(PREFS_STARRED_KEY, emptySet())
            if (!starredSet.isNullOrEmpty() && starredSet.contains(item.fileName)) {
                val newStarredSet = HashSet(starredSet)
                newStarredSet.remove(item.fileName)
                edit.putStringSet(PREFS_STARRED_KEY, newStarredSet)
            }
            edit.commit()
            MediaScannerConnection.scanFile(
                context,
                arrayOf(item.file.absolutePath),
                arrayOf("application/pdf"),
                null
            )
            deleted
        } catch (_: Exception) {
            false
        }
    }

    fun viewPdf(context: Context, item: SavedPdfItem) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                item.file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open Land Record PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sharePdf(context: Context, item: SavedPdfItem) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                item.file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, item.getDisplayTitle(false))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Land Record PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
