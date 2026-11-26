# PRAXION — Native Android

**Brand line:** INTELLIGENCE. IN ACTION.

A native Android starter app built with Kotlin + Jetpack Compose. The interface follows the supplied holographic character-selector reference: deep navy surfaces, cyan/mint lighting, thin borders, rounded panels and a dedicated appearance studio.

## Included in this build

- Home dashboard and bottom navigation
- Assistant chat interface with local demo responses
- Character Lab with Female / Male / Neutral presentation
- Interaction presets: Close Companion, Best Friend, Personal Assistant, Listener, Study Partner, plus Creative Muse (female presentation) and Gaming Buddy (male presentation)
- Appearance Studio with five selectable looks: Waves, Silk, Bob, Aurora and Nova
- Separate identity, interaction mode and appearance settings
- Voice Studio feature screen
- Local Memory and Notes editors
- Skills screen
- Settings screen for AI service endpoint and API key configuration
- Local persistence for character name, presentation, selected mode, look, notes, memory and connection settings

## Build

Open this folder in Android Studio (recent stable version), allow Gradle sync, then choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

- Minimum Android: 8.0 (API 26)
- Compile / target SDK: 35
- Kotlin: 2.0.21
- Jetpack Compose Material 3
- Java target: 17

## Important integration status

This is a functional native UI prototype, not a completed production AI client. The chat currently uses a clearly labelled local demo response. Voice, live model replies, external tools, authentication, cloud sync and production memory require the actual PRAXION backend/API contract. The Settings screen stores a configurable endpoint and key locally, but this initial build does not make network requests. Do not ship a private model-provider key inside a distributed app; route requests through your secured backend.

## Project structure

```text
PRAXION-Android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/praxion/ai/MainActivity.kt
│       └── res/values/styles.xml
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Build an APK without a local Android SDK

This repository includes `.github/workflows/android-apk.yml`. Upload the project to a GitHub repository, open **Actions**, select **Build PRAXION Android APK**, and run the workflow (or push to `main`). When the workflow completes, download the `PRAXION-debug-apk` artifact from that run. This produces a debug APK for testing; a signed release APK needs a release keystore and signing configuration.
