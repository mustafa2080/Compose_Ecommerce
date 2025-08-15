package com.company.npw.presentation.components.responsive

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Screen size categories
enum class ScreenSize {
    SMALL,   // < 600dp
    MEDIUM,  // 600dp - 840dp
    LARGE    // > 840dp
}

// Window size classes
data class WindowSizeClass(
    val width: ScreenSize,
    val height: ScreenSize
)

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val screenWidth = with(density) { configuration.screenWidthDp.dp }
    val screenHeight = with(density) { configuration.screenHeightDp.dp }
    
    return WindowSizeClass(
        width = when {
            screenWidth < 600.dp -> ScreenSize.SMALL
            screenWidth < 840.dp -> ScreenSize.MEDIUM
            else -> ScreenSize.LARGE
        },
        height = when {
            screenHeight < 600.dp -> ScreenSize.SMALL
            screenHeight < 840.dp -> ScreenSize.MEDIUM
            else -> ScreenSize.LARGE
        }
    )
}

// Responsive values
@Composable
fun responsiveValue(
    small: Dp,
    medium: Dp = small,
    large: Dp = medium
): Dp {
    val windowSize = rememberWindowSizeClass()
    return when (windowSize.width) {
        ScreenSize.SMALL -> small
        ScreenSize.MEDIUM -> medium
        ScreenSize.LARGE -> large
    }
}

@Composable
fun <T> responsiveValue(
    small: T,
    medium: T = small,
    large: T = medium
): T {
    val windowSize = rememberWindowSizeClass()
    return when (windowSize.width) {
        ScreenSize.SMALL -> small
        ScreenSize.MEDIUM -> medium
        ScreenSize.LARGE -> large
    }
}

// Grid columns based on screen size
@Composable
fun getGridColumns(): Int {
    return responsiveValue(
        small = 2,
        medium = 3,
        large = 4
    )
}

// Padding values
object ResponsivePadding {
    @Composable
    fun horizontal(): Dp = responsiveValue(
        small = 16.dp,
        medium = 24.dp,
        large = 32.dp
    )
    
    @Composable
    fun vertical(): Dp = responsiveValue(
        small = 16.dp,
        medium = 20.dp,
        large = 24.dp
    )
    
    @Composable
    fun small(): Dp = responsiveValue(
        small = 8.dp,
        medium = 12.dp,
        large = 16.dp
    )
    
    @Composable
    fun medium(): Dp = responsiveValue(
        small = 16.dp,
        medium = 20.dp,
        large = 24.dp
    )
    
    @Composable
    fun large(): Dp = responsiveValue(
        small = 24.dp,
        medium = 32.dp,
        large = 40.dp
    )
}

// Card sizes
object ResponsiveCardSize {
    @Composable
    fun productCardHeight(): Dp = responsiveValue(
        small = 280.dp,
        medium = 320.dp,
        large = 360.dp
    )
    
    @Composable
    fun categoryCardHeight(): Dp = responsiveValue(
        small = 120.dp,
        medium = 140.dp,
        large = 160.dp
    )
    
    @Composable
    fun bannerHeight(): Dp = responsiveValue(
        small = 180.dp,
        medium = 220.dp,
        large = 260.dp
    )
}

// Text sizes
object ResponsiveTextSize {
    @Composable
    fun isCompact(): Boolean {
        val windowSize = rememberWindowSizeClass()
        return windowSize.width == ScreenSize.SMALL
    }
    
    @Composable
    fun shouldUseCompactLayout(): Boolean {
        val windowSize = rememberWindowSizeClass()
        return windowSize.width == ScreenSize.SMALL || windowSize.height == ScreenSize.SMALL
    }
}

// Layout helpers
@Composable
fun isTablet(): Boolean {
    val windowSize = rememberWindowSizeClass()
    return windowSize.width >= ScreenSize.MEDIUM
}

@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp > configuration.screenHeightDp
}

@Composable
fun shouldUseBottomSheet(): Boolean {
    return !isTablet()
}

@Composable
fun shouldUseSideNavigation(): Boolean {
    return isTablet() && isLandscape()
}
