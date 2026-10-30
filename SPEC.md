# خوابوں کی تعبیر: Android app plan

**Idea:** an Urdu app to look up the meaning (tabeer) of dream symbols from well-known classical tabeer books, with the Quran and Sunnah etiquette of dreams. Users who want a personal interpretation fill in a form, and the app sends it to your WhatsApp. You agree the fee or hadya (gift) in the WhatsApp chat.

**Status:** plan only. Next comes a clickable preview, then the Android build.

---

## 1. Users and goals

| User | Wants |
|---|---|
| Ordinary Urdu reader | Type “saanp”, “سانپ” or “snake” and read what the books say |
| Someone with a worrying dream | Know the Sunnah steps first, then possibly ask an expert |
| You (the interpreter) | Get structured dream requests on WhatsApp, agree the hadya and reply |

---

## 2. Content sources (no invented interpretations)

Every interpretation in the app must name its book. Nothing is written from imagination.

### 2.1 Quran and hadith (dream etiquette)

| Topic | Reference |
|---|---|
| Yusuf (AS) and dream interpretation | Quran 12:4–6, 12:36–49, 12:100 |
| A good dream is from Allah, a bad one from Shaytan | Sahih al-Bukhari 6985; Sahih Muslim 2261 |
| A good dream is one part of 46 parts of prophethood | Sahih al-Bukhari 6983 |
| After a bad dream: blow lightly to the left 3 times, seek refuge 3 times, turn over, tell no one | Sahih Muslim 2261–2262 |
| Tell a good dream only to someone you love or someone wise | Jami at-Tirmidhi 2278 |
| Whoever sees the Prophet ﷺ in a dream has truly seen him | Sahih al-Bukhari 6994 |
| Warning against lying about a dream | Sahih al-Bukhari 7042 |

### 2.2 Classical tabeer books

| Book | Author | Copyright | Note |
|---|---|---|---|
| تعطیر الانام فی تعبیر المنام | Abd al-Ghani al-Nabulsi (d. 1143 AH) | Public domain (Arabic original) | Alphabetical, very complete. **Main source.** |
| الاشارات فی علم العبارات | Khalil ibn Shahin al-Zahiri (d. 873 AH) | Public domain (Arabic original) | Organised by category |
| منتخب الکلام فی تفسیر الاحلام | Attributed to Muhammad ibn Sirin (d. 110 AH) | Public domain (Arabic original) | Most famous name in Urdu. **Scholars dispute whether Ibn Sirin wrote it.** The app says “attributed to” (منسوب). |

**Copyright rule:** modern Urdu translations (for example, Lahore editions of “Khwabon ki Tabeer”) are copyrighted. Do not copy them. Either:
- **A.** translate from the Arabic originals yourself, or with a translator, or
- **B.** get written permission from a publisher.

Each entry stores the book, volume and page so anyone can check it.

### 2.3 Review

Before release, an alim checks every entry. Entries waiting for review do not ship.

---

## 3. Categories (اقسام)

25 categories, the way the classical books group symbols:

1. انبیاء، صحابہ اور اولیاء
2. فرشتے
3. عبادات (نماز، روزہ، حج، قرآن، اذان)
4. انسان اور رشتے دار
5. جسم کے اعضاء
6. موت، قبر اور جنازہ
7. شادی، حمل اور ولادت
8. جانور (چوپائے)
9. پرندے
10. کیڑے مکوڑے اور رینگنے والے
11. پانی، دریا، سمندر اور بارش
12. آگ اور روشنی
13. آسمان، سورج، چاند اور ستارے
14. زمین، پہاڑ اور درخت
15. پھل، سبزیاں اور اناج
16. کھانے پینے کی چیزیں
17. لباس اور زیور
18. گھر، عمارتیں اور مسجد
19. سفر اور سواریاں
20. رنگ
21. اعداد
22. مال، پیسہ اور تجارت
23. جنگ اور ہتھیار
24. بیماری اور علاج
25. جن، جادو، قیامت، جنت اور دوزخ

Users can also browse by **Urdu alphabet** (ا ب پ ت …), like an index.

---

## 4. Screens

