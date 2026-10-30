# Publishing on Google Play: step by step

Files are in `D:\KhwabApp\store`:
- `khwab-v1.0-release.aab`: upload this to Play Console
- `khwab-v1.0-release.apk`: share directly (WhatsApp/website), not for Play
- `graphics/`: icon 512 and feature graphic 1024×500
- `LISTING.md`: app name, short and full descriptions (Urdu + English)
- `privacy-policy.html`: publish this page, then use its link

**Signing key:** `D:\keys\khwab-upload.jks`, passwords in `D:\keys\KHWAB-KEY-BACKUP.txt`. **Copy the whole `D:\keys` folder to a USB and Google Drive now.** It is outside the project on purpose.

## 1. Publish the privacy policy (10 min)
Blogger: **Pages > New page**, title "Privacy Policy – Khwab", switch to **HTML view**, paste everything between `<body>` and `</body>` from `privacy-policy.html`, publish. Copy the page URL.

## 2. Create the app (5 min)
Play Console > **Create app**: name "خوابوں کی تعبیر", default language **Urdu**, **App**, **Free**, accept declarations.

## 3. App content: answers to paste
| Section | Answer |
|---|---|
| Privacy policy | URL from step 1 |
| App access | All functionality available without special access |
| Ads | **No**, the app has no ads |
| Content rating | Category **Reference, News, or Educational**. Violence: No. Sexuality: **mild references in text** (classical dream book). Language: No. Drugs: No. Gambling: No. User interaction: No (WhatsApp opens outside the app). Shares location: No. Digital purchases: No |
| Target audience | **18 and over** only |
| News app | No |
| Data safety | Collects data: **No**. Shares data: **No**. (Network requests carry no personal data; favourites stay on the device; WhatsApp messages are sent by the user in WhatsApp.) |
| Government app | No |
| Financial features | None |
| Health | None |

## 4. Store listing (10 min)
Paste from `LISTING.md`. Upload icon, feature graphic and at least 4 phone screenshots. Add the English listing under **Manage translations**. Contact: your email (required) and website darsenizami.net.

## 5. Closed test: required for new personal accounts (14 days)
**Testing > Closed testing > Create track**, upload `khwab-v1.0-release.aab`, add at least **12 testers** (their Gmail addresses), send them the opt-in link, and they must keep the app installed for **14 days**. Accept **Play App Signing** when asked.

## 6. Production
After 14 days: **Apply for production**, answer the questions about your test, then **Production > Create release**, upload the same AAB, **Send for review** (1–7 days).

## Updates later
Raise `versionCode` (1 → 2) and `versionName` in `app/build.gradle.kts`, run `.\gradlew.bat bundleRelease`, upload the new AAB. Content changes (new translations, WhatsApp number) need **no** app update: they come from the database.
