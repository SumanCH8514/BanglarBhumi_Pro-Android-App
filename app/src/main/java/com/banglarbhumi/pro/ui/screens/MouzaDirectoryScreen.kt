package com.banglarbhumi.pro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

@Composable
fun MouzaDirectoryScreen(
    viewModel: MainViewModel,
    onSelectMouza: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    val searchResults by viewModel.mouzaSearchResults.collectAsState()
    val isSearching by viewModel.isSearchingMouzas.collectAsState()
    var queryText by remember { mutableStateOf("") }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBengali) "মৌজা ও জে.এল ডিরেক্টরি" else "Mouza & JL Directory",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = if (isBengali) "পশ্চিমবঙ্গের যেকোনো মৌজার নাম বা JL নম্বর দিয়ে সন্ধান করুন" else "Search across 40,000+ mouzas by name or JL number",
                    fontSize = 12.sp,
                    color = Slate500
                )

                OutlinedTextField(
                    value = queryText,
                    onValueChange = {
                        queryText = it
                        viewModel.searchMouzasGlobal(it)
                    },
                    placeholder = { Text(if (isBengali) "মৌজা বা JL নং লিখুন (উদাঃ 055, শ্যামপুর)..." else "Type name or JL no...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald700) },
                    trailingIcon = {
                        if (queryText.isNotEmpty()) {
                            IconButton(onClick = {
                                queryText = ""
                                viewModel.searchMouzasGlobal("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Emerald700
                    )
                )

                if (isSearching) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Emerald700)
                }
            }
        }

        if (searchResults.isEmpty() && queryText.length >= 2 && !isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBengali) "কোনো মৌজা পাওয়া যায়নি" else "No matching mouza found",
                    color = Slate400,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(searchResults) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectFromGlobalSearch(item)
                                onSelectMouza()
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = item.mouzaNameEn,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    if (item.mouzaNameBn.isNotEmpty()) {
                                        Text(
                                            text = "(${item.mouzaNameBn})",
                                            fontSize = 13.sp,
                                            color = Slate600
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = Emerald100,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "JL ${item.mouzaCode}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald800,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "${item.blockNameEn} • ${item.districtNameEn}",
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            Icon(
                                Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
