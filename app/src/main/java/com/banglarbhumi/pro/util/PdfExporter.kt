package com.banglarbhumi.pro.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.banglarbhumi.pro.data.model.LandRecordResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    suspend fun generateLandRecordPdf(
        context: Context,
        result: LandRecordResult
    ): File = withContext(Dispatchers.IO) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.isAntiAlias = true

        paint.color = Color.parseColor("#15803D")
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Government of West Bengal", 30f, 38f, paint)

        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Department of Land & Land Reforms - BanglarBhumi Pro Record", 30f, 60f, paint)

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = "Date: " + dateFormat.format(Date(result.queryTimestamp))
        paint.textSize = 10f
        canvas.drawText(dateStr, 30f, 76f, paint)

        paint.color = Color.parseColor("#F8FAFC")
        canvas.drawRect(30f, 105f, 565f, 175f, paint)
        paint.color = Color.parseColor("#E2E8F0")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRect(30f, 105f, 565f, 175f, paint)
        paint.style = Paint.Style.FILL

        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("District: ${result.districtName}", 45f, 130f, paint)
        canvas.drawText("Block: ${result.blockName}", 45f, 155f, paint)
        canvas.drawText("Mouza: ${result.mouzaName}", 310f, 130f, paint)
        val typeLabel = if (result.searchType.name == "PLOT") "Plot / Dag No:" else "Khatian No:"
        canvas.drawText("$typeLabel ${result.searchNumber}", 310f, 155f, paint)

        var y = 205f
        paint.color = Color.parseColor("#0F3E21")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Ownership & Record of Rights Details", 30f, y, paint)

        y += 15f
        paint.color = Color.parseColor("#15803D")
        canvas.drawRect(30f, y, 565f, y + 25f, paint)

        paint.color = Color.WHITE
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("No.", 40f, y + 16f, paint)
        canvas.drawText("Raiyat Name", 80f, y + 16f, paint)
        canvas.drawText("Guardian Name", 240f, y + 16f, paint)
        canvas.drawText("Classification", 380f, y + 16f, paint)
        canvas.drawText("Share", 460f, y + 16f, paint)
        canvas.drawText("Area (Acre)", 510f, y + 16f, paint)

        y += 25f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 9.5f

        for ((index, owner) in result.owners.withIndex()) {
            if (index % 2 == 1) {
                paint.color = Color.parseColor("#F1F5F9")
                canvas.drawRect(30f, y, 565f, y + 22f, paint)
            }

            paint.color = Color.parseColor("#1E293B")
            canvas.drawText((index + 1).toString(), 40f, y + 15f, paint)
            canvas.drawText(owner.ownerName.take(24), 80f, y + 15f, paint)
            canvas.drawText(owner.guardianName.take(20), 240f, y + 15f, paint)
            canvas.drawText(owner.landClassification, 380f, y + 15f, paint)
            canvas.drawText(owner.share, 460f, y + 15f, paint)
            canvas.drawText(owner.areaAcre, 510f, y + 15f, paint)

            y += 22f
            if (y > 750f) break
        }

        y += 15f
        paint.color = Color.parseColor("#15803D")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Total Recorded Area: ${result.totalAreaAcre} Acre", 30f, y, paint)

        y += 40f
        paint.color = Color.parseColor("#FEF3C7")
        canvas.drawRect(30f, y, 565f, y + 45f, paint)
        paint.color = Color.parseColor("#92400E")
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Notice: This is a digital computer-generated copy generated through BanglarBhumi Pro.", 40f, y + 18f, paint)
        canvas.drawText("For court proceedings or official registration, certified copies from BL&LRO are required.", 40f, y + 32f, paint)

        document.finishPage(page)

        val outputDir = File(context.cacheDir, "land_records")
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "LandRecord_${result.searchNumber.replace('/', '_')}_${System.currentTimeMillis()}.pdf")
        val stream = FileOutputStream(file)
        document.writeTo(stream)
        stream.close()
        document.close()

        file
    }
}
