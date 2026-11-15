package com.banglarbhumi.pro.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

@Composable
fun PrivacyPolicyScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
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
        // CARD 1: HERO PRIVACY CARD
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
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.5.dp, Color(0xFFA7F3D0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(82.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Privacy Shield",
                            tint = Emerald800,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBengali) "গোপনীয়তা নীতি" else "Privacy Policy",
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

                Text(
                    text = if (isBengali)
                        "তথ্য সুরক্ষা, জিরো-ট্র্যাকিং ও সেশন নিরাপত্তা নির্দেশিকা"
                    else
                        "Data Protection, Zero-Tracking & Session Security",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate600,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

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
                            text = if (isBengali) "নাগরিক তথ্য সুরক্ষা নির্দেশিকা • কার্যকর" else "DPDP Act 2023 Compliant • In Effect",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 2: ZERO-TRACKING & STORAGE
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
                PolicySectionHeader(
                    icon = Icons.Default.Storage,
                    title = if (isBengali) "১. তথ্য সংগ্রহ ও জিরো-ট্র্যাকিং নীতি" else "1. Information Collection & Zero-Tracking"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "BanglarBhumi Pro ব্যবহারকারীর ব্যক্তিগত তথ্যের পরম গোপনীয়তা বজায় রাখে। আমরা আপনার অনুসন্ধানকৃত কোনো জমি, খতিয়ান বা দাগ নম্বর কোনো সার্ভারে জমা করি না বা কোনো থার্ড-পার্টি বিজ্ঞাপন বা ট্র্যাকিং নেটওয়ার্কের সাথে শেয়ার করি না।"
                    else
                        "BanglarBhumi Pro operates on a strict zero-tracking, privacy-first architecture. We never collect, sell, lease, or monetize your search queries, Khatian/Plot inquiries, or citizen identity with any external data brokers or ad analytics networks.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isBengali) "অন-ডিভাইস স্যান্ডবক্সড স্টোরেজ" else "Device Sandboxed Storage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate800
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBengali)
                                "সংরক্ষিত রেকর্ড ও ক্যাশে সম্পূর্ণভাবে আপনার ফোনের নিজস্ব সুরক্ষিত মেমরিতে থাকে। অ্যাপ আনইনস্টল বা ডেটা ক্লিয়ার করলে তা স্থায়ীভাবে মুছে যায়।"
                            else
                                "Saved land queries, cached credentials, and generated PDF reports reside strictly in your device's isolated application storage.",
                            fontSize = 12.sp,
                            color = Slate600,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 3: PORTAL SESSION INTEGRITY
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
                PolicySectionHeader(
                    icon = Icons.Default.Lock,
                    title = if (isBengali) "২. সরকারি পোর্টাল সেশন ও ক্রেডেনশিয়াল" else "2. BanglarBhumi Portal Session Security"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "সরকারি বাংলারভূমি পোর্টালে প্রবেশের সময় আপনার ইউজারনেম, পাসওয়ার্ড ও ওটিপি সরাসরি পশ্চিমবঙ্গ সরকারের অফিসিয়াল সার্ভারে সুরক্ষিত HTTPS (TLS) এনক্রিপশনের মাধ্যমে প্রেরিত হয়।"
                    else
                        "Citizen portal username, password, and OTP tokens entered during login are transmitted directly via TLS/HTTPS encrypted connections to the official Directorate of Land Records and Surveys (WBLR, Govt. of West Bengal) servers.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Emerald800,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBengali)
                                "আমরা কোনো আর্থিক বা ব্যাঙ্কিং তথ্য সংগ্রহ বা পরিচালনা করি না।"
                            else
                                "No financial or payment transaction data is ever handled or accessed.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Emerald800
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 4: SMS CONSENT & PERMISSIONS
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
                PolicySectionHeader(
                    icon = Icons.Default.PhoneAndroid,
                    title = if (isBengali) "৩. এসএমএস কনসেন্ট ও ডিভাইস অনুমতি" else "3. SMS OTP Consent API & Permissions"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "লগইনের সময় ওটিপি স্বয়ংক্রিয়ভাবে শনাক্ত করার জন্য গুগল প্লে সার্ভিসেস SMS User Consent API ব্যবহার করা হয়। এতে কোনো সংবেদনশীল READ_SMS পারমিশন প্রয়োজন হয় না এবং আপনার ব্যক্তিগত কোনো মেসেজ পড়া হয় না।"
                    else
                        "The application utilizes Google Play Services SMS User Consent API solely for auto-detecting the single one-time password during citizen portal verification. The app does NOT request READ_SMS permission and never reads your personal text messages.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTTOM ACTION BUTTON
        Button(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@sumanonline.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Privacy Inquiry - BanglarBhumi Pro")
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(
                        context,
                        "Contact support at support@sumanonline.com",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isBengali) "গোপনীয়তা সহায়তায় ইমেইল করুন" else "Contact Privacy Support",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PolicySectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF0D9488),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
    }
}