| # | Screen | What it does |
|---|---|---|
| 1 | **ہوم (Home)** | Search box (Urdu, Roman Urdu, English), category grid, “آج کی حدیث” card, big “اپنا خواب بھیجیں” button |
| 2 | **قسم (Category)** | Symbols in the category, A–Z by Urdu letter |
| 3 | **علامت (Symbol detail)** | Symbol in Urdu, Arabic and Roman. One card per book with its interpretation and page reference. Conditions noted (for example, “if the dreamer is unmarried…”). Favourite and share buttons. |
| 4 | **آدابِ خواب (Dream etiquette)** | The Quran and hadith in section 2.1, with the Sunnah steps for good and bad dreams |
| 5 | **خواب بھیجیں (Send dream)** | Form, see section 5 |
| 6 | **ہدیہ (Fee / hadya)** | Explains that the fee is agreed on WhatsApp and paid there. Text comes from the config, see section 6. |
| 7 | **پسندیدہ (Favourites)** | Saved symbols |
| 8 | **خواب کا علم (All about dreams)** | 14 topics, each with Quran or hadith references: three types of dreams (Muslim 2263), true dreams as part of prophethood (Bukhari 6983, 6990), revelation began with true dreams (Bukhari 3), dreams of the prophets in the Quran (12:4, 12:43–49, 12:100, 37:102, 48:27), what to do after a good dream and after a bad one, seeing the Prophet ﷺ (Bukhari 6994), lying about dreams (Bukhari 7042), whose dreams are most truthful (Muslim 2263, Bukhari 7017), whom to ask, and that tabeer can be wrong (Bukhari 7046), sleep etiquette (Bukhari 247, 2311, 5017), ihtilam and ghusl (Bukhari 282), istikhara and dreams (Bukhari 1162), common misconceptions. Ends with the source books, disclaimer and privacy note. |

Bottom navigation (right to left): ہوم · اقسام · خواب بھیجیں · پسندیدہ · خواب کا علم

**Policy filter:** clauses describing sexual acts and clauses about religious groups (Jews, Christians, Zoroastrians) are removed from the Arabic before shipping, along with 8 headwords on those topics (818 clauses in total, each marked "…"). The build script is `tools/build_all.py` (run `tools/all_entries.py` first on the downloaded usul.ai pages).

**Content:** the full text of al-Nabulsi (2,322 headwords after the policy filter) ships in Arabic (`data-all.js` in the preview). Urdu translations are added entry by entry; untranslated entries show the Arabic with a "ترجمہ باقی" label.

**English font:** Calibri where installed, otherwise Carlito (SIL OFL, metric-compatible with Calibri, safe to bundle). Calibri itself is a Microsoft font and must not be bundled in the app.

**Urdu paragraphs:** 16sp, line height 2.6×, justified, half-line gap between paragraphs.

**Font:** all UI text uses Jameel Noori Nastaleeq where it is allowed, with Noto Nastaliq Urdu as the fallback. The preview uses Jameel Noori if it is installed on the viewer's PC. **The Android app may bundle Jameel Noori only with written permission**, because no clear redistribution licence was found for it. Without permission, ship Noto Nastaliq Urdu (SIL OFL, free to bundle). Only Quran and Arabic words use Amiri (Naskh), as in printed Urdu books.

---

## 5. WhatsApp dream form

**Fields**
1. نام (optional)
2. مرد / عورت
3. عمر: under 18 · 18–30 · 31–50 · 50+
4. شادی شدہ / غیر شادی شدہ (classical books give different meanings by state)
5. خواب کب دیکھا: رات کا پہلا حصہ · آخری حصہ / فجر کے قریب · دن میں
6. خواب کیسا لگا: اچھا · عام · ڈراؤنا
7. خواب کی تفصیل (multi-line, required, 20 to 1500 characters)
8. Checkbox: “میں تصدیق کرتا/کرتی ہوں کہ یہ خواب میں نے واقعی دیکھا ہے” (Bukhari 7042)

**Bad dream guard:** if “ڈراؤنا” is picked, first show the Sunnah steps (Muslim 2261–2262) and the advice not to tell a bad dream. Then show two buttons: “ٹھیک ہے، نہیں بھیجتا” and “پھر بھی بھیجیں”.

**Sending:** build a formatted Urdu message and open `https://wa.me/<NUMBER>?text=<encoded message>`.
- If WhatsApp is not installed, the same link opens in the browser.
- The app saves nothing and has no server.

**Message format**
```
السلام علیکم، خواب کی تعبیر درکار ہے
نام: …
جنس: … | عمر: … | حالت: …
وقت: … | کیفیت: …
خواب:
…
(خوابوں کی تعبیر ایپ سے بھیجا گیا)
```

---

## 6. Config: one file to edit

`app/src/main/java/.../AppConfig.kt`
```kotlin
const val WHATSAPP_NUMBER = "923132020392"   // shown as +92 313 2020392
const val SCHOLAR_NAME = "ایس ایم طلحہ"      // Islamic scholar SM Talha (About section under خواب کا علم)
const val WEBSITE = "https://darsenizami.net"
const val HADYA_TEXT = "تعبیر کا ہدیہ واٹس ایپ پر طے کیا جائے گا۔"
const val REPLY_TIME_TEXT = "عموماً 24 سے 48 گھنٹے میں جواب"
```

---

## 7. Design: Urdu style

