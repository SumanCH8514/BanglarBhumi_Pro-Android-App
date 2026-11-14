package com.banglarbhumi.pro.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.banglarbhumi.pro.R
import com.banglarbhumi.pro.data.model.LandRecordResult
import com.banglarbhumi.pro.data.model.SearchType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 30f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2) // 535f

    suspend fun generateLandRecordPdf(
        context: Context,
        result: LandRecordResult,
        destFile: File? = null
    ): File = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        val fillPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val strokePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.parseColor("#C0C0C0")
        }

        val textPaint = Paint().apply {
            isAntiAlias = true
            textSize = 9.5f
            color = Color.BLACK
        }

        var currentPageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var y = MARGIN

        // --- 1. Top Headers (Live Data & JL / Thana) ---
        val liveTime = if (result.liveTimestamp.isNotBlank()) {
            "(Live Data As On ${result.liveTimestamp})"
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy,HH:mm:ss", Locale.getDefault())
            "(Live Data As On ${sdf.format(Date(result.queryTimestamp))})"
        }

        textPaint.color = Color.parseColor("#0000FF")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12.5f
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(liveTime, MARGIN, y + 12f, textPaint)
        y += 18f

        val jl = result.jlNumber.ifBlank { result.mouzaCode }
        val thana = result.thanaName.ifBlank { result.blockName }
        val locationText = "জে.এল নং : $jl   থানা : $thana"

        textPaint.color = Color.parseColor("#800000")
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 12f
        canvas.drawText(locationText, MARGIN, y + 12f, textPaint)
        y += 20f

        // --- 2. Table 1: Summary Table ---
        if (result.searchType == SearchType.KHATIAN) {
            val ks = result.khatianSummary
            val firstOwner = result.owners.firstOrNull()

            val displayKhatian = ks?.khatianNo?.ifBlank { result.searchNumber } ?: result.searchNumber
            val ownerDisplayName = ks?.ownerName?.ifBlank { firstOwner?.ownerName ?: "-" } ?: "-"
            val guardianDisplayName = ks?.guardianName?.ifBlank { firstOwner?.guardianName ?: "-" } ?: "-"
            val rayatTypeDisplayName = ks?.rayatType?.ifBlank { "ব্যক্তি" } ?: "ব্যক্তি"
            val addressDisplayName = ks?.address?.ifBlank { "নিজ" } ?: "নিজ"
            val areaDisplayName = ks?.totalAreaAcre?.ifBlank { "${result.totalAreaAcre} একর" } ?: "${result.totalAreaAcre} একর"
            val plotCountDisplayName = ks?.plotCount?.ifBlank { "${result.owners.size}" } ?: "${result.owners.size}"

            val summaryRows = listOf(
                Pair("খতিয়ান নং :", displayKhatian),
                Pair("অত্রস্বত্বের দখলকারের নাম :", ownerDisplayName),
                Pair("পিতা/স্বামী :", guardianDisplayName),
                Pair("রায়তের ধরণ :", rayatTypeDisplayName),
                Pair("ঠিকানা :", addressDisplayName),
                Pair("জমির পরিমাণ: ", areaDisplayName),
                Pair("দাগের সংখ্যা :", plotCountDisplayName)
            )

            val col1W = 235f
            val rowH = 21f

            for ((idx, row) in summaryRows.withIndex()) {
                val rowTop = y
                val rowBottom = y + rowH

                // Row background (alternating #FFFFFF and #EAEAEA)
                fillPaint.color = if (idx % 2 == 0) Color.WHITE else Color.parseColor("#EAEAEA")
                canvas.drawRect(MARGIN, rowTop, MARGIN + CONTENT_WIDTH, rowBottom, fillPaint)

                // Borders
                strokePaint.color = Color.parseColor("#C0C0C0")
                canvas.drawRect(MARGIN, rowTop, MARGIN + col1W, rowBottom, strokePaint)
                canvas.drawRect(MARGIN + col1W, rowTop, MARGIN + CONTENT_WIDTH, rowBottom, strokePaint)

                // Col 1 Text
                textPaint.color = Color.BLACK
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.textSize = 9.5f
                textPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(row.first, MARGIN + 8f, rowTop + 14.5f, textPaint)

                // Col 2 Text
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(row.second, MARGIN + col1W + 8f, rowTop + 14.5f, textPaint)

                y += rowH
            }
            y += 14f
        } else {
            // Plot Summary Table (3 Columns matching portal layout)
            val col1W = 140f
            val col2W = 140f
            val col3W = CONTENT_WIDTH - col1W - col2W // 255f
            val rowH = 21f

            // Header
            fillPaint.color = Color.parseColor("#D5E8D4")
            canvas.drawRect(MARGIN, y, MARGIN + CONTENT_WIDTH, y + rowH, fillPaint)

            strokePaint.color = Color.parseColor("#B0B0B0")
            canvas.drawRect(MARGIN, y, MARGIN + col1W, y + rowH, strokePaint)
            canvas.drawRect(MARGIN + col1W, y, MARGIN + col1W + col2W, y + rowH, strokePaint)
            canvas.drawRect(MARGIN + col1W + col2W, y, MARGIN + CONTENT_WIDTH, y + rowH, strokePaint)

            textPaint.color = Color.BLACK
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 9.5f
            textPaint.textAlign = Paint.Align.CENTER

            canvas.drawText("দাগ নং", MARGIN + (col1W / 2f), y + 14.5f, textPaint)
            canvas.drawText("শ্রেণী", MARGIN + col1W + (col2W / 2f), y + 14.5f, textPaint)
            canvas.drawText("জমির মোট পরিমাণ(একর)", MARGIN + col1W + col2W + (col3W / 2f), y + 14.5f, textPaint)
            y += rowH

            // Data Row
            fillPaint.color = Color.WHITE
            canvas.drawRect(MARGIN, y, MARGIN + CONTENT_WIDTH, y + rowH, fillPaint)

            strokePaint.color = Color.parseColor("#C0C0C0")
            canvas.drawRect(MARGIN, y, MARGIN + col1W, y + rowH, strokePaint)
            canvas.drawRect(MARGIN + col1W, y, MARGIN + col1W + col2W, y + rowH, strokePaint)
            canvas.drawRect(MARGIN + col1W + col2W, y, MARGIN + CONTENT_WIDTH, y + rowH, strokePaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val plotClass = result.plotSummary?.landClassification?.ifBlank { null }
                ?: result.owners.firstOrNull()?.landClassification?.ifBlank { null }
                ?: "শালি"

            canvas.drawText(result.searchNumber, MARGIN + (col1W / 2f), y + 14.5f, textPaint)
            canvas.drawText(plotClass, MARGIN + col1W + (col2W / 2f), y + 14.5f, textPaint)
            canvas.drawText(result.totalAreaAcre, MARGIN + col1W + col2W + (col3W / 2f), y + 14.5f, textPaint)
            y += rowH + 14f
        }

        // --- 3. Subheading & Purple Note ---
        val sectionTitle = if (result.searchType == SearchType.KHATIAN)
            "অত্রস্বত্বের দাগের বিবরণ ও পরিমাণ:"
        else
            "দাগের অত্রস্বত্বের রায়তের বিবরণ:"

        textPaint.color = Color.BLACK
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 10.5f
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(sectionTitle, MARGIN, y + 10f, textPaint)
        y += 14f

        val sectionNote = if (result.searchType == SearchType.KHATIAN)
            "(* দাগের অত্রস্বত্বের জমির যথার্থ পরিমাণ, দাগের মধ্যে অত্রস্বত্বের অংশে অনুসারে নির্ধারিত হইবে।)"
        else
            "(* রায়তের অংশ অনুযায়ী দাগের জমির যথার্থ পরিমাণ নির্ধারিত হইবে।)"

        textPaint.color = Color.parseColor("#800080") // Purple
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        textPaint.textSize = 8.8f
        canvas.drawText(sectionNote, MARGIN, y + 9f, textPaint)
        y += 15f

        // --- 4. Table 2: Detail Table Layout Specifications ---
        val isKhatian = result.searchType == SearchType.KHATIAN

        val colWidths: FloatArray = if (isKhatian) {
            // দাগ নং, শ্রেণী, অংশ, অংশ পরিমাণ(একর), দখলদার, মন্তব্য
            floatArrayOf(65f, 75f, 95f, 130f, 85f, 85f) // Sum = 535f
        } else {
            // খতিয়ান নং, দখলকারের নাম, পিতা/স্বামী, অংশ, অংশ পরিমাণ(একর) (শ্রেণী), দখলদার, মন্তব্য
            floatArrayOf(60f, 125f, 110f, 55f, 105f, 40f, 40f) // Sum = 535f
        }

        val colHeaders: Array<String> = if (isKhatian) {
            arrayOf("দাগ নং", "শ্রেণী", "অংশ", "অংশ পরিমাণ(একর)", "দখলদার", "মন্তব্য")
        } else {
            arrayOf("খতিয়ান নং", "দখলকারের নাম", "পিতা/স্বামী", "অংশ", "অংশ পরিমাণ(একর)", "দখলদার", "মন্তব্য")
        }

        val rowH = 20.5f

        fun drawTable2Header(canvas: Canvas, topY: Float) {
            fillPaint.color = Color.parseColor("#D5E8D4")
            canvas.drawRect(MARGIN, topY, MARGIN + CONTENT_WIDTH, topY + rowH, fillPaint)

            strokePaint.color = Color.parseColor("#B0B0B0")
            textPaint.color = Color.BLACK
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textPaint.textSize = 9f
            textPaint.textAlign = Paint.Align.CENTER

            var currentX = MARGIN
            for (i in colHeaders.indices) {
                val w = colWidths[i]
                canvas.drawRect(currentX, topY, currentX + w, topY + rowH, strokePaint)
                canvas.drawText(colHeaders[i], currentX + (w / 2f), topY + 14f, textPaint)
                currentX += w
            }
        }

        drawTable2Header(canvas, y)
        y += rowH

        // Draw Table 2 Rows
        for ((index, owner) in result.owners.withIndex()) {
            // Check for page overflow
            if (y + rowH > PAGE_HEIGHT - MARGIN) {
                document.finishPage(page)

                currentPageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas

                y = MARGIN
                drawTable2Header(canvas, y)
                y += rowH
            }

            val rowTop = y
            val rowBottom = y + rowH

            // Background
            fillPaint.color = if (index % 2 == 0) Color.WHITE else Color.parseColor("#FAFAFA")
            canvas.drawRect(MARGIN, rowTop, MARGIN + CONTENT_WIDTH, rowBottom, fillPaint)

            strokePaint.color = Color.parseColor("#C0C0C0")

            val cells = if (isKhatian) {
                arrayOf(
                    owner.khatianOrPlotNo.ifBlank { "-" },
                    owner.landClassification.ifBlank { "-" },
                    owner.share.ifBlank { "-" },
                    owner.areaAcre.ifBlank { "-" },
                    owner.possessor.ifBlank { "Nil" },
                    owner.remarks.ifBlank { "Nil" }
                )
            } else {
                arrayOf(
                    owner.khatianOrPlotNo.ifBlank { "-" },
                    owner.ownerName.ifBlank { "-" },
                    owner.guardianName.ifBlank { "-" },
                    owner.share.ifBlank { "-" },
                    owner.areaAcre.ifBlank { "-" },
                    owner.possessor.ifBlank { "Nil" },
                    owner.remarks.ifBlank { "Nil" }
                )
            }

            var currentX = MARGIN
            for (i in cells.indices) {
                val w = colWidths[i]
                canvas.drawRect(currentX, rowTop, currentX + w, rowBottom, strokePaint)

                val text = cells[i]

                // Specific styling matching uploaded portal image
                if (text.contains("বর্গা")) {
                    textPaint.color = Color.parseColor("#0000FF") // Blue for Barga
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                } else if (text.equals("Nil", ignoreCase = true) || text.contains("Nil Remarks", ignoreCase = true)) {
                    textPaint.color = Color.parseColor("#CC0000") // Red for Nil / Nil Remarks
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                } else {
                    textPaint.color = Color.BLACK
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }

                textPaint.textSize = 8.5f
                textPaint.textAlign = Paint.Align.CENTER

                // Clip cell to prevent text bleeding into adjacent cells
                canvas.save()
                canvas.clipRect(currentX + 1f, rowTop + 1f, currentX + w - 1f, rowBottom - 1f)
                canvas.drawText(text, currentX + (w / 2f), rowTop + 14f, textPaint)
                canvas.restore()

                currentX += w
            }

            y += rowH
        }

        // --- 5. Promotional Banner at the End of the PDF ---
        val bannerBitmap = loadBannerBitmap(context)
        if (bannerBitmap != null) {
            val bannerWidth = CONTENT_WIDTH
            val bannerAspect = bannerBitmap.height.toFloat() / bannerBitmap.width.toFloat()
            val bannerHeight = bannerWidth * bannerAspect

            y += 18f

            // If banner exceeds page bottom, put it on a fresh page
            if (y + bannerHeight > PAGE_HEIGHT - MARGIN) {
                document.finishPage(page)

                currentPageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = MARGIN
            }

            val srcRect = Rect(0, 0, bannerBitmap.width, bannerBitmap.height)
            val dstRect = RectF(MARGIN, y, MARGIN + bannerWidth, y + bannerHeight)

            val bmpPaint = Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
            }

            canvas.drawBitmap(bannerBitmap, srcRect, dstRect, bmpPaint)

            // Subtle border around banner
            val bPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 0.8f
                color = Color.parseColor("#CBD5E1")
            }
            canvas.drawRoundRect(dstRect, 4f, 4f, bPaint)

            y += bannerHeight + 10f
        }

        document.finishPage(page)

        val outputFile = destFile ?: run {
            val outputDir = File(context.cacheDir, "land_records")
            if (!outputDir.exists()) outputDir.mkdirs()
            File(outputDir, "LandRecord_${result.searchNumber.replace('/', '_')}_${System.currentTimeMillis()}.pdf")
        }

        outputFile.parentFile?.let {
            if (!it.exists()) it.mkdirs()
        }

        FileOutputStream(outputFile).use { fos ->
            document.writeTo(fos)
        }
        document.close()

        outputFile
    }

    private fun loadBannerBitmap(context: Context): Bitmap? {
        // 1. Check direct file path specified by user
        val userBannerPath = "E:/Projects/Android Application Dev/BanglarBhumi-Pro Application/Banglarbhumi_Pro-Banner.png"
        try {
            val file = File(userBannerPath)
            if (file.exists() && file.canRead()) {
                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                if (bmp != null) return bmp
            }
        } catch (_: Exception) {}

        // 2. Check drawable resource bundled in APK
        try {
            val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.banglarbhumi_pro_banner)
            if (bmp != null) return bmp
        } catch (_: Exception) {}

        // 3. Check assets folder bundled in APK
        try {
            context.assets.open("Banglarbhumi_Pro-Banner.png").use { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                if (bmp != null) return bmp
            }
        } catch (_: Exception) {}

        return null
    }
}
