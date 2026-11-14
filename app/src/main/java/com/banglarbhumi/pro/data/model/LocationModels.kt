package com.banglarbhumi.pro.data.model

import com.google.gson.annotations.SerializedName

data class LocationDatabase(
    @SerializedName("version") val version: Int,
    @SerializedName("districts") val districts: List<District>
)

data class District(
    @SerializedName("code") val code: String,
    @SerializedName("en") val en: String,
    @SerializedName("bn") val bn: String,
    @SerializedName("blocks") val blocks: List<Block>?
)

data class Block(
    @SerializedName("code") val code: String,
    @SerializedName("en") val en: String,
    @SerializedName("bn") val bn: String,
    @SerializedName("mouzas") val mouzas: List<Mouza>?
)

data class Mouza(
    @SerializedName("code") val code: String,
    @SerializedName("en") val en: String,
    @SerializedName("bn") val bn: String,
    @SerializedName("mid") val mid: String?
)

data class MouzaSearchResult(
    val districtCode: String,
    val districtNameEn: String,
    val districtNameBn: String,
    val blockCode: String,
    val blockNameEn: String,
    val blockNameBn: String,
    val mouzaCode: String,
    val mouzaNameEn: String,
    val mouzaNameBn: String
)
