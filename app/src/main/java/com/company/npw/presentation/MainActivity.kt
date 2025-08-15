package com.company.npw.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.company.npw.presentation.navigation.ECommerceNavigation
import com.company.npw.presentation.theme.ECommerceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            val splashScreen = installSplashScreen()
            super.onCreate(savedInstanceState)

            enableEdgeToEdge()

            setContent {
                ECommerceTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ECommerceNavigation()
                    }
                }
            }
        } catch (e: Exception) {
            // Log the error and try to recover
            android.util.Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
            super.onCreate(savedInstanceState)

            // Try to set content without splash screen
            try {
                setContent {
                    ECommerceTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ECommerceNavigation()
                        }
                    }
                }
            } catch (contentException: Exception) {
                android.util.Log.e("MainActivity", "Failed to set content: ${contentException.message}", contentException)
                // If all else fails, finish the activity
                finish()
            }
        }
    }
}
