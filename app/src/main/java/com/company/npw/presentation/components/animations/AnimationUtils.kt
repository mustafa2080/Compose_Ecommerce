package com.company.npw.presentation.components.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import com.company.npw.core.util.Constants

// Animation Durations
object AnimationDurations {
    const val SHORT = Constants.ANIMATION_DURATION_SHORT
    const val MEDIUM = Constants.ANIMATION_DURATION_MEDIUM
    const val LONG = Constants.ANIMATION_DURATION_LONG
}

// Common Animation Specs
object AnimationSpecs {
    val fastOutSlowIn = tween<Float>(durationMillis = AnimationDurations.MEDIUM)
    val bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val smooth = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

// Fade Animations
@Composable
fun FadeInAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationDurations.MEDIUM,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis)),
        content = content
    )
}

// Slide Animations
@Composable
fun SlideInFromTopAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationDurations.MEDIUM,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            animationSpec = tween(durationMillis),
            initialOffsetY = { -it }
        ) + fadeIn(animationSpec = tween(durationMillis)),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis),
            targetOffsetY = { -it }
        ) + fadeOut(animationSpec = tween(durationMillis)),
        content = content
    )
}

@Composable
fun SlideInFromBottomAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationDurations.MEDIUM,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            animationSpec = tween(durationMillis),
            initialOffsetY = { it }
        ) + fadeIn(animationSpec = tween(durationMillis)),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis),
            targetOffsetY = { it }
        ) + fadeOut(animationSpec = tween(durationMillis)),
        content = content
    )
}

@Composable
fun SlideInFromLeftAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationDurations.MEDIUM,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInHorizontally(
            animationSpec = tween(durationMillis),
            initialOffsetX = { -it }
        ) + fadeIn(animationSpec = tween(durationMillis)),
        exit = slideOutHorizontally(
            animationSpec = tween(durationMillis),
            targetOffsetX = { -it }
        ) + fadeOut(animationSpec = tween(durationMillis)),
        content = content
    )
}

@Composable
fun SlideInFromRightAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationDurations.MEDIUM,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInHorizontally(
            animationSpec = tween(durationMillis),
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = tween(durationMillis)),
        exit = slideOutHorizontally(
            animationSpec = tween(durationMillis),
            targetOffsetX = { it }
        ) + fadeOut(animationSpec = tween(durationMillis)),
        content = content
    )
}

// Expand/Collapse Animation
@Composable
fun ExpandCollapseAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = AnimationDurations.MEDIUM,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(animationSpec = tween(durationMillis)) + 
                fadeIn(animationSpec = tween(durationMillis)),
        exit = shrinkVertically(animationSpec = tween(durationMillis)) + 
               fadeOut(animationSpec = tween(durationMillis)),
        content = content
    )
}

// Scale Animation
@Composable
fun ScaleAnimation(
    targetScale: Float,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.bouncy,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(targetScale) {
        scale.animateTo(targetScale, animationSpec)
    }
    
    Box(
        modifier = modifier.scale(scale.value)
    ) {
        content()
    }
}

// Clickable with Scale Effect
@Composable
fun Modifier.clickableWithScale(
    enabled: Boolean = true,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier {
    val scale = remember { Animatable(1f) }
    val interactionSource = remember { MutableInteractionSource() }
    
    return this
        .scale(scale.value)
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null
        ) {
            onClick()
        }
        .graphicsLayer {
            LaunchedEffect(interactionSource) {
                // Add press animation logic here if needed
            }
        }
}

// Rotation Animation
@Composable
fun RotationAnimation(
    targetRotation: Float,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = AnimationSpecs.smooth,
    content: @Composable () -> Unit
) {
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(targetRotation) {
        rotation.animateTo(targetRotation, animationSpec)
    }
    
    Box(
        modifier = modifier.graphicsLayer {
            rotationZ = rotation.value
        }
    ) {
        content()
    }
}
