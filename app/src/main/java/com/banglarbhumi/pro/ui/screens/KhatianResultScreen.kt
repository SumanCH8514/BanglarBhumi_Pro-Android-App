package com.banglarbhumi.pro.ui.screens

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.banglarbhumi.pro.data.model.LandRecordResult
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import com.banglarbhumi.pro.util.PdfExporter
import kotlinx.coroutines.launch

/**
 * Dedicated Result Page for Khatian numbers.
 * Displays clean, 100% full-width Khatian summary and Plot distributions
 * without shrinkage or table width constraints.
 */
@Composable
fun KhatianResultScreen(
    result: LandRecordResult,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isBengali by viewModel.isBengali.collectAsState()
    var isExportingPdf by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate100)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Navigation & Action Header (Matching exact look)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.clearSearchResult() },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Slate50,
                            contentColor = Slate800
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate300)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Emerald800
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBengali) "নতুন সন্ধান" else "Search Again",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    // Location / Record Tag (Expanded and perfectly laid out)
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp, end = 4.dp)
                    ) {
                        Text(
                            text = formatLocationHierarchy(result.districtName, result.blockName, result.mouzaName, isBengali),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isBengali) "খতিয়ান: ${result.searchNumber}" else "Khatian: ${result.searchNumber}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Main Document View (Full-width 100% responsive for Khatian records)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            settings.defaultTextEncodingName = "UTF-8"
                            settings.textZoom = 100
                            setInitialScale(100)
                            webViewClient = WebViewClient()
                            webViewInstance = this
                            loadDataWithBaseURL(
                                "https://banglarbhumi.gov.in/BanglarBhumi/",
                                result.rawHtml,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    },
                    update = { wv ->
                        webViewInstance = wv
                        wv.settings.textZoom = 100
                        wv.setInitialScale(100)
                        wv.loadDataWithBaseURL(
                            "https://banglarbhumi.gov.in/BanglarBhumi/",
                            result.rawHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating "SAVE PDF" Button at Bottom Right (Matching screenshot)
        Button(
            onClick = {
                val wv = webViewInstance
                if (wv != null) {
                    try {
                        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                        val jobName = "BanglarBhumi_KHATIAN_${result.searchNumber}"
                        val printAdapter = wv.createPrintDocumentAdapter(jobName)
                        printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
                    } catch (_: Exception) {
                        coroutineScope.launch {
                            try {
                                isExportingPdf = true
                                val pdfFile = PdfExporter.generateLandRecordPdf(context, result)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    pdfFile
                                )
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Land Record PDF"))
                            } catch (ex: Exception) {
                                Toast.makeText(context, "Export failed: ${ex.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isExportingPdf = false
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D47A1),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 8.dp),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp)
        ) {
            if (isExportingPdf) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "GENERATING...", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            } else {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SAVE PDF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
