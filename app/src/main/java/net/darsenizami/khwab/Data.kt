package net.darsenizami.khwab

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** The Supabase project that holds every entry. The publishable key only allows reading. */
object Api {
    const val URL = "https://ctmxaemgduaskngdodvp.supabase.co/rest/v1"
    const val KEY = "sb_publishable_oczRrTN3-Fcw1Q6h5fxKHw_JFUvJDHR"
}

enum class Lang { UR, EN }

/** Picks the Urdu or English text for the current language. */
fun Lang.t(ur: String, en: String) = if (this == Lang.UR) ur else en

/** Contact details; defaults are used until the web copy loads. */
data class Contact(
    val whatsapp: String = "923132020392",
    val whatsappShown: String = "+92 313 2020392",
    val scholarUr: String = "ایس ایم طلحہ",
    val scholarEn: String = "SM Talha",
    val website: String = "https://darsenizami.net",
)

/** One row of the light index downloaded at start-up (no long texts). */
data class SymbolLite(
    val id: String,
    val headwordAr: String,
    val nameUr: String?,
    val nameEn: String?,
    val roman: String?,
    val letter: String,
    val category: Int,
    val isHadith: Boolean,
    val hasUr: Boolean,
    val hasEn: Boolean,
) {
    val searchKey = normalize(listOfNotNull(nameUr, nameEn, roman, headwordAr).joinToString(" "))
    fun name(lang: Lang): String? = if (lang == Lang.UR) nameUr else nameEn
    fun translated(lang: Lang) = if (lang == Lang.UR) hasUr else hasEn
    val enLetter: String get() = nameEn?.firstOrNull { it.isLetter() }?.uppercase() ?: ""
}

/** Full entry, fetched when a symbol is opened. */
data class SymbolDetail(
    val lite: SymbolLite,
    val arabic: String,
    val tabeerUr: String?,
    val tabeerEn: String?,
    val page: Int?,
    val reviewed: Boolean,
) {
    fun tabeer(lang: Lang) = if (lang == Lang.UR) tabeerUr else tabeerEn
    val reference: String
        get() = if (lite.isHadith) "Sahih al-Bukhari 6994" else "al-Nabulsi, Ta'tir al-Anam, Dar al-Fikr ed., p. $page"
}

data class Topic(val titleUr: String, val titleEn: String?, val ayah: String?, val parasUr: List<String>, val parasEn: List<String>, val refs: List<String>) {
    fun title(lang: Lang) = if (lang == Lang.EN && !titleEn.isNullOrBlank()) titleEn else titleUr
    fun paragraphs(lang: Lang) = if (lang == Lang.EN && parasEn.isNotEmpty()) parasEn else parasUr
}

data class Category(val id: Int, val nameUr: String, val nameEn: String, val icon: String) {
    fun name(lang: Lang) = if (lang == Lang.UR) nameUr else nameEn
}

/** Everything the app needs before showing screens. */
class Library(val symbols: List<SymbolLite>, val topics: List<Topic>, val categories: List<Category>, val contact: Contact) {
    private val byId = symbols.associateBy { it.id }
    fun get(id: String) = byId[id]

    fun translatedCount(lang: Lang) = symbols.count { it.translated(lang) }

    /** Translated entries first, then the rest of the book. */
    fun search(query: String, lang: Lang): List<SymbolLite> {
        val q = normalize(query.trim())
        if (q.isEmpty()) return emptyList()
        return symbols.filter { q in it.searchKey }.sortedBy { !it.translated(lang) }
    }

    fun inCategory(id: Int) = symbols.filter { it.category == id }

    fun byLetter(letter: String, lang: Lang) =
        symbols.filter { (if (lang == Lang.UR) it.letter else it.enLetter) == letter }.sortedBy { !it.translated(lang) }
}

/** Folds Arabic/Urdu letter variants and strips harakat so "حية", "حيّة" and "حیہ" match. */
fun normalize(s: String): String = s.lowercase()
    .replace(Regex("[ً-ْٰ]"), "")
    .replace(Regex("[أإآ]"), "ا")
    .replace('ك', 'ک').replace('ي', 'ی').replace('ى', 'ی').replace('ه', 'ہ').replace('ة', 'ہ')

/**
 * Reads from the web and keeps a copy on the phone, so the app works offline after the first load.
 * The index is refreshed once a day; opened entries are kept for a week.
 */
class Repository(context: Context) {
    private val dir = File(context.filesDir, "web").apply { mkdirs() }

