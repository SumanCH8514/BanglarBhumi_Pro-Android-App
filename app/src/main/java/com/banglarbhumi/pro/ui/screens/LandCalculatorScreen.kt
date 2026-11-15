package com.banglarbhumi.pro.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import java.util.Locale

enum class LandUnit(val en: String, val bn: String, val toSqFeet: Double) {
    SATAK("Satak / Decimal", "শতক / ডেসিমল", 435.6),
    KATHA("Katha", "কাঠা", 720.0),
    BIGHA("Bigha", "বিঘা", 14400.0),
    CHATAK("Chatak", "ছটাক", 45.0),
    ACRE("Acre", "একর", 43560.0),
    SQ_FEET("Square Feet", "বর্গফুট", 1.0),
    SQ_METER("Square Meter", "বর্গমিটার", 10.7639)
}

data class ConversionItem(
    val unit: LandUnit,
    val titleEn: String,
    val titleBn: String,
    val value: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandCalculatorScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isBengali by viewModel.isBengali.collectAsState()
    var inputValue by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf(LandUnit.SATAK) }
    var unitMenuExpanded by remember { mutableStateOf(false) }

    val numericInput = inputValue.toDoubleOrNull() ?: 0.0
    val totalSqFeet = numericInput * selectedUnit.toSqFeet

    val bigha = totalSqFeet / LandUnit.BIGHA.toSqFeet
    val katha = totalSqFeet / LandUnit.KATHA.toSqFeet
    val chatak = totalSqFeet / LandUnit.CHATAK.toSqFeet
    val satak = totalSqFeet / LandUnit.SATAK.toSqFeet
    val acre = totalSqFeet / LandUnit.ACRE.toSqFeet
    val sqMeters = totalSqFeet / LandUnit.SQ_METER.toSqFeet

    fun fmt(v: Double): String {
        return if (v == 0.0) "0" else String.format(Locale.US, "%,.4f", v).trimEnd('0').trimEnd('.')
    }

    val conversionResults = remember(totalSqFeet, bigha, katha, chatak, satak, acre, sqMeters) {
        listOf(
            ConversionItem(LandUnit.SATAK, "Satak / Decimal", "শতক / ডেসিমল", fmt(satak)),
            ConversionItem(LandUnit.KATHA, "Katha", "কাঠা", fmt(katha)),
            ConversionItem(LandUnit.BIGHA, "Bigha", "বিঘা", fmt(bigha)),
            ConversionItem(LandUnit.CHATAK, "Chatak", "ছটাক", fmt(chatak)),
            ConversionItem(LandUnit.ACRE, "Acre", "একর", fmt(acre)),
            ConversionItem(LandUnit.SQ_FEET, "Square Feet", "বর্গফুট", fmt(totalSqFeet)),
            ConversionItem(LandUnit.SQ_METER, "Square Meter", "বর্গমিটার", fmt(sqMeters))
        )
    }

    val presets = remember {
        listOf(
            Triple("1 Satak", "১ শতক", LandUnit.SATAK),
            Triple("1 Katha", "১ কাঠা", LandUnit.KATHA),
            Triple("1 Bigha", "১ বিঘা", LandUnit.BIGHA),
            Triple("1 Acre", "১ একর", LandUnit.ACRE)
        )
    }

    val formulas = remember(isBengali) {
        if (isBengali) {
            listOf(
                "১ বিঘা = ২০ কাঠা = ১৪,৪০০ বর্গফুট (প্রায় ৩৩.০৬ ডেসিমল)",
                "১ কাঠা = ১৬ ছটাক = ৭২০ বর্গফুট (১.৬৫ ডেসিমল)",
                "১ ছটাক = ৪৫ বর্গফুট",
                "১ ডেসিমল / শতক = ৪৩৫.৬ বর্গফুট = ০.০১ একর",
                "১ একর = ১০০ শতক = ৩.০২৫ বিঘা"
            )
        } else {
            listOf(
                "1 Bigha = 20 Katha = 14,400 Sq. Ft (~33.06 Decimals)",
                "1 Katha = 16 Chatak = 720 Sq. Ft (1.65 Decimals)",
                "1 Chatak = 45 Sq. Ft",
                "1 Decimal / Satak = 435.6 Sq. Ft = 0.01 Acre",
                "1 Acre = 100 Satak = 3.025 Bighas"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Converter Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with title, subtitle, and calculator icon badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBengali) "বাংলার ভূমি পরিমাপ ক্যালকুলেটর" else "Bengal Land Unit Converter",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBengali) "শতক, কাঠা, বিঘা ও একরের সঠিক রূপান্তর" else "Accurate Bengal standard area conversion",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = Color(0xFF0F5132),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Two inputs: Area Value and Unit Dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = { Text(if (isBengali) "জমির পরিমাণ" else "Area Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald600,
                            unfocusedBorderColor = Slate300
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    ExposedDropdownMenuBox(
                        expanded = unitMenuExpanded,
                        onExpandedChange = { unitMenuExpanded = !unitMenuExpanded },
                        modifier = Modifier.weight(1.3f)
                    ) {
                        OutlinedTextField(
                            value = if (isBengali) selectedUnit.bn else selectedUnit.en,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "একক" else "Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenuExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Emerald600,
                                unfocusedBorderColor = Slate300
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = unitMenuExpanded,
                            onDismissRequest = { unitMenuExpanded = false }
                        ) {
                            LandUnit.values().forEach { unit ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (isBengali) unit.bn else unit.en,
                                            fontWeight = if (selectedUnit == unit) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedUnit == unit) Emerald800 else Slate900
                                        )
                                    },
                                    onClick = {
                                        selectedUnit = unit
                                        unitMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // PRESETS Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBengali) "প্রিসেটস:" else "PRESETS:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate400,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presets.forEach { (enLabel, bnLabel, unit) ->
                            val isSelectedPreset = selectedUnit == unit && inputValue == "1"
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelectedPreset) Color(0xFFE2E8F0) else Slate100,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        inputValue = "1"
                                        selectedUnit = unit
                                    }
                            ) {
                                Text(
                                    text = if (isBengali) bnLabel else enLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate700,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Converted Results Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate200),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header with Swap Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "রূপান্তরিত ফলাফল" else "Converted Results",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Result list rows
                conversionResults.forEach { item ->
                    val isHighlighted = selectedUnit == item.unit
                    val displayTitle = if (isBengali) item.titleBn else item.titleEn

                    ConversionRow(
                        title = displayTitle,
                        value = item.value,
                        highlight = isHighlighted,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(displayTitle, item.value)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "$displayTitle copied: ${item.value}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // 3. Formula Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF7F0)),
            border = BorderStroke(1.dp, Color(0xFFC7EBD1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Emerald800,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBengali) "পশ্চিমবঙ্গের প্রচলিত ভূমি পরিমাপ সূত্র" else "Standard Bengal Land Units Formula",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                formulas.forEach { formula ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "•",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = formula,
                            fontSize = 12.5.sp,
                            color = Slate800,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversionRow(
    title: String,
    value: String,
    highlight: Boolean,
    onCopy: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCopy() },
        shape = RoundedCornerShape(12.dp),
        color = if (highlight) Color(0xFFE8F8F0) else Slate50,
        border = BorderStroke(
            width = if (highlight) 1.dp else 0.5.dp,
            color = if (highlight) Color(0xFFA7F3D0) else Slate200
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (highlight) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Emerald600)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
                    color = Slate900
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy",
                        tint = if (highlight) Emerald700 else Slate400,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
