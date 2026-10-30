package net.darsenizami.khwab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private const val MAX_ROWS = 150

@Composable
fun AppScaffold(
    library: Library,
    repo: Repository,
    tab: Tab,
    page: Page?,
    favorites: Set<String>,
    onTab: (Tab) -> Unit,
    onOpen: (Page) -> Unit,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleLang: () -> Unit,
) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    Scaffold(containerColor = c.page, bottomBar = { BottomBar(tab, page == null, onTab) }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (page) {
                is Page.Symbol -> SymbolScreen(page.id, library, repo, page.id in favorites, onBack, { onToggleFavorite(page.id) }, { onTab(Tab.SEND) })
                is Page.CategoryList -> {
                    val cat = library.categories.first { it.id == page.id }
                    Column {
                        Header(cat.name(lang), onBack = onBack)
                        val list = remember(page.id, lang) { library.inCategory(page.id).sortedBy { !it.translated(lang) } }
                        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (list.isEmpty()) item { Muted(lang.t("اس قسم کی علامات ترجمے کے بعد شامل ہوں گی۔", "Symbols in this category appear as translation finishes.")) }
                            else rowsWithCount(list, onOpen)
                        }
                    }
                }
                null -> when (tab) {
                    Tab.HOME -> HomeScreen(library, onOpen, onTab, onToggleLang)
                    Tab.CATEGORIES -> CategoriesScreen(library, onOpen)
                    Tab.SEND -> SendScreen(library.contact)
                    Tab.FAVORITES -> FavoritesScreen(library, favorites, onOpen)
                    Tab.KNOWLEDGE -> KnowledgeScreen(library, onToggleLang)
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(failed: Boolean, onRetry: () -> Unit, onToggleLang: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    Column(Modifier.fillMaxSize().background(c.page)) {
        Header(lang.t("خوابوں کی تعبیر", "Dream Interpretation"), lang.t("قرآن، سنت اور کلاسیکی کتب کی روشنی میں", "In the light of Quran, Sunnah and classical books"), actions = { LangButton(onToggleLang) })
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            if (failed) {
                Text(lang.t("انٹرنیٹ سے رابطہ نہیں ہو سکا۔ پہلی بار ڈیٹا لانے کے لیے انٹرنیٹ ضروری ہے۔", "Couldn't reach the internet. The first load needs a connection."),
                    style = lang.text(16, lineHeight = 2.2).copy(textAlign = TextAlign.Center))
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry) { Text(lang.t("دوبارہ کوشش کریں", "Try again"), style = lang.text(15, lineHeight = 1.6)) }
            } else {
                CircularProgressIndicator(color = c.gold)
                Spacer(Modifier.height(16.dp))
                Text(lang.t("علامات لوڈ ہو رہی ہیں…", "Loading symbols…"), style = lang.text(15), color = c.muted)
            }
        }
    }
}

@Composable
private fun BottomBar(tab: Tab, atRoot: Boolean, onTab: (Tab) -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    val items = listOf(
        Triple(Tab.HOME, lang.t("ہوم", "Home"), Icons.Filled.Home),
        Triple(Tab.CATEGORIES, lang.t("اقسام", "Browse"), Icons.AutoMirrored.Filled.List),
        Triple(Tab.SEND, lang.t("خواب بھیجیں", "Send dream"), Icons.AutoMirrored.Filled.Send),
        Triple(Tab.FAVORITES, lang.t("پسندیدہ", "Saved"), Icons.Filled.Favorite),
        Triple(Tab.KNOWLEDGE, lang.t("خواب کا علم", "Learn"), Icons.Filled.Info),
    )
    NavigationBar(containerColor = c.card) {
        items.forEach { (t, label, icon) ->
            NavigationBarItem(
                selected = tab == t && atRoot,
                onClick = { onTab(t) },
                icon = { Icon(icon, contentDescription = null, tint = if (t == Tab.SEND) c.whatsapp else LocalContentColor.current) },
                label = { Text(label, style = lang.text(11, lineHeight = 1.6), maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = c.gold, selectedTextColor = c.gold, indicatorColor = c.goldSoft,
                    unselectedIconColor = c.muted, unselectedTextColor = c.muted,
                ),
            )
        }
    }
}

@Composable
fun Header(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null, actions: @Composable () -> Unit = {}) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    Row(
        Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(c.night, c.night2)))
            .statusBarsPadding()
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, lang.t("واپس", "Back"), tint = c.onNight) }
        } else {
            Spacer(Modifier.width(8.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = lang.text(22, FontWeight.Bold, lineHeight = 1.7), color = c.onNight, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) Text(subtitle, style = lang.text(12, lineHeight = 1.7), color = c.onNight.copy(alpha = .8f))
        }
        actions()
    }
}

