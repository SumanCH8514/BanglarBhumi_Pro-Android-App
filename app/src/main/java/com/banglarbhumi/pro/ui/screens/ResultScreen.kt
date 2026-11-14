package com.banglarbhumi.pro.ui.screens

import androidx.compose.runtime.Composable
import com.banglarbhumi.pro.data.model.LandRecordResult
import com.banglarbhumi.pro.data.model.SearchType
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

/**
 * Main result router that routes to dedicated pages for Plot/Dag number and Khatian number.
 */
@Composable
fun ResultScreen(
    result: LandRecordResult,
    viewModel: MainViewModel
) {
    if (result.searchType == SearchType.PLOT) {
        PlotResultScreen(result = result, viewModel = viewModel)
    } else {
        KhatianResultScreen(result = result, viewModel = viewModel)
    }
}
