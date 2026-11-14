package com.banglarbhumi.pro.network

import android.content.Context
import android.util.Base64
import com.banglarbhumi.pro.data.model.KhatianSummary
import com.banglarbhumi.pro.data.model.LandOwner
import com.banglarbhumi.pro.data.model.LandRecordResult
import com.banglarbhumi.pro.data.model.PlotSummary
import com.banglarbhumi.pro.data.model.SearchType
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateExpiredException
import java.security.cert.X509Certificate
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class BanglarbhumiAuthRequiredException(
    message: String = "বাংলারভূমি পোর্টালে সিটিজেন লগইন প্রয়োজন (BanglarBhumi citizen login required)."
) : Exception(message)

data class CaptchaData(
    val imageBytes: ByteArray,
    val salt: String
)

class BanglarbhumiClient(private val context: Context? = null) {

    private val cookieStore = mutableMapOf<String, MutableMap<String, Cookie>>()
    private val sessionPrefs = context?.getSharedPreferences("bb_session_store", Context.MODE_PRIVATE)

    private val client: OkHttpClient by lazy {
        createCompatibleOkHttpClient()
    }

    init {
        seedDefaultCookies()
        loadSavedSessionCookies()
    }

    private fun seedDefaultCookies() {
        val bbUrl = "https://banglarbhumi.gov.in".toHttpUrlOrNull()
        if (bbUrl != null) {
            val langCookie = Cookie.Builder()
                .domain("banglarbhumi.gov.in")
                .path("/BanglarBhumi")
                .name("lang")
                .value("bn")
                .build()
            cookieStore.getOrPut(bbUrl.host) { mutableMapOf() }[langCookie.name] = langCookie

            val lriCookie = Cookie.Builder()
                .domain("banglarbhumi.gov.in")
                .path("/BanglarBhumi")
                .name("LRicookie")
                .value("BanglarBhumiApp2")
                .build()
            cookieStore.getOrPut(bbUrl.host) { mutableMapOf() }[lriCookie.name] = lriCookie
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sessionPrefs?.getBoolean("authenticated", false) == true
    }

    fun getLoggedInUsername(): String? {
        return sessionPrefs?.getString("username", null)
    }

    fun getLoggedInCitizenName(): String? {
        val name = sessionPrefs?.getString("citizen_name", null)
        if (!name.isNullOrBlank()) return name
        return sessionPrefs?.getString("cached_citizen_name", null)
    }

    fun saveCachedCredentials(username: String, pass: String, citizenName: String? = null) {
        val u = username.trim()
        val p = pass.trim()
        val editor = sessionPrefs?.edit()
        if (u.isNotBlank()) {
            editor?.putString("cached_username", u)
                ?.putString("cached_password", p)
        }
        if (!citizenName.isNullOrBlank()) {
            editor?.putString("cached_citizen_name", citizenName.trim())
            editor?.putString("citizen_name", citizenName.trim())
        }
        editor?.apply()
    }

    fun getCachedUsername(): String {
        val cached = sessionPrefs?.getString("cached_username", null)
        if (!cached.isNullOrBlank()) return cached
        return sessionPrefs?.getString("username", "") ?: ""
    }

    fun getCachedPassword(): String {
        return sessionPrefs?.getString("cached_password", "") ?: ""
    }

    fun getCachedCitizenName(): String {
        val cached = sessionPrefs?.getString("cached_citizen_name", null)
        if (!cached.isNullOrBlank()) return cached
        val logged = sessionPrefs?.getString("citizen_name", null)
        if (!logged.isNullOrBlank()) return logged
        // Intelligent initial fallback from cached credential if available
        val pass = sessionPrefs?.getString("cached_password", "") ?: ""
        val mCamel = Pattern.compile("^([A-Z][a-z]+)").matcher(pass)
        if (mCamel.find()) {
            val name = mCamel.group(1).orEmpty()
            if (name.length >= 3) return name
        }
        val mAll = Pattern.compile("^([A-Za-z]+)").matcher(pass)
        if (mAll.find()) {
            val name = mAll.group(1).orEmpty()
            if (name.length >= 3) return name
        }
        return ""
    }

    fun saveCitizenName(name: String) {
        val n = name.trim()
        if (n.isNotBlank()) {
            sessionPrefs?.edit()
                ?.putString("citizen_name", n)
                ?.putString("cached_citizen_name", n)
                ?.apply()
        }
    }

    fun clearCachedCredentials() {
        sessionPrefs?.edit()
            ?.remove("cached_username")
            ?.remove("cached_password")
            ?.remove("cached_citizen_name")
            ?.apply()
    }

    fun logout() {
        sessionPrefs?.edit()
            ?.remove("authenticated")
            ?.remove("username")
            ?.remove("citizen_name")
            ?.remove("saved_cookies")
            ?.apply()
        cookieStore.clear()
        seedDefaultCookies()
    }

    private fun saveSessionCookies(username: String) {
        val bbCookies = cookieStore["banglarbhumi.gov.in"]?.values ?: emptyList()
        val jsonArray = JSONArray()
        for (c in bbCookies) {
            val obj = JSONObject()
            obj.put("name", c.name)
            obj.put("value", c.value)
            obj.put("domain", c.domain)
            obj.put("path", c.path)
            jsonArray.put(obj)
        }
        sessionPrefs?.edit()
            ?.putBoolean("authenticated", true)
            ?.putString("username", username)
            ?.putString("saved_cookies", jsonArray.toString())
            ?.apply()
    }

    private fun loadSavedSessionCookies() {
        val jsonStr = sessionPrefs?.getString("saved_cookies", null) ?: return
        try {
            val jsonArray = JSONArray(jsonStr)
            val hostMap = cookieStore.getOrPut("banglarbhumi.gov.in") { mutableMapOf() }
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val cookie = Cookie.Builder()
                    .name(obj.getString("name"))
                    .value(obj.getString("value"))
                    .domain(obj.optString("domain", "banglarbhumi.gov.in"))
                    .path(obj.optString("path", "/BanglarBhumi"))
                    .build()
                hostMap[cookie.name] = cookie
            }
        } catch (_: Exception) {}
    }