/** Switches between Urdu and English; shows the language you will switch to. */
@Composable
fun LangButton(onToggle: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    Box(
        Modifier.padding(horizontal = 4.dp).clip(RoundedCornerShape(10.dp)).background(c.onNight.copy(alpha = .14f))
            .clickable(onClick = onToggle).padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        if (lang == Lang.UR) Text("English", style = latin(14, FontWeight.Bold), color = c.onNight)
        else Text("اردو", style = urdu(15, FontWeight.Bold, lineHeight = 1.5), color = c.onNight)
    }
}

@Composable
private fun Muted(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, style = LocalLang.current.text(14), color = LocalKhwab.current.muted)
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, Modifier.padding(top = 4.dp), style = LocalLang.current.text(14, lineHeight = 1.8), color = LocalKhwab.current.muted)
}

@Composable
private fun SoftCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val c = LocalKhwab.current
    val shape = RoundedCornerShape(16.dp)
    Card(
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clip(shape).clickable(onClick = onClick) else Modifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = c.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) { content() }
}

@Composable
fun SymbolRow(s: SymbolLite, onClick: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    val name = s.name(lang)
    SoftCard(onClick = onClick) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                if (name != null) {
                    Text(name, style = lang.text(17, FontWeight.SemiBold, lineHeight = 1.8))
                    val sub = listOfNotNull(if (lang == Lang.UR) s.roman else s.nameUr).joinToString()
                    if (sub.isNotBlank()) Text(sub, style = if (lang == Lang.UR) latin(13) else urdu(13, lineHeight = 1.7), color = c.muted)
                } else {
                    Text(s.headwordAr, style = arabic(19, 1.6))
                    Text(lang.t("ترجمہ جلد", "Translation coming"), style = lang.text(12, lineHeight = 1.7), color = c.muted)
                }
            }
            if (name != null) Text(s.headwordAr, style = arabic(19, 1.5), color = c.gold)
        }
    }
}

private fun LazyListScope.rowsWithCount(list: List<SymbolLite>, onOpen: (Page) -> Unit) {
    item {
        val lang = LocalLang.current
        val more = if (list.size > MAX_ROWS) lang.t("، پہلے ${digits(MAX_ROWS, lang)} دکھائے گئے", ", showing the first $MAX_ROWS") else ""
        Muted(lang.t("${digits(list.size, lang)} نتائج", "${list.size} results") + more)
    }
    items(list.take(MAX_ROWS), key = { it.id }) { SymbolRow(it) { onOpen(Page.Symbol(it.id)) } }
}

