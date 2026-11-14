package com.banglarbhumi.pro.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
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
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2)

    suspend fun generateLandRecordPdf(
        context: Context,
        result: LandRecordResult,
        destFile: File? = null
    ): File = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        val paint = Paint().apply {
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            isAntiAlias = true
            textSize = 9.5f
            color = Color.parseColor("#1E293B")
        }

        var currentPageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        fun drawHeader(canvas: Canvas) {
            // Top Green Banner
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#0F5132")
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 75f, paint)

            // Header Titles
            paint.color = Color.WHITE
            paint.textSize = 15f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Government of West Bengal", MARGIN, 28f, paint)

            paint.textSize = 10.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Department of Land & Land Reforms • BanglarBhumi Pro Record", MARGIN, 46f, paint)

            val liveTimeStr = if (result.liveTimestamp.isNotBlank()) {
                "Live Portal Data: ${result.liveTimestamp}"
            } else {
                val sdf = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault())
                "Generated: ${sdf.format(Date(result.queryTimestamp))}"
            }
            paint.textSize = 8.5f
            canvas.drawText(liveTimeStr, MARGIN, 62f, paint)

            // SumanOnline Branding Pill at top right
            paint.color = Color.parseColor("#157347")
            canvas.drawRoundRect(PAGE_WIDTH - 180f, 18f, PAGE_WIDTH - MARGIN, 56f, 8f, 8f, paint)
            paint.color = Color.WHITE
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("SumanOnline Project", PAGE_WIDTH - 170f, 36f, paint)
            paint.textSize = 8f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Verified Portal Record", PAGE_WIDTH - 170f, 49f, paint)
        }

        fun drawFooter(canvas: Canvas, pageNum: Int) {
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#94A3B8")
            paint.strokeWidth = 0.5f
            canvas.drawLine(MARGIN, PAGE_HEIGHT - 35f, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 35f, paint)

            paint.color = Color.parseColor("#64748B")
            paint.textSize = 8f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(
                "Notice: Computer-generated copy for informational reference. Official copies issued by BL&LRO.",
                MARGIN,
                PAGE_HEIGHT - 22f,
                paint
            )
            canvas.drawText(
                "Page $pageNum",
                PAGE_WIDTH - MARGIN - 35f,
                PAGE_HEIGHT - 22f,
                paint
            )
        }

        // Draw initial page header
        drawHeader(canvas)

        var y = 92f

        // Location Info Card (District, Block, Mouza, JL, Thana, Record Number)
        paint.color = Color.parseColor("#F8FAFC")
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 68f, 8f, 8f, paint)

        paint.color = Color.parseColor("#E2E8F0")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 68f, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val cleanDist = result.districtName.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
        val cleanBlk = result.blockName.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
        val cleanMouza = result.mouzaName.replace(Regex("\\s*\\[\\d+\\]"), "").trim()

        canvas.drawText("District: $cleanDist", MARGIN + 12f, y + 20f, paint)
        canvas.drawText("Block: $cleanBlk", MARGIN + 12f, y + 38f, paint)
        if (result.thanaName.isNotBlank()) {
            canvas.drawText("Thana: ${result.thanaName}", MARGIN + 12f, y + 56f, paint)
        } else {
            canvas.drawText("Block Code: ${result.blockCode}", MARGIN + 12f, y + 56f, paint)
        }

        canvas.drawText("Mouza: $cleanMouza", 295f, y + 20f, paint)
        val jlText = if (result.jlNumber.isNotBlank()) "J.L. No: ${result.jlNumber}" else "Mouza Code: ${result.mouzaCode}"
        canvas.drawText(jlText, 295f, y + 38f, paint)

        val typeText = if (result.searchType == SearchType.PLOT) "Plot / Dag No: ${result.searchNumber}" else "Khatian No: ${result.searchNumber}"
        paint.color = Color.parseColor("#0F5132")
        canvas.drawText(typeText, 295f, y + 56f, paint)

        y += 82f

        // Summary Table Section
        if (result.searchType == SearchType.PLOT) {
            paint.color = Color.parseColor("#E2F0D9")
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 22f, paint)

            paint.color = Color.parseColor("#155724")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            canvas.drawText("দাগ নং (Plot No)", MARGIN + 10f, y + 15f, paint)
            canvas.drawText("শ্রেণী (Classification)", MARGIN + 160f, y + 15f, paint)
            canvas.drawText("মোট পরিমাণ (Total Area)", MARGIN + 350f, y + 15f, paint)

            y += 22f
            paint.color = Color.WHITE
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 22f, paint)
            paint.color = Color.parseColor("#D0D5DD")
            paint.style = Paint.Style.STROKE
            canvas.drawRect(MARGIN, y - 22f, PAGE_WIDTH - MARGIN, y + 22f, paint)
            paint.style = Paint.Style.FILL

            paint.color = Color.parseColor("#1E293B")
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            val landClass = result.plotSummary?.landClassification?.ifBlank { null }
                ?: result.owners.firstOrNull()?.landClassification?.ifBlank { null }
                ?: "বাস্তু / শালি"

            canvas.drawText(result.searchNumber, MARGIN + 10f, y + 15f, paint)
            canvas.drawText(landClass, MARGIN + 160f, y + 15f, paint)
            canvas.drawText("${result.totalAreaAcre} একর (Acre)", MARGIN + 350f, y + 15f, paint)

            y += 34f
        } else {
            // Khatian Summary Table
            val kSum = result.khatianSummary
            if (kSum != null && (kSum.ownerName.isNotBlank() || kSum.guardianName.isNotBlank())) {
                paint.color = Color.parseColor("#E2F0D9")
                canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 22f, paint)

                paint.color = Color.parseColor("#155724")
                paint.textSize = 9f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

                canvas.drawText("খতিয়ান নং", MARGIN + 8f, y + 15f, paint)
                canvas.drawText("রায়তের নাম", MARGIN + 80f, y + 15f, paint)
                canvas.drawText("পিতা/স্বামী", MARGIN + 220f, y + 15f, paint)
                canvas.drawText("মোট দাগ", MARGIN + 360f, y + 15f, paint)
                canvas.drawText("মোট জমি", MARGIN + 430f, y + 15f, paint)

                y += 22f
                paint.color = Color.WHITE
                canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 22f, paint)
                paint.color = Color.parseColor("#D0D5DD")
                paint.style = Paint.Style.STROKE
                canvas.drawRect(MARGIN, y - 22f, PAGE_WIDTH - MARGIN, y + 22f, paint)
                paint.style = Paint.Style.FILL

                paint.color = Color.parseColor("#1E293B")
                paint.textSize = 9f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

                canvas.drawText(result.searchNumber, MARGIN + 8f, y + 15f, paint)
                canvas.drawText(kSum.ownerName.take(18), MARGIN + 80f, y + 15f, paint)
                canvas.drawText(kSum.guardianName.take(18), MARGIN + 220f, y + 15f, paint)
                canvas.drawText(kSum.plotCount.ifBlank { "${result.owners.size}" }, MARGIN + 360f, y + 15f, paint)
                canvas.drawText("${result.totalAreaAcre} একর", MARGIN + 430f, y + 15f, paint)

                y += 34f
            }
        }

        // Section Title: Ownership / Plot Details
        paint.color = Color.parseColor("#0F5132")
        paint.textSize = 10.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val sectionTitle = if (result.searchType == SearchType.PLOT)
            "দাগের অত্রস্বত্বের রায়তের বিবরণ (Plot Ownership Details)"
        else
            "খতিয়ানের অন্তর্ভূক্ত দাগসমূহের বিবরণ (Khatian Plot Details)"
        canvas.drawText(sectionTitle, MARGIN, y, paint)

        y += 12f

        // Table Header
        fun drawTableHeader(canvas: Canvas, yPos: Float) {
            paint.color = Color.parseColor("#D5E8D4")
            paint.style = Paint.Style.FILL
            canvas.drawRect(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 24f, paint)

            paint.color = Color.parseColor("#155724")
            paint.textSize = 8.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            canvas.drawText("ক্রঃ", MARGIN + 6f, yPos + 16f, paint)
            val col2 = if (result.searchType == SearchType.PLOT) "খতিয়ান নং" else "দাগ নং"
            canvas.drawText(col2, MARGIN + 26f, yPos + 16f, paint)
            val col3 = if (result.searchType == SearchType.PLOT) "দখলকারের নাম" else "শ্রেণী"
            canvas.drawText(col3, MARGIN + 90f, yPos + 16f, paint)
            val col4 = if (result.searchType == SearchType.PLOT) "পিতা/স্বামী" else "মোট পরিমাণ"
            canvas.drawText(col4, MARGIN + 235f, yPos + 16f, paint)
            canvas.drawText("অংশ", MARGIN + 365f, yPos + 16f, paint)
            canvas.drawText("অংশ পরিমাণ", MARGIN + 420f, yPos + 16f, paint)
            canvas.drawText("দখলদার/শ্রেণী", MARGIN + 485f, yPos + 16f, paint)

            // Header border
            paint.color = Color.parseColor("#B7D5B5")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRect(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 24f, paint)
            paint.style = Paint.Style.FILL
        }

        drawTableHeader(canvas, y)
        y += 24f

        val rowHeight = 22f

        for ((index, owner) in result.owners.withIndex()) {
            // Check if page overflow
            if (y + rowHeight > PAGE_HEIGHT - 50f) {
                drawFooter(canvas, currentPageNumber)
                document.finishPage(page)

                currentPageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas

                drawHeader(canvas)
                y = 90f
                drawTableHeader(canvas, y)
                y += 24f
            }

            // Alternating Row background
            if (index % 2 == 1) {
                paint.color = Color.parseColor("#F9FBF9")
                paint.style = Paint.Style.FILL
                canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight, paint)
            }

            // Cell border line
            paint.color = Color.parseColor("#E2E8F0")
            paint.strokeWidth = 0.5f
            paint.style = Paint.Style.STROKE
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight, paint)
            paint.style = Paint.Style.FILL

            // Text content
            textPaint.color = Color.parseColor("#1E293B")
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textPaint.textSize = 8.5f

            val slNo = (index + 1).toString()
            val recNo = owner.khatianOrPlotNo.ifBlank { result.searchNumber }
            val name = owner.ownerName.ifBlank { owner.landClassification }.take(22)
            val guardian = owner.guardianName.take(20)
            val share = owner.share
            val shareArea = owner.areaAcre
            val possessor = owner.possessor.ifBlank { owner.remarks }.ifBlank { "Nil" }.take(10)

            canvas.drawText(slNo, MARGIN + 6f, y + 15f, textPaint)
            canvas.drawText(recNo, MARGIN + 26f, y + 15f, textPaint)
            canvas.drawText(name, MARGIN + 90f, y + 15f, textPaint)
            canvas.drawText(guardian, MARGIN + 235f, y + 15f, textPaint)
            canvas.drawText(share, MARGIN + 365f, y + 15f, textPaint)
            canvas.drawText(shareArea, MARGIN + 420f, y + 15f, textPaint)
            canvas.drawText(possessor, MARGIN + 485f, y + 15f, textPaint)

            y += rowHeight
        }

        drawFooter(canvas, currentPageNumber)
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
}
