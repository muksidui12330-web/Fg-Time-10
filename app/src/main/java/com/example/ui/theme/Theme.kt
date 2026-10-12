package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LeafPrimary,
    onPrimary = Color.Black,
    primaryContainer = LeafPrimaryDark,
    onPrimaryContainer = LeafAccent,
    secondary = LeafAccent,
    onSecondary = Color.Black,
    tertiary = LeafSoftGreen,
    background = DarkBg,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB8D4C2),
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = LeafPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = LeafSoftGreen,
    onPrimaryContainer = LeafPrimaryDark,
    secondary = LeafPrimary,
    onSecondary = Color.White,
    tertiary = LeafSecondary,
    background = LightBg,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF3B5644),
    outline = LightOutline
)

@Composable
fun FGTimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
