package com.banglarbhumi.pro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.data.model.SavedLandRecord
import com.banglarbhumi.pro.data.model.SearchType
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedRecordsScreen(
    viewModel: MainViewModel,
    onViewRecord: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    val savedRecords by viewModel.savedRecords.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBengali) "সংরক্ষিত জমির রেকর্ড" else "Saved Land Records",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = if (isBengali) "আপনার সংরক্ষিত খতিয়ান ও দাগের তালিকা" else "Offline saved Khatians and Plot records",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${savedRecords.size}",
                            color = Emerald800,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

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
                        Icons.Default.Bookmark,
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
                        text = if (isBengali) "যেকোনো দাগ বা খতিয়ানের বিবরণী অনুসন্ধান করার পর বুকমার্ক বাটনে ক্লিক করে এখানে সংরক্ষণ করতে পারেন।" else "Search any Plot or Khatian record and tap the bookmark icon to save it here for quick offline access.",
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

            Divider(color = Slate200, thickness = 0.5.dp)

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