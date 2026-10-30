package net.darsenizami.khwab

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = Repository(applicationContext)
        val prefs = Prefs(applicationContext)
        setContent {
            var lang by remember { mutableStateOf(prefs.lang()) }
            KhwabTheme {
                // Urdu runs right-to-left, English left-to-right.
                CompositionLocalProvider(
                    LocalLang provides lang,
                    LocalLayoutDirection provides if (lang == Lang.UR) LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    App(repo, prefs, onToggleLang = {
                        lang = if (lang == Lang.UR) Lang.EN else Lang.UR
                        prefs.saveLang(lang)
                    })
                }
            }
        }
    }
}

enum class Tab { HOME, CATEGORIES, SEND, FAVORITES, KNOWLEDGE }

/** Where the user is inside a tab: a category list or a symbol page. */
sealed interface Page {
    data class CategoryList(val id: Int) : Page
    data class Symbol(val id: String) : Page
}

@Composable
fun App(repo: Repository, prefs: Prefs, onToggleLang: () -> Unit) {
    var library by remember { mutableStateOf<Library?>(null) }
    var failed by remember { mutableStateOf(false) }
    var attempt by remember { mutableIntStateOf(0) }

    LaunchedEffect(attempt) {
        failed = false
        try {
            library = repo.load(forceRefresh = attempt > 0 && library == null)
        } catch (e: Exception) {
            failed = library == null
        }
    }

    val lib = library
    if (lib == null) {
        LoadingScreen(failed, onRetry = { attempt++ }, onToggleLang = onToggleLang)
        return
    }

    var favorites by remember { mutableStateOf(prefs.favorites()) }
    var tab by rememberSaveable { mutableStateOf(Tab.HOME) }
    val stack = remember { mutableStateListOf<Page>() }

    BackHandler(enabled = stack.isNotEmpty() || tab != Tab.HOME) {
        if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex) else tab = Tab.HOME
    }

    AppScaffold(
        library = lib,
        repo = repo,
        tab = tab,
        page = stack.lastOrNull(),
        favorites = favorites,
        onTab = { tab = it; stack.clear() },
        onOpen = { stack.add(it) },
        onBack = { if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex) },
        onToggleFavorite = { id ->
            favorites = if (id in favorites) favorites - id else favorites + id
            prefs.saveFavorites(favorites)
        },
        onToggleLang = onToggleLang,
    )
}

fun openWhatsApp(context: Context, contact: Contact, message: String?) {
    val url = "https://wa.me/${contact.whatsapp}" + (message?.let { "?text=" + Uri.encode(it) } ?: "")
    openUrl(context, url)
}

fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "WhatsApp / browser not found", Toast.LENGTH_SHORT).show()
    }
}

fun shareText(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, null))
}
