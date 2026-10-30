# Translation rules for dream-symbol batches

Input: `data/batches/bNNN.json`, a list of entries from al-Nabulsi's تعطير الأنام في تعبير المنام:
`{"id", "headword_ar", "page", "arabic"}`. The `arabic` field is the entry text (already filtered for
Google Play policy; "…" marks removed passages).

Output: `data/tr/bNNN.json`, a JSON array with **one object per input entry, same order, same `id`**:

```json
{
  "id": "n123",
  "name_ur": "سانپ",
  "name_en": "Snake",
  "roman": "saanp",
  "category": 9,
  "tabeer_ur": "…full Urdu translation…",
  "tabeer_en": "…full English translation…"
}
```

## Rules

1. **Translate the whole entry, faithfully.** No summarising, no skipping sentences, no added commentary, advice or opinions. Keep every condition ("if the dreamer is a merchant…").
2. Keep "…" wherever the Arabic has "…". Do not try to restore removed text.
3. **Quran quotes** (text inside ﴿ ﴾): keep the Arabic exactly as given, then its meaning in brackets. Only add a surah:ayah reference if you are certain of it; otherwise leave the reference out. Never invent references or hadith numbers.
4. Hadith quoted in the entry: translate as written; do not add grading or numbers.
5. **Urdu:** standard, respectful Urdu (as in Urdu Islamic books). Use "خواب میں …" style. Prophets: علیہ السلام; the Prophet: ﷺ. Use Urdu punctuation (۔ ، ؟). Short sentences. No Hindi words.
6. **English:** plain modern English. "In a dream, X means…". Use "the Prophet ﷺ".
7. `name_ur`: the everyday Urdu word for the symbol (e.g. حية → سانپ, قط → بلی). `name_en`: everyday English word, capitalised. `roman`: Roman Urdu of `name_ur`, lowercase (e.g. "saanp").
8. `category`: the best single index from this list, or -1 if none fits:
   0 انبیاء و اولیاء · 1 فرشتے · 2 عبادات · 3 انسان و رشتے · 4 جسم کے اعضاء · 5 موت و قبر · 6 شادی و ولادت ·
   7 جانور · 8 پرندے · 9 کیڑے مکوڑے · 10 پانی و بارش · 11 آگ و روشنی · 12 آسمان و ستارے · 13 زمین و درخت ·
   14 پھل و اناج · 15 کھانا پینا · 16 لباس و زیور · 17 گھر و مسجد · 18 سفر و سواری · 19 رنگ · 20 اعداد ·
   21 مال و تجارت · 22 جنگ و ہتھیار · 23 بیماری و علاج · 24 جن، قیامت، جنت
9. Output must be valid JSON (UTF-8, no comments, no trailing commas). Check it with:
   `python -I -c "import json,sys; d=json.load(open(sys.argv[1],encoding='utf-8')); print(len(d))" data/tr/bNNN.json`

10. Also write `data/tr/bNNN.notes.md`: one line per entry whose Arabic was unclear, cut off, or whose headword sense you guessed (`- id (headword): what was unclear and what you chose`). These go to the scholar for review.
