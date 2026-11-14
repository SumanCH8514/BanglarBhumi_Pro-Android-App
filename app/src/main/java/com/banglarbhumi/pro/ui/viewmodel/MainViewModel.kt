package com.banglarbhumi.pro.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.banglarbhumi.pro.data.model.Block
import com.banglarbhumi.pro.data.model.District
import com.banglarbhumi.pro.data.model.LandRecordResult
import com.banglarbhumi.pro.data.model.Mouza
import com.banglarbhumi.pro.data.model.MouzaSearchResult
import com.banglarbhumi.pro.data.model.SavedLandRecord
import com.banglarbhumi.pro.data.model.SearchType
import com.banglarbhumi.pro.data.repository.LandRecordRepository
import com.banglarbhumi.pro.data.repository.LocationRepository
import com.banglarbhumi.pro.network.BanglarbhumiAuthRequiredException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PortalLoginStep {
    CREDENTIALS_AND_CAPTCHA,
    OTP_VERIFICATION
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)
    private val landRecordRepository = LandRecordRepository(application)

    private val _districts = MutableStateFlow<List<District>>(emptyList())
    val districts: StateFlow<List<District>> = _districts.asStateFlow()

    private val _blocks = MutableStateFlow<List<Block>>(emptyList())
    val blocks: StateFlow<List<Block>> = _blocks.asStateFlow()

    private val _mouzas = MutableStateFlow<List<Mouza>>(emptyList())
    val mouzas: StateFlow<List<Mouza>> = _mouzas.asStateFlow()

    private val _selectedDistrict = MutableStateFlow<District?>(null)
    val selectedDistrict: StateFlow<District?> = _selectedDistrict.asStateFlow()

    private val _selectedBlock = MutableStateFlow<Block?>(null)
    val selectedBlock: StateFlow<Block?> = _selectedBlock.asStateFlow()

    private val _selectedMouza = MutableStateFlow<Mouza?>(null)
    val selectedMouza: StateFlow<Mouza?> = _selectedMouza.asStateFlow()

    private val _searchType = MutableStateFlow(SearchType.PLOT)
    val searchType: StateFlow<SearchType> = _searchType.asStateFlow()

    private val _searchNumber = MutableStateFlow("")
    val searchNumber: StateFlow<String> = _searchNumber.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchResult = MutableStateFlow<LandRecordResult?>(null)
    val searchResult: StateFlow<LandRecordResult?> = _searchResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _savedRecords = MutableStateFlow<List<SavedLandRecord>>(emptyList())
    val savedRecords: StateFlow<List<SavedLandRecord>> = _savedRecords.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _mouzaSearchResults = MutableStateFlow<List<MouzaSearchResult>>(emptyList())
    val mouzaSearchResults: StateFlow<List<MouzaSearchResult>> = _mouzaSearchResults.asStateFlow()

    private val _isSearchingMouzas = MutableStateFlow(false)
    val isSearchingMouzas: StateFlow<Boolean> = _isSearchingMouzas.asStateFlow()

    private val _isBengali = MutableStateFlow(false)
    val isBengali: StateFlow<Boolean> = _isBengali.asStateFlow()

    private val _isPortalLoggedIn = MutableStateFlow(landRecordRepository.isPortalLoggedIn())
    val isPortalLoggedIn: StateFlow<Boolean> = _isPortalLoggedIn.asStateFlow()

    private val _loggedInPortalUsername = MutableStateFlow(landRecordRepository.getLoggedInPortalUsername())
    val loggedInPortalUsername: StateFlow<String?> = _loggedInPortalUsername.asStateFlow()

    private val _showPortalLoginDialog = MutableStateFlow(false)
    val showPortalLoginDialog: StateFlow<Boolean> = _showPortalLoginDialog.asStateFlow()

    private val _portalLoginStep = MutableStateFlow(PortalLoginStep.CREDENTIALS_AND_CAPTCHA)
    val portalLoginStep: StateFlow<PortalLoginStep> = _portalLoginStep.asStateFlow()

    private val _captchaBitmap = MutableStateFlow<Bitmap?>(null)
    val captchaBitmap: StateFlow<Bitmap?> = _captchaBitmap.asStateFlow()

    private val _captchaSalt = MutableStateFlow("")
    val captchaSalt: StateFlow<String> = _captchaSalt.asStateFlow()

    private val _isCaptchaLoading = MutableStateFlow(false)
    val isCaptchaLoading: StateFlow<Boolean> = _isCaptchaLoading.asStateFlow()

    private val _isLoginSubmitting = MutableStateFlow(false)
    val isLoginSubmitting: StateFlow<Boolean> = _isLoginSubmitting.asStateFlow()

    private val _portalLoginError = MutableStateFlow<String?>(null)
    val portalLoginError: StateFlow<String?> = _portalLoginError.asStateFlow()

    private val _portalLoginStatusMessage = MutableStateFlow<String?>(null)
    val portalLoginStatusMessage: StateFlow<String?> = _portalLoginStatusMessage.asStateFlow()

    private val _cachedUsername = MutableStateFlow(landRecordRepository.getCachedUsername())
    val cachedUsername: StateFlow<String> = _cachedUsername.asStateFlow()

    private val _cachedPassword = MutableStateFlow(landRecordRepository.getCachedPassword())
    val cachedPassword: StateFlow<String> = _cachedPassword.asStateFlow()

    private val _citizenName = MutableStateFlow(
        landRecordRepository.getCachedCitizenName().ifBlank {
            landRecordRepository.getLoggedInCitizenName().orEmpty()
        }
    )
    val citizenName: StateFlow<String> = _citizenName.asStateFlow()

    private var pendingUsername = ""
    private var pendingPassword = ""
    private var pendingSearchOnLoginSuccess = false

    init {
        loadDistricts()
        loadSavedRecords()
        if (_isPortalLoggedIn.value) {
            refreshCitizenNameFromPortal()
        }
    }

    fun toggleLanguage() {
        _isBengali.value = !_isBengali.value
    }

    private fun loadDistricts() {
        viewModelScope.launch {
            try {
                val list = locationRepository.getDistricts()
                _districts.value = list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load districts: ${e.message}"
            }
        }
    }

    fun selectDistrict(district: District) {
        _selectedDistrict.value = district
        _selectedBlock.value = null
        _selectedMouza.value = null
        _blocks.value = emptyList()
        _mouzas.value = emptyList()

        viewModelScope.launch {
            try {
                val blks = locationRepository.getBlocks(district.code)
                _blocks.value = blks
            } catch (_: Exception) {}
        }
    }

    fun selectBlock(block: Block) {
        _selectedBlock.value = block
        _selectedMouza.value = null
        _mouzas.value = emptyList()

        val dist = _selectedDistrict.value ?: return
        viewModelScope.launch {
            try {
                val mzs = locationRepository.getMouzas(dist.code, block.code)
                _mouzas.value = mzs
            } catch (_: Exception) {}
        }
    }

    fun selectMouza(mouza: Mouza) {
        _selectedMouza.value = mouza
    }

    fun setSearchType(type: SearchType) {
        _searchType.value = type
    }

    fun setSearchNumber(num: String) {
        _searchNumber.value = num
    }

    fun performSearch() {
        val dist = _selectedDistrict.value
        val blk = _selectedBlock.value
        val mz = _selectedMouza.value
        val queryNum = _searchNumber.value.trim()

        if (dist == null || blk == null || mz == null || queryNum.isEmpty()) {
            _errorMessage.value = if (_isBengali.value) "সবগুলো ঘর পূরণ করুন" else "Please fill all fields"
            return
        }

        _errorMessage.value = null
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val distName = if (_isBengali.value) "${dist.en} (${dist.bn})" else dist.en
                val blkName = if (_isBengali.value) "${blk.en} (${blk.bn})" else blk.en
                val mzName = if (_isBengali.value) "${mz.en} (${mz.bn}) [${mz.code}]" else "${mz.en} [${mz.code}]"

                val result = landRecordRepository.searchLandRecord(
                    searchType = _searchType.value,
                    districtCode = dist.code,
                    districtName = distName,
                    blockCode = blk.code,
                    blockName = blkName,
                    mouzaCode = mz.code,
                    mouzaName = mzName,
                    searchNumber = queryNum
                )

                if (result.isSuccess) {
                    val record = result.getOrNull()
                    _searchResult.value = record
                    checkIfSaved(record)
                } else {
                    val ex = result.exceptionOrNull()
                    if (ex is BanglarbhumiAuthRequiredException) {
                        _isPortalLoggedIn.value = false
                        _loggedInPortalUsername.value = null
                        pendingSearchOnLoginSuccess = true
                        _portalLoginError.value = if (_isBengali.value)
                            "বাংলারভূমি পোর্টালে এই মুহূর্তে সিটিজেন লগইন প্রয়োজন। লগইন সম্পন্ন হলে স্বয়ংক্রিয়ভাবে রেকর্ড প্রদর্শিত হবে।"
                        else
                            "Citizen login is required by the portal to fetch this record. Login to proceed."
                        openPortalLoginDialog()
                    } else {
                        _errorMessage.value = ex?.message ?: "Search failed"
                    }
                }
            } catch (e: Exception) {
                if (e is BanglarbhumiAuthRequiredException) {
                    _isPortalLoggedIn.value = false
                    _loggedInPortalUsername.value = null
                    pendingSearchOnLoginSuccess = true
                    _portalLoginError.value = if (_isBengali.value)
                        "বাংলারভূমি পোর্টালে এই মুহূর্তে সিটিজেন লগইন প্রয়োজন। লগইন সম্পন্ন হলে স্বয়ংক্রিয়ভাবে রেকর্ড প্রদর্শিত হবে।"
                    else
                        "Citizen login is required by the portal to fetch this record. Login to proceed."
                    openPortalLoginDialog()
                } else {
                    _errorMessage.value = e.message ?: "Network error"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun openPortalLoginDialog() {
        _portalLoginError.value = null
        _portalLoginStatusMessage.value = null
        _portalLoginStep.value = PortalLoginStep.CREDENTIALS_AND_CAPTCHA
        _cachedUsername.value = landRecordRepository.getCachedUsername()
        _cachedPassword.value = landRecordRepository.getCachedPassword()
        _showPortalLoginDialog.value = true
        refreshCaptcha()
    }

    fun closePortalLoginDialog() {
        _showPortalLoginDialog.value = false
        pendingSearchOnLoginSuccess = false
        _portalLoginError.value = null
        _portalLoginStatusMessage.value = null
    }

    fun refreshCaptcha() {
        _isCaptchaLoading.value = true
        _portalLoginError.value = null
        viewModelScope.launch {
            try {
                val result = landRecordRepository.fetchCaptcha()
                if (result.isSuccess) {
                    val data = result.getOrNull()
                    if (data != null) {
                        val bmp = BitmapFactory.decodeByteArray(data.imageBytes, 0, data.imageBytes.size)
                        _captchaBitmap.value = bmp
                        _captchaSalt.value = data.salt
                    }
                } else {
                    _portalLoginError.value = result.exceptionOrNull()?.message ?: "ক্যাপচা লোড করা যায়নি"
                }
            } catch (e: Exception) {
                _portalLoginError.value = e.message ?: "ক্যাপচা লোড ত্রুটি"
            } finally {
                _isCaptchaLoading.value = false
            }
        }
    }

    fun requestLoginOtp(username: String, pass: String, captcha: String) {
        if (username.isBlank() || pass.isBlank() || captcha.isBlank()) {
            _portalLoginError.value = if (_isBengali.value)
                "ইউজার আইডি, পাসওয়ার্ড ও ক্যাপচা সবগুলি পূরণ করুন"
            else
                "Please enter Username, Password and Captcha"
            return
        }
        _portalLoginError.value = null
        _portalLoginStatusMessage.value = null
        _isLoginSubmitting.value = true
        pendingUsername = username.trim()
        pendingPassword = pass.trim()

        viewModelScope.launch {
            try {
                val result = landRecordRepository.requestLoginOtp(
                    pendingUsername,
                    pendingPassword,
                    captcha.trim(),
                    _captchaSalt.value
                )
                if (result.isSuccess) {
                    landRecordRepository.saveCachedCredentials(pendingUsername, pendingPassword)
                    _cachedUsername.value = pendingUsername
                    _cachedPassword.value = pendingPassword
                    _portalLoginStatusMessage.value = result.getOrNull()
                    _portalLoginStep.value = PortalLoginStep.OTP_VERIFICATION
                } else {
                    _portalLoginError.value = result.exceptionOrNull()?.message ?: "OTP পাঠানো সম্ভব হয়নি"
                    refreshCaptcha()
                }
            } catch (e: Exception) {
                _portalLoginError.value = e.message ?: "ত্রুটি হয়েছে"
                refreshCaptcha()
            } finally {
                _isLoginSubmitting.value = false
            }
        }
    }

    fun verifyLoginOtp(otp: String) {
        if (otp.isBlank()) {
            _portalLoginError.value = if (_isBengali.value) "প্রাপ্ত ওটিপি কোডটি লিখুন" else "Please enter the OTP"
            return
        }
        _portalLoginError.value = null
        _isLoginSubmitting.value = true

        viewModelScope.launch {
            try {
                val result = landRecordRepository.verifyLoginOtp(
                    pendingUsername,
                    pendingPassword,
                    otp.trim(),
                    _captchaSalt.value
                )
                if (result.isSuccess) {
                    val savedName = landRecordRepository.getCachedCitizenName()
                    landRecordRepository.saveCachedCredentials(pendingUsername, pendingPassword, savedName)
                    _cachedUsername.value = pendingUsername
                    _cachedPassword.value = pendingPassword
                    _isPortalLoggedIn.value = true
                    _loggedInPortalUsername.value = pendingUsername
                    _citizenName.value = savedName
                    _showPortalLoginDialog.value = false
                    _portalLoginStatusMessage.value = null

                    refreshCitizenNameFromPortal()

                    if (pendingSearchOnLoginSuccess) {
                        pendingSearchOnLoginSuccess = false
                        performSearch()
                    }
                } else {
                    _portalLoginError.value = result.exceptionOrNull()?.message ?: "ওটিপি যাচাই ব্যর্থ হয়েছে"
                }
            } catch (e: Exception) {
                _portalLoginError.value = e.message ?: "লগইন যাচাই ত্রুটি"
            } finally {
                _isLoginSubmitting.value = false
            }
        }
    }

    fun refreshCitizenNameFromPortal() {
        viewModelScope.launch {
            try {
                val result = landRecordRepository.fetchCitizenName()
                if (result.isSuccess) {
                    val name = result.getOrNull()
                    if (!name.isNullOrBlank()) {
                        _citizenName.value = name
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun updateCitizenName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank()) {
            landRecordRepository.saveCitizenName(trimmed)
            _citizenName.value = trimmed
        }
    }

    fun logoutPortal() {
        landRecordRepository.logoutPortal()
        _isPortalLoggedIn.value = false
        _loggedInPortalUsername.value = null
        _cachedUsername.value = landRecordRepository.getCachedUsername()
        _cachedPassword.value = landRecordRepository.getCachedPassword()
        _citizenName.value = landRecordRepository.getCachedCitizenName()
    }

    fun saveCachedCredentials(username: String, pass: String, citizenName: String? = null) {
        val u = username.trim()
        val p = pass.trim()
        if (u.isNotBlank()) {
            landRecordRepository.saveCachedCredentials(u, p, citizenName)
            _cachedUsername.value = u
            _cachedPassword.value = p
            if (!citizenName.isNullOrBlank()) {
                _citizenName.value = citizenName.trim()
            }
        }
    }

    fun clearCachedCredentials() {
        landRecordRepository.clearCachedCredentials()
        _cachedUsername.value = ""
        _cachedPassword.value = ""
        _citizenName.value = ""
    }

    fun backToCredentialsStep() {
        _portalLoginStep.value = PortalLoginStep.CREDENTIALS_AND_CAPTCHA
        _portalLoginError.value = null
        refreshCaptcha()
    }

    fun clearSearchResult() {
        _searchResult.value = null
    }

    fun showRecord(record: LandRecordResult) {
        _searchResult.value = record
        checkIfSaved(record)
    }

    fun toggleSaveCurrentRecord() {
        val record = _searchResult.value ?: return
        viewModelScope.launch {
            val savedNow = landRecordRepository.toggleSaveRecord(record)
            _isSaved.value = savedNow
            loadSavedRecords()
        }
    }

    fun deleteSavedRecord(id: String) {
        viewModelScope.launch {
            landRecordRepository.deleteSavedRecord(id)
            loadSavedRecords()
        }
    }

    private fun loadSavedRecords() {
        viewModelScope.launch {
            _savedRecords.value = landRecordRepository.getSavedRecords()
        }
    }

    private fun checkIfSaved(record: LandRecordResult?) {
        if (record == null) {
            _isSaved.value = false
            return
        }
        val saved = _savedRecords.value
        _isSaved.value = saved.any {
            it.record.districtCode == record.districtCode &&
            it.record.blockCode == record.blockCode &&
            it.record.mouzaCode == record.mouzaCode &&
            it.record.searchType == record.searchType &&
            it.record.searchNumber == record.searchNumber
        }
    }

    fun searchMouzasGlobal(query: String) {
        if (query.trim().length < 2) {
            _mouzaSearchResults.value = emptyList()
            return
        }
        _isSearchingMouzas.value = true
        viewModelScope.launch {
            try {
                val list = locationRepository.searchMouzas(query)
                _mouzaSearchResults.value = list
            } finally {
                _isSearchingMouzas.value = false
            }
        }
    }

    fun selectFromGlobalSearch(result: MouzaSearchResult) {
        val dist = _districts.value.find { it.code == result.districtCode }
            ?: District(result.districtCode, result.districtNameEn, result.districtNameBn, null)
        _selectedDistrict.value = dist

        viewModelScope.launch {
            try {
                val blks = locationRepository.getBlocks(dist.code)
                _blocks.value = blks
                val blk = blks.find { it.code == result.blockCode }
                    ?: Block(result.blockCode, result.blockNameEn, result.blockNameBn, null)
                _selectedBlock.value = blk

                val mzs = locationRepository.getMouzas(dist.code, blk.code)
                _mouzas.value = mzs
                val mz = mzs.find { it.code == result.mouzaCode }
                    ?: Mouza(result.mouzaCode, result.mouzaNameEn, result.mouzaNameBn, null)
                _selectedMouza.value = mz
            } catch (_: Exception) {}
        }
    }
}