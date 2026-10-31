# Milk Diary (दूध डायरी)
> **Founded by Ajay Pal** • Smart Milk & Accounts Management for Android

Milk Diary is a fully native, 100% offline Android application built with Kotlin and Jetpack Compose (Material 3). It tracks daily milk purchases (morning & evening), monthly rates per liter, cash advances, grocery/goods expenses with automatic deductions, multiple dairy accounts, editing audit trail logs, and WhatsApp billing exports.

---

## 📱 Application Specifications

| Property | Value |
| :--- | :--- |
| **Application Name** | Milk Diary |
| **Package ID (`applicationId`)** | `com.aistudio.milkdiary.vzkjpl` |
| **Framework** | Kotlin, Jetpack Compose, Material Design 3 |
| **Database** | Android Jetpack Room (Offline SQLite) |
| **Minimum Android Version** | Android 7.0 Nougat (API Level 24) |
| **Target Android Version** | Android 15 / 16 (API Level 36) |
| **Version** | v2.1.0 (Version Code 2) |

---

## 🚀 How to Obtain and Install the Android APK

You have three easy ways to generate and install the APK on your Android device:

---

### Option 1: Direct Download from Google AI Studio (Fastest)

1. Open the project in **Google AI Studio**.
2. Click on the **Settings** / **Export** icon in the top navigation bar.
3. Select **Export as ZIP** or **Download APK / AAB**.
4. Transfer the exported `.apk` file directly to your Android phone via USB cable, Google Drive, or WhatsApp.

---

### Option 2: Build with Android Studio (Recommended for Developers)

1. **Prerequisites**: Install [Android Studio](https://developer.android.com/studio) (Hedgehog or newer) with JDK 17.
2. **Open Project**:
   - Launch Android Studio.
   - Click **Open** and select the root directory of this project.
   - Allow Gradle to sync dependencies automatically.
3. **Build the APK**:
   - In the top menu, go to **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
   - Wait for the build to finish (typically 30–60 seconds).
   - In the bottom-right corner notification popup, click **locate** to reveal the APK folder.
4. **Locate Output**:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

### Option 3: Command-Line (CLI) Build via Terminal

Open your terminal or command prompt in the root project folder:

#### For Linux / macOS:
```bash
# Make gradlew executable
chmod +x gradlew

# Build the Debug APK (ready to install on any Android phone without a custom keystore)
./gradlew :app:assembleDebug

# Or build the Release APK
./gradlew :app:assembleRelease
```

#### For Windows:
```cmd
# Build the Debug APK
gradlew.bat :app:assembleDebug

# Build the Release APK
gradlew.bat :app:assembleRelease
```

#### Expected Output APK Paths:
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 📲 Installing the APK on Your Android Device

### Method A: Direct Phone Sideloading (No Computer Required)
1. Send the `app-debug.apk` file to your phone (via WhatsApp, Telegram, Google Drive, or USB cable).
2. Tap on the `.apk` file on your phone.
3. When prompted, tap **Settings** and enable **"Allow from this source"** (Install Unknown Apps permission).
4. Tap **Install** and then open **Milk Diary**.

### Method B: Via Android Debug Bridge (ADB)
If your phone is connected to your computer with USB Debugging enabled:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔐 Generating a Signed Release APK for Google Play

To distribute on the Google Play Store or share a signed production APK:

1. **Generate your production Keystore**:
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. **Set the environment variables**:
   ```bash
   export KEYSTORE_PATH="/path/to/my-upload-key.jks"
   export STORE_PASSWORD="your-store-password"
   export KEY_PASSWORD="your-key-password"
   ```
3. **Build Signed Release APK / AAB**:
   ```bash
   ./gradlew :app:assembleRelease
   # Or for Google Play App Bundle:
   ./gradlew :app:bundleRelease
   ```
   Signed output will be located in `app/build/outputs/apk/release/app-release.apk` or `app/build/outputs/bundle/release/app-release.aab`.

---

## 🔄 Automated GitHub Actions CI/CD

This repository includes `.github/workflows/build-apk.yml`. When you push this project to GitHub:
1. GitHub automatically spins up a clean Ubuntu container with JDK 17.
2. Compiles both Debug and Release APKs.
3. Uploads the final `MilkDiary-Debug-APK` directly into the workflow **Artifacts** tab where anyone can click and download it immediately.

---

## 🛠 Features Summary

- **Multi-Account Management**: Maintain separate records for different suppliers/customers.
- **Daily Milk Entries**: Track morning and evening milk quantities with date picking.
- **Monthly Rate Setting**: Flexible price per liter customized per account and month.
- **Advance & Goods Deduction**: Auto-deducts cash advances and groceries using the formula:
  $$\text{Net Payable} = (\text{Liters} \times \text{Rate}) - \text{Advance} - \text{Goods}$$
- **Transparency Audit Trail**: Every edit or modification is logged with original values and timestamp.
- **WhatsApp Bill Share**: One-click formatted summary ready to send directly to WhatsApp.
- **Offline & Private**: All data is stored in the local Room SQLite database on your device with JSON backup/restore support.
- **Founder Badge**: Founded by Ajay Pal with built-in version info modal (v2.1.0).

---

## 👤 Credits & Support
- **Founder**: Ajay Pal
- **App Name**: Milk Diary (दूध डायरी)
- **License**: Private / Proprietary
