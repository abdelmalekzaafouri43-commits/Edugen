package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(val title: String) {
    SAPPHIRE_DARK("Sapphire Dark"),
    HIGH_CONTRAST_LIGHT("High Contrast Light"),
    EMERALD_DARK("Emerald Dark"),
    VIOLET_DARK("Violet Dark")
}

private val SapphireColorScheme = darkColorScheme(
    primary = SapphireBlue,
    onPrimary = TextPrimary,
    secondary = VibrantCyan,
    onSecondary = DarkBackground,
    tertiary = ElectricPurple,
    onTertiary = TextPrimary,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = TextSecondary,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    secondary = SecondaryLight,
    onSecondary = Color.White,
    tertiary = PrimaryLight,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = TextSecondaryLight,
)

private val EmeraldColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    onPrimary = TextPrimary,
    secondary = MintGreen,
    onSecondary = EmeraldBackground,
    tertiary = MintGreen,
    onTertiary = EmeraldBackground,
    background = EmeraldBackground,
    onBackground = TextPrimary,
    surface = EmeraldSurface,
    onSurface = TextPrimary,
    surfaceVariant = EmeraldSurfaceVariant,
    onSurfaceVariant = TextSecondary,
)

private val VioletColorScheme = darkColorScheme(
    primary = VioletPrimary,
    onPrimary = TextPrimary,
    secondary = VioletSecondary,
    onSecondary = VioletBackground,
    tertiary = VioletTertiary,
    onTertiary = VioletBackground,
    background = VioletBackground,
    onBackground = TextPrimary,
    surface = VioletSurface,
    onSurface = TextPrimary,
    surfaceVariant = VioletSurfaceVariant,
    onSurfaceVariant = TextSecondary,
)

@Composable
fun MyApplicationTheme(
    theme: AppTheme = AppTheme.SAPPHIRE_DARK,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (theme) {
        AppTheme.SAPPHIRE_DARK -> SapphireColorScheme
        AppTheme.HIGH_CONTRAST_LIGHT -> LightColorScheme
        AppTheme.EMERALD_DARK -> EmeraldColorScheme
        AppTheme.VIOLET_DARK -> VioletColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
