package com.banglarbhumi.pro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

@Composable
fun TermsConditionsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
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
        // CARD 1: HERO TERMS CARD
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
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "Terms & Law",
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
                        text = if (isBengali) "শর্তাবলী ও নিয়মাবলী" else "Terms & Conditions",
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
                        "নাগরিক সেবা প্ল্যাটফর্ম ব্যবহারের নিয়ম ও আইনি নির্দেশিকা"
                    else
                        "Citizen Accessibility Guidelines & Legal Notice",
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
                            text = if (isBengali) "আইনি নির্দেশিকা ও ডিসক্লেইমার • কার্যকর" else "Legal Disclaimer • In Effect",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 2: INDEPENDENT CITIZEN DISCLAIMER
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
                TermsSectionHeader(
                    icon = Icons.Default.Info,
                    title = if (isBengali) "১. স্বাধীন নাগরিক তথ্য সহায়ক প্ল্যাটফর্ম" else "1. Independent Citizen Companion"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "BanglarBhumi Pro সুমনঅনলাইন ওয়েব সার্ভিসেস কর্তৃক তৈরি একটি স্বাধীন নাগরিক সেবা অ্যাপ। এটি পশ্চিমবঙ্গ সরকার বা ভূমি ও ভূমি সংস্কার দপ্তরের কোনো সরকারি অ্যাপ্লিকেশন নয় এবং কোনো সরকারি প্রতিষ্ঠানের সাথে সরাসরি যুক্ত নয়।"
                    else
                        "BanglarBhumi Pro is developed by SumanOnline Web Services as an independent citizen accessibility tool. This application is NOT an official government entity and is NOT affiliated with, sponsored by, or endorsed by the Directorate of Land Records & Surveys, Govt. of West Bengal.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isBengali) "স্বীকৃতি ও তথ্যসূত্র" else "Acknowledgement & Data Source",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF92400E)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBengali)
                                "সমস্ত খতিয়ান, দাগ এবং মৌজা সংক্রান্ত তথ্য পশ্চিমবঙ্গ সরকারের পাবলিক পোর্টাল (banglarbhumi.gov.in) থেকে প্রাপ্ত।"
                            else
                                "All land records, Khatian/Plot queries, and Mouza details are sourced directly from the official public portal (banglarbhumi.gov.in).",
                            fontSize = 12.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 3: INFORMATIONAL REFERENCE VS CERTIFIED COPIES
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
                TermsSectionHeader(
                    icon = Icons.Default.Description,
                    title = if (isBengali) "২. রেফারেন্স তথ্য বনাম সহি-মুহুরি (সার্টিফাইড) কপি" else "2. Informational Reference vs Certified Copies"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "এই অ্যাপে প্রদর্শিত তথ্য শুধুমাত্র ব্যক্তিগত যাচাই, সচেতনতা ও রেফারেন্সের জন্য। জমি ক্রয়-বিক্রয়, রেজিস্ট্রি, ব্যাঙ্কিং লোন বা আদালতের আইনি প্রয়োজনে সংশ্লিষ্ট ব্লক ভূমি ও ভূমি সংস্কার আধিকারিক (BL&LRO) অফিস থেকে সরকারি সিলমোহরযুক্ত সার্টিফাইড কপি সংগ্রহ করা বাধ্যতামূলক।"
                    else
                        "Information retrieved through this application is strictly for personal reference, informational verification, and citizen guidance. For legal disputes, court proceedings, property registration, or bank mortgages, citizens are advised to obtain official certified copies from their respective BL&LRO office.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 4: PORTAL UPTIME & SERVER AVAILABILITY
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
                TermsSectionHeader(
                    icon = Icons.Default.CloudQueue,
                    title = if (isBengali) "৩. সরকারি পোর্টাল আপটাইম ও ফেয়ার ইউজ" else "3. Government Portal Uptime & Fair Usage"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "তথ্যের প্রাপ্যতা ও অনুসন্ধানের গতি সরাসরি সরকারি সার্ভারের সক্রিয়তার উপর নির্ভরশীল। পোর্টাল মেইনটেন্যান্স বা ট্রাফিক জ্যামের কারণে অনুসন্ধানে বিলম্ব হতে পারে।"
                    else
                        "Data retrieval speed and search availability are directly dependent on the state government's server uptime. Scheduled portal maintenance, server overload, or network congestion may affect response times.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 5: PERMITTED USE & INTELLECTUAL PROPERTY
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
                TermsSectionHeader(
                    icon = Icons.Default.Shield,
                    title = if (isBengali) "৪. অনুমোদিত ব্যবহার ও বুদ্ধিবৃত্তিক সম্পত্তি" else "4. Permitted Use & Software Rights"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isBengali)
                        "নাগরিক ও আইন পেশাজীবীরা ব্যক্তিগত ও পেশাগত তথ্য অনুসন্ধানে অ্যাপটি ব্যবহার করতে পারেন। অ্যাপ্লিকেশনের সোর্স কোড অননুমোদিতভাবে ক্লোন বা রিডিস্ট্রিবিউট করা আইনত নিষিদ্ধ।"
                    else
                        "Citizens, landowners, and professionals are granted non-exclusive rights to use the calculator, search indexes, and PDF reporting tools for lawful purposes. Reverse engineering or unauthorized distribution is strictly prohibited.",
                    fontSize = 13.sp,
                    lineHeight = 19.5.sp,
                    color = Slate600
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTTOM ACTION BUTTON
        Button(
            onClick = { onBack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Emerald800)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isBengali) "আমি শর্তাবলী বুঝেছি ও মেনে নিচ্ছি" else "I Understand & Accept Terms",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun TermsSectionHeader(
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
