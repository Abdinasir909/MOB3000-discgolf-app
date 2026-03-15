package no.usn.mob3000_gruppe15.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import no.usn.mob3000_gruppe15.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.Font



val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Inter")

val interFamily = FontFamily(
    Font(
        googleFont = fontName,
        fontProvider = provider,
        weight = FontWeight.Normal
    )
)

val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = interFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    )
)

val Typography.searchField: TextStyle       // Original: val SearchField = TextStyle( ... )
    get() = TextStyle(                      //
        fontFamily = interFamily,           // Jeg ville at den skulle vises under:
        fontWeight = FontWeight.Normal,     // MaterialTheme.typography.searchField
        fontSize = 18.sp,                   // Spurte ChatGPT, den sa at jeg kunne utvide
        lineHeight = 16.sp,                 // typography ved å endre til slik den er nå.
        color = Color(0xFF858585)    //                              - Severin
    )