@Composable
private fun SearchBox(value: String, onChange: (String) -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    TextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = lang.text(15, lineHeight = 1.6),
        placeholder = { Text(lang.t("خواب میں کیا دیکھا؟ سانپ، saanp، حية…", "What did you see? snake, saanp, حية…"), style = lang.text(14, lineHeight = 1.6), color = c.muted, maxLines = 1) },
        leadingIcon = { Icon(Icons.Filled.Search, null, tint = c.muted) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = c.card, unfocusedContainerColor = c.card,
            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun SendCta(onClick: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    SoftCard(onClick = onClick) {
        Row(
            Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(c.goldSoft, c.card))).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(c.whatsapp), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(lang.t("اپنا خواب بھیجیں", "Send your dream"), style = lang.text(17, FontWeight.Bold, lineHeight = 1.8))
                Text(lang.t("تفصیل لکھیں، واٹس ایپ پر ذاتی تعبیر حاصل کریں", "Describe it and get a personal interpretation on WhatsApp"), style = lang.text(13, lineHeight = 1.8), color = c.muted)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryGrid(categories: List<Category>, onOpen: (Page) -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 3) {
        categories.forEach { cat ->
            SoftCard(Modifier.weight(1f), onClick = { onOpen(Page.CategoryList(cat.id)) }) {
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(cat.icon, style = latin(22))
                    Text(cat.name(lang), style = lang.text(12, lineHeight = 1.8).copy(textAlign = TextAlign.Center), maxLines = 1, overflow = TextOverflow.Ellipsis, color = c.ink)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(library: Library, onOpen: (Page) -> Unit, onTab: (Tab) -> Unit, onToggleLang: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query, lang) { library.search(query, lang) }
    Column {
        Header(lang.t("خوابوں کی تعبیر", "Dream Interpretation"), lang.t("قرآن، سنت اور کلاسیکی کتب کی روشنی میں", "In the light of Quran, Sunnah and classical books"),
            actions = { LangButton(onToggleLang) })
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { SearchBox(query) { query = it } }
            if (query.isNotBlank()) {
                if (results.isEmpty()) item { Muted(lang.t("کوئی نتیجہ نہیں ملا۔ عربی لفظ سے تلاش کریں، یا اپنا خواب واٹس ایپ پر بھیجیں۔", "No results. Try the Arabic word, or send your dream on WhatsApp.")) }
                else rowsWithCount(results, onOpen)
                return@LazyColumn
            }
            item { SendCta { onTab(Tab.SEND) } }
            item {
                SectionLabel(lang.t("عام خواب", "Common dreams"))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("saanp", "daant", "pani", "sooraj", "barish", "namaz", "doodh", "billi").mapNotNull(library::get).forEach { s ->
                        OutlinedButton(onClick = { onOpen(Page.Symbol(s.id)) }, border = BorderStroke(1.dp, c.line), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)) {
                            Text(s.name(lang) ?: s.headwordAr, style = lang.text(14, lineHeight = 1.6), color = c.ink)
                        }
                    }
                }
            }
            item {
                SoftCard {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text(lang.t("آج کی حدیث", "Hadith of the day"), style = lang.text(13, lineHeight = 1.8), color = c.muted)
                        Text(
                            lang.t(
                                "اچھا خواب اللہ کی طرف سے ہے اور برا خواب شیطان کی طرف سے۔ جب کوئی پسندیدہ خواب دیکھے تو صرف اسی کو بتائے جس سے محبت رکھتا ہو۔",
                                "A good dream is from Allah and a bad dream is from Satan. When you see a dream you like, tell only someone you love.",
                            ),
                            style = lang.text(16, lineHeight = 2.4, justify = true),
                        )
                        Text("Sahih Muslim 2261", style = latin(13), color = c.gold)
                    }
                }
            }
            item {
                SectionLabel(lang.t("اقسام", "Categories"))
                CategoryGrid(library.categories.take(9), onOpen)
            }
            item {
                OutlinedButton(onClick = { onTab(Tab.CATEGORIES) }, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, c.line)) {
                    Text(lang.t("تمام اقسام اور مکمل کتاب دیکھیں", "All categories and the full book"), style = lang.text(14, lineHeight = 1.6), color = c.ink)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesScreen(library: Library, onOpen: (Page) -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    var letter by rememberSaveable { mutableStateOf<String?>(null) }
    val list = remember(letter, lang) { letter?.let { library.byLetter(it, lang) }.orEmpty() }
    val letters = if (lang == Lang.UR) URDU_LETTERS else ENGLISH_LETTERS
    Column {
        Header(
            lang.t("اقسام", "Browse"),
            lang.t(
                "مکمل کتاب میں ${digits(library.symbols.size, lang)} علامات، جن میں سے ${digits(library.translatedCount(lang), lang)} کا اردو ترجمہ ہو چکا",
                "${library.symbols.size} symbols in the full book; ${library.translatedCount(lang)} translated into English so far",
            ),
        )
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { SectionLabel(lang.t("حروفِ تہجی سے تلاش", "A to Z")) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    letters.forEach { l ->
                        val on = l == letter
                        Box(
                            Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(if (on) c.night else c.card)
                                .clickable { letter = if (on) null else l },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(l, style = if (lang == Lang.UR) arabic(19, 1.2).copy(textAlign = TextAlign.Center) else latin(16, FontWeight.Bold), color = if (on) c.onNight else c.ink)
                        }
                    }
                }
            }
            if (letter != null) {
                if (list.isEmpty()) item { Muted(lang.t("«$letter» سے کوئی علامت نہیں۔", "No symbols under \"$letter\" yet.")) } else rowsWithCount(list, onOpen)
            }
            item { SectionLabel(lang.t("موضوع کے لحاظ سے", "By topic")) }
            item { CategoryGrid(library.categories, onOpen) }
        }
    }
}

@Composable
fun SymbolScreen(id: String, library: Library, repo: Repository, isFavorite: Boolean, onBack: () -> Unit, onToggleFavorite: () -> Unit, onSend: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    val context = LocalContext.current
    val lite = library.get(id)
    var detail by remember(id) { mutableStateOf<SymbolDetail?>(null) }
    var failed by remember(id) { mutableStateOf(false) }
    var attempt by remember(id) { mutableStateOf(0) }
    LaunchedEffect(id, attempt) {
        failed = false
        try { detail = repo.detail(id) } catch (e: Exception) { failed = true }
    }
    val title = lite?.name(lang) ?: lite?.headwordAr ?: ""
    Column {
        Header(title, onBack = onBack) {
            IconButton(onClick = onToggleFavorite) { Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, lang.t("پسندیدہ", "Save"), tint = c.onNight) }
            IconButton(onClick = {
                val d = detail ?: return@IconButton
                shareText(context, listOfNotNull(title, d.tabeer(lang), d.arabic, d.reference, lang.t("— خوابوں کی تعبیر ایپ", "— Dream Interpretation app")).filter { it.isNotBlank() }.joinToString("\n\n"))
            }) { Icon(Icons.Filled.Share, lang.t("شیئر", "Share"), tint = c.onNight) }
        }
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (lite != null) item {
                SoftCard {
                    Column(Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        lite.name(lang)?.let { Text(it, style = lang.text(28, FontWeight.Bold, lineHeight = 1.8)) }
                        Text(lite.headwordAr, style = arabic(if (lite.name(lang) == null) 32 else 24, 1.6), color = c.gold)
                        val other = if (lang == Lang.UR) listOfNotNull(lite.roman, lite.nameEn).joinToString(" · ") else lite.nameUr
                        if (!other.isNullOrBlank()) Text(other, style = if (lang == Lang.UR) latin(13) else urdu(14, lineHeight = 1.7), color = c.muted)
                    }
                }
            }
            when {
                detail != null -> item { InterpretationCard(detail!!) }
                failed -> item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Muted(lang.t("انٹرنیٹ سے رابطہ نہیں ہو سکا۔", "Couldn't load this entry. Check your internet."))
                        Button(onClick = { attempt++ }) { Text(lang.t("دوبارہ کوشش کریں", "Try again"), style = lang.text(15, lineHeight = 1.6)) }
                    }
                }
                else -> item { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = c.gold) } }
            }
            item {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.warnBg).padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Text(lang.t("تعبیر ایک رائے ہے، غیب کا علم نہیں۔ اپنے خواب کی ذاتی تعبیر کے لیے نیچے بٹن دبائیں۔", "An interpretation is an opinion, not knowledge of the unseen. For a personal interpretation, tap below."),
                        style = lang.text(14, lineHeight = 2.1), color = c.warnFg)
                }
            }
            item { SendCta(onSend) }
        }
    }
}

