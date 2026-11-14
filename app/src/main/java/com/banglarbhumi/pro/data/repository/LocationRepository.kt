package com.banglarbhumi.pro.data.repository

import android.content.Context
import com.banglarbhumi.pro.data.model.Block
import com.banglarbhumi.pro.data.model.District
import com.banglarbhumi.pro.data.model.LocationDatabase
import com.banglarbhumi.pro.data.model.Mouza
import com.banglarbhumi.pro.data.model.MouzaSearchResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class LocationRepository(private val context: Context) {

    private var cachedDatabase: LocationDatabase? = null
    private val gson = Gson()

    suspend fun getDistricts(): List<District> = withContext(Dispatchers.IO) {
        val db = ensureLoaded()
        db.districts
    }

    suspend fun getBlocks(districtCode: String): List<Block> = withContext(Dispatchers.IO) {
        val db = ensureLoaded()
        val district = db.districts.find { it.code == districtCode }
        district?.blocks ?: emptyList()
    }

    suspend fun getMouzas(districtCode: String, blockCode: String): List<Mouza> = withContext(Dispatchers.IO) {
        val db = ensureLoaded()
        val district = db.districts.find { it.code == districtCode } ?: return@withContext emptyList()
        val cleanCode = if (blockCode.contains("_")) blockCode.split("_")[0] else blockCode
        val block = district.blocks?.find { it.code == cleanCode || it.code == blockCode }
        block?.mouzas ?: emptyList()
    }

    suspend fun searchMouzas(query: String, limit: Int = 30): List<MouzaSearchResult> = withContext(Dispatchers.IO) {
        val q = query.trim().lowercase()
        if (q.length < 2) return@withContext emptyList()

        val db = ensureLoaded()
        val results = mutableListOf<MouzaSearchResult>()

        for (district in db.districts) {
            for (block in district.blocks.orEmpty()) {
                for (mouza in block.mouzas.orEmpty()) {
                    if (
                        mouza.en.lowercase().contains(q) ||
                        mouza.bn.contains(q) ||
                        mouza.code.contains(q)
                    ) {
                        results.add(
                            MouzaSearchResult(
                                districtCode = district.code,
                                districtNameEn = district.en,
                                districtNameBn = district.bn,
                                blockCode = block.code,
                                blockNameEn = block.en,
                                blockNameBn = block.bn,
                                mouzaCode = mouza.code,
                                mouzaNameEn = mouza.en,
                                mouzaNameBn = mouza.bn
                            )
                        )
                        if (results.size >= limit) return@withContext results
                    }
                }
            }
        }

        results
    }

    fun resolveLocationHierarchy(
        districtCode: String,
        blockCode: String,
        mouzaCode: String,
        isBengali: Boolean,
        fallbackDistrict: String = "",
        fallbackBlock: String = "",
        fallbackMouza: String = ""
    ): String {
        try {
            val db = ensureLoaded()
            val cleanDistCode = districtCode.trim()
            val district = db.districts.find {
                it.code == cleanDistCode ||
                it.code.trimStart('0') == cleanDistCode.trimStart('0') ||
                it.en.equals(cleanDistCode, ignoreCase = true) ||
                it.en.equals(fallbackDistrict.substringBefore("[").substringBefore("(").trim(), ignoreCase = true)
            }

            val cleanBlockCode = if (blockCode.contains("_")) blockCode.split("_")[0].trim() else blockCode.trim()
            val block = district?.blocks?.find {
                it.code == cleanBlockCode ||
                it.code == blockCode.trim() ||
                it.code.trimStart('0') == cleanBlockCode.trimStart('0') ||
                cleanBlockCode.endsWith(it.code) ||
                it.en.equals(fallbackBlock.substringBefore("[").substringBefore("(").trim(), ignoreCase = true)
            }

            val cleanMouzaCode = mouzaCode.trim()
            val mouza = block?.mouzas?.find {
                it.code == cleanMouzaCode ||
                it.code.trimStart('0') == cleanMouzaCode.trimStart('0') ||
                it.mid == cleanMouzaCode ||
                it.en.equals(fallbackMouza.substringBefore("[").substringBefore("(").trim(), ignoreCase = true)
            }

            fun cleanFallback(raw: String): String {
                return if (isBengali) {
                    val bn = Regex("""\(([^)]+)\)""").find(raw)?.groupValues?.get(1)?.trim()
                    if (!bn.isNullOrBlank()) bn else raw.substringBefore("[").substringBefore("(").trim()
                } else {
                    raw.substringBefore("[").substringBefore("(").trim().uppercase()
                }
            }

            val d = if (isBengali) (district?.bn?.ifBlank { null } ?: cleanFallback(fallbackDistrict)) else (district?.en?.uppercase() ?: cleanFallback(fallbackDistrict))
            val b = if (isBengali) (block?.bn?.ifBlank { null } ?: cleanFallback(fallbackBlock)) else (block?.en?.uppercase() ?: cleanFallback(fallbackBlock))
            val m = if (isBengali) (mouza?.bn?.ifBlank { null } ?: cleanFallback(fallbackMouza)) else (mouza?.en?.uppercase() ?: cleanFallback(fallbackMouza))

            return "$d > $b > $m"
        } catch (_: Exception) {
            fun clean(raw: String): String {
                return if (isBengali) {
                    val bn = Regex("""\(([^)]+)\)""").find(raw)?.groupValues?.get(1)?.trim()
                    if (!bn.isNullOrBlank()) bn else raw.substringBefore("[").substringBefore("(").trim()
                } else {
                    raw.substringBefore("[").substringBefore("(").trim().uppercase()
                }
            }
            return "${clean(fallbackDistrict)} > ${clean(fallbackBlock)} > ${clean(fallbackMouza)}"
        }
    }

    private fun ensureLoaded(): LocationDatabase {
        val current = cachedDatabase
        if (current != null) return current

        val inputStream = context.assets.open("banglarbhumi_locations.json")
        val reader = InputStreamReader(inputStream, Charsets.UTF_8)
        val loaded = gson.fromJson(reader, LocationDatabase::class.java)
        reader.close()
        inputStream.close()
        cachedDatabase = loaded
        return loaded
    }
}
