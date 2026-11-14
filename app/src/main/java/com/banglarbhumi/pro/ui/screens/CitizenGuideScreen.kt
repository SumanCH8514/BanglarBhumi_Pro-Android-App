package com.banglarbhumi.pro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel

data class GuideTopic(
    val titleBn: String,
    val titleEn: String,
    val icon: ImageVector,
    val contentBn: String,
    val contentEn: String
)

@Composable
fun CitizenGuideScreen(viewModel: MainViewModel) {
    val isBengali by viewModel.isBengali.collectAsState()

    val guides = remember {
        listOf(
            GuideTopic(
                titleBn = "নামজারি (Mutation) আবেদন পদ্ধতি",
                titleEn = "Land Mutation Procedure",
                icon = Icons.Default.Description,
                contentBn = """১. জমি ক্রয়ের পর সর্বপ্রথম কাজ হলো BL&LRO অফিসে নামজারি আবেদন করা।
২. অনলাইনে BanglarBhumi পোর্টালে Citizen Services > Online Application > Mutation Application-এ যান।
৩. প্রয়োজনীয় কাগজপত্র: রেজিস্ট্রিকৃত দলিল (Registered Deed), পূর্ববর্তী খতিয়ানের নকল, রাজস্ব দাখিলা রশিদ, এবং পরিচয়পত্র (Aadhaar/PAN)।
৪. আবেদনের পর একটি Application No ও Fee Receipt পাবেন। নির্ধারিত ফি নেট ব্যাংকিং বা GRIPS চালানের মাধ্যমে পরিশোধ করুন।
৫. শুনানির নোটিশ (Notice of Hearing) ডাউনলোড করুন এবং নির্দিষ্ট দিনে BL&LRO অফিসে উপস্থিত হয়ে মূল দলিল যাচাই করিয়ে খতিয়ান অনুমোদন করান।""",
                contentEn = """1. After purchasing land, applying for mutation at the BL&LRO office updates the government Record of Rights (RoR) in your name.
2. Apply online via BanglarBhumi Portal under Citizen Services > Mutation Application.
3. Required Documents: Registered Title Deed, Previous Khatian copy, Up-to-date Land Revenue Receipt (Khajna), and Identity Proof (Aadhaar/PAN).
4. After submitting, generate fee payment challan via GRIPS and complete payment.
5. Attend the scheduled hearing at the local BL&LRO office with original deeds for verification to receive your mutated Khatian."""
            ),
            GuideTopic(
                titleBn = "জমির শ্রেণি পরিবর্তন (Conversion) নিয়মাবলী",
                titleEn = "Land Conversion (Sec 4C)",
                icon = Icons.Default.SwapHoriz,
                contentBn = """১. কোনো জমি তার মূল শ্রেণি (যেমন সালি/আমন কৃষি জমি) থেকে বাস্তু বা বাণিজ্যিক কাজে ব্যবহার করতে হলে ডিস্ট্রিক্ট ল্যান্ড অ্যান্ড ল্যান্ড রিফর্মস অফিসারের অনুমোদন প্রয়োজন (WBLR Act 4C ধারা)।
২. অনলাইন কনভার্সন ফর্ম পূরণ করে সাইট প্ল্যান এবং প্রস্তাবিত ব্যবহারের নকশা আপলোড করুন।
৩. কৃষিজমি অকৃষি বা বাস্তু জমিতে রূপান্তরের জন্য নির্ধারিত ফি জমা দিতে হয়।
৪. রাজস্ব পরিদর্শক সরেজমিনে তদন্ত (Spot Inspection) করে রিপোর্ট দিলে কনভার্সন সার্টিফিকেট ইস্যু হয়।
৫. জলাভূমি বা বরো জমি সাধারণভাবে রূপান্তর নিষিদ্ধ।""",
                contentEn = """1. Changing land classification (e.g., agricultural Sali/Aman to homestead Bastu or Commercial) requires permission under Section 4C of the WBLR Act.
2. Submit online application with site plan, layout of proposed usage, and mutation certificate.
3. Pay the prescribed conversion fee determined by government valuation of the area.
4. Revenue Inspector conducts field inspection before the certificate of conversion is granted.
5. Conversion of wetlands and water bodies is strictly restricted by law."""
            ),
            GuideTopic(
                titleBn = "ওয়ারিশান সূত্রে সম্পত্তির বণ্টন (Warish Rules)",
                titleEn = "Warish / Inheritance Succession",
                icon = Icons.Default.Groups,
                contentBn = """১. জমির মূল মালিকের প্রয়াণের পর ওয়ারিশান সনদের (Warish/Legal Heir Certificate) ভিত্তিতে উত্তরাধিকারীদের মধ্যে নাম নথিভুক্ত করা হয়।
২. স্থানীয় পঞ্চায়েত প্রধান বা পৌরসভার চেয়ারম্যান প্রদত্ত আইনসম্মত ওয়ারিশ সনদ সংগ্রহ করুন।
৩. অনলাইন মিউটেশন ফর্মে 'Warish' অপশন সিলেক্ট করে আবেদন করতে হবে। মৃত ব্যক্তির মৃত্যু সনদ (Death Certificate) বাধ্যতামূলক।
৪. মুসলিম উত্তরাধিকার আইনে ফরায়েজ (Faraiz) এবং হিন্দু উত্তরাধিকার আইনে ১৯৫৬ সালের হিন্দু সাকসেশন আইন অনুযায়ী নির্দিষ্ট অংশ বণ্টন হয়।""",
                contentEn = """1. After the death of a landowner, property devolves upon legal heirs based on an official Legal Heir / Warish Certificate.
2. Obtain certificate from Gram Panchayat Pradhan, Municipality Chairperson, or competent civil authority.
3. Apply under online mutation selecting 'Warish' category, attaching Death Certificate and Warish list.
4. Shares are apportioned under the Hindu Succession Act or Muslim Personal Law (Faraiz)."""
            ),
            GuideTopic(
                titleBn = "অনলাইন খাজনা পরিশোধ ও দাখিলা (Khajna)",
                titleEn = "Online Land Revenue (Khajna)",
                icon = Icons.Default.Payment,
                contentBn = """১. অনলাইনে প্রতি আর্থিক বছরের জমি রাজস্ব বা খাজনা পরিশোধ করা যায়।
২. BanglarBhumi পোর্টালে Citizen Services > Online Application > Land Revenue (Khajna) অপশন সিলেক্ট করুন।
৩. জেলা, ব্লক, মৌজা ও খতিয়ান নম্বর দিলে বকেয়া খাজনার পরিমাণ প্রদর্শিত হবে।
৪. নেট ব্যাংকিং বা UPI এর মাধ্যমে ফি প্রদান করলেই তাৎক্ষণিক ডিজিটাল দাখিলা (E-Receipt) ডাউনলোড করা যায়।""",
                contentEn = """1. Land revenue (Khajna) for each financial year can be paid online directly.
2. Select Citizen Services > Online Application > Land Revenue (Khajna) on the portal.
3. Enter District, Block, Mouza and Khatian Number to calculate due tax.
4. Complete payment via Net Banking, Debit Card, or UPI to instantly obtain digital rent receipt."""
            ),
            GuideTopic(
                titleBn = "ভূমি পরিভাষার সংক্ষিপ্ত অর্থ (Glossary)",
                titleEn = "Land Terminology & Glossary",
                icon = Icons.Default.MenuBook,
                contentBn = """• খতিয়ান (Khatian): মালিকের স্বত্ব ও অধিকারের দলিল (Record of Rights / RoR)।
• দাগ নম্বর (Dag No): মৌজা নকশায় চিহ্নিত নির্দিষ্ট ভূখণ্ডের প্লট নম্বর।
• বাটা দাগ (Bata Dag): মূল প্লট ভেঙে নতুন উপ-প্লট গঠিত হলে তাকে ভগ্নাংশ আকারে লেখা হয় (যেমন ১০৫/১)।
• মৌজা (Mouza): রাজস্ব আদায়ের সর্বনিম্ন ভৌগোলিক প্রশাসনিক একক।
• জে.এল নম্বর (JL No): জুরিসডিকশন লিস্ট নম্বর, যা নির্দিষ্ট থানার অধীনে মৌজার ক্রম প্রকাশ করে।
• সি.এস / আর.এস / এল.আর: ক্যাডাস্ট্রাল সার্ভে, রিভিশনাল সার্ভে, এবং ল্যান্ড রিফর্মস সার্ভে খতিয়ান।
• রায়ত (Raiyat): জমির বৈধ স্বত্বাধিকারী প্রজা বা মালিক।""",
                contentEn = """• Khatian: Official Record of Rights (RoR) defining land ownership and shares.
• Dag Number: Cadastral plot number assigned to a specific tract of land on the mouza map.
• Bata Dag: Sub-divided plot designated by fractional number (e.g. 105/1).
• Mouza: Primary administrative revenue village unit.
• JL Number: Jurisdiction List number identifying the revenue village in a police station circle.
• CS / RS / LR: Cadastral Survey, Revisional Survey, and Land Reforms Survey stages.
• Raiyat: Recognized legal tenant / landowner in the government registry."""
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBengali) "নাগরিক নির্দেশিকা ও সেবা" else "Citizen Land Guide",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = if (isBengali) "নামজারি, কনভার্সন ও ভূমি আইনের প্রামাণ্য গাইড" else "Step-by-step guidance for mutation, conversion & land laws",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = Emerald800,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        guides.forEach { guide ->
            GuideAccordionItem(guide = guide, isBengali = isBengali)
        }
    }
}

@Composable
private fun GuideAccordionItem(
    guide: GuideTopic,
    isBengali: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = guide.icon,
                            contentDescription = null,
                            tint = Emerald800,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Text(
                        text = if (isBengali) guide.titleBn else guide.titleEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Slate500
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Divider(color = Slate100, thickness = 1.dp)
                    Text(
                        text = if (isBengali) guide.contentBn else guide.contentEn,
                        fontSize = 13.sp,
                        color = Slate700,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