@Composable
private fun InterpretationCard(d: SymbolDetail) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    val text = d.tabeer(lang)
    SoftCard {
        Row {
            Box(Modifier.width(4.dp).heightIn(min = 40.dp).background(c.gold))
            Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    if (d.lite.isHadith) lang.t("صحیح بخاری (حدیث)", "Sahih al-Bukhari (hadith)")
                    else lang.t("تعطیر الانام فی تعبیر المنام (عبدالغنی نابلسی)", "Ta'tir al-Anam fi Ta'bir al-Manam (Abd al-Ghani al-Nabulsi)"),
                    style = lang.text(15, FontWeight.SemiBold, lineHeight = 1.8), color = c.gold,
                )
                if (!d.reviewed) {
                    val badge = if (text != null) lang.t("ترجمہ: عالم کی نظرِ ثانی باقی", "Translation awaiting scholar review")
                    else lang.t("ترجمہ جلد آ رہا ہے، نیچے مکمل عربی متن", "Translation coming soon; full Arabic text below")
                    Text(badge, Modifier.clip(RoundedCornerShape(8.dp)).background(c.warnBg).padding(horizontal = 8.dp), style = lang.text(12, lineHeight = 1.8), color = c.warnFg)
                }
                if (text != null) SelectionContainer { Text(text, style = lang.text(16, lineHeight = 2.5, justify = true)) }
                if (d.arabic.isNotBlank()) {
                    Spacer(Modifier.height(1.dp).fillMaxWidth().background(c.line))
                    Text(lang.t("اصل عربی متن", "Original Arabic"), style = lang.text(13, lineHeight = 1.8), color = c.muted)
                    SelectionContainer { Text(d.arabic, style = arabic(18, 2.1)) }
                }
                Text(d.reference, style = latin(13), color = c.gold)
            }
        }
    }
}

