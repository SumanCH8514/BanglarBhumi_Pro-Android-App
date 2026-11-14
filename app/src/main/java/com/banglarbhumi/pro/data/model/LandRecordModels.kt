package com.banglarbhumi.pro.data.model

enum class SearchType {
    PLOT,
    KHATIAN
}

data class KhatianSummary(
    val khatianNo: String = "",
    val ownerName: String = "",
    val guardianName: String = "",
    val rayatType: String = "",
    val address: String = "",
    val totalAreaAcre: String = "",
    val plotCount: String = "",
    val bataKhatianNo: String = ""
)

data class PlotSummary(
    val plotNo: String = "",
    val landClassification: String = "",
    val totalAreaAcre: String = "",
    val bataPlotNo: String = ""
)

data class LandOwner(
    val khatianOrPlotNo: String,
    val ownerName: String,
    val guardianName: String,
    val landClassification: String,
    val share: String,
    val areaAcre: String,
    val remarks: String = "",
    val possessor: String = "",
    val rayatType: String = "",
    val address: String = ""
)

data class LandRecordResult(
    val searchType: SearchType,
    val searchNumber: String,
    val districtCode: String,
    val districtName: String,
    val blockCode: String,
    val blockName: String,
    val mouzaCode: String,
    val mouzaName: String,
    val owners: List<LandOwner>,
    val totalAreaAcre: String,
    val rawHtml: String,
    val source: String,
    val liveTimestamp: String = "",
    val jlNumber: String = "",
    val thanaName: String = "",
    val khatianSummary: KhatianSummary? = null,
    val plotSummary: PlotSummary? = null,
    val queryTimestamp: Long = System.currentTimeMillis()
)

data class SavedLandRecord(
    val id: String,
    val savedTimestamp: Long,
    val record: LandRecordResult
)