    private fun get(path: String): String {
        val conn = URL("${Api.URL}/$path").openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout = 30_000
        conn.setRequestProperty("apikey", Api.KEY)
        conn.setRequestProperty("Authorization", "Bearer ${Api.KEY}")
        conn.setRequestProperty("Accept", "application/json")
        try {
            if (conn.responseCode !in 200..299) throw IllegalStateException("HTTP ${conn.responseCode}")
            return conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    private fun cached(name: String, maxAgeMs: Long, fetch: () -> String): String {
        val f = File(dir, name)
        if (f.exists() && System.currentTimeMillis() - f.lastModified() < maxAgeMs) return f.readText()
        return try {
            fetch().also { f.writeText(it) }
        } catch (e: Exception) {
            if (f.exists()) f.readText() else throw e
        }
    }

    /** PostgREST returns at most 1000 rows per request, so page through the index. */
    private fun fetchIndex(): String {
        val all = JSONArray()
        var offset = 0
        while (true) {
            val page = JSONArray(get("symbols?select=id,headword_ar,name_ur,name_en,roman,letter,category,is_hadith,has_ur,has_en&order=priority,page&limit=1000&offset=$offset"))
            for (i in 0 until page.length()) all.put(page.get(i))
            if (page.length() < 1000) break
            offset += 1000
        }
        return all.toString()
    }

    suspend fun load(forceRefresh: Boolean = false): Library = withContext(Dispatchers.IO) {
        val day = if (forceRefresh) 0L else 24 * 3600 * 1000L
        val index = JSONArray(cached("index.json", day, ::fetchIndex))
        val topics = JSONArray(cached("topics.json", day) { get("topics?select=*&order=id") })
        val cats = JSONArray(cached("categories.json", day) { get("categories?select=*&order=id") })
        val config = JSONArray(cached("config.json", day) { get("app_config?select=key,value") })

        val cfg = (0 until config.length()).associate { config.getJSONObject(it).let { o -> o.getString("key") to o.getString("value") } }
        Library(
            symbols = List(index.length()) { lite(index.getJSONObject(it)) },
            topics = List(topics.length()) { i ->
                val o = topics.getJSONObject(i)
                Topic(o.getString("title_ur"), o.optStr("title_en"), o.optStr("ayah"), o.strings("paragraphs_ur"), o.strings("paragraphs_en"), o.strings("refs"))
            },
            categories = List(cats.length()) { i ->
                val o = cats.getJSONObject(i)
                Category(o.getInt("id"), o.getString("name_ur"), o.getString("name_en"), o.getString("icon"))
            },
            contact = Contact(
                whatsapp = cfg["whatsapp_number"] ?: Contact().whatsapp,
                whatsappShown = cfg["whatsapp_shown"] ?: Contact().whatsappShown,
                scholarUr = cfg["scholar_name_ur"] ?: Contact().scholarUr,
                scholarEn = cfg["scholar_name_en"] ?: Contact().scholarEn,
                website = cfg["website"] ?: Contact().website,
            ),
        )
    }

    suspend fun detail(id: String): SymbolDetail = withContext(Dispatchers.IO) {
        val safe = URLEncoder.encode(id, "UTF-8")
        val arr = JSONArray(cached("d_$safe.json", 7 * 24 * 3600 * 1000L) { get("symbols?id=eq.$safe&select=*") })
        val o = arr.getJSONObject(0)
        SymbolDetail(lite(o), o.optString("arabic"), o.optStr("tabeer_ur"), o.optStr("tabeer_en"), if (o.isNull("page")) null else o.getInt("page"), o.optBoolean("reviewed"))
    }

    private fun lite(o: JSONObject) = SymbolLite(
        id = o.getString("id"),
        headwordAr = o.getString("headword_ar"),
        nameUr = o.optStr("name_ur"),
        nameEn = o.optStr("name_en"),
        roman = o.optStr("roman"),
        letter = o.optString("letter"),
        category = if (o.isNull("category")) -1 else o.getInt("category"),
        isHadith = o.optBoolean("is_hadith"),
        hasUr = o.optBoolean("has_ur"),
        hasEn = o.optBoolean("has_en"),
    )
}

private fun JSONObject.optStr(key: String): String? = if (isNull(key)) null else optString(key).ifBlank { null }

private fun JSONObject.strings(key: String): List<String> {
    val a = optJSONArray(key) ?: return emptyList()
    return List(a.length()) { a.getString(it) }
}

/** Saved settings: favourites and language, kept only on this phone. */
class Prefs(context: Context) {
    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    fun favorites(): Set<String> = prefs.getStringSet("favorites", emptySet())!!.toSet()
    fun saveFavorites(ids: Set<String>) = prefs.edit().putStringSet("favorites", ids).apply()
    fun lang(): Lang = if (prefs.getString("lang", "ur") == "en") Lang.EN else Lang.UR
    fun saveLang(l: Lang) = prefs.edit().putString("lang", if (l == Lang.EN) "en" else "ur").apply()
}

val URDU_LETTERS = "ا ب پ ت ٹ ث ج چ ح خ د ڈ ذ ر ڑ ز ژ س ش ص ض ط ظ ع غ ف ق ک گ ل م ن و ہ ی".split(" ")
val ENGLISH_LETTERS = ('A'..'Z').map { it.toString() }

fun digits(n: Int, lang: Lang): String =
    if (lang == Lang.EN) n.toString() else n.toString().map { if (it.isDigit()) "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")
