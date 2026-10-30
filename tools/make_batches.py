"""Build data/entries.json (every symbol, common ones first) and data/batches/bNNN.json for translation.

Inputs: preview/data-all.js (policy-filtered Nabulsi text) and data/app-assets-old/symbols.json
(the 93 symbols that already have Urdu names and IDs used by favourites).
"""
import json, os, re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BATCH = 50

# Dream symbols people ask about most, in rough order (Arabic headwords as printed in the book).
COMMON = """حية ثعبان ماء مطر نار بيت شمس قمر كوكب سماء بحر نهر بئر سن أسنان سقوط شعر لحية عين يد رجل دم
موت ميت قبر جنازة كفن عرس عروس ولد صبي بنت أم أب أخ أخت زوج زوجة حمل ولادة طفل بكاء ضحك
صلاة وضوء أذان مسجد كعبة حج عمرة قرآن مصحف صوم زكاة صدقة دعاء نبي رسول ملك ملائكة جن شيطان سحر
كلب قط هر أسد ذئب ثعلب فأر فأرة عقرب نمل نحل ذباب بعوض قمل بقر غنم ضأن كبش جمل ناقة فرس خيل حمار بغل فيل قرد خنزير
دجاج ديك حمام غراب عصفور نسر صقر طاوس طير سمك تمساح سلحفاة ضفدع
خبز لحم لبن عسل تمر عنب رمان تفاح بطيخ تين زيتون موز ملح سكر أرز بيض جبن زبد سمن خل ماء زمزم
ذهب فضة خاتم سوار قلادة لؤلؤ ثوب قميص عمامة نعل حذاء سراويل حرير صوف
باب سلم سقف حائط دار سوق طريق جسر جبل شجرة نخل ورد زرع حنطة شعير ريح رعد برق ثلج برد مطر سحاب قوس قزح زلزلة
سفينة سفر طيران سيارة دابة سيف سكين رمح قوس حرب سجن قيد مرض طبيب دواء حمى جرح
مفتاح قفل كتاب قلم مرآة سراج نور ظلمة دخان رماد ذهب دراهم دنانير مال كنز
ملك سلطان قاضي عالم شيخ معلم امرأة رجل عجوز غلام جارية""".split()


def load_js_array(path, var):
    s = open(path, encoding="utf-8").read()
    return json.loads(s[s.index(var) + len(var):].strip().lstrip("=").strip().rstrip(";"))


def main():
    all_entries = load_js_array(os.path.join(ROOT, "preview", "data-all.js"), "window.NABULSI_ALL")
    app_syms = json.load(open(os.path.join(ROOT, "data", "app-assets-old", "symbols.json"), encoding="utf-8"))

    # Existing translated symbols keep their IDs, Urdu names and categories.
    known = {}
    for s in app_syms:
        if s["full"]:
            continue
        m = re.search(r"p\. (\d+)", s["books"][0]["ref"])
        if m:
            known[(s["ar"], int(m.group(1)))] = s

    entries = []
    for i, (h, page, text) in enumerate(all_entries):
        s = known.get((h, page)) or next((v for (ar, p), v in known.items() if p == page and (ar.startswith(h) or h.startswith(ar.split(" ")[0]))), None)
        entries.append({
            "id": s["id"] if s else f"n{i}",
            "headword_ar": h,
            "page": page,
            "arabic": f"({h}) {text}",
            "name_ur": s["ur"] if s else None,
            "category": s["cat"] if s and s["cat"] >= 0 else None,
        })

    def rank(e):
        bare = re.sub(r"[ً-ْ]", "", e["headword_ar"]).replace("ال", "", 1) if e["headword_ar"].startswith("ال") else e["headword_ar"]
        if e["name_ur"]:
            return 0
        for k, w in enumerate(COMMON):
            if bare == w or bare.split(" ")[0] == w:
                return 1 + k
        return 1000
    for e in entries:
        e["priority"] = rank(e)
    entries.sort(key=lambda e: (e["priority"], e["page"]))

    os.makedirs(os.path.join(ROOT, "data", "batches"), exist_ok=True)
    os.makedirs(os.path.join(ROOT, "data", "tr"), exist_ok=True)
    json.dump(entries, open(os.path.join(ROOT, "data", "entries.json"), "w", encoding="utf-8"), ensure_ascii=False)
    n = 0
    for b in range(0, len(entries), BATCH):
        n += 1
        part = [{"id": e["id"], "headword_ar": e["headword_ar"], "page": e["page"], "arabic": e["arabic"]} for e in entries[b:b + BATCH]]
        json.dump(part, open(os.path.join(ROOT, "data", "batches", f"b{n:03d}.json"), "w", encoding="utf-8"), ensure_ascii=False, indent=0)
    common = sum(1 for e in entries if e["priority"] < 1000)
    print(f"{len(entries)} entries, {common} common-first, {n} batches")


main()
