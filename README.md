# خوابوں کی تعبیر: Android app

Kotlin + Jetpack Compose. Fully right-to-left Urdu app: dream-symbol search across the full al-Nabulsi book, 25 categories, an Urdu A–Z index, "خواب کا علم" (14 topics from Quran and hadith), About (SM Talha, darsenizami.net), favourites, and a dream form that opens WhatsApp to +92 313 2020392.

## Run it (about 5 minutes)

1. Open Android Studio, then **File > Open**, then `D:\KhwabApp`.
2. Wait for Gradle sync to finish (bottom bar).
3. Start an emulator (**Device Manager > ▶**) or plug in your phone with USB debugging on.
4. Click the green **Run ▶** button.

## Where things are

| What | File |
|---|---|
| WhatsApp number, scholar name, website, hadya text | `app/src/main/java/net/darsenizami/khwab/Data.kt` (`AppConfig`) |
| All dream symbols (2,323) | `app/src/main/assets/symbols.json` |
| "خواب کا علم" topics | `app/src/main/assets/topics.json` |
| Categories | `app/src/main/assets/categories.json` |
| Screens | `app/src/main/java/net/darsenizami/khwab/Screens.kt` |
| Colours and fonts | `app/src/main/java/net/darsenizami/khwab/Theme.kt` |
| Clickable web preview | `preview/index.html` |
| Data build + Google Play policy filter | `tools/all_entries.py`, `tools/build_all.py` |

**Fonts** (all SIL Open Font License, free to bundle): Noto Nastaliq Urdu (Urdu), Amiri (Quran/Arabic), Carlito (English; looks like Calibri). Jameel Noori Nastaleeq is **not** bundled: no licence allowing that was found.

## Before publishing

1. **Check the Urdu translations.** 93 entries are translated (summaries); each card says "عالم کی نظرِ ثانی باقی". The other entries show the full Arabic with "اردو ترجمہ ابھی باقی ہے".
2. **Privacy policy page:** put a short page on darsenizami.net saying the app collects no data, and that text a user chooses to send goes to WhatsApp.

## Build a signed release for Google Play (about 15 minutes)

1. In Android Studio: **Build > Generate Signed App Bundle or APK > Android App Bundle > Next**.
2. Under "Key store path" click **Create new…**. Save it as `D:\keys\khwab.jks` (outside the project), choose a password, alias `khwab`.
3. **Back up the .jks file and the passwords.** Without them you can never update the app.
4. Choose **release**, then **Create**. The file is `app/release/app-release.aab`.

Command-line option: create `D:\KhwabApp\keystore.properties` (git-ignored):

```
storeFile=D:/keys/khwab.jks
storePassword=YOUR_PASSWORD
keyAlias=khwab
keyPassword=YOUR_PASSWORD
```

then run `.\gradlew.bat bundleRelease` (AAB for Play) or `.\gradlew.bat assembleRelease` (APK to share directly).

**On this PC, command-line builds need a short temp folder** or Java fails with "Unable to establish loopback connection":

```
$env:TEMP="D:\KhwabApp\.tmp"; $env:TMP=$env:TEMP; $env:JAVA_HOME="D:\GoogleStudio\jbr"
```

## Publish on Google Play (about 3 weeks in total)

1. Sign up at https://play.google.com/console (one-time US$25, ID verification).
2. **Create app:** name "خوابوں کی تعبیر", default language Urdu, App, Free.
3. **App content:**
   - Privacy policy URL (darsenizami.net page)
   - Data safety: "No data collected or shared"
   - Target audience: **18+**
   - Content rating questionnaire: reference app; answer honestly about mild sexual references in the text
   - Ads: No
4. **Store listing:** Urdu and English descriptions, 512×512 icon, 1024×500 feature graphic, at least 4 phone screenshots from the emulator.
5. New personal accounts need a **closed test with at least 12 testers for 14 days**. Upload the `.aab` to **Testing > Closed testing**, add testers' emails, and wait 14 days.
6. **Production > Create release**, upload the `.aab`, **Send for review** (usually 1 to 7 days).

For every update: raise `versionCode` and `versionName` in `app/build.gradle.kts`.

**Listing wording:** describe it as a reference to classical dream interpretation with sources. Avoid "100% sahi tabeer" or future predictions. The app sells nothing inside; the hadya is agreed on WhatsApp. Check Google Play's Payments policy before launch.