- **Whole UI is right to left** (`LayoutDirection.Rtl` at the root), not just the text.
- **Font:** Noto Nastaliq Urdu (OFL licence, bundled in `res/font`) for Urdu, and Amiri for Arabic/Quran. Line height at least 2× because Nastaliq is tall.
- **Colours:** deep night-blue (dreams, night) with a soft gold accent and a light paper background. Dark mode supported.
- **Motifs:** crescent and stars in the header, simple Islamic geometric border on cards. No pictures of people or animals.
- **Numbers:** Urdu digits (۱۲۳) in the UI, Latin digits in hadith numbers for easy lookup.
- Text size setting: چھوٹا · درمیانہ · بڑا

---

## 8. Tech

| Part | Choice |
|---|---|
| Language / UI | Kotlin, Jetpack Compose, Material 3 |
| Min / target SDK | 26 / 36 |
| Data | `assets/tabeer.json` loaded into memory (a few MB at most). Switch to Room with full-text search if it grows past ~5,000 entries. |
| Search | Normalise Urdu (remove harakat, unify ی/ي and ک/ك and ہ/ه), match Urdu, Roman and English keywords |
| Favourites | DataStore (on device) |
| WhatsApp | `Intent.ACTION_VIEW` with a `wa.me` link |
| Internet | Not required. No INTERNET permission. |
| Ads / analytics | None in v1 (keeps the Data Safety form simple) |

**Entry format (`tabeer.json`)**
```json
{
  "id": "snake",
  "ur": "سانپ", "ar": "حية", "roman": ["saanp", "sanp"], "en": ["snake"],
  "category": 10,
  "interpretations": [
    { "book": "nabulsi", "volume": 1, "page": 0, "text_ur": "…", "conditions_ur": null }
  ],
  "reviewed": false
}
```
Entries with `"reviewed": false` are left out of release builds.

**Build environment on this PC:** JDK at `D:\GoogleStudio\jbr`, SDK platform 36. Command-line Gradle builds need `TEMP`/`TMP` set to a short path such as `D:\KhwabApp\.tmp`, or Java fails with “Unable to establish loopback connection”. Android Studio's Run button is the simplest path.

---

## 9. Google Play checklist

Follow https://play.google.com/console and the Developer Program Policies.

1. **Account:** personal developer account (US$25 once) with ID verification.
2. **Testing:** new personal accounts need a **closed test with at least 12 testers for 14 days** before production.
3. **Build:** signed **AAB**, target the current required API level, `versionCode` raised on every upload.
4. **Privacy policy URL** (required). Say: the app collects no data; text you choose to send goes to WhatsApp under WhatsApp's own privacy policy.
5. **Data safety:** “No data collected or shared by the app.”
6. **Content rating:** questionnaire, reference app, no violence or gambling.
7. **Target audience:** 18+, because the WhatsApp service involves payment and the full classical text includes adult (sexual) dream interpretations. Answer the content-rating questionnaire honestly about sexual references in text. Some classical passages also contain insulting remarks about other religions; Google Play's hate-speech policy can reject that, so decide before launch whether to keep them in the Arabic or the Urdu.
8. **Payments policy:** the app sells nothing and has no price or payment buttons. The fee for your personal interpretation is agreed and paid in WhatsApp. **Check the current Google Play Payments policy before launch,** since rules on paid human services change.
9. **Misleading claims:** no “100% sahi tabeer”, no predictions of the future. The app says tabeer is an opinion (رائے), not knowledge of the unseen.
10. **Intellectual property:** only public domain originals, or translations you own or have permission for (section 2.2).
11. **Store listing:** Urdu and English title and description, 512×512 icon, 1024×500 feature graphic, 4 or more phone screenshots.

---

## 10. Disclaimer (shown in the app)

> خواب کی تعبیر ایک رائے ہے، غیب کا علم نہیں۔ اچھا خواب اللہ کی طرف سے بشارت ہے اور برا خواب شیطان کی طرف سے، جس کا ذکر نہ کریں (صحیح مسلم ۲۲۶۱)۔ کسی بھی خواب کی بنیاد پر اہم فیصلے نہ کریں۔ تعبیرات کلاسیکی کتب سے حوالے کے ساتھ لی گئی ہیں۔

---

## 11. Build phases

| Phase | Output | Time |
|---|---|---|
| 1 | Clickable Urdu preview (HTML) | Today |
| 2 | Android app: all screens, WhatsApp form, about 30 sample entries marked “نمونہ” | 1 session |
| 3 | Content: translate 300 to 500 symbols from al-Nabulsi with page references, then scholar review | Weeks; this is human work |
| 4 | Signed AAB, store listing, 14-day closed test | About 3 weeks |
| 5 | Production release | 1 to 7 days review |

## 12. Later ideas (not in v1)

1. Search history kept on the device
2. Istikhara guide with its hadith (Sahih al-Bukhari 1162)
3. Morning and evening adhkar reminders
4. English version of the UI
5. A web version for your Blogger site
