package com.banglarbhumi.pro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.data.model.SavedLandRecord
import com.banglarbhumi.pro.data.model.SearchType
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import com.banglarbhumi.pro.util.PdfStorageManager
import com.banglarbhumi.pro.util.SavedPdfItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedRecordsScreen(
    viewModel: MainViewModel,
    onViewRecord: () -> Unit
) {
    val context = LocalContext.current
    val isBengali by viewModel.isBengali.collectAsState()
    val savedRecords by viewModel.savedRecords.collectAsState()
    val savedPdfs by viewModel.savedPdfs.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadSavedPdfs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Card with Header and Refresh
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                "Downloaded PDF files and saved offline records",
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

        // Segmented Tabs: Downloaded PDFs vs Bookmarks
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
                        fontSize = 13.sp
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = if (isBengali) "বুকমার্ক (${savedRecords.size})" else "Bookmarks (${savedRecords.size})",
                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            )
        }

        // Tab Content
        if (selectedTabIndex == 0) {
            // DOWNLOADED PDF LIST
            if (savedPdfs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedPdfs, key = { it.fileName }) { item ->
                        SavedPdfListItem(
                            item = item,
                            isBengali = isBengali,
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
        } else {
            // BOOKMARKS LIST
            if (savedRecords.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = Slate300,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = if (isBengali) "কোনো রেকর্ড সংরক্ষিত নেই" else "No saved records found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                        Text(
                            text = if (isBengali)
                                "যেকোনো দাগ বা খতিয়ানের বিবরণী অনুসন্ধান করার পর বুকমার্ক বাটনে ক্লিক করে এখানে সংরক্ষণ করতে পারেন।"
                            else
                                "Search any Plot or Khatian record and tap the bookmark icon to save it here for quick offline access.",
                            fontSize = 13.sp,
                            color = Slate500,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedRecords, key = { it.id }) { item ->
                        SavedRecordItem(
                            saved = item,
                            isBengali = isBengali,
                            onView = {
                                viewModel.showRecord(item.record)
                                onViewRecord()
                            },
                            onDelete = {
                                viewModel.deleteSavedRecord(item.id)
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
    onView: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Type Pill (Dag vs Khatian) + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (item.searchType == SearchType.PLOT) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = if (item.searchType == SearchType.PLOT) Color(0xFF2E7D32) else Color(0xFFF57F17),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (item.searchType == SearchType.PLOT)
                                (if (isBengali) "দাগ নম্বর রেকর্ড" else "Plot Record")
                            else
                                (if (isBengali) "খতিয়ান রেকর্ড" else "Khatian Record"),
                            color = if (item.searchType == SearchType.PLOT) Color(0xFF1B5E20) else Color(0xFFE65100),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Name: Mouja and dag or Khatian - no
            Text(
                text = item.getDisplayTitle(isBengali),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            // Location Hierarchy if available (District > Block)
            if (item.districtName.isNotBlank() && item.blockName.isNotBlank()) {
                val distClean = item.districtName.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
                val blkClean = item.blockName.replace(Regex("\\s*\\([^)]*\\)"), "").trim()
                Text(
                    text = "$distClean > $blkClean",
                    fontSize = 12.sp,
                    color = Slate600
                )
            }

            // Metadata row: Date and File size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.formattedDate} • ${item.formattedSize}",
                    fontSize = 11.5.sp,
                    color = Slate500
                )
            }

            HorizontalDivider(color = Slate200, thickness = 0.5.dp)

            // Bottom Actions Row: View Option and Share Option
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
                    contentPadding = PaddingValues(vertical = 8.dp)
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
                    border = BorderStroke(1.dp, Color(0xFF15803D)),
                    contentPadding = PaddingValues(vertical = 8.dp)
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

@Composable
private fun SavedRecordItem(
    saved: SavedLandRecord,
    isBengali: Boolean,
    onView: () -> Unit,
    onDelete: () -> Unit
) {
    val record = saved.record
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(saved.savedTimestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (record.searchType == SearchType.PLOT) Emerald100 else GoldLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (record.searchType == SearchType.PLOT)
                            (if (isBengali) "দাগ নম্বর: ${record.searchNumber}" else "Plot: ${record.searchNumber}")
                        else
                            (if (isBengali) "খতিয়ান: ${record.searchNumber}" else "Khatian: ${record.searchNumber}"),
                        color = if (record.searchType == SearchType.PLOT) Emerald800 else GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444)
                    )
                }
            }

            Text(
                text = "${record.districtName} > ${record.blockName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate800
            )

            Text(
                text = "মৌজা: ${record.mouzaName}",
                fontSize = 12.sp,
                color = Slate600
            )

            HorizontalDivider(color = Slate200, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${record.owners.size} জন অংশীদার • ${record.totalAreaAcre} একর",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Emerald800
                )

                Text(
                    text = dateString,
                    fontSize = 11.sp,
                    color = Slate400
                )
            }
        }
    }
}
