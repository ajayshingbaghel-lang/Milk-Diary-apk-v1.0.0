# Implementation Plan: Convert & Build Downloadable Android APK

Prepare and verify the downloadable Android APK for **Milk Diary (Founded by Ajay Pal)**, ensuring it can be built and installed on any Android smartphone while preserving 100% of the existing native Kotlin Jetpack Compose code, Room database, offline audit trail, and multi-account features.

---

## 1. Project Analysis & Architecture Confirmation

- **Current Application Type**: Fully native Android app written in Kotlin with Jetpack Compose (Material 3).
- **Core Persistence**: Local Room SQLite database (`MilkDiaryDatabase`) with zero external web dependency.
- **Decision on WebView**: Retain native Jetpack Compose. A WebView wrapper is unnecessary and would reduce performance, battery efficiency, and offline stability.
- **Application Identity**:
  - App Name: `Milk Diary`
  - Application ID: `com.aistudio.milkdiary.vzkjpl`
  - Target SDK: Android 15 / 16 (SDK 36), Min SDK: Android 7.0 (SDK 24, covering >95% of active Android devices)
  - Version: `2.1.0` (Version Code 2)

---

## 2. Deliverables & Planned Changes

### A. Comprehensive Documentation (`README.md`)
Create a production-grade `README.md` in the project root detailing:
1. **Direct AI Studio APK Export**: How to download the ready APK/AAB or export project ZIP directly from the AI Studio top/settings menu.
2. **Local Android Studio Build**:
   - Opening the project in Android Studio.
   - Using the GUI: `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
   - Locating the generated `.apk` file.
3. **Command-Line (CLI) Build Commands**:
   - Building Debug APK: `./gradlew assembleDebug`
   - Building Release APK: `./gradlew assembleRelease`
4. **Exact Output Paths**:
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release-unsigned.apk` (or signed APK when keystore is supplied).
5. **Phone Installation Guide (Step-by-Step)**:
   - Sideloading via USB cable or file transfer (WhatsApp / Google Drive).
   - Enabling "Install Unknown Apps" in Android device settings.
   - Installing via ADB: `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
6. **Production Keystore & Signing Setup**:
   - Generating a custom release keystore with `keytool`.
   - Configuring environment variables (`STORE_PASSWORD`, `KEYSTORE_PATH`, `KEY_PASSWORD`) without hardcoding credentials.

### B. Automated GitHub Actions CI Workflow (`.github/workflows/build-apk.yml`)
- Add an automated CI pipeline that:
  - Checks out the project.
  - Sets up JDK 17 with Gradle cache.
  - Executes `./gradlew assembleDebug` and `./gradlew assembleRelease`.
  - Automatically uploads the compiled `.apk` files as downloadable GitHub workflow artifacts or release assets on every push and version tag.

### C. APK Build Verification
- Execute `gradle :app:assembleDebug` to verify that the project compiles cleanly into an actual `app-debug.apk` binary.
- Ensure all resources, manifest permissions, adaptive launcher icons, ProGuard rules, and strings remain consistent and error-free.

---

## 3. Verification & Acceptance Criteria
- [x] All existing features preserved (Morning/Evening entries, rate editor, advance/goods deduction, WhatsApp share, edit history log, blank slate reset, founder console).
- [ ] Debug APK builds successfully with `./gradlew assembleDebug` and produces an installable APK binary.
- [ ] Complete `README.md` and GitHub Actions workflow file in place.
- [ ] All unit and Robolectric tests continue to pass.
