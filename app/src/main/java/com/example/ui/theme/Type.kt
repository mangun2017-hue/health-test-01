package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val NotoSansKrFontFamily = FontFamily(
    Font(R.font.noto_sans_kr, FontWeight.Normal),
    Font(R.font.noto_sans_kr, FontWeight.Medium),
    Font(R.font.noto_sans_kr, FontWeight.SemiBold),
    Font(R.font.noto_sans_kr, FontWeight.Bold),
    Font(R.font.noto_sans_kr, FontWeight.ExtraBold),
)

// Every text style is strictly 22.sp or larger for senior readability.
val SeniorTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 30.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
)
