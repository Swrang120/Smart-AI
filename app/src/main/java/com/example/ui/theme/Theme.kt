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
    primary = AuraPrimary,
    onPrimary = Color(0xFF1E0A3C),
    primaryContainer = AuraSurfaceVariantDark,
    onPrimaryContainer = AuraMist,
    secondary = AuraSecondary,
    onSecondary = Color(0xFF402200),
    tertiary = AuraTertiary,
    background = AuraBackgroundDark,
    surface = AuraSurfaceDark,
    surfaceVariant = AuraSurfaceVariantDark,
    onBackground = Color(0xFFF3EDFD),
    onSurface = Color(0xFFF3EDFD),
    onSurfaceVariant = AuraSubtext
)

private val LightColorScheme = lightColorScheme(
    primary = AuraPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADBFF),
    onPrimaryContainer = Color(0xFF26084D),
    secondary = Color(0xFF9E4B00),
    tertiary = Color(0xFF006A60),
    background = Color(0xFFFCFAFF),
    surface = Color(0xFFF6F2FC),
    surfaceVariant = Color(0xFFE8E0F2),
    onBackground = Color(0xFF1B1824),
    onSurface = Color(0xFF1B1824),
    onSurfaceVariant = Color(0xFF534C60)
)

@Composable
fun AuraTheme(
    darkTheme: Boolean = true, // Aura defaults to comforting twilight sanctuary mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AuraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
