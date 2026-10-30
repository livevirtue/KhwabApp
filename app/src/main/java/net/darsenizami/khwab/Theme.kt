package net.darsenizami.khwab

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val Nastaliq = FontFamily(Font(R.font.nastaliq))
val Naskh = FontFamily(Font(R.font.amiri))
val Latin = FontFamily(Font(R.font.carlito), Font(R.font.carlito_bold, FontWeight.Bold))

/** Night-blue and gold palette shared by both themes. */
@Immutable
data class KhwabColors(
    val page: Color, val card: Color, val line: Color, val ink: Color, val muted: Color,
    val night: Color, val night2: Color, val onNight: Color, val gold: Color, val goldSoft: Color,
    val warnBg: Color, val warnFg: Color, val whatsapp: Color,
)

private val Light = KhwabColors(
    page = Color(0xFFF4F6FB), card = Color.White, line = Color(0xFFD8DEEA), ink = Color(0xFF18213A), muted = Color(0xFF5D6784),
    night = Color(0xFF16234A), night2 = Color(0xFF22356B), onNight = Color(0xFFF1E6C6), gold = Color(0xFFA27A12), goldSoft = Color(0xFFF3E7C4),
    warnBg = Color(0xFFFBE9D6), warnFg = Color(0xFF8A4513), whatsapp = Color(0xFF1FA855),
)
private val Dark = KhwabColors(
    page = Color(0xFF0E1428), card = Color(0xFF161E38), line = Color(0xFF26305A), ink = Color(0xFFE7EAF5), muted = Color(0xFF9AA3C4),
    night = Color(0xFF0A1230), night2 = Color(0xFF1A2A5C), onNight = Color(0xFFF1E6C6), gold = Color(0xFFE3C063), goldSoft = Color(0xFF3A3016),
    warnBg = Color(0xFF3D2614), warnFg = Color(0xFFF3C191), whatsapp = Color(0xFF1FA855),
)

val LocalKhwab = staticCompositionLocalOf { Light }

@Composable
fun KhwabTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val c = if (dark) Dark else Light
    val scheme = if (dark) {
        darkColorScheme(primary = c.gold, onPrimary = c.night, background = c.page, surface = c.card, onSurface = c.ink, outline = c.line)
    } else {
        lightColorScheme(primary = c.night, onPrimary = c.onNight, secondary = c.gold, background = c.page, surface = c.card, onSurface = c.ink, outline = c.line)
    }
    androidx.compose.runtime.CompositionLocalProvider(LocalKhwab provides c) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

// Nastaliq has very tall font metrics: drop the extra font padding and set line height explicitly instead.
private val tall = PlatformTextStyle(includeFontPadding = false)
private val centered = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)

fun urdu(size: Int, weight: FontWeight = FontWeight.Normal, lineHeight: Double = 1.9, justify: Boolean = false) = TextStyle(
    fontFamily = Nastaliq, fontSize = size.sp, fontWeight = weight, lineHeight = lineHeight.em,
    textDirection = TextDirection.Rtl, textAlign = if (justify) TextAlign.Justify else TextAlign.Start,
    platformStyle = tall, lineHeightStyle = centered,
)

fun arabic(size: Int, lineHeight: Double = 2.0) = TextStyle(
    fontFamily = Naskh, fontSize = size.sp, lineHeight = lineHeight.em,
    textDirection = TextDirection.Rtl, textAlign = TextAlign.Justify, platformStyle = tall, lineHeightStyle = centered,
)

fun latin(size: Int, weight: FontWeight = FontWeight.Normal, lineHeight: Double = 1.35, justify: Boolean = false) = TextStyle(
    fontFamily = Latin, fontSize = size.sp, fontWeight = weight, lineHeight = lineHeight.em, textDirection = TextDirection.Ltr,
    textAlign = if (justify) TextAlign.Justify else TextAlign.Start,
)

val LocalLang = staticCompositionLocalOf { Lang.UR }

/** UI text in the current language: Nastaleeq for Urdu, Carlito for English (Urdu needs ~1.4× the line height). */
fun Lang.text(size: Int, weight: FontWeight = FontWeight.Normal, lineHeight: Double = 1.9, justify: Boolean = false) =
    if (this == Lang.UR) urdu(size, weight, lineHeight, justify) else latin(size + 1, weight, (lineHeight / 1.4).coerceAtLeast(1.25), justify)
