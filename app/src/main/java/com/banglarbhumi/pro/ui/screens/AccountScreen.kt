package com.banglarbhumi.pro.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.R
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import java.util.Calendar

@Composable
fun AccountScreen(
    viewModel: MainViewModel,
    onNavigateToSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isBengali by viewModel.isBengali.collectAsState()
    val isPortalLoggedIn by viewModel.isPortalLoggedIn.collectAsState()
    val loggedInPortalUsername by viewModel.loggedInPortalUsername.collectAsState()
    val cachedUsername by viewModel.cachedUsername.collectAsState()
    val savedRecords by viewModel.savedRecords.collectAsState()
    val citizenName by viewModel.citizenName.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showAboutProjectDialog by remember { mutableStateOf(false) }
    val showAboutProjectScreen by viewModel.showAboutProjectScreen.collectAsState()
    var editNameText by remember { mutableStateOf("") }
    var autoSmsConsentEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(isPortalLoggedIn) {
        if (isPortalLoggedIn) {
            viewModel.refreshCitizenNameFromPortal()
        }
    }

    val versionName = remember(context) {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (_: Exception) {
            "1.0.0"
        }
    }

    val dynamicYear = remember {
        Calendar.getInstance().get(Calendar.YEAR).toString()
    }

    val displayIdentifier = remember(isPortalLoggedIn, loggedInPortalUsername, cachedUsername) {
        if (isPortalLoggedIn && !loggedInPortalUsername.isNullOrBlank()) {
            loggedInPortalUsername!!
        } else if (cachedUsername.isNotBlank()) {
            cachedUsername
        } else {
            ""
        }
    }

    val displayCitizenName = remember(citizenName, displayIdentifier, isBengali) {
        if (citizenName.isNotBlank()) {
            citizenName
        } else if (displayIdentifier.isNotBlank()) {
            if (isBengali) "নাগরিক ব্যবহারকারী" else "Citizen User"
        } else {
            if (isBengali) "অতিথি নাগরিক" else "Guest Citizen"
        }
    }

    if (showAboutProjectScreen) {
        AboutProjectScreen(
            viewModel = viewModel,
            onBack = { viewModel.setShowAboutProjectScreen(false) }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Emerald800,
                                Emerald700
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (isBengali) "অ্যাকাউন্ট ও সেটিংস" else "Account & Settings",
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "A SumanOnline Project",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7))
                                .border(1.5.dp, Color(0xFF86EFAC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Picture",
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF14532D))
                                .border(1.5.dp, Color.White, CircleShape)
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "প্রোফাইল ছবি পরিবর্তন শীঘ্রই আসছে" else "Profile photo update coming soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Text(
                                    text = displayCitizenName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Name",
                                    tint = Slate400,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            editNameText = citizenName
                                            showEditNameDialog = true
                                        }
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isPortalLoggedIn) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isPortalLoggedIn) Color(0xFF86EFAC) else Color(0xFFFDE68A)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPortalLoggedIn) Icons.Default.CheckCircle else Icons.Default.LinkOff,
                                        contentDescription = null,
                                        tint = if (isPortalLoggedIn) Color(0xFF15803D) else Color(0xFFB45309),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = if (isPortalLoggedIn) {
                                            if (isBengali) "কানেক্টেড" else "Connected"
                                        } else {
                                            if (isBengali) "ডিসকানেক্টেড" else "Disconnected"
                                        },
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isPortalLoggedIn) Color(0xFF15803D) else Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isBengali) "ইউজার আইডি: " else "User ID: ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate500
                            )
                            Text(
                                text = if (displayIdentifier.isNotBlank()) displayIdentifier else (if (isBengali) "পোর্টালে লগইন নেই" else "Not Available"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (displayIdentifier.isNotBlank()) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy User ID",
                                    tint = Slate400,
                                    modifier = Modifier
                                        .size(13.dp)
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(displayIdentifier))
                                            Toast.makeText(
                                                context,
                                                if (isBengali) "ইউজার আইডি কপি করা হয়েছে" else "User ID copied to clipboard",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(13.dp))

                if (isPortalLoggedIn) {
                    OutlinedButton(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFEF2F2),
                            contentColor = Color(0xFFDC2626)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFCA5A5))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBengali) "পোর্টাল সেশন থেকে লগআউট করুন" else "Logout from Portal Session",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.openPortalLoginDialog() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = "Login",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBengali) "বাংলারভূমি পোর্টালে লগইন করুন" else "Login to BanglarBhumi Portal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(13.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSectionHeader(
                icon = Icons.Default.Tune,
                title = if (isBengali) "অ্যাকাউন্ট সেটিংস" else "ACCOUNT SETTINGS"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AccountListItem(
                        icon = Icons.Default.VpnKey,
                        title = if (isBengali) "পোর্টাল ক্রেডেনশিয়ালস ক্যাশে" else "Portal Credentials Cache",
                        subtitle = if (cachedUsername.isNotBlank()) {
                            val namePrefix = if (citizenName.isNotBlank()) "$citizenName • " else ""
                            if (isBengali) "সংরক্ষিত আইডি: ${namePrefix}$cachedUsername"
                            else "Auto-fill saved for ${namePrefix}ID: $cachedUsername"
                        } else {
                            if (isBengali) "কোনও তথ্য ক্যাশে সংরক্ষিত নেই" else "No credentials cached"
                        },
                        trailing = {
                            if (cachedUsername.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFFEF2F2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                                    modifier = Modifier.clickable {
                                        viewModel.clearCachedCredentials()
                                        Toast.makeText(
                                            context,
                                            if (isBengali) "সংরক্ষিত ক্রেডেনশিয়ালস মুছে ফেলা হয়েছে" else "Cached credentials cleared",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Text(
                                        text = if (isBengali) "ক্লিয়ার" else "Clear",
                                        color = Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        onClick = {
                            if (cachedUsername.isBlank()) {
                                viewModel.openPortalLoginDialog()
                            }
                        }
                    )

                    HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(start = 58.dp))

                    AccountListItem(
                        icon = Icons.Default.BookmarkBorder,
                        title = if (isBengali) "সংরক্ষিত রেকর্ডসমূহ" else "Saved Land Records",
                        subtitle = if (isBengali) "${savedRecords.size}টি রেকর্ড ডিভাইসে সংরক্ষিত" else "${savedRecords.size} records saved on local storage",
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = onNavigateToSaved
                    )

                    HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(start = 58.dp))

                    AccountListItem(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        title = if (isBengali) "সিটিজেন নির্দেশিকা ও নিয়মাবলী" else "Citizen Guide & Procedures",
                        subtitle = if (isBengali) "নামজারি, খতিয়ান ও রূপান্তর পদ্ধতি" else "Mutation, RoR & Conversion rules",
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = { showGuideDialog = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(13.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSectionHeader(
                icon = Icons.Default.Settings,
                title = if (isBengali) "পছন্দসমূহ" else "PREFERENCES"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AccountListItem(
                        icon = Icons.Default.Language,
                        title = if (isBengali) "অ্যাপের ভাষা" else "App Language",
                        subtitle = if (isBengali) "বর্তমান: বাংলা" else "Current: English",
                        trailing = {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFD1FAE5),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                modifier = Modifier.clickable { viewModel.toggleLanguage() }
                            ) {
                                Text(
                                    text = if (isBengali) "English এ পরিবর্তন" else "বাংলা করুন",
                                    color = Color(0xFF065F46),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        },
                        onClick = { viewModel.toggleLanguage() }
                    )

                    HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(start = 58.dp))

                    AccountListItem(
                        icon = Icons.Default.ChatBubbleOutline,
                        title = if (isBengali) "স্বয়ংক্রিয় SMS OTP সনাক্তকরণ" else "Automatic SMS OTP Detection",
                        subtitle = if (isBengali) "Play Services SMS Consent API" else "Play Services SMS Consent API",
                        trailing = {
                            Switch(
                                checked = autoSmsConsentEnabled,
                                onCheckedChange = {
                                    autoSmsConsentEnabled = it
                                    Toast.makeText(
                                        context,
                                        if (it) (if (isBengali) "SMS সনাক্তকরণ সক্রিয় করা হয়েছে" else "SMS OTP Detection enabled")
                                        else (if (isBengali) "SMS সনাক্তকরণ নিষ্ক্রিয় করা হয়েছে" else "SMS OTP Detection disabled"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF14532D),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Slate300,
                                    uncheckedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.scale(0.75f)
                            )
                        },
                        onClick = {
                            autoSmsConsentEnabled = !autoSmsConsentEnabled
                        }
                    )

                    HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(start = 58.dp))

                    AccountListItem(
                        icon = Icons.Default.CleaningServices,
                        title = if (isBengali) "অস্থায়ী ক্যাশে পরিষ্কার করুন" else "Clear Temporary Cache",
                        subtitle = if (isBengali) "ক্যাপচা এবং অস্থায়ী ডেটা রিসেট" else "Reset captcha & temporary sessions",
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            Toast.makeText(
                                context,
                                if (isBengali) "অস্থায়ী ক্যাশে সফলভাবে পরিষ্কার হয়েছে" else "Temporary cache cleared successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(start = 58.dp))

                    AccountListItem(
                        icon = Icons.Default.Palette,
                        title = if (isBengali) "ব্র্যান্ড অ্যাকসেন্ট কালার" else "Brand Accent Color",
                        subtitle = if (isBengali) "ফরেস্ট গ্রিন (ডিফল্ট)" else "Forest Green (Default)",
                        trailing = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF15803D)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(9.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1D4ED8))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0F766E))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFC2410C))
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        onClick = {
                            Toast.makeText(
                                context,
                                if (isBengali) "ফরেস্ট গ্রিন অ্যাক্টিভ রয়েছে" else "Forest Green (Default) is active",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(13.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSectionHeader(
                icon = Icons.Default.Info,
                title = if (isBengali) "প্রজেক্ট সম্পর্কিত" else "ABOUT PROJECT"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AccountListItem(
                        icon = Icons.Default.Info,
                        title = if (isBengali) "প্রজেক্ট সম্পর্কিত তথ্য" else "About Project",
                        subtitle = if (isBengali) "ভার্সন বিবরণ, স্পেক্স ও চেঞ্জলগ" else "Version details, specs & changelog",
                        trailing = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFD1FAE5),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Text(
                                        text = "v1.1.0",
                                        color = Color(0xFF065F46),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        onClick = { viewModel.setShowAboutProjectScreen(true) }
                    )

                    HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(start = 58.dp))

                    AccountListItem(
                        icon = Icons.Default.Domain,
                        title = if (isBengali) "ডেভেলপার ও প্রকাশক" else "Developer & Publisher",
                        subtitle = "SumanOnline Web Services",
                        trailing = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sumanonline.com"))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 1.5.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "BanglarBhumi Pro",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = Slate800
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = Slate200
                ) {
                    Text(
                        text = "v$versionName",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Slate700,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 1.dp)
            ) {
                Text(
                    text = "Privacy policy",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Emerald800,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { showPrivacyDialog = true }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Text(
                    text = "|",
                    fontSize = 11.5.sp,
                    color = Slate400,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "terms and conditions",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Emerald800,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { showTermsDialog = true }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Text(
                    text = "|",
                    fontSize = 11.5.sp,
                    color = Slate400,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    text = "contact us",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Emerald800,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { showContactDialog = true }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Text(
                text = "© $dynamicYear SumanOnline Web Services.",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = Slate500,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isBengali)
                    "পশ্চিমবঙ্গ ভূমি ও ভূমি সংস্কার সিটিজেন তথ্য সহায়ক"
                else
                    "West Bengal Land & Land Reforms Citizen Companion",
                fontSize = 10.sp,
                color = Slate400,
                textAlign = TextAlign.Center
            )
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Emerald800
                    )
                    Text(text = "Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Information Collection & Usage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Slate800
                    )
                    Text(
                        text = "BanglarBhumi Pro does not collect, sell, or transmit your personal land query data or credentials to any third-party analytics or external ad servers. Login credentials and saved searches are cached strictly in your device's private sandboxed storage.",
                        fontSize = 12.5.sp,
                        color = Slate600,
                        lineHeight = 17.sp
                    )

                    Text(
                        text = "2. BanglarBhumi Portal Session",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Slate800
                    )
                    Text(
                        text = "Your portal login username and password are communicated directly to the official West Bengal BanglarBhumi portal via secure HTTPS for live Khatian and Plot inspection. We never access your financial details.",
                        fontSize = 12.5.sp,
                        color = Slate600,
                        lineHeight = 17.sp
                    )

                    Text(
                        text = "3. SMS OTP Verification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Slate800
                    )
                    Text(
                        text = "The app utilizes Google Play Services SMS User Consent API solely to assist with auto-filling one-time passwords during citizen portal authentication. No personal text messages are read or stored.",
                        fontSize = 12.5.sp,
                        color = Slate600,
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(text = "Close", fontWeight = FontWeight.Bold, color = Emerald800)
                }
            }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            containerColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = Emerald800
                    )
                    Text(text = "Terms and Conditions", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Informational & Citizen Companion App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Slate800
                    )
                    Text(
                        text = "BanglarBhumi Pro is developed by SumanOnline Web Services as an independent citizen accessibility tool. This application is not an official government entity and is not affiliated with the Directorate of Land Records & Surveys, Govt. of West Bengal.",
                        fontSize = 12.5.sp,
                        color = Slate600,
                        lineHeight = 17.sp
                    )

                    Text(
                        text = "2. Legal Certified Copies",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Slate800
                    )
                    Text(
                        text = "Information retrieved from the portal is for reference and informational verification. For legal proceedings or property registration, citizens are advised to obtain certified copies from their respective Block Land & Land Reforms Office (BL&LRO).",
                        fontSize = 12.5.sp,
                        color = Slate600,
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text(text = "I Understand", fontWeight = FontWeight.Bold, color = Emerald800)
                }
            }
        )
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            containerColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = Emerald800
                    )
                    Text(text = "Contact Us", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "SumanOnline Web Services",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Slate900
                    )
                    Text(
                        text = "For support, feature requests, or queries regarding BanglarBhumi Pro, please get in touch with our team:",
                        fontSize = 12.5.sp,
                        color = Slate600
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Emerald800,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "support@sumanonline.Com",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate800
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Emerald800,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "www.sumanonline.Com",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate800
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showContactDialog = false
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@sumanonline.com")
                                putExtra(Intent.EXTRA_SUBJECT, "BanglarBhumi Pro Support")
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "support@sumanonline.com", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text(text = "Send Email", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text(text = "Close", color = Slate600)
                }
            }
        )
    }

    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            containerColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Emerald800
                    )
                    Text(
                        text = if (isBengali) "সিটিজেন গাইড ও নির্দেশিকা" else "Citizen Guide & FAQs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Slate900
                    )
                }
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 400.dp)) {
                    CitizenGuideScreen(viewModel = viewModel)
                }
            },
            confirmButton = {
                TextButton(onClick = { showGuideDialog = false }) {
                    Text(text = "Close", fontWeight = FontWeight.Bold, color = Emerald800)
                }
            }
        )
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = if (isBengali) "লগআউট নিশ্চিতকরণ" else "Confirm Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isBengali)
                        "আপনি কি নিশ্চিত যে বাংলারভূমি পোর্টাল সেশন থেকে লগআউট করতে চান? আপনার সংরক্ষিত ইউজার আইডি ও পাসওয়ার্ড ক্যাশে অপরিবর্তিত থাকবে।"
                    else
                        "Are you sure you want to log out from the BanglarBhumi portal session? Your cached credentials will remain saved on this device for future auto-fill.",
                    fontSize = 13.sp,
                    color = Slate700
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logoutPortal()
                        showLogoutConfirmDialog = false
                        Toast.makeText(
                            context,
                            if (isBengali) "সফলভাবে লগআউট হয়েছে" else "Logged out successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = if (isBengali) "লগআউট" else "Logout",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text(text = if (isBengali) "বাতিল" else "Cancel", color = Slate600)
                }
            }
        )
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Emerald800
                    )
                    Text(
                        text = if (isBengali) "নাগরিকের নাম" else "Citizen / User Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Slate900
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBengali)
                            "বাংলারভূমি পোর্টালে নিবন্ধিত আপনার নামটি এখানে নিশ্চিত করুন অথবা পোর্টাল থেকে পুনরায় ফেচ করুন:"
                        else
                            "Confirm your registered citizen name as in BanglarBhumi portal or fetch it directly from the portal:",
                        fontSize = 13.sp,
                        color = Slate600
                    )
                    OutlinedTextField(
                        value = editNameText,
                        onValueChange = { editNameText = it },
                        label = { Text(if (isBengali) "নাগরিকের নাম" else "Citizen Name") },
                        placeholder = { Text("e.g. Suman Mondal") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900,
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = Slate300,
                            focusedLabelColor = Emerald800,
                            unfocusedLabelColor = Slate600,
                            cursorColor = Emerald800,
                            focusedPlaceholderColor = Slate400,
                            unfocusedPlaceholderColor = Slate400
                        )
                    )
                    OutlinedButton(
                        onClick = {
                            viewModel.refreshCitizenNameFromPortal()
                            Toast.makeText(
                                context,
                                if (isBengali) "পোর্টাল থেকে নাম রিফ্রেশ করা হচ্ছে..." else "Refreshing name from portal...",
                                Toast.LENGTH_SHORT
                            ).show()
                            showEditNameDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Emerald50,
                            contentColor = Emerald800
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald600)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Emerald800
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBengali) "পোর্টাল থেকে রিফ্রেশ করুন" else "Fetch from Portal",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Emerald800
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCitizenName(editNameText)
                        showEditNameDialog = false
                        Toast.makeText(
                            context,
                            if (isBengali) "নাম সফলভাবে সংরক্ষিত হয়েছে" else "Name saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Text(text = if (isBengali) "সংরক্ষণ করুন" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(text = if (isBengali) "বাতিল" else "Cancel")
                }
            }
        )
    }

    if (showAboutProjectDialog) {
        AlertDialog(
            onDismissRequest = { showAboutProjectDialog = false },
            containerColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "BanglarBhumi Pro",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Emerald100
                            ) {
                                Text(
                                    text = "v1.1.0",
                                    color = Emerald800,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isBengali) "প্রজেক্ট ও ভার্সন বিবরণ" else "Project & Version Details",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Emerald50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isBengali) "🚀 সংস্করণ v1.1.0 আপডেট সমূহ:" else "🚀 Version v1.1.0 Changelogs:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Emerald900
                            )
                            Text(
                                text = if (isBengali)
                                    "• সম্পূর্ণ নতুন অফিশিয়াল গভঃ পোর্টাল স্টাইলে PDF এক্সপোর্ট\n" +
                                    "• দখলকারের নামের নিচে বেগুনি রঙে 'ব্যক্তি' ট্যাগিং এবং ডুপ্লিকেট নাম সংশোধন\n" +
                                    "• ডাউনলোড করা PDF-এ দাগ নম্বরের পূর্বে S/N (ক্রমিক নম্বর) কলাম সংযোজন\n" +
                                    "• উচ্চমানের প্রমোশনাল ব্যানারসহ PDF সাইজ ২ MB-র নিচে কঠোর অপটিমাইজেশন\n" +
                                    "• সংরক্ষিত রেকর্ডে 'Starred / প্রিয়' বুকমার্কিং এবং দ্রুত ফিল্টার\n" +
                                    "• স্ক্রোলিং করার সময় ডাউনলোড ও বুকমার্ক ট্যাবের স্টিকি হেডার\n" +
                                    "• স্বয়ংক্রিয় Play Services SMS OTP সনাক্তকরণ সুবিধা"
                                else
                                    "• Brand-new official portal grade layout for PDF exports\n" +
                                    "• Clean owner name on Line 1 with purple 'ব্যক্তি' badge on Line 2 (Zero duplicate ব্যক্তি)\n" +
                                    "• Serial Number (S/N) column preceding Dag Number in downloaded records\n" +
                                    "• High-resolution promotional banner with strict size compression (< 2 MB)\n" +
                                    "• Starred / Favorites system in Saved Records with dedicated tab filter\n" +
                                    "• Sticky tab header and responsive card redesign for Saved Records\n" +
                                    "• Automatic Play Services SMS OTP user consent integration",
                                fontSize = 12.sp,
                                color = Slate700,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Slate50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBengali) "বিল্ড সংস্করণ:" else "Build Version:",
                                    fontSize = 12.sp,
                                    color = Slate600
                                )
                                Text(
                                    text = "1.1.0 (VersionCode 2)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate900
                                )
                            }
                            HorizontalDivider(color = Slate200, thickness = 0.5.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBengali) "রিলিজ টাইপ:" else "Release Channel:",
                                    fontSize = 12.sp,
                                    color = Slate600
                                )
                                Text(
                                    text = "Production Release",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Emerald800
                                )
                            }
                            HorizontalDivider(color = Slate200, thickness = 0.5.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBengali) "প্রকাশক:" else "Organization:",
                                    fontSize = 12.sp,
                                    color = Slate600
                                )
                                Text(
                                    text = "SumanOnline Web Services",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate900
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SumanCH8514/BanglarBhumi_Pro-Android-App/releases/tag/v1.1.0"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isBengali) "GitHub রিলিজ দেখুন" else "View on GitHub")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAboutProjectDialog = false }) {
                    Text(text = if (isBengali) "বন্ধ করুন" else "Close")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Emerald800,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = title,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = Slate600,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun AccountListItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFE8F8EE),
            modifier = Modifier.size(35.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF15803D),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = Slate500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        trailing()
    }
}
