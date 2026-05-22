package com.taoufikcode.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val ColorScheme.extended: ExtendedColors
    @ReadOnlyComposable
    @Composable
    get() = LocalExtendedColors.current

@Immutable
data class ExtendedColors(
    // Button states
    val primaryHover: Color,
    val destructiveHover: Color,
    val destructiveSecondaryOutline: Color,
    val disabledOutline: Color,
    val disabledFill: Color,
    val successOutline: Color,
    val success: Color,
    val onSuccess: Color,
    val secondaryFill: Color,

    // Text variants
    val textPrimary: Color,
    val textTertiary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,

    // Surface variants
    val surfaceLower: Color,
    val surfaceHigher: Color,
    val surfaceOutline: Color,
    val overlay: Color,

    // Accent colors
    val accentBlue: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentPink: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentLightBlue: Color,
    val accentGrey: Color,

    // Cake colors for chat bubbles
    val cakeViolet: Color,
    val cakeGreen: Color,
    val cakeBlue: Color,
    val cakePink: Color,
    val cakeOrange: Color,
    val cakeYellow: Color,
    val cakeTeal: Color,
    val cakePurple: Color,
    val cakeRed: Color,
    val cakeMint: Color,
)

val LightExtendedColors = ExtendedColors(
    primaryHover = KrossBrand600,
    destructiveHover = KrossRed600,
    destructiveSecondaryOutline = KrossRed200,
    disabledOutline = KrossBase200,
    disabledFill = KrossBase150,
    successOutline = KrossBrand100,
    success = KrossBrand600,
    onSuccess = KrossBase0,
    secondaryFill = KrossBase100,

    textPrimary = KrossBase1000,
    textTertiary = KrossBase800,
    textSecondary = KrossBase900,
    textPlaceholder = KrossBase700,
    textDisabled = KrossBase400,

    surfaceLower = KrossBase100,
    surfaceHigher = KrossBase100,
    surfaceOutline = KrossBase1000Alpha14,
    overlay = KrossBase1000Alpha80,

    accentBlue = KrossBlue,
    accentPurple = KrossPurple,
    accentViolet = KrossViolet,
    accentPink = KrossPink,
    accentOrange = KrossOrange,
    accentYellow = KrossYellow,
    accentGreen = KrossGreen,
    accentTeal = KrossTeal,
    accentLightBlue = KrossLightBlue,
    accentGrey = KrossGrey,

    cakeViolet = KrossCakeLightViolet,
    cakeGreen = KrossCakeLightGreen,
    cakeBlue = KrossCakeLightBlue,
    cakePink = KrossCakeLightPink,
    cakeOrange = KrossCakeLightOrange,
    cakeYellow = KrossCakeLightYellow,
    cakeTeal = KrossCakeLightTeal,
    cakePurple = KrossCakeLightPurple,
    cakeRed = KrossCakeLightRed,
    cakeMint = KrossCakeLightMint,
)

val DarkExtendedColors = ExtendedColors(
    primaryHover = KrossBrand600,
    destructiveHover = KrossRed600,
    destructiveSecondaryOutline = KrossRed200,
    disabledOutline = KrossBase900,
    disabledFill = KrossBase1000,
    successOutline = KrossBrand500Alpha40,
    success = KrossBrand500,
    onSuccess = KrossBase1000,
    secondaryFill = KrossBase900,

    textPrimary = KrossBase0,
    textTertiary = KrossBase200,
    textSecondary = KrossBase150,
    textPlaceholder = KrossBase400,
    textDisabled = KrossBase500,

    surfaceLower = KrossBase1000,
    surfaceHigher = KrossBase900,
    surfaceOutline = KrossBase100Alpha10Alt,
    overlay = KrossBase1000Alpha80,

    accentBlue = KrossBlue,
    accentPurple = KrossPurple,
    accentViolet = KrossViolet,
    accentPink = KrossPink,
    accentOrange = KrossOrange,
    accentYellow = KrossYellow,
    accentGreen = KrossGreen,
    accentTeal = KrossTeal,
    accentLightBlue = KrossLightBlue,
    accentGrey = KrossGrey,

    cakeViolet = KrossCakeDarkViolet,
    cakeGreen = KrossCakeDarkGreen,
    cakeBlue = KrossCakeDarkBlue,
    cakePink = KrossCakeDarkPink,
    cakeOrange = KrossCakeDarkOrange,
    cakeYellow = KrossCakeDarkYellow,
    cakeTeal = KrossCakeDarkTeal,
    cakePurple = KrossCakeDarkPurple,
    cakeRed = KrossCakeDarkRed,
    cakeMint = KrossCakeDarkMint,
)

val LightColorScheme = lightColorScheme(
    primary = KrossBrand500,
    onPrimary = KrossBrand1000,
    primaryContainer = KrossBrand100,
    onPrimaryContainer = KrossBrand900,

    secondary = KrossBase700,
    onSecondary = KrossBase0,
    secondaryContainer = KrossBase100,
    onSecondaryContainer = KrossBase900,

    tertiary = KrossBrand900,
    onTertiary = KrossBase0,
    tertiaryContainer = KrossBrand100,
    onTertiaryContainer = KrossBrand1000,

    error = KrossRed500,
    onError = KrossBase0,
    errorContainer = KrossRed200,
    onErrorContainer = KrossRed600,

    background = KrossBrand1000,
    onBackground = KrossBase0,
    surface = KrossBase0,
    onSurface = KrossBase1000,
    surfaceVariant = KrossBase100,
    onSurfaceVariant = KrossBase900,

    outline = KrossBase1000Alpha8,
    outlineVariant = KrossBase200,
)

val DarkColorScheme = darkColorScheme(
    primary = KrossBrand500,
    onPrimary = KrossBrand1000,
    primaryContainer = KrossBrand900,
    onPrimaryContainer = KrossBrand500,

    secondary = KrossBase400,
    onSecondary = KrossBase1000,
    secondaryContainer = KrossBase900,
    onSecondaryContainer = KrossBase150,

    tertiary = KrossBrand500,
    onTertiary = KrossBase1000,
    tertiaryContainer = KrossBrand900,
    onTertiaryContainer = KrossBrand500,

    error = KrossRed500,
    onError = KrossBase0,
    errorContainer = KrossRed600,
    onErrorContainer = KrossRed200,

    background = KrossBase1000,
    onBackground = KrossBase0,
    surface = KrossBase950,
    onSurface = KrossBase0,
    surfaceVariant = KrossBase900,
    onSurfaceVariant = KrossBase150,

    outline = KrossBase100Alpha10,
    outlineVariant = KrossBase800,
)