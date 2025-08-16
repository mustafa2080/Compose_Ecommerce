package com.company.npw.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.company.npw.R
import com.company.npw.core.util.Constants
import com.company.npw.presentation.auth.AuthState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    authState: AuthState
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    // Animation effects
    LaunchedEffect(key1 = true) {
        try {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    delayMillis = 100
                )
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800,
                    delayMillis = 200
                )
            )
        } catch (e: Exception) {
            // Animation failed, continue anyway
        }
    }

    // Navigation logic with proper delay and error handling
    val hasNavigated = remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (hasNavigated.value) return@LaunchedEffect

        try {
            // Wait for animations to complete
            delay(2000)

            when (authState) {
                is AuthState.Authenticated -> {
                    hasNavigated.value = true
                    onNavigateToMain()
                }
                is AuthState.Unauthenticated -> {
                    hasNavigated.value = true
                    onNavigateToLogin()
                }
                is AuthState.Loading -> {
                    // Wait a bit more for auth to resolve
                    delay(1000)
                    if (!hasNavigated.value) {
                        hasNavigated.value = true
                        onNavigateToLogin() // Default to login if auth takes too long
                    }
                }
            }
        } catch (e: Exception) {
            // If anything fails, navigate to login as fallback
            if (!hasNavigated.value) {
                hasNavigated.value = true
                try {
                    onNavigateToLogin()
                } catch (navException: Exception) {
                    // Last resort - do nothing and let user restart app
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // App Logo/Icon
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your Shopping Destination",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator (only show when loading)
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .alpha(alpha.value),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
