package com.banglarbhumi.pro.data.repository

import android.content.Context
import com.banglarbhumi.pro.data.model.LandRecordResult
import com.banglarbhumi.pro.data.model.SavedLandRecord
import com.banglarbhumi.pro.data.model.SearchType
import com.banglarbhumi.pro.network.BanglarbhumiClient
import com.banglarbhumi.pro.network.CaptchaData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class LandRecordRepository(private val context: Context) {

    private val client = BanglarbhumiClient(context)
    private val prefs = context.getSharedPreferences("banglarbhumi_records", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun isPortalLoggedIn(): Boolean = client.isUserLoggedIn()

    fun getLoggedInPortalUsername(): String? = client.getLoggedInUsername()

    fun getLoggedInCitizenName(): String? = client.getLoggedInCitizenName()

    fun logoutPortal() = client.logout()

    fun saveCachedCredentials(username: String, pass: String, citizenName: String? = null) =
        client.saveCachedCredentials(username, pass, citizenName)

    fun getCachedUsername(): String = client.getCachedUsername()

    fun getCachedPassword(): String = client.getCachedPassword()

    fun getCachedCitizenName(): String = client.getCachedCitizenName()

    fun saveCitizenName(name: String) = client.saveCitizenName(name)

    fun clearCachedCredentials() = client.clearCachedCredentials()

    suspend fun fetchCitizenName(): Result<String> {
        val name = client.fetchCitizenNameFromPortal()
        return if (!name.isNullOrBlank()) {
            Result.success(name)
        } else {
            Result.failure(Exception("Citizen name not found"))
        }
    }

    suspend fun fetchCaptcha(): Result<CaptchaData> = client.fetchCaptcha()

    suspend fun requestLoginOtp(
        username: String,
        password: String,
        captcha: String,
        salt: String
    ): Result<String> = client.requestLoginOtp(username, password, captcha, salt)

    suspend fun verifyLoginOtp(
        username: String,
        password: String,
        otp: String,
        salt: String
    ): Result<String> = client.verifyLoginOtp(username, password, otp, salt)

    suspend fun searchLandRecord(
        searchType: SearchType,
        districtCode: String,
        districtName: String,
        blockCode: String,
        blockName: String,
        mouzaCode: String,
        mouzaName: String,
        searchNumber: String,
        bataNumber: String = ""
    ): Result<LandRecordResult> {
        return client.queryLandRecord(
            searchType = searchType,
            districtCode = districtCode,
            districtName = districtName,
            blockCode = blockCode,
            blockName = blockName,
            mouzaCode = mouzaCode,
            mouzaName = mouzaName,
            searchNumber = searchNumber,
            bataNumber = bataNumber
        )
    }

    suspend fun getSavedRecords(): List<SavedLandRecord> = withContext(Dispatchers.IO) {
        val json = prefs.getString("saved_list", null) ?: return@withContext emptyList()
        val type = object : TypeToken<List<SavedLandRecord>>() {}.type
        try {
            gson.fromJson<List<SavedLandRecord>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun toggleSaveRecord(result: LandRecordResult): Boolean = withContext(Dispatchers.IO) {
        val current = getSavedRecords().toMutableList()
        val existingIndex = current.indexOfFirst {
            it.record.districtCode == result.districtCode &&
            it.record.blockCode == result.blockCode &&
            it.record.mouzaCode == result.mouzaCode &&
            it.record.searchType == result.searchType &&
            it.record.searchNumber == result.searchNumber
        }

        val isSavedNow: Boolean
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
            isSavedNow = false
        } else {
            val item = SavedLandRecord(
                id = UUID.randomUUID().toString(),
                savedTimestamp = System.currentTimeMillis(),
                record = result
            )
            current.add(0, item)
            isSavedNow = true
        }

        prefs.edit().putString("saved_list", gson.toJson(current)).apply()
        isSavedNow
    }

    suspend fun deleteSavedRecord(id: String) = withContext(Dispatchers.IO) {
        val current = getSavedRecords().toMutableList()
        current.removeAll { it.id == id }
        prefs.edit().putString("saved_list", gson.toJson(current)).apply()
    }
}
