package com.company.npw.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,
    tertiary = Warning,
    onTertiary = OnWarning,
    error = Error,
    onError = OnError,
    errorContainer = Error,
    onErrorContainer = OnError,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = CardBackgroundDark,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    scrim = BlackOverlay,
    inverseSurface = Background,
    inverseOnSurface = OnBackground,
    inversePrimary = Primary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,
    tertiary = Warning,
    onTertiary = OnWarning,
    error = Error,
    onError = OnError,
    errorContainer = Error,
    onErrorContainer = OnError,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    scrim = BlackOverlay,
    inverseSurface = BackgroundDark,
    inverseOnSurface = OnBackgroundDark,
    inversePrimary = PrimaryDark
)

@Composable
fun ECommerceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Extension properties for custom colors
val ColorScheme.success: androidx.compose.ui.graphics.Color
    @Composable get() = Success

val ColorScheme.onSuccess: androidx.compose.ui.graphics.Color
    @Composable get() = OnSuccess

val ColorScheme.warning: androidx.compose.ui.graphics.Color
    @Composable get() = Warning

val ColorScheme.onWarning: androidx.compose.ui.graphics.Color
    @Composable get() = OnWarning

val ColorScheme.ratingStar: androidx.compose.ui.graphics.Color
    @Composable get() = RatingStar

val ColorScheme.discountRed: androidx.compose.ui.graphics.Color
    @Composable get() = DiscountRed

val ColorScheme.priceGreen: androidx.compose.ui.graphics.Color
    @Composable get() = PriceGreen

val ColorScheme.outOfStock: androidx.compose.ui.graphics.Color
    @Composable get() = OutOfStock

val ColorScheme.cardBackground: androidx.compose.ui.graphics.Color
    @Composable get() = if (isSystemInDarkTheme()) CardBackgroundDark else CardBackground

val ColorScheme.divider: androidx.compose.ui.graphics.Color
    @Composable get() = if (isSystemInDarkTheme()) DividerDark else DividerLight

val ColorScheme.textPrimary: androidx.compose.ui.graphics.Color
    @Composable get() = if (isSystemInDarkTheme()) TextPrimaryDark else TextPrimary

val ColorScheme.textSecondary: androidx.compose.ui.graphics.Color
    @Composable get() = if (isSystemInDarkTheme()) TextSecondaryDark else TextSecondary

val ColorScheme.textHint: androidx.compose.ui.graphics.Color
    @Composable get() = if (isSystemInDarkTheme()) TextHintDark else TextHint

val ColorScheme.shimmerColor: androidx.compose.ui.graphics.Color
    @Composable get() = if (isSystemInDarkTheme()) ShimmerColorDark else ShimmerColorLight
