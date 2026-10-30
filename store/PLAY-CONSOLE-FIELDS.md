# Google Play Console: every field for خوابوں کی تعبیر

Fill the sections in this order. Text in `code` can be copied exactly.

---

## 1. Create app

| Field | Value |
|---|---|
| App name | `خوابوں کی تعبیر` |
| Default language | **Urdu – ur** |
| App or game | **App** |
| Free or paid | **Free** (can never change to paid later) |
| Declarations | Tick both: Developer Program Policies, US export laws |

---

## 2. Store settings (Grow > Store presence > Store settings)

| Field | Value |
|---|---|
| App category | **Books & Reference** |
| Tags | Religion, Reference, Books (pick up to 5 offered) |
| Email address | *your email* (required, shown publicly) |
| Phone number | `+92 313 2020392` (optional) |
| Website | `https://darsenizami.net` |
| External marketing | On |

---

## 3. Main store listing: Urdu (default)

| Field | Limit | Value |
|---|---|---|
| App name | 30 | `خوابوں کی تعبیر` |
| Short description | 80 | `قرآن، سنت اور امام نابلسی کی کتاب سے خوابوں کی تعبیر، اردو اور انگریزی میں` |
| Full description | 4000 | Copy from `LISTING.md` (Urdu section) |
| App icon | 512×512 PNG | `graphics/app-icon-512.png` |
| Feature graphic | 1024×500 PNG | `graphics/feature-graphic-1024x500.png` |
| Phone screenshots | 2–8, min 320 px | Your 4–8 screenshots from the new app |
| Video | optional | Leave empty |

**Add English:** Store listing > **Manage translations > Add your own translations > English (United States)**

| Field | Value |
|---|---|
| App name | `Khwab ki Tabeer: Dream Meaning` |
| Short description | `Islamic dream interpretation from Quran, Sunnah and al-Nabulsi: Urdu & English` |
| Full description | Copy from `LISTING.md` (English section) |
| Graphics | Same files (or leave and it uses the Urdu ones) |

---

## 4. App content (Policy > App content)

### Privacy policy
`https://<your Blogger or darsenizami.net page>` (publish `privacy-policy.html` first)

### Ads
**No, my app does not contain ads**

### App access
**All functionality in my app is available without any access restrictions**

### Content rating (IARC questionnaire)
| Question | Answer |
|---|---|
| Email | your email |
| Category | **Reference, News, or Educational** |
| Violence (realistic or graphic) | No |
| Fear / horror content | No |
| Sexuality: sexual content or nudity in images | No |
| Sexuality: references to sex in text | **Yes, mild** (classical dream book mentions marriage and intimacy in text) |
| Profanity / crude humour | No |
| Drugs, alcohol, tobacco: references | **Yes, references only** (the book mentions wine and intoxication as dream symbols) |
| Gambling / simulated gambling | No |
| Users interact or share content in the app | **No** (WhatsApp opens outside the app) |
| Shares user's location | No |
| Digital purchases | No |
| Unrestricted web browsing | No |
| Hate speech / discrimination | No (filtered out) |
Expected result: about **Teen / 12+**. That's fine.

### Target audience and content
| Field | Value |
|---|---|
| Target age groups | **18 and over** only |
| Could the app unintentionally appeal to children? | **No** |

### News apps
**No, my app is not a news app**

### Data safety
| Question | Answer |
|---|---|
| Does your app collect or share any of the required user data types? | **No** |
| (If asked) Is all user data encrypted in transit? | Yes (HTTPS) |
| (If asked) Can users request data deletion? | No data is collected |
Summary shown to users: *"No data collected · No data shared"*.

### Government apps
**No**

### Financial features
**My app doesn't provide any financial features**

### Health apps
**My app does not have any health features**

### Advertising ID (if shown)
**No**, the app does not use an advertising ID

---

## 5. Countries / regions (Release > Production > Countries)

Recommended: **Pakistan, India, Saudi Arabia, UAE, Qatar, Kuwait, Oman, Bahrain, United Kingdom, United States, Canada**. Or simply "Add all countries".

---

## 6. Closed testing (required first for new personal accounts)

| Field | Value |
|---|---|
| Track | Closed testing > **Create track**, name `beta-testers` |
| Testers | Email list with **at least 12 Gmail addresses** |
| App bundle | `khwab-v1.0-release.aab` |
| Play App Signing | **Accept** (Google keeps the final signing key; you keep the upload key in `D:\keys`) |
| Release name | `1.0` |
| Release notes (Urdu) | see below |
| Release notes (English) | see below |

**Release notes:**
```
<ur>
پہلا ورژن: ۲۳۰۰ سے زیادہ خوابوں کی تعبیرات، اردو اور انگریزی میں، خواب کا علم، اور واٹس ایپ پر ذاتی تعبیر۔
</ur>
<en-US>
First release: 2,300+ dream symbols in Urdu and English, "About dreams" guide, and personal interpretation on WhatsApp.
</en-US>
```

Testers must **keep the app installed for 14 days**. Then: **Dashboard > Apply for production**.

---

## 7. Production release (after 14 days)

Production > **Create new release** > upload the **same** `khwab-v1.0-release.aab` (or a newer one with versionCode 2) > same release notes > **Next > Save > Send for review**. Review takes 1–7 days.
