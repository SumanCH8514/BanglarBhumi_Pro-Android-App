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
import com.banglarbhumi.pro.util.PdfStorageManager
import kotlinx.coroutines.launch

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

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp, end = 4.dp)
                    ) {
                        Text(
                            text = viewModel.getLocationHierarchy(result, isBengali),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.End)
                        )
                        Text(
                            text = if (isBengali) "খতিয়ান: ${result.searchNumber}" else "Khatian: ${result.searchNumber}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                }
            }

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
                        wv.settings.loadWithOverviewMode = true
                        wv.settings.useWideViewPort = true
                        wv.settings.textZoom = 100
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

        Button(
            onClick = {
                if (!isExportingPdf) {
                    coroutineScope.launch {
                        try {
                            isExportingPdf = true
                            val saveResult = PdfStorageManager.savePdf(context, webViewInstance, result)
                            if (saveResult.isSuccess) {
                                val file = saveResult.getOrThrow()
                                viewModel.loadSavedPdfs()
                                val msg = if (isBengali)
                                    "পিডিএফ সফলভাবে সংরক্ষিত হয়েছে:\n${file.name}"
                                else
                                    "PDF saved to Downloads:\n${file.name}"
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            } else {
                                val err = saveResult.exceptionOrNull()?.message ?: "Save failed"
                                Toast.makeText(context, "Error saving PDF: $err", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isExportingPdf = false
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
                Text(
                    text = if (isBengali) "সংরক্ষণ হচ্ছে..." else "SAVING...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "PDF সেভ করুন" else "SAVE PDF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