@Composable
fun FavoritesScreen(library: Library, favorites: Set<String>, onOpen: (Page) -> Unit) {
    val lang = LocalLang.current
    val list = library.symbols.filter { it.id in favorites }
    Column {
        Header(lang.t("پسندیدہ", "Saved"))
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (list.isEmpty()) item { Muted(lang.t("کسی علامت پر دل کا نشان دبائیں، وہ یہاں محفوظ ہو جائے گی۔", "Tap the heart on any symbol to save it here."), Modifier.padding(vertical = 30.dp)) }
            items(list, key = { it.id }) { SymbolRow(it) { onOpen(Page.Symbol(it.id)) } }
        }
    }
}

/** Form options as (Urdu, English) pairs; the WhatsApp message uses the current language. */
private val GENDERS = listOf("مرد" to "Male", "عورت" to "Female")
private val AGES = listOf("18 سے کم" to "Under 18", "18–30" to "18–30", "31–50" to "31–50", "50+" to "50+")
private val STATUSES = listOf("شادی شدہ" to "Married", "غیر شادی شدہ" to "Unmarried")
private val TIMES = listOf("رات کا پہلا حصہ" to "Early night", "فجر کے قریب" to "Near Fajr", "دن میں" to "Daytime")
private val FEELINGS = listOf("اچھا" to "Good", "عام" to "Ordinary", "ڈراؤنا" to "Frightening")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SendScreen(contact: Contact) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf(-1) }
    var age by rememberSaveable { mutableStateOf(-1) }
    var status by rememberSaveable { mutableStateOf(-1) }
    var time by rememberSaveable { mutableStateOf(-1) }
    var feel by rememberSaveable { mutableStateOf(-1) }
    var text by rememberSaveable { mutableStateOf("") }
    var confirmed by rememberSaveable { mutableStateOf(false) }
    var badAck by rememberSaveable { mutableStateOf(false) }

    fun pick(list: List<Pair<String, String>>, i: Int) = if (i < 0) "—" else lang.t(list[i].first, list[i].second)
    fun message() = lang.t(
        "السلام علیکم، خواب کی تعبیر درکار ہے\nنام: ${name.ifBlank { "—" }}\nجنس: ${pick(GENDERS, gender)} | عمر: ${pick(AGES, age)} | حالت: ${pick(STATUSES, status)}\nوقت: ${pick(TIMES, time)} | کیفیت: ${pick(FEELINGS, feel)}\nخواب:\n$text\n(خوابوں کی تعبیر ایپ سے بھیجا گیا)",
        "Assalamu alaikum, I would like a dream interpreted\nName: ${name.ifBlank { "—" }}\nGender: ${pick(GENDERS, gender)} | Age: ${pick(AGES, age)} | Status: ${pick(STATUSES, status)}\nTime: ${pick(TIMES, time)} | Feeling: ${pick(FEELINGS, feel)}\nDream:\n$text\n(Sent from the Dream Interpretation app)",
    )
    val valid = text.trim().length >= 20 && confirmed && gender >= 0 && feel >= 0

    @Composable
    fun Choice(label: String, options: List<Pair<String, String>>, value: Int, onPick: (Int) -> Unit) {
        Column {
            Text(label, style = lang.text(14, lineHeight = 1.8))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEachIndexed { i, o ->
                    FilterChip(
                        selected = value == i,
                        onClick = { onPick(if (value == i) -1 else i) },
                        label = { Text(lang.t(o.first, o.second), style = lang.text(13, lineHeight = 1.7)) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.night, selectedLabelColor = c.onNight),
                    )
                }
            }
        }
    }

    Column {
        Header(lang.t("خواب بھیجیں", "Send your dream"), "WhatsApp: ${contact.whatsappShown}")
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (feel == 2 && !badAck) {
                item {
                    Column(Modifier.clip(RoundedCornerShape(14.dp)).background(c.warnBg).padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(lang.t("برا یا ڈراؤنا خواب؟", "A bad or frightening dream?"), style = lang.text(16, FontWeight.Bold, lineHeight = 2.0), color = c.warnFg)
                        Text(lang.t("نبی کریم ﷺ نے فرمایا کہ برا خواب شیطان کی طرف سے ہے، اسے کسی کو نہ بتائیں، تو وہ نقصان نہیں دے گا۔",
                            "The Prophet ﷺ said a bad dream is from Satan; do not tell anyone about it and it will not harm you."),
                            style = lang.text(15, lineHeight = 2.3, justify = true), color = c.warnFg)
                        listOf(
                            lang.t("۱۔ بائیں طرف تین بار ہلکا سا تھتکاریں", "1. Blow lightly to your left three times"),
                            lang.t("۲۔ تین بار «أعوذ بالله من الشيطان الرجيم» پڑھیں", "2. Say «A'udhu billahi minash-shaytanir-rajim» three times"),
                            lang.t("۳۔ کروٹ بدل لیں", "3. Turn onto your other side"),
                            lang.t("۴۔ چاہیں تو اٹھ کر نماز پڑھیں", "4. If you wish, get up and pray"),
                        ).forEach { Text(it, style = lang.text(15, lineHeight = 2.1), color = c.warnFg) }
                        Text("Sahih Muslim 2261–2263", style = latin(13), color = c.gold)
                    }
                }
                item {
                    Button(onClick = { feel = -1 }, modifier = Modifier.fillMaxWidth()) { Text(lang.t("ٹھیک ہے، نہیں بھیجتا", "OK, I won't send it"), style = lang.text(15, lineHeight = 1.6)) }
                    OutlinedButton(onClick = { badAck = true }, modifier = Modifier.fillMaxWidth()) { Text(lang.t("پھر بھی بھیجیں", "Send anyway"), style = lang.text(15, lineHeight = 1.6), color = c.ink) }
                }
                return@LazyColumn
            }
            item { Muted(lang.t("تفصیل لکھیں، بٹن دبائیں، واٹس ایپ کھل جائے گا۔ ایپ آپ کا کوئی ڈیٹا محفوظ نہیں کرتی۔", "Fill this in and tap the button; WhatsApp opens. The app stores none of your data.")) }
            item {
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text(lang.t("نام (اختیاری)", "Name (optional)"), style = lang.text(13, lineHeight = 1.6)) },
                    textStyle = lang.text(15, lineHeight = 1.7), singleLine = true, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = c.card, unfocusedContainerColor = c.card))
            }
            item { Choice(lang.t("جنس", "Gender"), GENDERS, gender) { gender = it } }
            item { Choice(lang.t("عمر", "Age"), AGES, age) { age = it } }
            item { Choice(lang.t("حالت", "Marital status"), STATUSES, status) { status = it } }
            item { Choice(lang.t("خواب کب دیکھا", "When did you see it"), TIMES, time) { time = it } }
            item { Choice(lang.t("خواب کیسا لگا", "How did it feel"), FEELINGS, feel) { feel = it; badAck = false } }
            item {
                OutlinedTextField(text, { if (it.length <= 1500) text = it }, Modifier.fillMaxWidth().heightIn(min = 140.dp),
                    label = { Text(lang.t("خواب کی تفصیل", "Describe your dream"), style = lang.text(13, lineHeight = 1.6)) },
                    textStyle = lang.text(15, lineHeight = 2.1), shape = RoundedCornerShape(12.dp),
                    supportingText = { Text(lang.t("${text.trim().length} / 1500 (کم از کم 20)", "${text.trim().length} / 1500 (at least 20)"), style = latin(12)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = c.card, unfocusedContainerColor = c.card))
            }
            item {
                Row(verticalAlignment = Alignment.Top) {
                    Checkbox(confirmed, { confirmed = it })
                    Text(lang.t("میں تصدیق کرتا/کرتی ہوں کہ یہ خواب میں نے واقعی دیکھا ہے۔ (جھوٹا خواب بیان کرنے پر وعید: صحیح بخاری ۷۰۴۲)",
                        "I confirm I really saw this dream. (Warning against lying about dreams: Sahih al-Bukhari 7042)"),
                        Modifier.padding(top = 6.dp), style = lang.text(13, lineHeight = 2.0))
                }
            }
            item {
                Button(
                    onClick = { openWhatsApp(context, contact, message()) },
                    enabled = valid,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = c.whatsapp, contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                    Spacer(Modifier.width(8.dp))
                    Text(lang.t("واٹس ایپ پر بھیجیں", "Send on WhatsApp"), style = lang.text(16, lineHeight = 1.6))
                }
            }
            item {
                Text(lang.t("تعبیر کا ہدیہ واٹس ایپ پر طے کیا جائے گا۔ عموماً ۲۴ سے ۴۸ گھنٹے میں جواب", "The hadya (fee) is agreed on WhatsApp. Replies usually within 24–48 hours."),
                    Modifier.fillMaxWidth(), style = lang.text(13, lineHeight = 2.0).copy(textAlign = TextAlign.Center), color = c.muted)
            }
        }
    }
}

