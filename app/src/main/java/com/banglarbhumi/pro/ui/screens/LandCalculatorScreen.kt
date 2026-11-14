package com.banglarbhumi.pro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandCalculatorScreen(viewModel: MainViewModel) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBengali) "বাংলার ভূমি পরিমাপ ক্যালকুলেটর" else "Bengal Land Unit Converter",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = if (isBengali) "শতক, কাঠা, বিঘা ও একরের সঠিক রূপান্তর" else "Accurate Bengal standard area conversion",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Emerald800,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = { Text(if (isBengali) "জমির পরিমাণ" else "Area Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f)
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
                            label = { Text(if (isBengali) "পরিমাপ একক" else "Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenuExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = unitMenuExpanded,
                            onDismissRequest = { unitMenuExpanded = false }
                        ) {
                            LandUnit.values().forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(if (isBengali) unit.bn else unit.en) },
                                    onClick = {
                                        selectedUnit = unit
                                        unitMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "রূপান্তরিত পরিমাপ ফলাফল" else "Converted Results",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Icon(Icons.Default.SwapVert, contentDescription = null, tint = Emerald700)
                }

                ConversionRow(
                    title = if (isBengali) "শতক / ডেসিমল (Satak / Decimal)" else "Satak / Decimal",
                    value = fmt(satak),
                    highlight = selectedUnit == LandUnit.SATAK
                )
                ConversionRow(
                    title = if (isBengali) "কাঠা (Katha)" else "Katha",
                    value = fmt(katha),
                    highlight = selectedUnit == LandUnit.KATHA
                )
                ConversionRow(
                    title = if (isBengali) "বিঘা (Bigha)" else "Bigha",
                    value = fmt(bigha),
                    highlight = selectedUnit == LandUnit.BIGHA
                )
                ConversionRow(
                    title = if (isBengali) "ছটাক (Chatak)" else "Chatak",
                    value = fmt(chatak),
                    highlight = selectedUnit == LandUnit.CHATAK
                )
                ConversionRow(
                    title = if (isBengali) "একর (Acre)" else "Acre",
                    value = fmt(acre),
                    highlight = selectedUnit == LandUnit.ACRE
                )
                ConversionRow(
                    title = if (isBengali) "বর্গফুট (Sq. Feet)" else "Square Feet",
                    value = fmt(totalSqFeet),
                    highlight = selectedUnit == LandUnit.SQ_FEET
                )
                ConversionRow(
                    title = if (isBengali) "বর্গমিটার (Sq. Meter)" else "Square Meter",
                    value = fmt(sqMeters),
                    highlight = selectedUnit == LandUnit.SQ_METER
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Emerald50),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBengali) "পশ্চিমবঙ্গের প্রচলিত ভূমি পরিমাপ সূত্র" else "Standard Bengal Land Units Formula",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald900
                )
                Text(
                    text = if (isBengali)
                        "• ১ বিঘা = ২০ কাঠা = ১৪,৪০০ বর্গফুট (প্রায় ৩৩ ডেসিমল)\n• ১ কাঠা = ১৬ ছটাক = ৭২০ বর্গফুট (১.৬৫ ডেসিমল)\n• ১ ছটাক = ২০ গণ্ডা = ৪৫ বর্গফুট\n• ১ ডেসিমল / শতক = ৪৩৫.৬ বর্গফুট = ১০০ বর্গকড়ি\n• ১ একর = ১০০ শতক = ৩ বিঘা ৮ ছটাক (৩.০২৫ বিঘা)"
                    else
                        "• 1 Bigha = 20 Katha = 14,400 Sq. Ft (~33.06 Decimals)\n• 1 Katha = 16 Chatak = 720 Sq. Ft (1.65 Decimals)\n• 1 Chatak = 45 Sq. Ft\n• 1 Decimal / Satak = 435.6 Sq. Ft = 0.01 Acre\n• 1 Acre = 100 Satak = 3.025 Bighas",
                    fontSize = 12.sp,
                    color = Emerald800,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ConversionRow(
    title: String,
    value: String,
    highlight: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (highlight) Emerald50 else Slate50
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
                color = if (highlight) Emerald900 else Slate700
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlight) Emerald800 else Slate900
            )
        }
    }
}
