package com.banglarbhumi.pro.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.R
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

@Composable
fun AboutProjectScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isBengali by viewModel.isBengali.collectAsState()

    BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(top = 10.dp, bottom = 32.dp)
    ) {
        // CARD 1: HERO APP CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular App Logo Container
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.5.dp, Color(0xFFA7F3D0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(82.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // App Name + Red PRO Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBengali) "বাংলারভূমি" else "BanglarBhumi",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDC2626)
                    ) {
                        Text(
                            text = "PRO",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tagline
                Text(
                    text = if (isBengali)
                        "পশ্চিমবঙ্গ ভূমি ও ভূমি সংস্কার সিটিজেন তথ্য সহায়ক"
                    else
                        "West Bengal Land Records & Citizen Companion",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate600,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Version Status Pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = "Version 1.1.0 (Build 2) • Stable",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 2: PROJECT OVERVIEW
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (isBengali) "প্রজেক্ট পরিচিতি" else "Project Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "BanglarBhumi PRO হল পশ্চিমবঙ্গ ভূমি ও ভূমি সংস্কার (WBLR) পোর্টালের একটি আধুনিক সিটিজেন ফ্রেন্ডলি অ্যাক্সেসিবিলিটি প্ল্যাটফর্ম। এটি খতিয়ান ও দাগের তথ্য যাচাই, দখলদার রায়ত রেকর্ড, মিউটেশন পদ্ধতি গাইড, এবং নিখুঁত ভূমি পরিমাপ ক্যালকুলেটর সহ অফিশিয়াল পোর্টাল গ্রেড PDF রিপোর্ট তৈরিতে সহায়তা করে।"
                    else
                        "BanglarBhumi PRO is an enterprise-grade citizen accessibility platform designed for West Bengal landowners, citizens, and legal professionals. It streamlines real-time Khatian & Plot (Dag) verification, mutation guidance, land area unit conversions, and certified portal-grade PDF reporting.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Specs Table
                SpecRow(
                    label = if (isBengali) "রক্ষণাবেক্ষণে" else "Maintained By",
                    value = "SumanOnline.Com"
                )
                SpecRow(
                    label = if (isBengali) "লিড ইঞ্জিনিয়ার" else "Lead Engineer",
                    value = "Suman Chakrabortty"
                )
                SpecRow(
                    label = if (isBengali) "প্ল্যাটফর্ম" else "Platform",
                    value = "Android 8.0+ (API 26–35)"
                )
                SpecRow(
                    label = if (isBengali) "আর্কিটেকচার" else "Architecture",
                    value = "MVVM + Jetpack Compose + Coroutines"
                )
                SpecRow(
                    label = if (isBengali) "পোর্টাল ইঞ্জিন" else "Portal Engine",
                    value = "BanglarBhumi Portal (WBLR, Govt. of WB)"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 3: APP PACKAGE & SECURITY SETUP
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF0D9488),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBengali) "অ্যাপ আইডেন্টিটি ও সিকিউরিটি সেটআপ" else "App Identity & Signing Certificate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isBengali)
                        "গুগল প্লে কনসোল, প্লে সার্ভিসেস SMS কনসেন্ট API এবং নিরাপত্তা যাচাইয়ের জন্য অফিশিয়াল প্যাকেজ নেম ও অ্যাক্টিভ সাইনিং SHA-1 ফিঙ্গারপ্রিন্ট:"
                    else
                        "Official application package credentials and SHA-1 release signing fingerprint for Play Console, SMS Consent API & integrity verification:",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                // PACKAGE NAME CONTAINER
                val appPackageName = context.packageName.ifBlank { "com.sumanonline.banglarbhumipro" }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Package Name",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate500
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = appPackageName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Slate900
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE6FFFA),
                            border = BorderStroke(1.dp, Color(0xFF99F6E4)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(appPackageName))
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "প্যাকেজ নেম কপি করা হয়েছে" else "Package name copied to clipboard",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color(0xFF0D9488),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "COPY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0D9488)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ACTIVE SIGNING SHA-1 CONTAINER
                val sha1String = "24:9F:4C:0B:1B:96:9D:C1:30:03:C5:61:AC:DE:FC:F9:1E:7D:7A:4E"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Text(
                                    text = "Active Signing SHA-1",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE6FFFA),
                                border = BorderStroke(1.dp, Color(0xFF99F6E4)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(sha1String))
                                        Toast.makeText(
                                            context,
                                            if (isBengali) "SHA-1 ক্লিপবোর্ডে কপি করা হয়েছে" else "SHA-1 fingerprint copied to clipboard",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = Color(0xFF0D9488),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "COPY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0D9488)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = sha1String,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                color = Slate800,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 4: WHAT'S NEW IN V1.1.0
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF0D9488),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBengali) "v1.1.0 সংস্করণে নতুন কী কী রয়েছে" else "What's New in v1.1.0",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ChangelogItem(
                    title = if (isBengali) "অফিশিয়াল পোর্টাল-গ্রেড PDF এক্সপোর্ট" else "Portal-Grade PDF Export Engine",
                    description = if (isBengali)
                        "পশ্চিমবঙ্গ রাজ্য প্রতীক, অফিশিয়াল হেডার, মৌজা ও জে.এল. বিবরণী এবং স্পষ্ট প্রিন্ট ফরম্যাটে সম্পূর্ণ নতুন PDF লেআউট।"
                    else
                        "Brand-new official government portal layout with state emblem, certified header, Mouza & JL details, and print-optimized typography."
                )

                ChangelogItem(
                    title = if (isBengali) "রায়ত দখলদার ও ডুপ্লিকেট নাম সংশোধন" else "Rayat Ownership & Duplicate Removal",
                    description = if (isBengali)
                        "দখলকারের নামের নিচে সুন্দর বেগুনি রঙে 'ব্যক্তি' ট্যাগিং এবং ডুপ্লিকেট 'ব্যক্তি' টেক্সট স্থায়ীভাবে অপসারিত।"
                    else
                        "Clean owner name on Line 1 with dedicated purple 'ব্যক্তি' (Individual) badge on Line 2, with total elimination of redundant 'ব্যক্তি' text."
                )

                ChangelogItem(
                    title = if (isBengali) "ক্রমিক নম্বর (S/N) কলাম সংযোজন" else "Serial Number (S/N) Indexing",
                    description = if (isBengali)
                        "ডাউনলোড করা খতিয়ান রেকর্ডে দাগ নম্বরের পূর্বে স্পষ্ট ক্রমিক নম্বর (S/N) কলাম সংযোজিত।"
                    else
                        "Added dedicated Serial Number column preceding Dag (Plot) numbers in downloaded records for fast reference and cross-checking."
                )

                ChangelogItem(
                    title = if (isBengali) "কঠোর ফাইল সাইজ অপটিমাইজেশন (< 2 MB)" else "Strict File Size Compression (< 2 MB)",
                    description = if (isBengali)
                        "হাই-রেজোলিউশন ব্যানার থাকা সত্ত্বেও স্মার্ট বিটম্যাপ কম্প্রেশন নিশ্চিত করে যে PDF সাইজ সর্বদা ২ MB-র নিচে থাকে।"
                    else
                        "Smart bitmap downsampling ensuring downloadable record PDFs remain under 2 MB for instant WhatsApp and email sharing."
                )

                ChangelogItem(
                    title = if (isBengali) "প্রিয় রেকর্ডস (Starred) ও স্টিকি হেডার" else "Starred Records & Sticky Tab Redesign",
                    description = if (isBengali)
                        "এক ট্যাপে খতিয়ান ও দাগ ফেভারিট করার সুবিধা, All / Starred ট্যাব ফিল্টার এবং স্ক্রোলিংয়ের সময় স্টিকি হেডার।"
                    else
                        "Bookmark favorite Khatians and Plots with one-tap star actions, tabbed filtering (All / Starred), and sticky navigation header while scrolling."
                )

                ChangelogItem(
                    title = if (isBengali) "স্বয়ংক্রিয় SMS OTP সনাক্তকরণ" else "Automatic SMS OTP Consent Integration",
                    description = if (isBengali)
                        "নাগরিক পোর্টাল লগইনে দ্রুত ও নিরাপদ এককালীন পাসওয়ার্ড (OTP) স্বয়ংক্রিয়ভাবে ইনপুট নেওয়ার জন্য Play Services SMS Consent API সংযোজিত।"
                    else
                        "Integrated Google Play Services SMS User Consent API for frictionless, secure one-time password autofill during citizen portal login."
                )

                ChangelogItem(
                    title = if (isBengali) "ভূমি পরিমাপ ক্যালকুলেটর ও মৌজা ডিরেক্টরি" else "Land Calculator & Mouza Directory",
                    description = if (isBengali)
                        "শতক/ডেসিমেল, বিঘা, কাঠা ও ছটাক রূপান্তর এবং পশ্চিমবঙ্গের ২৩টি জেলার সম্পূর্ণ মৌজা ও জে.এল. তালিকা।"
                    else
                        "Complete Satak/Decimal, Bigha, Katha, Chatak conversions and full Mouza/JL directory indexing across all 23 West Bengal districts.",
                    isLast = true
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GITHUB RELEASE BUTTON
        Button(
            onClick = {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/SumanCH8514/BanglarBhumi_Pro-Android-App/releases/tag/v1.1.0")
                    )
                    context.startActivity(intent)
                } catch (_: Exception) {}
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(46.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isBengali) "GitHub-এ সম্পূর্ণ রিলিজ দেখুন" else "View Release on GitHub",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )
        }
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Slate500,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Slate900,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ChangelogItem(
    title: String,
    description: String,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.5.dp)
                .size(7.5.dp)
                .clip(CircleShape)
                .background(Color(0xFF0D9488))
        )
        Column {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(2.5.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.5.sp,
                color = Slate600
            )
        }
    }
}
