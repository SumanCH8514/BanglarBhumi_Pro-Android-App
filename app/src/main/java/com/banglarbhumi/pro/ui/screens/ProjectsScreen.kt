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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.R
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

@Composable
fun ProjectsScreen(
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
        // CARD 1: HERO PORTFOLIO CARD
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
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Ecosystem Hub",
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
                        text = if (isBengali) "সুমনঅনলাইন" else "SumanOnline",
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
                        "ডিজিটাল সলিউশন, আইনি ও সিটিজেন অ্যাক্সেসিবিলিটি প্ল্যাটফর্ম"
                    else
                        "Digital Solutions & Citizen Accessibility Platforms",
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
                            text = if (isBengali) "অফিশিয়াল প্রোডাক্ট ইকোসিস্টেম • অ্যাক্টিভ" else "Official Product Ecosystem • Active",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 2: FLAGSHIP PROJECTS
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
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = Color(0xFF0D9488),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBengali) "ফ্ল্যাগশিপ প্রজেক্ট সমূহ" else "Flagship Digital Projects",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                ProjectEntry(
                    title = "BanglarBhumi Pro",
                    badge = "v1.1.0 • Current App",
                    badgeColor = Emerald800,
                    badgeBg = Emerald50,
                    description = if (isBengali)
                        "পশ্চিমবঙ্গ ভূমি ও ভূমি সংস্কার (WBLR) পোর্টালের নির্ভুল খতিয়ান ও দাগ ভেরিফিকেশন, রায়ত দখলদার ট্র্যাকিং এবং সরকারি ফরম্যাটে PDF ডাউনলোড ইঞ্জিন।"
                    else
                        "Enterprise-grade citizen accessibility companion for West Bengal land records (RoR/Plot), zero-captcha repeated inspection, and print-grade PDF export.",
                    url = "https://github.com/SumanCH8514/BanglarBhumi_Pro-Android-App"
                )

                HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                ProjectEntry(
                    title = "HisabKhata PRO",
                    badge = "v1.2.0 • Live",
                    badgeColor = Color(0xFF1D4ED8),
                    badgeBg = Color(0xFFEFF6FF),
                    description = if (isBengali)
                        "ভারতীয় ক্ষুদ্র ও মাঝারি ব্যবসায়ী এবং পাইকারদের জন্য স্মার্ট ডিজিটাল লেজার ও বাকি আদায় অ্যাপ। তাৎক্ষণিক ডেবিট/ক্রেডিট বুকিং ও স্বয়ংক্রিয় রিমাইন্ডার।"
                    else
                        "Smart digital ledger and debt recovery platform designed for Indian MSMEs, retailers, and wholesalers with automated transaction alerts and statements.",
                    url = "https://sumanonline.com"
                )

                HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                ProjectEntry(
                    title = "Mouza Directory Engine",
                    badge = "Full State Index",
                    badgeColor = Color(0xFF7C3AED),
                    badgeBg = Color(0xFFF5F3FF),
                    description = if (isBengali)
                        "পশ্চিমবঙ্গের ২৩টি জেলার ৪১,০০০+ মৌজা ও জে.এল. নম্বরের অফলাইন স্মার্ট সার্চ ইনডেক্সিং সিস্টেম।"
                    else
                        "Complete offline searchable GIS directory spanning all 23 districts and 41,000+ Mouzas across West Bengal.",
                    url = "https://sumanonline.com"
                )

                HorizontalDivider(color = Slate100, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                ProjectEntry(
                    title = "SumanOnline Web Services",
                    badge = "Cloud Portal",
                    badgeColor = Color(0xFFC2410C),
                    badgeBg = Color(0xFFFFF7ED),
                    description = if (isBengali)
                        "নাগরিক ও ব্যবসায়িক সফটওয়্যার সলিউশন, সুরক্ষিত ক্লাউড অবকাঠামো এবং টেকনিক্যাল ইঞ্জিনিয়ারিং গেটওয়ে।"
                    else
                        "Official enterprise web portal providing custom software architecture, digital integration services, and developer toolkits.",
                    url = "https://sumanonline.com",
                    isLast = true
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CARD 3: ENGINEERING STANDARDS
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
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = Color(0xFF0D9488),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBengali) "ইঞ্জিনিয়ারিং ও টেক স্ট্যাক" else "Engineering Standards & Tech Stack",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TechItem(
                    label = if (isBengali) "আধুনিক আর্কিটেকচার" else "Modern Architecture",
                    detail = "MVI / MVVM + Clean Outbox + Repository Pattern"
                )
                TechItem(
                    label = if (isBengali) "ইউজার ইন্টারফেস" else "UI Toolkit",
                    detail = "Jetpack Compose + Material 3 Design System"
                )
                TechItem(
                    label = if (isBengali) "নিরাপত্তা ও গোপনীয়তা" else "Security & Privacy",
                    detail = "Zero-Tracker Sandbox, Safe Local Storage, HTTPS Only"
                )
                TechItem(
                    label = if (isBengali) "প্রধান প্রকৌশলী" else "Lead Engineer",
                    detail = "Suman Chakrabortty (SumanOnline Web Services)",
                    isLast = true
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // BOTTOM ACTION BUTTON
        Button(
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sumanonline.com"))
                    context.startActivity(intent)
                } catch (_: Exception) {
                    Toast.makeText(context, "https://sumanonline.com", Toast.LENGTH_SHORT).show()
                }
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
                text = if (isBengali) "সুমনঅনলাইন ওয়েবসাইট ভিজিট করুন" else "Visit SumanOnline.Com",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )
        }
    }
}

@Composable
private fun ProjectEntry(
    title: String,
    badge: String,
    badgeColor: Color,
    badgeBg: Color,
    description: String,
    url: String,
    isLast: Boolean = false
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeBg
            ) {
                Text(
                    text = badge,
                    color = badgeColor,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            fontSize = 12.sp,
            lineHeight = 17.5.sp,
            color = Slate600
        )
    }
}

@Composable
private fun TechItem(
    label: String,
    detail: String,
    isLast: Boolean = false
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
            fontSize = 12.5.sp,
            color = Slate500,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = detail,
            fontSize = 12.5.sp,
            color = Slate900,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}
