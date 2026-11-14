package com.banglarbhumi.pro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.banglarbhumi.pro.data.model.Block
import com.banglarbhumi.pro.data.model.District
import com.banglarbhumi.pro.data.model.Mouza
import com.banglarbhumi.pro.data.model.SearchType
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(viewModel: MainViewModel) {
    val isBengali by viewModel.isBengali.collectAsState()
    val districts by viewModel.districts.collectAsState()
    val blocks by viewModel.blocks.collectAsState()
    val mouzas by viewModel.mouzas.collectAsState()

    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedBlock by viewModel.selectedBlock.collectAsState()
    val selectedMouza by viewModel.selectedMouza.collectAsState()

    val searchType by viewModel.searchType.collectAsState()
    val searchNumber by viewModel.searchNumber.collectAsState()
    val bataNumber by viewModel.bataNumber.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isPortalLoggedIn by viewModel.isPortalLoggedIn.collectAsState()
    val loggedInUsername by viewModel.loggedInPortalUsername.collectAsState()
    val citizenName by viewModel.citizenName.collectAsState()

    var showDistrictDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showMouzaDialog by remember { mutableStateOf(false) }

    val bataFocusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBengali) "খতিয়ান ও দাগের তথ্য" else "Know Your Property",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = if (isBengali) "পশ্চিমবঙ্গ সরকারের ভূমি রেকর্ড পোর্টাল" else "West Bengal Land Records Portal",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }

                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text(
                            text = "LIVE",
                            color = Emerald800,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Portal Session Connection Banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            if (isPortalLoggedIn) {
                                viewModel.logoutPortal()
                            } else {
                                viewModel.openPortalLoginDialog()
                            }
                        },
                    color = if (isPortalLoggedIn) Emerald50 else Slate100,
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = if (isPortalLoggedIn) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isPortalLoggedIn) Emerald700 else Slate600,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isPortalLoggedIn) {
                                    val u = loggedInUsername.orEmpty()
                                    if (citizenName.isNotBlank() && u.isNotBlank()) {
                                        if (isBengali) "পোর্টাল: $citizenName ($u)" else "Portal: $citizenName ($u)"
                                    } else if (citizenName.isNotBlank()) {
                                        if (isBengali) "পোর্টাল: $citizenName" else "Portal: $citizenName"
                                    } else if (u.isNotBlank()) {
                                        if (isBengali) "পোর্টাল ইউজার: $u" else "Portal User: $u"
                                    } else {
                                        if (isBengali) "পোর্টাল সংযুক্ত" else "Portal Connected"
                                    }
                                } else {
                                    if (isBengali) "সিটিজেন লগইন (পোর্টালে কানেক্ট করুন)" else "Citizen Login (Connect portal)"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPortalLoggedIn) Emerald900 else Slate700
                            )
                        }

                        Text(
                            text = if (isPortalLoggedIn)
                                (if (isBengali) "লগআউট" else "Logout")
                            else
                                (if (isBengali) "লগইন" else "Login"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPortalLoggedIn) Color(0xFFB91C1C) else Emerald700
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (searchType == SearchType.PLOT) Emerald700 else Color.Transparent)
                            .clickable { viewModel.setSearchType(SearchType.PLOT) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBengali) "দাগ নম্বর (Plot)" else "Plot / Dag",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (searchType == SearchType.PLOT) Color.White else Slate600
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (searchType == SearchType.KHATIAN) Emerald700 else Color.Transparent)
                            .clickable { viewModel.setSearchType(SearchType.KHATIAN) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBengali) "খতিয়ান নম্বর (Khatian)" else "Khatian No",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (searchType == SearchType.KHATIAN) Color.White else Slate600
                        )
                    }
                }

                DropdownSelector(
                    label = if (isBengali) "১. জেলা নির্বাচন করুন (District)" else "1. Select District",
                    value = selectedDistrict?.let { "${it.en} (${it.bn})" } ?: if (isBengali) "-- জেলা বাছাই করুন --" else "-- Choose District --",
                    icon = Icons.Default.LocationOn,
                    onClick = { showDistrictDialog = true }
                )

                DropdownSelector(
                    label = if (isBengali) "২. ব্লক নির্বাচন করুন (Block)" else "2. Select Block",
                    value = selectedBlock?.let { "${it.en} (${it.bn})" } ?: if (isBengali) "-- ব্লক বাছাই করুন --" else "-- Choose Block --",
                    icon = Icons.Default.Layers,
                    enabled = selectedDistrict != null,
                    onClick = { showBlockDialog = true }
                )

                DropdownSelector(
                    label = if (isBengali) "৩. মৌজা নির্বাচন করুন (Mouza)" else "3. Select Mouza",
                    value = selectedMouza?.let { "JL ${it.code} - ${it.en} (${it.bn})" } ?: if (isBengali) "-- মৌজা বাছাই করুন --" else "-- Choose Mouza --",
                    icon = Icons.Default.Map,
                    enabled = selectedBlock != null,
                    onClick = { showMouzaDialog = true }
                )

                // 4. Dag / Khatian Number & Bata Number Inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchNumber,
                        onValueChange = { viewModel.setSearchNumber(it) },
                        label = {
                            Text(
                                text = if (searchType == SearchType.PLOT) {
                                    if (isBengali) "দাগ নম্বর" else "Plot / Dag No"
                                } else {
                                    if (isBengali) "খতিয়ান নম্বর" else "Khatian No"
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        placeholder = {
                            Text(
                                text = if (searchType == SearchType.PLOT) "59" else "1013",
                                color = Slate400
                            )
                        },
                        modifier = Modifier
                            .weight(1.35f)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        delay(200)
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                bataFocusRequester.requestFocus()
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900,
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = Slate300,
                            focusedLabelColor = Emerald800,
                            unfocusedLabelColor = Slate600
                        )
                    )

                    OutlinedTextField(
                        value = bataNumber,
                        onValueChange = { viewModel.setBataNumber(it) },
                        label = {
                            Text(
                                text = if (searchType == SearchType.PLOT) {
                                    if (isBengali) "বাটা নং" else "Bata No"
                                } else {
                                    if (isBengali) "বাটা নং" else "Bata No"
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        placeholder = {
                            Text(
                                text = if (isBengali) "ঐচ্ছিক (১)" else "e.g. 1",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1.05f)
                            .focusRequester(bataFocusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        delay(200)
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (!isLoading) {
                                    viewModel.performSearch()
                                }
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900,
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = Slate300,
                            focusedLabelColor = Emerald800,
                            unfocusedLabelColor = Slate600
                        )
                    )
                }

                Text(
                    text = if (isBengali) {
                        if (searchType == SearchType.PLOT)
                            "দাগ নম্বর এবং বাটা থাকলে বাটা দাগ (যেমন: ১) লিখুন"
                        else
                            "খতিয়ান নম্বর এবং বাটা থাকলে বাটা খতিয়ান (যেমন: ১) লিখুন"
                    } else {
                        if (searchType == SearchType.PLOT)
                            "Enter Dag No. and Bata if applicable (e.g. 1)"
                        else
                            "Enter Khatian No. and Bata if applicable (e.g. 1)"
                    },
                    fontSize = 11.5.sp,
                    color = Slate500,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            Text(text = errorMessage.orEmpty(), color = Color(0xFF991B1B), fontSize = 12.sp)
                        }
                    }
                }

                Button(
                    onClick = { viewModel.performSearch() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBengali) "রেকর্ড সন্ধান করুন" else "Search Land Record",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showDistrictDialog) {
        SelectionDialog(
            title = if (isBengali) "জেলা নির্বাচন করুন" else "Select District",
            items = districts,
            itemLabel = { "${it.en} - ${it.bn} (${it.code})" },
            onSelect = {
                viewModel.selectDistrict(it)
                showDistrictDialog = false
            },
            onDismiss = { showDistrictDialog = false }
        )
    }

    if (showBlockDialog) {
        SelectionDialog(
            title = if (isBengali) "ব্লক নির্বাচন করুন" else "Select Block",
            items = blocks,
            itemLabel = { "${it.en} (${it.bn})" },
            onSelect = {
                viewModel.selectBlock(it)
                showBlockDialog = false
            },
            onDismiss = { showBlockDialog = false }
        )
    }

    if (showMouzaDialog) {
        SelectionDialog(
            title = if (isBengali) "মৌজা নির্বাচন করুন" else "Select Mouza",
            items = mouzas,
            itemLabel = { "JL ${it.code} - ${it.en} (${it.bn})" },
            onSelect = {
                viewModel.selectMouza(it)
                showMouzaDialog = false
            },
            onDismiss = { showMouzaDialog = false }
        )
    }

    if (isLoading) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                tonalElevation = 6.dp,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(54.dp),
                        color = Color(0xFF00B4D8),
                        strokeWidth = 4.dp,
                        trackColor = Slate100
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isBengali) "অনুগ্রহ করে অপেক্ষা করুন" else "Please wait",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2E5A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isBengali) "জমির রেকর্ড অনুসন্ধান করা হচ্ছে..." else "Searching land record...",
                        fontSize = 14.sp,
                        color = Slate600,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DropdownSelector(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate600)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = enabled) { onClick() },
            color = if (enabled) Slate50 else Slate100,
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = if (enabled) Emerald700 else Slate400, modifier = Modifier.size(18.dp))
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (enabled) Slate900 else Slate400
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate500)
            }
        }
    }
}

@Composable
private fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    var searchFilter by remember { mutableStateOf("") }
    val filteredItems = items.filter { itemLabel(it).contains(searchFilter, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate900)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text("Filter items...", color = Slate400) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate500) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Slate900,
                        unfocusedTextColor = Slate900,
                        focusedBorderColor = Emerald700,
                        unfocusedBorderColor = Slate300,
                        cursorColor = Emerald800
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredItems) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(item) }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(text = itemLabel(item), fontSize = 13.sp, color = Slate800)
                        }
                        Divider(color = Slate100)
                    }
                }
            }
        }
    }
}
