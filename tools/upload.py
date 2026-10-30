"""Upload symbols (+ any finished translations in data/tr), categories and topics to Supabase.

Usage: python -I tools/upload.py <upload-token-file>
Re-run any time: rows are upserted by id, so finished translation batches simply fill in.
"""
import glob, json, os, re, sys, urllib.error, urllib.request

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
URL = "https://ctmxaemgduaskngdodvp.supabase.co/rest/v1"
KEY = "sb_publishable_oczRrTN3-Fcw1Q6h5fxKHw_JFUvJDHR"
TOKEN = open(sys.argv[1], encoding="utf-8").read().strip()

CATEGORY_EN = ["Prophets & saints", "Angels", "Worship", "People & relatives", "Body", "Death & grave",
               "Marriage & birth", "Animals", "Birds", "Insects & reptiles", "Water & rain", "Fire & light",
               "Sky & stars", "Earth & trees", "Fruit & grain", "Food & drink", "Clothes & jewellery",
               "Home & mosque", "Travel & riding", "Colours", "Numbers", "Wealth & trade", "War & weapons",
               "Illness & cure", "Jinn, Judgement, Paradise"]


def normalize(s):
    s = re.sub(r"[ً-ْٰ]", "", (s or "").lower())
    for a, b in (("أ", "ا"), ("إ", "ا"), ("آ", "ا"), ("ك", "ک"), ("ي", "ی"), ("ى", "ی"), ("ه", "ہ"), ("ة", "ہ")):
        s = s.replace(a, b)
    return s


def post(table, rows):
    for i in range(0, len(rows), 200):
        req = urllib.request.Request(
            f"{URL}/{table}?on_conflict=id", data=json.dumps(rows[i:i + 200], ensure_ascii=False).encode("utf-8"), method="POST",
            headers={"apikey": KEY, "Authorization": f"Bearer {KEY}", "Content-Type": "application/json",
                     "Prefer": "resolution=merge-duplicates,return=minimal", "x-upload-token": TOKEN})
        try:
            with urllib.request.urlopen(req) as r:
                assert r.status in (200, 201, 204), r.status
        except urllib.error.HTTPError as e:
            raise SystemExit(f"{table} rows {i}-{i + 200}: HTTP {e.code} {e.read().decode()[:400]}")


def main():
    entries = json.load(open(os.path.join(ROOT, "data", "entries.json"), encoding="utf-8"))
    # Batch bNNN holds entries[(NNN-1)*50 : NNN*50] in order, so map translations by position.
    tr = {}
    for f in sorted(glob.glob(os.path.join(ROOT, "data", "tr", "b*.json"))):
        n = int(re.search(r"b(\d+)\.json$", f).group(1))
        try:
            part = json.load(open(f, encoding="utf-8"))
        except ValueError as e:
            print("skip unfinished", os.path.basename(f)); continue
        for k, t in enumerate(part):
            i = (n - 1) * 50 + k
            if i < len(entries):
                tr[entries[i]["id"]] = t

    rows = []
    for e in entries:
        t = tr.get(e["id"], {})
        name_ur = t.get("name_ur") or e.get("name_ur")
        cat = t.get("category", e.get("category"))
        first = normalize(name_ur or e["headword_ar"])[:1]
        rows.append({
            "id": e["id"], "headword_ar": e["headword_ar"], "page": e["page"], "arabic": e["arabic"],
            "name_ur": name_ur, "name_en": t.get("name_en"), "roman": t.get("roman"),
            "category": cat if isinstance(cat, int) and 0 <= cat <= 24 else None,
            "letter": first, "tabeer_ur": t.get("tabeer_ur"), "tabeer_en": t.get("tabeer_en"),
            "priority": e["priority"], "source": "nabulsi", "is_hadith": False, "reviewed": False,
            "search_key": normalize(" ".join(x for x in (name_ur, t.get("name_en"), t.get("roman"), e["headword_ar"]) if x)),
        })

    # The hadith entry (seeing the Prophet ﷺ) is not from al-Nabulsi.
    app = json.load(open(os.path.join(ROOT, "data", "app-assets-old", "symbols.json"), encoding="utf-8"))
    nabi = next(s for s in app if s["id"] == "nabi")
    rows.append({
        "id": "nabi", "headword_ar": nabi["ar"], "page": None, "arabic": "", "name_ur": nabi["ur"], "name_en": "Seeing the Prophet ﷺ",
        "roman": "nabi ki ziyarat", "category": 0, "letter": "ن", "source": "bukhari", "is_hadith": True, "priority": 0,
        "tabeer_ur": nabi["books"][0]["t"], "reviewed": True,
        "tabeer_en": "Whoever sees me in a dream has truly seen me, for Satan cannot take my form. (Sahih al-Bukhari 6994)",
        "search_key": normalize(nabi["ur"] + " " + nabi["ar"] + " nabi prophet"),
    })
    post("symbols", rows)

    cats = json.load(open(os.path.join(ROOT, "data", "app-assets-old", "categories.json"), encoding="utf-8"))
    post("categories", [{"id": i, "name_ur": c[0], "name_en": CATEGORY_EN[i], "icon": c[1]} for i, c in enumerate(cats)])

    topics_en = {}
    p = os.path.join(ROOT, "data", "topics_en.json")
    if os.path.exists(p):
        topics_en = {t["id"]: t for t in json.load(open(p, encoding="utf-8"))}
    topics = json.load(open(os.path.join(ROOT, "data", "app-assets-old", "topics.json"), encoding="utf-8"))
    post("topics", [{"id": i, "title_ur": t["t"], "ayah": t.get("a"), "paragraphs_ur": t["p"], "refs": t["r"],
                     "title_en": topics_en.get(i, {}).get("title_en"), "paragraphs_en": topics_en.get(i, {}).get("paragraphs_en", [])}
                    for i, t in enumerate(topics)])

    done = sum(1 for r in rows if r["tabeer_ur"] and r["tabeer_en"])
    print(f"uploaded {len(rows)} symbols ({done} with Urdu + English), {len(cats)} categories, {len(topics)} topics")


main()