@Composable
fun KnowledgeScreen(library: Library, onToggleLang: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    val context = LocalContext.current
    var open by rememberSaveable { mutableStateOf(0) }
    Column {
        Header(lang.t("خواب کا علم", "About dreams"), lang.t("قرآن و حدیث کی روشنی میں", "In the light of Quran and hadith"), actions = { LangButton(onToggleLang) })
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { AboutCard(library.contact, onWhatsApp = { openWhatsApp(context, library.contact, null) }, onSite = { openUrl(context, library.contact.website) }) }
            items(library.topics.size) { i ->
                val t = library.topics[i]
                SoftCard(onClick = { open = if (open == i) -1 else i }) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(28.dp).clip(CircleShape).background(c.night), contentAlignment = Alignment.Center) {
                                Text(digits(i + 1, lang), style = if (lang == Lang.UR) urdu(12, lineHeight = 1.3) else latin(13, FontWeight.Bold), color = c.onNight)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(t.title(lang), Modifier.weight(1f), style = lang.text(16, FontWeight.SemiBold, lineHeight = 2.0))
                            Text(if (open == i) "⌃" else "⌄", style = latin(16), color = c.muted)
                        }
                        if (open == i) {
                            Column(Modifier.padding(bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                t.ayah?.let { Text(it, Modifier.fillMaxWidth(), style = arabic(21, 2.0).copy(textAlign = TextAlign.Center), color = c.gold) }
                                t.paragraphs(lang).forEach { Text(it, style = lang.text(16, lineHeight = 2.4, justify = true)) }
                                Text(t.refs.joinToString("  ·  "), style = latin(13), color = c.gold)
                            }
                        }
                    }
                }
            }
            item {
                SoftCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(lang.t("ماخذ کتب", "Sources"), style = lang.text(17, FontWeight.Bold, lineHeight = 2.0), color = c.gold)
                        Text(lang.t("تعطیر الانام فی تعبیر المنام — عبدالغنی النابلسی (م ۱۱۴۳ھ)، دار الفکر ایڈیشن", "Ta'tir al-Anam fi Ta'bir al-Manam — Abd al-Ghani al-Nabulsi (d. 1143 AH), Dar al-Fikr edition"), style = lang.text(15, lineHeight = 2.2))
                        Text(lang.t("قرآن مجید، صحیح بخاری، صحیح مسلم، جامع ترمذی، سنن ابی داؤد", "The Quran, Sahih al-Bukhari, Sahih Muslim, Jami at-Tirmidhi, Sunan Abi Dawud"), style = lang.text(15, lineHeight = 2.2))
                        Spacer(Modifier.height(4.dp))
                        Text(lang.t("ضروری وضاحت", "Please note"), style = lang.text(17, FontWeight.Bold, lineHeight = 2.0), color = c.gold)
                        Text(
                            lang.t(
                                "خواب کی تعبیر ایک رائے ہے، غیب کا علم نہیں۔ کسی بھی خواب کی بنیاد پر اہم فیصلے نہ کریں۔ ترجمے پر عالم کی نظرِ ثانی جاری ہے۔ ایپ آپ کا کوئی ڈیٹا جمع نہیں کرتی؛ پسندیدہ علامات صرف آپ کے فون میں محفوظ رہتی ہیں۔",
                                "An interpretation is an opinion, not knowledge of the unseen. Don't make major decisions based on a dream. Translations are being reviewed by a scholar. The app collects no data; saved symbols stay on your phone.",
                            ),
                            style = lang.text(15, lineHeight = 2.3, justify = true),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutCard(contact: Contact, onWhatsApp: () -> Unit, onSite: () -> Unit) {
    val c = LocalKhwab.current
    val lang = LocalLang.current
    SoftCard {
        Column {
            Box(Modifier.fillMaxWidth().height(4.dp).background(c.gold))
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(lang.t("ہمارے بارے میں", "About us"), style = lang.text(13, lineHeight = 1.8), color = c.muted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(54.dp).clip(CircleShape).background(Brush.linearGradient(listOf(c.night, c.night2))), contentAlignment = Alignment.Center) {
                        Text("ط", style = urdu(26, lineHeight = 1.3), color = c.onNight)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(lang.t(contact.scholarUr, contact.scholarEn), style = lang.text(19, FontWeight.Bold, lineHeight = 1.9))
                        Text(lang.t("اسلامی اسکالر، ${contact.scholarEn}", "Islamic scholar, ${contact.scholarUr}"), style = lang.text(13, lineHeight = 1.8), color = c.muted)
                    }
                }
                Text(lang.t("اپنے خواب کی ذاتی تعبیر کے لیے خواب واٹس ایپ پر بھیجیں۔", "For a personal interpretation of your dream, send it on WhatsApp."), style = lang.text(15, lineHeight = 2.2))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onWhatsApp, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = c.whatsapp)) {
                        Text(contact.whatsappShown, style = latin(14, FontWeight.Bold))
                    }
                    OutlinedButton(onClick = onSite, Modifier.weight(1f), border = BorderStroke(1.dp, c.line)) {
                        Text(contact.website.removePrefix("https://"), style = latin(14), color = c.ink)
                    }
                }
            }
        }
    }
}
