# BanglarBhumi Pro (বাংলারভূমি প্রো)
### *A SumanOnline Project*

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-26%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg?style=flat)](#license)

**BanglarBhumi Pro** is a modern, high-performance, and native Android application designed to provide citizens of West Bengal with fast, accurate, and seamless access to land records (খতিয়ান ও দাগের তথ্য - Record of Rights / RoR & Plot details).

---

## ✨ Key Features

- ⚡ **Live Citizen Portal Queries**: Direct access to West Bengal Land & Land Reforms (BanglarBhumi) official live data without manual captchas on repeated visits.
- 📋 **Pixel-Perfect Government Format**: Displays land records matching the exact official BanglarBhumi format:
  - Official Live Timestamp (`Live Data As On DD/MM/YYYY,HH:mm:ss`) in blue.
  - JL Number & Thana headers in maroon.
  - Alternating shaded 2-column Khatian/Plot summary tables.
  - Multi-column Details Table (দাগ নং, শ্রেণী, অংশ, অংশ পরিমাণ, দখলদার, মন্তব্য) with Borga (`বর্গা`) markings.
- 🔐 **Intelligent Portal Authentication & OTP Integration**:
  - Secure Salt-based MD5 password hashing compliant with the government portal's auth flow.
  - **Zero-Permission Automatic OTP Detection** powered by the Google Play Services SMS Retriever API (`SmsRetriever`).
  - Safe, private on-device credential caching in encrypted app storage.
- 🖨️ **Vector PDF Generation & Printing**:
  - One-tap **SAVE PDF** floating action integrated with Android `PrintManager`.
  - Offline PDF exporter fallback for quick document sharing.
- 💾 **Offline Bookmarks / Saved Records**:
  - Save queried Khatians and Plots locally for instant offline review.
  - Delete or view saved records without network connectivity.
- 🧮 **Land Measurement Calculator**:
  - Fast conversion between West Bengal regional and standard land units: Satak (শতক), Katha (কাঠা), Bigha (বিঘা), Acre (একর), Hectare (হেক্টর), Sq. Feet, and Sq. Meters.
- 🗺️ **Comprehensive Mouza & JL Directory**:
  - Offline searchable directory covering all Districts, Blocks, and Mouzas across West Bengal.
- 🌐 **Bilingual Support (বাংলা & English)**:
  - Full native Bengali (বাংলা) and English language localization with instant toggle.
- 📱 **Modern UI & Adaptive Layout**:
  - Built with Jetpack Compose Material 3.
  - Full Edge-to-Edge support with IME keyboard insets and auto-scrolling input fields.

---

## 🌟 What's New in Version 1.1.0 (Build 2)

- 📄 **Portal-Grade PDF Export Engine**: Brand-new official government portal layout with West Bengal state emblem, certified header, Mouza & JL details, and print-optimized typography.
- 👤 **Owner (Rayat) & Duplicate Optimization**: Clean owner name display on Line 1 with purple `ব্যক্তি` badge on Line 2, eliminating duplicate tagging.
- 🔢 **Serial Number (S/N) Column**: Dedicated serial column preceding Plot / Dag numbers in exported PDF records.
- 🏛️ **Dedicated Project & Legal Pages**:
  - **About Project**: Complete build specifications, app identity, signing certificate SHA-1 credentials, and changelog.
  - **SumanOnline Projects**: Showcase of the digital ecosystem (BanglarBhumi Pro, HisabKhata PRO, Mouza Directory Engine, SumanOnline Web Services) with native Android specification cards.
  - **Privacy Policy**: DPDP Act 2023 compliance, device-sandboxed storage, zero data-selling, and SMS Consent API details.
  - **Terms & Conditions**: Citizen accessibility guidelines, independent tool disclaimer, and certified copies notice.
  - **Contact Us**: Interactive direct support channels (`support@sumanonline.com`, `https://sumanonline.com`), support topics, and SLA operating hours.
- 🧮 **Optimized Land Unit Converter**: Compact Area Value field with inline Bengal standard units (Satak, Katha, Bigha, Chatak, Acre, etc.), quick presets, and 1-tap clipboard copying.
- 🎨 **Adaptive TopAppBar & Back Navigation**: Dynamic header updating title, tagline, and back button per sub-screen with full Bengali and English localization.

---

## 🏗️ Architecture & Tech Stack

The application follows the recommended Android **Modern Architecture (MVI/MVVM)** and **Clean Repository Pattern**:

```
BanglarBhumi-Pro Application/
├── app/
│   ├── src/main/java/com/banglarbhumi/pro/
│   │   ├── data/
│   │   │   ├── model/         # Land record domain models & data classes
│   │   │   └── repository/    # LandRecordRepository (data orchestration & caching)
│   │   ├── network/           # BanglarbhumiClient (OkHttp, CookieJar, Session, Parsing)
│   │   ├── ui/
│   │   │   ├── components/    # Reusable Compose components & PortalLoginDialog
│   │   │   ├── screens/       # SearchScreen, ResultScreen, SavedRecordsScreen, etc.
│   │   │   ├── theme/         # Material 3 Colors, Shapes, and Typography
│   │   │   └── viewmodel/     # MainViewModel (StateFlow & Coroutine scopes)
│   │   └── util/              # PdfExporter, SmsOtpHelper, Inset utilities
│   └── src/main/res/          # App drawables, mipmaps, strings, and XML resources
├── web/                       # Companion web inspection client & API bridge
└── gradle/                    # Version catalog (libs.versions.toml) and wrapper
```

### Core Libraries
- **UI Toolkit**: Jetpack Compose (BOM 2024.11.00) + Material 3
- **Language**: Kotlin 2.0.21
- **Asynchronous**: Kotlin Coroutines & `StateFlow`
- **Networking**: OkHttp 4.12.0 + Persistent CookieJar
- **SMS Consent**: Google Play Services Auth API Phone (`play-services-auth-api-phone:18.1.0`)
- **JSON Processing**: Google Gson 2.11.0

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- JDK 17 or higher
- Android SDK 35 (compileSdk 35, minSdk 26)

### Build & Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/SumanCH8514/BanglarBhumi_Pro-Android-App.git
   cd BanglarBhumi_Pro-Android-App
   ```

2. **Open in Android Studio**:
   - Open Android Studio, select **Open**, and navigate to the cloned project root.
   - Allow Gradle Sync to finish downloading dependencies.

3. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install and Run on Device/Emulator**:
   ```bash
   ./gradlew installDebug
   ```

---

## 🔒 Security & Privacy

- **No Remote Credential Logging**: User credentials, citizen passwords, and mobile numbers are stored exclusively inside the local private sandbox of the device (`Context.MODE_PRIVATE`). No third-party analytical or remote server receives user credentials.
- **Direct Portal Communication**: Network requests communicate directly with official West Bengal portal endpoints (`banglarbhumi.gov.in`).
- **Privacy Policy**: Detailed terms and privacy commitments are accessible directly inside the app's Account & Settings section.

---

## ⚖️ Disclaimer

*BanglarBhumi Pro is an independent citizen accessibility tool developed by SumanOnline Web Services. This application is for informational purposes only and is **not affiliated with, endorsed by, or representing any government entity or the Department of Land & Land Reforms, Government of West Bengal**.*

---

## 👨‍💻 Author & Attribution

Developed with ❤️ by **SumanOnline Web Services**  
- **Website**: [www.sumanonline.com](https://www.sumanonline.com)  
- **Support**: [support@sumanonline.com](mailto:support@sumanonline.com)  
- **Project Repository**: [GitHub Repository](https://github.com/SumanCH8514/BanglarBhumi_Pro-Android-App)

© 2026 SumanOnline Web Services. All rights reserved.
