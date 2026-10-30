import json, os, re, sys
here = os.path.dirname(os.path.abspath(__file__))
E = json.load(open(os.path.join(here, "all.json"), encoding="utf-8"))

# "(ومن رأى)" / "(من رأى)" are sub-paragraphs of the entry before them, not headwords.
merged = []
for e in E:
    if merged and re.match(r"^(و?من رأى|ومن|وإن|فإن|وقيل|قال)", e["h"]):
        merged[-1][2] += f" ({e['h']}) {e['t']}"
    else:
        merged.append([e["h"], e["p"], e["t"]])

# Google Play policy filter: drop clauses that describe sexual acts or insult religious groups.
SEX = re.compile(r"(?<!ال)جامع|يجامع|زنا|زنى|يزني|زان[ي ]|زانية|لواط|لاط |يلوط|لوطي|افتض|يفتض|فرجها|فرجه|فرج امرأة|فرج المرأة|ينكح|نكح|نكاح أم|استمن|قواد|تقود على|عسيلة|ذكره|إحليل|مني |بهيمة|الدبر|دبر|عورة|جماع|يطأ|وطئ|وطء|عاهر|بغي|فاحشة|شهوة")
HATE = re.compile(r"(يهود|نصار|نصراني|مجوس|ذمة|كافر|كفار)[^ ]*( [^ ]+){0,3} (ملعون|لعن|خبيث|نجس|خنزير|قرد)|(ملعون|ملعونة)[^ ]*( [^ ]+){0,2} (يهود|نصار)")
# Any clause that characterises a religious group is dropped (several equate them with enemies or animals).
RELIG = re.compile(r"يهود|نصارى|نصراني|مجوس|الذمة|تهود|تنصر|يفجر|فجور|أتى غلام|بكلبة|الصليب")
DROP_HEAD = re.compile(r"^(يهودي|مجوس|زنا|لواط|زنار النصارى|بيعة|كنيسة|صليب)")
SPLIT = re.compile(r" (?=(?:ومن|فمن|وإن|فإن|وقيل|وربما|وكذلك|وأما|ومن رأى|\(ومن رأى\))\b)")
removed = 0
def clean(t):
    global removed
    parts, keep = SPLIT.split(t), []
    for p in parts:
        if SEX.search(p) or HATE.search(p) or RELIG.search(p):
            removed += 1
            if not keep or keep[-1] != "…":
                keep.append("…")
        else:
            keep.append(p)
    return " ".join(keep).strip()
for m in merged:
    m[2] = clean(m[2])
merged = [m for m in merged if re.sub(r"[…\s]", "", m[2]) and not DROP_HEAD.match(m[0])]
print("policy filter removed", removed, "clauses")

out = sys.argv[1]
with open(out, "w", encoding="utf-8") as fh:
    fh.write("// Full text of al-Nabulsi, Ta'tir al-Anam fi Ta'bir al-Manam (d. 1143 AH, public domain).\n")
    fh.write("// Dar al-Fikr edition as published on Shamela / usul.ai. [headword, printed page, Arabic text]\n")
    fh.write("window.NABULSI_ALL = ")
    json.dump(merged, fh, ensure_ascii=False, separators=(",", ":"))
    fh.write(";\n")
print(len(merged), "entries,", os.path.getsize(out) // 1024, "KB")
