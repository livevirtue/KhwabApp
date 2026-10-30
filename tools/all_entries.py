import html, re, sys, glob, os, json

# Build one continuous text from all pages, then split it into "- (headword) ..." entries.
d = sys.argv[1]
here = os.path.dirname(os.path.abspath(__file__))
chunks = []
for f in glob.glob(os.path.join(d, "u*.html")):
    n = int(re.search(r"u(\d+)\.html", f).group(1))
    t = open(f, encoding="utf-8", errors="ignore").read()
    t = re.sub(r"<script.*?</script>|<style.*?</style>", " ", t, flags=re.S)
    t = html.unescape(re.sub(r"<[^>]+>", "\n", t))
    t = re.split(r"\n\s*\d+ / \d+\s*\n", t)[0]  # drop the site footer after the page counter
    start = t.find("- (")
    chunks.append((n, t[start:] if start >= 0 else ""))
chunks.sort()

entries = []
for n, t in chunks:
    for part in re.split(r"\n\s*(?=[-–]\s*\()", "\n" + t):
        m = re.match(r"\s*[-–]\s*\(([^)]{1,40})\)\s*(.*)", part, re.S)
        if m:
            entries.append({"h": m.group(1).strip(), "p": n, "t": re.sub(r"\s+", " ", m.group(2)).strip()})
        elif entries and part.strip():
            # continuation of the previous entry across a page break
            entries[-1]["t"] += " " + re.sub(r"\s+", " ", part).strip()

json.dump(entries, open(os.path.join(here, "all.json"), "w", encoding="utf-8"), ensure_ascii=False, indent=0)
with open(os.path.join(here, "index.txt"), "w", encoding="utf-8") as fh:
    for e in entries:
        fh.write(f"{e['p']}\t{e['h']}\t{len(e['t'])}\n")
print(len(entries), "entries")
