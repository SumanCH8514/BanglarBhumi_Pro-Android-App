package com.banglarbhumi.pro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.data.model.SearchType
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import com.banglarbhumi.pro.util.PdfStorageManager
import com.banglarbhumi.pro.util.SavedPdfItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedRecordsScreen(
    viewModel: MainViewModel,
    @Suppress("UNUSED_PARAMETER") onViewRecord: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali by viewModel.isBengali.collectAsState()
    val savedPdfs by viewModel.savedPdfs.collectAsState()
    val starredPdfs = remember(savedPdfs) { savedPdfs.filter { it.isStarred } }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadSavedPdfs()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. NON-FIXED "Saved Land Records" Banner Card - SCROLLS AWAY WITH CONTENT
            item(key = "header_banner") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBengali) "সংরক্ষিত জমির রেকর্ড" else "Saved Land Records",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = if (isBengali)
                                        "ডাউনলোড করা PDF ফাইল ও সংরক্ষিত রেকর্ড"
                                    else
                                        "Downloaded PDF files and saved offline.",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewModel.loadSavedPdfs()
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "তালিকা রিফ্রেশ করা হয়েছে" else "List refreshed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Emerald800
                                )
                            }
                        }
                    }
                }
            }

            // 2. STICKY HEADER: Downloaded PDFs and Bookmarks Row - REMAINS FIXED WHILE SCROLLING
            stickyHeader(key = "tabs_row") {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            contentColor = Emerald800,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 },
                                text = {
                                    Text(
                                        text = if (isBengali) "ডাউনলোড PDF (${savedPdfs.size})" else "Downloaded PDFs (${savedPdfs.size})",
                                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.5.sp
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 },
                                text = {
                                    Text(
                                        text = if (isBengali) "স্টার (${starredPdfs.size})" else "Starred (${starredPdfs.size})",
                                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.5.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // 3. Tab Content
            if (selectedTabIndex == 0) {
                // DOWNLOADED PDF LIST
                if (savedPdfs.isEmpty()) {
                    item(key = "empty_pdfs") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444).copy(alpha = 0.6f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    text = if (isBengali) "কোনো ডাউনলোড করা PDF পাওয়া যায়নি" else "No Downloaded PDFs Found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Text(
                                    text = if (isBengali)
                                        "যেকোনো দাগ বা খতিয়ানের অনুসন্ধান করার পর 'SAVE PDF' বাটনে চাপলে ফাইলটি সরাসরি আপনার ফোনে সেভ হবে এবং এখানে দেখতে পাবেন।"
                                    else
                                        "Search any Plot or Khatian record and tap 'SAVE PDF' to generate and save it directly on your device.",
                                    fontSize = 13.sp,
                                    color = Slate500,
                                    lineHeight = 18.sp
                                )

                                Surface(
                                    color = Slate100,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Folder,
                                            contentDescription = null,
                                            tint = Slate600,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Download/SumanOnline Web Services/BanglarBhoomi Pro",
                                            fontSize = 11.sp,
                                            color = Slate700,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(savedPdfs, key = { it.fileName }) { item ->
                        SavedPdfListItem(
                            item = item,
                            isBengali = isBengali,
                            onToggleStar = {
                                viewModel.toggleStarPdf(item)
                                Toast.makeText(
                                    context,
                                    if (!item.isStarred)
                                        (if (isBengali) "স্টার তালিকায় যুক্ত করা হয়েছে" else "Added to Starred")
                                    else
                                        (if (isBengali) "স্টার তালিকা থেকে সরানো হয়েছে" else "Removed from Starred"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onView = {
                                PdfStorageManager.viewPdf(context, item)
                            },
                            onShare = {
                                PdfStorageManager.sharePdf(context, item)
                            },
                            onDelete = {
                                viewModel.deleteSavedPdf(item)
                                Toast.makeText(
                                    context,
                                    if (isBengali) "ফাইলটি মুছে ফেলা হয়েছে" else "PDF deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            } else {
                // STARRED PDF LIST
                if (starredPdfs.isEmpty()) {
                    item(key = "empty_starred") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    text = if (isBengali) "কোনো স্টার করা PDF নেই" else "No Starred PDFs Found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                                Text(
                                    text = if (isBengali)
                                        "যেকোনো ডাউনলোড করা PDF-এর পাশে থাকা স্টার (★) আইকনে ক্লিক করে এখানে যুক্ত করতে পারেন যাতে দ্রুত খুঁজে পাওয়া যায়।"
                                    else
                                        "Tap the star (★) icon next to any downloaded PDF to add it here for quick offline access.",
                                    fontSize = 13.sp,
                                    color = Slate500,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    items(starredPdfs, key = { "starred_${it.fileName}" }) { item ->
                        SavedPdfListItem(
                            item = item,
                            isBengali = isBengali,
                            onToggleStar = {
                                viewModel.toggleStarPdf(item)
                                Toast.makeText(
                                    context,
                                    if (!item.isStarred)
                                        (if (isBengali) "স্টার তালিকায় যুক্ত করা হয়েছে" else "Added to Starred")
                                    else
                                        (if (isBengali) "স্টার তালিকা থেকে সরানো হয়েছে" else "Removed from Starred"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onView = {
                                PdfStorageManager.viewPdf(context, item)
                            },
                            onShare = {
                                PdfStorageManager.sharePdf(context, item)
                            },
                            onDelete = {
                                viewModel.deleteSavedPdf(item)
                                Toast.makeText(
                                    context,
                                    if (isBengali) "ফাইলটি মুছে ফেলা হয়েছে" else "PDF deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedPdfListItem(
    item: SavedPdfItem,
    isBengali: Boolean,
    onToggleStar: () -> Unit,
    onView: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Refined Type Badge on left + Star & Delete Buttons on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isPlot = item.searchType == SearchType.PLOT
                Surface(
                    color = if (isPlot) Color(0xFFF0FDF4) else Color(0xFFFFF7ED),
                    border = BorderStroke(1.dp, if (isPlot) Color(0xFFBBF7D0) else Color(0xFFFED7AA)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = if (isPlot) Color(0xFF166534) else Color(0xFFC2410C),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isPlot)
                                (if (isBengali) "দাগ নম্বর রেকর্ড" else "Plot Record")
                            else
                                (if (isBengali) "খতিয়ান নম্বর রেকর্ড" else "Khatian Record"),
                            color = if (isPlot) Color(0xFF166534) else Color(0xFF9A3412),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        )
                    }
                }

                // Right Action Buttons: Star beside Delete
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star Button
                    Surface(
                        onClick = onToggleStar,
                        shape = RoundedCornerShape(50),
                        color = if (item.isStarred) Color(0xFFFEF3C7) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (item.isStarred) Color(0xFFFCD34D) else Color(0xFFE2E8F0)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (item.isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = if (item.isStarred) "Starred" else "Not Starred",
                                tint = if (item.isStarred) Color(0xFFD97706) else Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Delete Button: Soft circular container with ripple
                    Surface(
                        onClick = onDelete,
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFEE2E2),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            // Primary Title: Mouza and Dag/Khatian - no (clean typography without duplicate wraps)
            Text(
                text = item.getDisplayTitle(isBengali),
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900,
                lineHeight = 22.sp
            )

            // Location Hierarchy (District > Block) with Map Pin icon
            val distClean = item.districtName.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
            val blkClean = item.blockName.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
            if (distClean.isNotBlank() && blkClean.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "$distClean > $blkClean",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }

            // Metadata Row: Time & File Size inside a clean, subtle pill
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = item.formattedDate,
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Text(
                        text = item.formattedSize,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Bottom Actions Row: Exact same position & layout (View PDF on left, Share PDF on right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // VIEW OPTION
                Button(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D47A1),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    contentPadding = PaddingValues(vertical = 9.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "পিডিএফ দেখুন" else "View PDF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // SHARE OPTION
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF15803D)
                    ),
                    border = BorderStroke(1.2.dp, Color(0xFF15803D)),
                    contentPadding = PaddingValues(vertical = 9.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "শেয়ার করুন" else "Share PDF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