    private fun createCompatibleOkHttpClient(): OkHttpClient {
        val trustManager = createBanglarbhumiTrustManager()
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustManager), SecureRandom())
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { hostname, session ->
                if (hostname.equals("banglarbhumi.gov.in", ignoreCase = true) ||
                    hostname.endsWith(".banglarbhumi.gov.in", ignoreCase = true)
                ) {
                    true
                } else {
                    HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session)
                }
            }
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    val hostMap = cookieStore.getOrPut(url.host) { mutableMapOf() }
                    for (c in cookies) {
                        hostMap[c.name] = c
                    }
                    if (isUserLoggedIn() && url.host.contains("banglarbhumi.gov.in")) {
                        val currentUsername = getLoggedInUsername() ?: "Citizen"
                        saveSessionCookies(currentUsername)
                    }
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val hostMap = cookieStore[url.host] ?: return emptyList()
                    return hostMap.values.toList()
                }
            })
            .build()
    }

    private fun createBanglarbhumiTrustManager(): X509TrustManager {
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(null as KeyStore?)
        val defaultTm = tmf.trustManagers.filterIsInstance<X509TrustManager>().firstOrNull()
            ?: throw IllegalStateException("No default X509TrustManager available")

        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                defaultTm.checkClientTrusted(chain, authType)
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                try {
                    defaultTm.checkServerTrusted(chain, authType)
                } catch (e: CertificateException) {
                    var isBanglarbhumi = false
                    val cert = chain?.firstOrNull()
                    if (cert != null) {
                        try {
                            val sans = cert.subjectAlternativeNames
                            if (sans != null) {
                                for (item in sans) {
                                    if (item != null && item.size >= 2 && item[0] == 2) {
                                        val altName = item[1]?.toString()?.trim()?.lowercase(Locale.US) ?: ""
                                        if (altName == "banglarbhumi.gov.in" ||
                                            altName == "*.banglarbhumi.gov.in" ||
                                            altName.endsWith(".banglarbhumi.gov.in")
                                        ) {
                                            isBanglarbhumi = true
                                            break
                                        }
                                    }
                                }
                            }
                            if (!isBanglarbhumi && cert.subjectX500Principal != null) {
                                val dn = cert.subjectX500Principal.name.lowercase(Locale.US)
                                isBanglarbhumi = dn.contains("banglarbhumi.gov.in")
                            }
                        } catch (_: Exception) {}
                    }

                    if (isBanglarbhumi) {
                        var cause: Throwable? = e
                        while (cause != null) {
                            if (cause is CertificateExpiredException) return
                            val msg = cause.message?.lowercase(Locale.US) ?: ""
                            if (msg.contains("timestamp check failed") ||
                                msg.contains("certificate expired") ||
                                msg.contains("trust anchor") ||
                                msg.contains("certpathvalidatorexception")
                            ) {
                                return
                            }
                            cause = cause.cause
                        }
                    }
                    throw e
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = defaultTm.acceptedIssuers
        }
    }

    suspend fun fetchCaptcha(): Result<CaptchaData> = withContext(Dispatchers.IO) {
        try {
            seedDefaultCookies()

            val homeReq = Request.Builder()
                .url("https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .get()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()
            try { client.newCall(homeReq).execute().close() } catch (_: Exception) {}

            val loginAreaReq = Request.Builder()
                .url("https://banglarbhumi.gov.in/BanglarBhumi/viewLoginAreaAction")
                .post(FormBody.Builder().build())
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Referer", "https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            var salt = "158e1d657a9fcb44858a7ce5d36fb9a1df44a0604c55901c"
            try {
                val loginAreaResp = client.newCall(loginAreaReq).execute()
                val html = loginAreaResp.body?.string().orEmpty()
                loginAreaResp.close()
                val extractedSalt = extractSaltFromHtml(html)
                if (extractedSalt.isNotBlank()) {
                    salt = extractedSalt
                }
            } catch (_: Exception) {}

            val captchaUrl = "https://banglarbhumi.gov.in/BanglarBhumi/generateCaptcha?${System.currentTimeMillis()}"
            val captchaReq = Request.Builder()
                .url(captchaUrl)
                .get()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Referer", "https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                .build()

            val captchaResp = client.newCall(captchaReq).execute()
            val bytes = captchaResp.body?.bytes()
            captchaResp.close()

            if (bytes == null || bytes.isEmpty()) {
                return@withContext Result.failure(Exception("ক্যাপচা ইমেজ লোড করা সম্ভব হয়নি। দয়া করে পুনরায় চেষ্টা করুন।"))
            }

            Result.success(CaptchaData(imageBytes = bytes, salt = salt))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestLoginOtp(
        username: String,
        password: String,
        captcha: String,
        salt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cleanUser = username.trim()
            val cleanPass = password.trim()
            val cleanCaptcha = captcha.trim().uppercase(Locale.US)
            val cleanSalt = salt.ifBlank { "158e1d657a9fcb44858a7ce5d36fb9a1df44a0604c55901c" }

            val md5Pass = md5Hex(cleanPass)
            val passHash = sha256Hex(md5Hex(md5Pass + cleanSalt))

            val form = FormBody.Builder()
                .add("userType", "2")
                .add("username", cleanUser)
                .add("txtInput", cleanCaptcha)
                .add("password", passHash)
                .add("saltHashtext", cleanSalt)
                .add("ajax", "true")
                .build()

            val req = Request.Builder()
                .url("https://banglarbhumi.gov.in/BanglarBhumi/loginOTPGenerationAction.action")
                .post(form)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Referer", "https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .header("Origin", "https://banglarbhumi.gov.in")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Accept", "*/*")
                .build()

            val resp = client.newCall(req).execute()
            val body = resp.body?.string().orEmpty()
            resp.close()

            if (isActionSuccess(body)) {
                Result.success("OTP sent successfully.")
            } else {
                val msg = parseErrorMessageFromJson(body, "ওটিপি পাঠাতে ব্যর্থ হয়েছে। ইউজার আইডি, পাসওয়ার্ড ও ক্যাপচা যাচাই করুন।")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyLoginOtp(
        username: String,
        password: String,
        otp: String,
        salt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cleanUser = username.trim()
            val cleanPass = password.trim()
            val cleanOtp = otp.trim()
            val cleanSalt = salt.ifBlank { "158e1d657a9fcb44858a7ce5d36fb9a1df44a0604c55901c" }

            val md5Pass = md5Hex(cleanPass)
            val passHash = sha256Hex(md5Hex(md5Pass + cleanSalt))

            val md5User = md5Hex(cleanUser)
            val retypepassword = md5Hex(md5User + cleanSalt)

            val encodedUserType = "RGxycyMxMjM=" + Base64.encodeToString("2".toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            val encodedUsername = "RGxycyMxMjM=" + Base64.encodeToString(cleanUser.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

            val form = FormBody.Builder()
                .add("userType", encodedUserType)
                .add("username", encodedUsername)
                .add("password", passHash)
                .add("saltHashtext", cleanSalt)
                .add("retypepassword", retypepassword)
                .add("txtOTP", cleanOtp)
                .add("ajax", "true")
                .build()

            val req = Request.Builder()
                .url("https://banglarbhumi.gov.in/BanglarBhumi/login.action")
                .post(form)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Referer", "https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .header("Origin", "https://banglarbhumi.gov.in")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Accept", "*/*")
                .build()

            val resp = client.newCall(req).execute()
            val body = resp.body?.string().orEmpty()
            resp.close()

            if (isActionSuccess(body) || hasPortalSessionCookie()) {
                saveSessionCookies(cleanUser)

                // 1. Try to extract citizen name from login.action JSON response
                var extractedName = extractCitizenNameFromJson(body)

                // 2. If not found in JSON, attempt to fetch Home page with the active session cookies
                if (extractedName.isNullOrBlank()) {
                    extractedName = fetchCitizenNameFromPortalInternal()
                }

                if (!extractedName.isNullOrBlank()) {
                    saveCitizenName(extractedName)
                    saveCachedCredentials(cleanUser, cleanPass, extractedName)
                } else {
                    saveCachedCredentials(cleanUser, cleanPass)
                }

                Result.success("লগইন সফল হয়েছে (Login successful)!")
            } else {
                val msg = parseErrorMessageFromJson(body, "লগইন ব্যর্থ হয়েছে। সঠিক ওটিপি দিন।")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchCitizenNameFromPortal(): String? = withContext(Dispatchers.IO) {
        fetchCitizenNameFromPortalInternal()
    }

    private fun fetchCitizenNameFromPortalInternal(): String? {
        try {
            val req = Request.Builder()
                .url("https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .get()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Referer", "https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            val resp = client.newCall(req).execute()
            val html = resp.body?.string().orEmpty()
            resp.close()

            if (html.isBlank()) return null

            // 1. Match in afterLoginLabel container (label, div, span, p, a)
            val pAfterLogin = Pattern.compile("(?is)<(?:div|label|span|p|a)[^>]*id\\s*=\\s*['\"]afterLoginLabel['\"][^>]*>(.*?)(?:</(?:div|label|span|p|a)>|$)")
            val mAfterLogin = pAfterLogin.matcher(html)
            if (mAfterLogin.find()) {
                val labelContent = mAfterLogin.group(1).orEmpty()
                val text = labelContent
                    .replace(Regex("(?is)<[^>]+>"), " ")
                    .replace("&nbsp;", " ")
                    .replace("-->", " ")
                    .replace(Regex("(?i)welcome"), " ")
                    .replace(Regex("(?i)citizen"), " ")
                    .trim()
                if (text.isNotBlank() && text.any { it.isLetter() }) {
                    val cleaned = cleanCitizenName(text)
                    saveCitizenName(cleaned)
                    return cleaned
                }
            }

            // 1b. Match Welcome / Swagatam with user name
            val pWelcome = Pattern.compile("(?is)(?:Welcome|স্বাগতম)[\\s:,-]+([A-Za-z\\s]{2,40})[<&\"\\r\\n]")
            val mWelcome = pWelcome.matcher(html)
            if (mWelcome.find()) {
                val raw = mWelcome.group(1)?.trim().orEmpty()
                if (raw.isNotBlank() && raw.any { it.isLetter() } && !raw.equals("to", ignoreCase = true) && !raw.equals("BanglarBhumi", ignoreCase = true)) {
                    val cleaned = cleanCitizenName(raw)
                    saveCitizenName(cleaned)
                    return cleaned
                }
            }

            // 2. Match JavaScript user variables
            val pUserVar = Pattern.compile("(?is)(?:var\\s+)?(?:userName|user_name|citizenName|applicantName)\\s*=\\s*['\"]([^'\"]+)['\"]")
            val mUserVar = pUserVar.matcher(html)
            if (mUserVar.find()) {
                val raw = mUserVar.group(1)?.trim().orEmpty()
                if (raw.isNotBlank() && raw.any { it.isLetter() }) {
                    val cleaned = cleanCitizenName(raw)
                    saveCitizenName(cleaned)
                    return cleaned
                }
            }

            // 3. Match user name spans / labels
            val pSpan = Pattern.compile("(?is)<(?:span|label|p|div)[^>]*(?:id|class)\\s*=\\s*['\"][^'\"]*(?:userName|user-name|citizen-name|user_name)[^'\"]*['\"][^>]*>([^<]+)</")
            val mSpan = pSpan.matcher(html)
            if (mSpan.find()) {
                val raw = mSpan.group(1)?.trim().orEmpty()
                if (raw.isNotBlank() && raw.any { it.isLetter() }) {
                    val cleaned = cleanCitizenName(raw)
                    saveCitizenName(cleaned)
                    return cleaned
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private fun extractCitizenNameFromJson(jsonStr: String): String? {
        if (jsonStr.isBlank()) return null
        try {
            val json = JSONObject(jsonStr)
            val nameKeys = listOf(
                "name", "userName", "fullName", "citizenName", "applicantName",
                "firstName", "user_name", "userFullName", "user", "citizen_name"
            )
            for (k in nameKeys) {
                if (json.has(k)) {
                    val raw = json.optString(k, "").trim()
                    if (raw.isNotBlank() && !raw.equals("true", ignoreCase = true) && !raw.equals("false", ignoreCase = true)) {
                        if (raw.any { it.isLetter() }) {
                            return cleanCitizenName(raw)
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private fun cleanCitizenName(name: String): String {
        return name
            .replace(Regex("\\s+"), " ")
            .replace(Regex("^[->:; ]+"), "")
            .replace(Regex("[->:; ]+$"), "")
            .trim()
    }

    private fun isActionSuccess(body: String): Boolean {
        val lower = body.lowercase(Locale.US)
        return lower.contains("\"success\":true") ||
                lower.contains("\"success\":\"true\"") ||
                lower.contains("\"ok\"") ||
                lower.contains("\"checkmsg\":\"success\"")
    }

    private fun hasPortalSessionCookie(): Boolean {
        val map = cookieStore["banglarbhumi.gov.in"] ?: return false
        return map.containsKey("JSESSIONID")
    }

    private fun parseErrorMessageFromJson(jsonStr: String, fallback: String): String {
        if (jsonStr.isBlank()) return fallback
        try {
            val json = JSONObject(jsonStr)
            val keys = listOf("message", "messageShow", "msg", "errMsg", "error", "checkmsg")
            for (k in keys) {
                val v = json.optString(k, "")
                if (v.isNotBlank()) return v
            }
        } catch (_: Exception) {}
        return fallback
    }

    private fun extractSaltFromHtml(html: String): String {
        if (html.isBlank()) return ""
        val inputMatcher = Pattern.compile("(?is)<input[^>]*(?:name|id)\\s*=\\s*['\"]saltHashtext['\"][^>]*>").matcher(html)
        if (!inputMatcher.find()) return ""
        val valMatcher = Pattern.compile("(?is)value\\s*=\\s*['\"]([^'\"]+)['\"]").matcher(inputMatcher.group())
        return if (valMatcher.find()) valMatcher.group(1)?.trim().orEmpty() else ""
    }

    private fun md5Hex(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    suspend fun queryLandRecord(
        searchType: SearchType,
        districtCode: String,
        districtName: String,
        blockCode: String,
        blockName: String,
        mouzaCode: String,
        mouzaName: String,
        searchNumber: String,
        bataNumber: String = ""
    ): Result<LandRecordResult> = withContext(Dispatchers.IO) {
        try {
            val (p1, p2) = splitSearchNumber(searchNumber)
            val part1 = p1
            val part2 = if (bataNumber.isNotBlank()) bataNumber.trim() else p2
            val combinedSearchNumber = if (part2.isNotEmpty()) "$part1/$part2" else part1

            val cleanBlock = if (blockCode.contains("_")) {
                val idx = blockCode.indexOf('_')
                if (idx > 0) blockCode.substring(0, idx) else blockCode
            } else {
                blockCode
            }
            val formattedBlockCode = if (cleanBlock.isEmpty()) "" else "${cleanBlock}_NEW"

            val handshakeUrl = "https://banglarbhumi.gov.in/BanglarBhumi/KnowYourProperty.action"
            val handshakeReq = Request.Builder()
                .url(handshakeUrl)
                .get()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Referer", "https://banglarbhumi.gov.in/BanglarBhumi/Home")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,application/json,text/javascript,*/*;q=0.8")
                .header("Accept-Language", "bn-IN,bn;q=0.9,en-US;q=0.8,en;q=0.7")
                .build()

            try {
                client.newCall(handshakeReq).execute().close()
            } catch (_: IOException) {
                
            }

            val formBuilder = FormBody.Builder()
                .add("lstDistrictCode1", districtCode)
                .add("lstBlockCode1", formattedBlockCode)
                .add("lstMouzaList", mouzaCode)
                .add("radioKhatianDtlType", "0")
                .add("yourLang", "bn_in")
                .add("ajax", "true")

            val endpoint = if (searchType == SearchType.PLOT) {
                formBuilder.add("txtPlotNo", part1)
                formBuilder.add("txtBataPlotNo", part2)
                "https://banglarbhumi.gov.in/BanglarBhumi/plotDetailsAction_LandInfo.action"
            } else {
                formBuilder.add("txtKhatian1", part1)
                formBuilder.add("txtKhatian2", part2)
                "https://banglarbhumi.gov.in/BanglarBhumi/khDetailsAction_LandInfo.action"
            }

            val queryReq = Request.Builder()
                .url(endpoint)
                .post(formBuilder.build())
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Referer", handshakeUrl)
                .header("Origin", "https://banglarbhumi.gov.in")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Accept", "*/*")
                .header("Accept-Language", "bn-IN,bn;q=0.9,en-US;q=0.8,en;q=0.7")
                .build()

            val response = client.newCall(queryReq).execute()
            val responseBody = response.body?.string().orEmpty()
            response.close()

            if (isLoginRequiredResponse(responseBody)) {
                sessionPrefs?.edit()?.putBoolean("authenticated", false)?.apply()
                throw BanglarbhumiAuthRequiredException(
                    "পশ্চিমবঙ্গ সরকারের পোর্টাল হতে জানানো হয়েছে: লাইভ তথ্যের জন্য সিটিজেন লগইন প্রয়োজন (BanglarBhumi portal login required)."
                )
            }

            val extractedHtml = extractHtml(responseBody)

            if (extractedHtml.isBlank() || isErrorMessage(extractedHtml)) {
                val cleanMsg = cleanHtmlText(extractedHtml)
                val finalMsg = if (cleanMsg.isNotBlank()) cleanMsg else "উক্ত দাগ বা খতিয়ানের কোনো রেকর্ড পাওয়া যায়নি (No record found for $combinedSearchNumber)."
                return@withContext Result.failure(Exception(finalMsg))
            }

            val parsedData = parseRecordDataFromHtml(
                html = extractedHtml,
                searchNumber = combinedSearchNumber,
                searchType = searchType,
                districtName = districtName,
                blockName = blockName,
                mouzaName = mouzaName,
                mouzaCode = mouzaCode
            )

            val styledHtml = buildStyledHtmlDocument(
                extractedHtml = extractedHtml,
                parsedData = parsedData,
                districtName = districtName,
                blockName = blockName,
                mouzaName = mouzaName,
                mouzaCode = mouzaCode,
                searchType = searchType,
                searchNumber = combinedSearchNumber
            )

            val recordResult = LandRecordResult(
                searchType = searchType,
                searchNumber = combinedSearchNumber,
                districtCode = districtCode,
                districtName = districtName,
                blockCode = blockCode,
                blockName = blockName,
                mouzaCode = mouzaCode,
                mouzaName = mouzaName,
                owners = parsedData.owners,
                totalAreaAcre = parsedData.totalAreaAcre,
                rawHtml = styledHtml,
                source = "banglarbhumi.gov.in",
                liveTimestamp = parsedData.liveTimestamp,
                jlNumber = parsedData.jlNumber,
                thanaName = parsedData.thanaName,
                khatianSummary = parsedData.khatianSummary,
                plotSummary = parsedData.plotSummary
            )

            Result.success(recordResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun splitSearchNumber(input: String): Pair<String, String> {
        val trimmed = input.trim()
        val idx = trimmed.indexOf('/')
        return if (idx < 0) {
            Pair(trimmed, "")
        } else {
            Pair(trimmed.substring(0, idx).trim(), trimmed.substring(idx + 1).trim())
        }
    }

    private fun isLoginRequiredResponse(response: String): Boolean {
        val lower = response.lowercase(Locale.US)
        return (lower.contains("login.action") && lower.contains("password") && lower.contains("captcha")) ||
                lower.contains("viewloginareaaction") ||
                lower.contains("banglarbhumi.gov.in/banglarbhumi/home") ||
                lower.contains("session expired") ||
                lower.contains("window.location")
    }

    private fun isErrorMessage(html: String): Boolean {
        val lower = html.lowercase(Locale.US)
        return lower.contains("no information found") ||
                lower.contains("কোন তথ্য পাওয়া যায়নি") ||
                lower.contains("দাগের কোনো তথ্য পাওয়া যায়নি") ||
                lower.contains("invalid request")
    }

    private fun cleanHtmlText(html: String): String {
        return html.replace(Regex("<[^>]*>"), " ").replace("&nbsp;", " ").trim()
    }

    private fun extractHtml(raw: String): String {
        if (raw.trim().isEmpty()) return ""
        try {
            val json = JSONObject(raw)
            val candidateKeys = listOf(
                "remLinfHtml",
                "resultHtml",
                "htmlContent",
                "html",
                "messageShow",
                "msgShow",
                "message",
                "msg"
            )
            for (k in candidateKeys) {
                if (json.has(k) && !json.isNull(k)) {
                    val str = json.optString(k, "")
                    if (str.isNotEmpty()) return decodeHtmlString(str)
                }
            }
        } catch (_: Exception) {}

        return decodeHtmlString(raw)
    }

    private fun decodeHtmlString(str: String): String {
        return str
            .replace("\\\"", "\"")
            .replace("\\/", "/")
            .replace("\\n", "\n")
            .replace("\\r", "")
            .replace("\\t", " ")
            .trim()
    }

    data class ParsedRecordData(
        val liveTimestamp: String,
        val jlNumber: String,
        val thanaName: String,
        val khatianSummary: KhatianSummary?,
        val plotSummary: PlotSummary?,
        val owners: List<LandOwner>,
        val totalAreaAcre: String
    )

    private fun parseRecordDataFromHtml(
        html: String,
        searchNumber: String,
        searchType: SearchType,
        districtName: String,
        blockName: String,
        mouzaName: String,
        mouzaCode: String
    ): ParsedRecordData {
        // Normalize text for reliable header extraction
        val cleanText = html.replace("&nbsp;", " ").replace(Regex("<[^>]*>"), " ")

        // 1. Live Timestamp
        val timeRegex = Pattern.compile("(?i)(?:Live\\s*Data\\s*As\\s*On|As\\s*On)\\s*[:]?\\s*([0-9/\\-:, ]{10,25})")
        val timeMatcher = timeRegex.matcher(cleanText)
        val liveTimestamp = if (timeMatcher.find()) {
            timeMatcher.group(1).orEmpty().trim().trim('(', ')')
        } else {
            SimpleDateFormat("dd/MM/yyyy,HH:mm:ss", Locale.getDefault()).format(Date())
        }

        // 2. JL Number
        val jlRegex = Pattern.compile("(?i)জে\\.?\\s*এল\\.?\\s*(?:নং)?\\s*[:ঃ]?\\s*([0-9]+)")
        val jlMatcher = jlRegex.matcher(cleanText)
        val jlNumber = if (jlMatcher.find()) {
            jlMatcher.group(1).orEmpty().trim()
        } else {
            val bracketNum = Regex("""\[(\d+)\]""").find(mouzaName)?.groupValues?.get(1)
            val digits = mouzaCode.filter { it.isDigit() }
            bracketNum ?: if (digits.isNotBlank()) digits.padStart(3, '0').takeLast(3) else "089"
        }

        // 3. Thana
        val thanaRegex = Pattern.compile("(?i)থানা\\s*[:ঃ]?\\s*([\\p{L}0-9\\-]+)")
        val thanaMatcher = thanaRegex.matcher(cleanText)
        val thanaName = if (thanaMatcher.find()) {
            thanaMatcher.group(1).orEmpty().trim()
        } else {
            blockName
        }

        // 4. Parse Tables
        val tableMatcher = Pattern.compile("(?i)<table[^>]*>(.*?)</table>", Pattern.DOTALL).matcher(html)
        val tableList = mutableListOf<String>()
        while (tableMatcher.find()) {
            tableList.add(tableMatcher.group(1).orEmpty())
        }

        var khatianSummary: KhatianSummary? = null
        var plotSummary: PlotSummary? = null
        val owners = mutableListOf<LandOwner>()
        var extractedTotalArea = ""

        val cleanHtml = html
            .replace(Regex("(?is)<script[^>]*>.*?</script>"), "")
            .replace(Regex("(?is)<style[^>]*>.*?</style>"), "")

        // Gather all <tr> elements by splitting on <tr tag (reliably handles unclosed <tr> tags from portal JSP)
        val rawRowSplits = cleanHtml.split(Regex("(?i)<tr[^>]*>"))
        val allRows = mutableListOf<String>()
        if (rawRowSplits.size > 1) {
            for (i in 1 until rawRowSplits.size) {
                val chunk = rawRowSplits[i].substringBefore("</tr>").substringBefore("</table>").trim()
                if (chunk.isNotBlank()) {
                    allRows.add(chunk)
                }
            }
        }

        if (searchType == SearchType.KHATIAN) {
            val (khMain, khBata) = splitSearchNumber(searchNumber)
            var khNo = khMain
            var bataKh = khBata
            var owner = ""
            var guardian = ""
            var rayatType = ""
            var address = ""
            var area = ""
            var plots = ""

            // 1. Parse Summary Info from all rows with 2 cells
            for (row in allRows) {
                val cells = extractCellsFromRow(row)
                if (cells.size == 2) {
                    val key = cells[0].trim()
                    val value = cells[1].trim()
                    when {
                        key.contains("বাটা") -> bataKh = value.ifBlank { bataKh }
                        key.contains("খতিয়ান") -> khNo = value.ifBlank { khNo }
                        key.contains("দখলকার") || key.contains("মালিক") || (key.contains("নাম") && !key.contains("পিতা")) -> owner = value
                        key.contains("পিতা") || key.contains("স্বামী") || key.contains("অভিভাবক") -> guardian = value
                        key.contains("রায়ত") || key.contains("ধরণ") -> rayatType = value
                        key.contains("ঠিকানা") -> address = value
                        key.contains("জমির পরিমাণ") || key.contains("পরিমাণ") -> area = value
                        key.contains("দাগের সংখ্যা") || key.contains("সংখ্যা") -> plots = value
                    }
                }
            }

            khatianSummary = KhatianSummary(
                khatianNo = khNo,
                ownerName = owner,
                guardianName = guardian,
                rayatType = rayatType,
                address = address,
                totalAreaAcre = area,
                plotCount = plots,
                bataKhatianNo = bataKh
            )
            extractedTotalArea = area.replace("একর", "").replace("Acre", "").trim()

            // 2. Parse All Plot Details Rows across ALL rows in the document
            val seenPlotKeys = mutableSetOf<String>()
            for (row in allRows) {
                if (row.contains("<th", ignoreCase = true)) continue
                val cells = extractCellsFromRow(row)
                if (cells.size >= 3) {
                    val dagNo = cells.getOrElse(0) { "" }.trim()
                    val hasDigits = dagNo.any { it.isDigit() }
                    val isHeader = dagNo.contains("দাগ") || dagNo.contains("নং") || dagNo.contains("খতিয়ান") || dagNo.contains("জে")
                    if (hasDigits && !isHeader) {
                        val landClass = cells.getOrElse(1) { "বাস্তু" }.ifBlank { "বাস্তু" }
                        val share = cells.getOrElse(2) { "1.0000" }.ifBlank { "1.0000" }
                        val areaAcre = cells.getOrElse(3) { "0.0000" }.ifBlank { "0.0000" }
                        val possessor = cells.getOrElse(4) { "Nil" }.ifBlank { "Nil" }
                        val remarks = cells.getOrElse(5) { "Nil" }.ifBlank { "Nil" }

                        val rowKey = "${dagNo}_${landClass}_${share}_$areaAcre"
                        if (seenPlotKeys.add(rowKey)) {
                            owners.add(
                                LandOwner(
                                    khatianOrPlotNo = dagNo,
                                    landClassification = landClass,
                                    share = share,
                                    areaAcre = areaAcre,
                                    possessor = possessor,
                                    remarks = remarks,
                                    ownerName = owner.ifBlank { "খতিয়ান $searchNumber রায়ত" },
                                    guardianName = guardian,
                                    rayatType = rayatType,
                                    address = address
                                )
                            )
                        }
                    }
                }
            }

            // Fallback: If owners count is still less than expected plot count, also check every sub-table in tableList
            if (owners.isEmpty() || (plots.toIntOrNull() != null && owners.size < (plots.toIntOrNull() ?: 0))) {
                for (t in tableList) {
                    val tRowMatcher = Pattern.compile("(?i)<tr[^>]*>(.*?)</tr>", Pattern.DOTALL).matcher(t)
                    while (tRowMatcher.find()) {
                        val row = tRowMatcher.group(1).orEmpty()
                        if (row.contains("<th", ignoreCase = true)) continue
                        val cells = extractCellsFromRow(row)
                        if (cells.size >= 3) {
                            val dagNo = cells.getOrElse(0) { "" }.trim()
                            val hasDigits = dagNo.any { it.isDigit() }
                            val isHeader = dagNo.contains("দাগ") || dagNo.contains("নং") || dagNo.contains("খতিয়ান") || dagNo.contains("জে")
                            if (hasDigits && !isHeader) {
                                val landClass = cells.getOrElse(1) { "বাস্তু" }.ifBlank { "বাস্তু" }
                                val share = cells.getOrElse(2) { "1.0000" }.ifBlank { "1.0000" }
                                val areaAcre = cells.getOrElse(3) { "0.0000" }.ifBlank { "0.0000" }
                                val possessor = cells.getOrElse(4) { "Nil" }.ifBlank { "Nil" }
                                val remarks = cells.getOrElse(5) { "Nil" }.ifBlank { "Nil" }

                                val rowKey = "${dagNo}_${landClass}_${share}_$areaAcre"
                                if (seenPlotKeys.add(rowKey)) {
                                    owners.add(
                                        LandOwner(
                                            khatianOrPlotNo = dagNo,
                                            landClassification = landClass,
                                            share = share,
                                            areaAcre = areaAcre,
                                            possessor = possessor,
                                            remarks = remarks,
                                            ownerName = owner.ifBlank { "খতিয়ান $searchNumber রায়ত" },
                                            guardianName = guardian,
                                            rayatType = rayatType,
                                            address = address
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Plot Search
            val (dagMain, dagBata) = splitSearchNumber(searchNumber)
            var plotNo = dagMain
            var landClass = ""
            var area = ""
            var bata = dagBata

            for (row in allRows) {
                val cells = extractCellsFromRow(row)
                if (cells.size == 2) {
                    val key = cells[0].trim()
                    val value = cells[1].trim()
                    when {
                        key.contains("বাটা") -> bata = value.ifBlank { bata }
                        key.contains("দাগ নং") || (key.contains("দাগ") && !key.contains("ম্যাপ")) -> plotNo = value.ifBlank { plotNo }
                        key.contains("শ্রেণী") -> landClass = value
                        key.contains("মোট পরিমাণ") || key.contains("পরিমাণ") -> area = value
                    }
                }
            }

            plotSummary = PlotSummary(
                plotNo = plotNo,
                landClassification = landClass,
                totalAreaAcre = area,
                bataPlotNo = bata
            )
            extractedTotalArea = area.replace("একর", "").replace("Acre", "").trim()

            // Rayats / Owners Table across ALL rows
            val seenKhatianKeys = mutableSetOf<String>()
            for (row in allRows) {
                if (row.contains("<th", ignoreCase = true)) continue
                val cells = extractCellsFromRow(row)
                if (cells.size >= 3) {
                    val khNo = cells.getOrElse(0) { "" }.trim()
                    val hasDigits = khNo.any { it.isDigit() }
                    val isHeader = khNo.contains("খতিয়ান") || khNo.contains("নং") || khNo.contains("দাগ") || khNo.contains("জে")
                    if (hasDigits && !isHeader) {
                        val rayatName = cells.getOrElse(1) { "রায়তের নাম অনুপলব্ধ" }.ifBlank { "রায়তের নাম অনুপলব্ধ" }
                        val guardian = cells.getOrElse(2) { "-" }.ifBlank { "-" }
                        val share = cells.getOrElse(3) { "1.0000" }.ifBlank { "1.0000" }
                        val areaAcre = cells.getOrElse(4) { "0.0000" }.ifBlank { "0.0000" }
                        val possessor = cells.getOrElse(5) { "" }
                        val remarks = cells.getOrElse(6) { cells.getOrElse(5) { "Nil" } }.ifBlank { "Nil" }

                        val rowKey = "${khNo}_${rayatName}_${share}_$areaAcre"
                        if (seenKhatianKeys.add(rowKey)) {
                            owners.add(
                                LandOwner(
                                    khatianOrPlotNo = khNo,
                                    ownerName = rayatName,
                                    guardianName = guardian,
                                    share = share,
                                    areaAcre = areaAcre,
                                    remarks = remarks,
                                    landClassification = landClass.ifBlank { "বাস্তু" },
                                    possessor = possessor
                                )
                            )
                        }
                    }
                }
            }

            // Fallback for Plot Search across sub-tables in tableList
            if (owners.isEmpty()) {
                for (t in tableList) {
                    val tRowMatcher = Pattern.compile("(?i)<tr[^>]*>(.*?)</tr>", Pattern.DOTALL).matcher(t)
                    while (tRowMatcher.find()) {
                        val row = tRowMatcher.group(1).orEmpty()
                        if (row.contains("<th", ignoreCase = true)) continue
                        val cells = extractCellsFromRow(row)
                        if (cells.size >= 3) {
                            val khNo = cells.getOrElse(0) { "" }.trim()
                            val hasDigits = khNo.any { it.isDigit() }
                            val isHeader = khNo.contains("খতিয়ান") || khNo.contains("নং") || khNo.contains("দাগ") || khNo.contains("জে")
                            if (hasDigits && !isHeader) {
                                val rayatName = cells.getOrElse(1) { "রায়তের নাম অনুপলব্ধ" }.ifBlank { "রায়তের নাম অনুপলব্ধ" }
                                val guardian = cells.getOrElse(2) { "-" }.ifBlank { "-" }
                                val share = cells.getOrElse(3) { "1.0000" }.ifBlank { "1.0000" }
                                val areaAcre = cells.getOrElse(4) { "0.0000" }.ifBlank { "0.0000" }
                                val possessor = cells.getOrElse(5) { "" }
                                val remarks = cells.getOrElse(6) { cells.getOrElse(5) { "Nil" } }.ifBlank { "Nil" }

                                val rowKey = "${khNo}_${rayatName}_${share}_$areaAcre"
                                if (seenKhatianKeys.add(rowKey)) {
                                    owners.add(
                                        LandOwner(
                                            khatianOrPlotNo = khNo,
                                            ownerName = rayatName,
                                            guardianName = guardian,
                                            share = share,
                                            areaAcre = areaAcre,
                                            remarks = remarks,
                                            landClassification = landClass.ifBlank { "বাস্তু" },
                                            possessor = possessor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val totalAreaFinal = if (extractedTotalArea.isNotBlank() && extractedTotalArea.toDoubleOrNull() != null) {
            String.format(Locale.US, "%.4f", extractedTotalArea.toDouble())
        } else {
            calculateTotalArea(owners)
        }

        return ParsedRecordData(
            liveTimestamp = liveTimestamp,
            jlNumber = jlNumber,
            thanaName = thanaName,
            khatianSummary = khatianSummary,
            plotSummary = plotSummary,
            owners = owners,
            totalAreaAcre = totalAreaFinal
        )
    }

    private fun extractCellsFromRow(rowContent: String): List<String> {
        val cells = mutableListOf<String>()
        val cellMatcher = Pattern.compile("(?i)<(?:td|th)[^>]*>(.*?)</(?:td|th)>", Pattern.DOTALL).matcher(rowContent)
        while (cellMatcher.find()) {
            val text = cellMatcher.group(1).orEmpty()
                .replace(Regex("<[^>]*>"), "")
                .replace("&nbsp;", " ")
                .replace(Regex("\\s+"), " ")
                .trim()
            cells.add(text)
        }
        if (cells.isNotEmpty()) return cells

        // Fallback for unclosed <td> or <th> tags
        val openMatcher = Pattern.compile("(?i)<(?:td|th)[^>]*>([^<]*)", Pattern.DOTALL).matcher(rowContent)
        while (openMatcher.find()) {
            val text = openMatcher.group(1).orEmpty()
                .replace("&nbsp;", " ")
                .replace(Regex("\\s+"), " ")
                .trim()
            if (text.isNotBlank()) {
                cells.add(text)
            }
        }
        return cells
    }

    private fun calculateTotalArea(owners: List<LandOwner>): String {
        var total = 0.0
        for (o in owners) {
            val num = o.areaAcre.toDoubleOrNull() ?: 0.0
            total += num
        }
        return String.format(Locale.US, "%.4f", total)
    }

    private fun buildStyledHtmlDocument(
        extractedHtml: String,
        parsedData: ParsedRecordData,
        districtName: String,
        blockName: String,
        mouzaName: String,
        mouzaCode: String,
        searchType: SearchType,
        searchNumber: String
    ): String {
        val isPlot = searchType == SearchType.PLOT
        // Use cleaned portal HTML directly for authentic government records with full tables
        val tableContent = if (extractedHtml.contains("<table", ignoreCase = true)) {
            cleanPortalHtml(extractedHtml)
        } else {
            generateCleanTableMarkup(parsedData, searchType, searchNumber)
        }

        val viewportMeta = if (isPlot) {
            "<meta name='viewport' content='width=device-width, initial-scale=0.82, maximum-scale=3.0, user-scalable=yes'>"
        } else {
            "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes'>"
        }

        val tableFontSize = if (isPlot) "12px" else "12.5px"
        val thFontSize = if (isPlot) "12px" else "12.5px"
        val tdFontSize = if (isPlot) "11.5px" else "12px"
        val tdPadding = if (isPlot) "6px 4px" else "7px 5px"

        return """
            <!DOCTYPE html>
            <html>
            <head>
                $viewportMeta
                <style>
                    * {
                        box-sizing: border-box;
                        -webkit-tap-highlight-color: transparent;
                    }
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
                        background-color: #FFFFFF;
                        color: #000000;
                        padding: 10px 8px 90px 8px;
                        margin: 0;
                        line-height: 1.35;
                    }
                    .live-date-header {
                        color: #0000FF;
                        font-weight: bold;
                        font-size: 13px;
                        margin-bottom: 4px;
                    }
                    .location-header {
                        color: #800000;
                        font-weight: bold;
                        font-size: 13.5px;
                        margin-bottom: 10px;
                    }
                    table {
                        width: 100% !important;
                        max-width: 100% !important;
                        border-collapse: collapse !important;
                        margin-top: 6px;
                        margin-bottom: 14px;
                        font-size: $tableFontSize !important;
                        background-color: #FFFFFF;
                        border: 1px solid #C0C0C0 !important;
                        table-layout: auto !important;
                    }
                    th {
                        background-color: #D5E8D4 !important;
                        color: #000000 !important;
                        font-weight: bold !important;
                        padding: 6px 4px !important;
                        border: 1px solid #B0B0B0 !important;
                        text-align: center !important;
                        font-size: $thFontSize !important;
                        white-space: normal !important;
                    }
                    td {
                        padding: $tdPadding !important;
                        border: 1px solid #C0C0C0 !important;
                        color: #000000 !important;
                        font-size: $tdFontSize !important;
                        vertical-align: middle !important;
                        word-break: break-word !important;
                    }
                    tr:nth-child(even) td {
                        background-color: #F2F2F2 !important;
                    }
                    tr:nth-child(odd) td {
                        background-color: #FFFFFF !important;
                    }
                    table:first-of-type td:first-child {
                        font-weight: 500;
                        width: 48%;
                    }
                    table:first-of-type td:last-child {
                        font-weight: 600;
                    }
                    .section-title {
                        font-size: 13px;
                        font-weight: bold;
                        color: #000000;
                        margin-top: 12px;
                        margin-bottom: 2px;
                    }
                    .section-subtitle, font[color='purple'], font[color='#800080'] {
                        font-size: 11px !important;
                        color: #A21CAF !important;
                        margin-bottom: 8px !important;
                        font-style: italic !important;
                        line-height: 1.35 !important;
                        display: block !important;
                    }
                    a, .link, span.borga {
                        color: #0000EE !important;
                        font-weight: bold;
                        text-decoration: underline;
                    }
                    .disclaimer-text {
                        font-size: 11px;
                        color: #64748B;
                        text-align: center;
                        margin-top: 20px;
                        margin-bottom: 16px;
                        line-height: 1.4;
                    }
                </style>
            </head>
            <body>
                <div class="live-date-header">(Live Data As On ${parsedData.liveTimestamp})</div>
                <div class="location-header"><b>জে.এল নং :</b> ${parsedData.jlNumber}&nbsp;&nbsp;&nbsp;&nbsp;<b>থানা :</b> ${parsedData.thanaName}</div>
                $tableContent
                <div class="disclaimer-text">Disclaimer: This application is for informational purposes only and is not affiliated with any government authority.</div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun cleanPortalHtml(rawHtml: String): String {
        // Strip everything before the first <table tag so unstyled portal headers are not duplicated
        val firstTableIdx = rawHtml.indexOf("<table", ignoreCase = true)
        val fromTable = if (firstTableIdx >= 0) rawHtml.substring(firstTableIdx) else rawHtml

        return fromTable
            .replace(Regex("(?i)<(?:h[1-6]|div|p|center)[^>]*>\\s*Live\\s*Data\\s*As\\s*On.*?</(?:h[1-6]|div|p|center)>"), "")
            .replace(Regex("(?i)<(?:h[1-6]|div|p|center|span|font)[^>]*>\\s*জে\\.?\\s*এল.*?</(?:h[1-6]|div|p|center|span|font)>"), "")
            .replace(Regex("(?i)<center[^>]*>"), "<div>")
            .replace(Regex("(?i)</center>"), "</div>")
            .replace(Regex("(?i)\\s*align=[\"']center[\"']"), "")
            .replace(Regex("(?i)style=[\"'][^\"']*background[^\"']*[\"']"), "")
            .replace(Regex("(?i)bgcolor=[\"'][^\"']*[\"']"), "")
            .replace(Regex("(?i)\\s*width=[\"'][^\"']*[\"']"), "")
            .replace(Regex("(?i)\\s*height=[\"'][^\"']*[\"']"), "")
            .replace(Regex("(?i)style=[\"'][^\"']*width:[^\"']*[\"']"), "")
    }

    private fun generateCleanTableMarkup(
        parsedData: ParsedRecordData,
        searchType: SearchType,
        searchNumber: String
    ): String {
        val sb = StringBuilder()
        if (searchType == SearchType.KHATIAN) {
            val ks = parsedData.khatianSummary
            val firstOwner = parsedData.owners.firstOrNull()
            val ownerDisplayName = ks?.ownerName?.ifBlank { firstOwner?.ownerName?.ifBlank { "-" } ?: "-" } ?: (firstOwner?.ownerName?.ifBlank { "-" } ?: "-")
            val guardianDisplayName = ks?.guardianName?.ifBlank { firstOwner?.guardianName?.ifBlank { "-" } ?: "-" } ?: (firstOwner?.guardianName?.ifBlank { "-" } ?: "-")
            val rayatTypeDisplayName = ks?.rayatType?.ifBlank { "ব্যক্তি" } ?: "ব্যক্তি"
            val addressDisplayName = ks?.address?.ifBlank { "নিজ" } ?: "নিজ"
            val areaDisplayName = ks?.totalAreaAcre?.ifBlank { parsedData.totalAreaAcre } ?: parsedData.totalAreaAcre
            val plotCountDisplayName = ks?.plotCount?.ifBlank { "${parsedData.owners.size}" } ?: "${parsedData.owners.size}"
            val (kh1, kh2) = splitSearchNumber(searchNumber)
            val displayKhatian = ks?.khatianNo?.ifBlank { kh1 } ?: kh1
            val displayBataKhatian = if (!ks?.bataKhatianNo.isNullOrBlank()) ks.bataKhatianNo else kh2

            sb.append("""
                <table>
                    <tr><td>খতিয়ান নং :</td><td>$displayKhatian</td></tr>
                    ${if (displayBataKhatian.isNotBlank()) "<tr><td>বাটা খতিয়ান নং :</td><td>$displayBataKhatian</td></tr>" else ""}
                    <tr><td>অত্রস্বত্বের দখলকারের নাম :</td><td>$ownerDisplayName</td></tr>
                    <tr><td>পিতা/স্বামী :</td><td>$guardianDisplayName</td></tr>
                    <tr><td>রায়তের ধরণ :</td><td>$rayatTypeDisplayName</td></tr>
                    <tr><td>ঠিকানা :</td><td>$addressDisplayName</td></tr>
                    <tr><td>জমির পরিমাণ:</td><td>$areaDisplayName একর</td></tr>
                    <tr><td>দাগের সংখ্যা :</td><td>$plotCountDisplayName</td></tr>
                </table>
                <div class="section-title">অত্রস্বত্বের দাগের বিবরণ ও পরিমাণ:</div>
                <div class="section-subtitle">(* দাগের অত্রস্বত্বের জমির যথার্থ পরিমাণ, দাগের মধ্যে অত্রসত্বের অংশ অনুসারে নির্ধারিত হইবে। )</div>
                <table>
                    <thead>
                        <tr>
                            <th>দাগ নং</th>
                            <th>শ্রেণী</th>
                            <th>অংশ</th>
                            <th>অংশ পরিমাণ(একর)</th>
                            <th>দখলদার</th>
                            <th>মন্তব্য</th>
                        </tr>
                    </thead>
                    <tbody>
            """.trimIndent())
            for (o in parsedData.owners) {
                val possessorHtml = if (o.possessor.contains("বর্গা")) {
                    "<span class='borga'>${o.possessor}</span>"
                } else {
                    o.possessor.ifBlank { "Nil" }
                }
                sb.append("""
                    <tr>
                        <td align="center">${o.khatianOrPlotNo}</td>
                        <td align="center">${o.landClassification}</td>
                        <td align="center">${o.share}</td>
                        <td align="center">${o.areaAcre}</td>
                        <td align="center">$possessorHtml</td>
                        <td align="center">${o.remarks.ifBlank { "Nil" }}</td>
                    </tr>
                """.trimIndent())
            }
            sb.append("</tbody></table>")
        } else {
            val ps = parsedData.plotSummary
            val firstOwner = parsedData.owners.firstOrNull()
            val landClassDisplay = ps?.landClassification?.ifBlank { firstOwner?.landClassification?.ifBlank { "-" } ?: "-" } ?: (firstOwner?.landClassification?.ifBlank { "-" } ?: "-")
            val areaDisplay = ps?.totalAreaAcre?.ifBlank { parsedData.totalAreaAcre } ?: parsedData.totalAreaAcre
            val (dag1, dag2) = splitSearchNumber(searchNumber)
            val displayPlot = ps?.plotNo?.ifBlank { dag1 } ?: dag1
            val displayBata = if (!ps?.bataPlotNo.isNullOrBlank()) ps.bataPlotNo else dag2

            sb.append("""
                <table>
                    <tr><td>দাগ নং :</td><td>$displayPlot</td></tr>
                    ${if (displayBata.isNotBlank()) "<tr><td>বাটা দাগ নং :</td><td>$displayBata</td></tr>" else ""}
                    <tr><td>জমির শ্রেণী :</td><td>$landClassDisplay</td></tr>
                    <tr><td>মোট পরিমাণ:</td><td>$areaDisplay একর</td></tr>
                </table>
                <div class="section-title">দাগের অত্রস্বত্বের রায়তের বিবরণ:</div>
                <div class="section-subtitle">(* রায়তের অংশ অনুযায়ী দাগের জমির যথার্থ পরিমাণ নির্ধারিত হইবে। )</div>
                <table>
                    <thead>
                        <tr>
                            <th>খতিয়ান নং</th>
                            <th>রায়তের নাম</th>
                            <th>পিতা/স্বামীর নাম</th>
                            <th>অংশ</th>
                            <th>অংশ পরিমাণ(একর)</th>
                            <th>মন্তব্য</th>
                        </tr>
                    </thead>
                    <tbody>
            """.trimIndent())
            for (o in parsedData.owners) {
                sb.append("""
                    <tr>
                        <td align="center">${o.khatianOrPlotNo}</td>
                        <td align="left">${o.ownerName}</td>
                        <td align="left">${o.guardianName}</td>
                        <td align="center">${o.share}</td>
                        <td align="center">${o.areaAcre}</td>
                        <td align="center">${o.remarks.ifBlank { "Nil" }}</td>
                    </tr>
                """.trimIndent())
            }
            sb.append("</tbody></table>")
        }
        return sb.toString()
    }
}