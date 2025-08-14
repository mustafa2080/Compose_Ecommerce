package com.company.npw.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.company.npw.presentation.cart.CartScreen
import com.company.npw.presentation.categories.CategoriesScreen
import com.company.npw.presentation.home.HomeScreen
import com.company.npw.presentation.navigation.BottomNavigationBar
import com.company.npw.presentation.navigation.Screen
import com.company.npw.presentation.profile.ProfileScreen
import com.company.npw.presentation.wishlist.WishlistScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = bottomNavController,
                cartItemCount = 0, // TODO: Get from cart state
                wishlistItemCount = 0 // TODO: Get from wishlist state
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSearch = { /* TODO: Navigate to search */ },
                    onNavigateToCart = { bottomNavController.navigate(Screen.Cart.route) },
                    onNavigateToNotifications = { /* TODO: Navigate to notifications */ },
                    onNavigateToCategory = { categoryId ->
                        // TODO: Navigate to category products
                    },
                    onNavigateToProduct = { productId ->
                        // TODO: Navigate to product details
                    },
                    onNavigateToAllProducts = {
                        // TODO: Navigate to all products
                    }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesScreen(
                    onSearchClick = { /* TODO: Navigate to search */ },
                    onCategoryClick = { categoryId, categoryName ->
                        // TODO: Navigate to category products
                    }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    onBackClick = { bottomNavController.popBackStack() },
                    onCheckoutClick = {
                        // TODO: Navigate to checkout screen
                    }
                )
            }

            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    onBackClick = { bottomNavController.popBackStack() },
                    onProductClick = { productId ->
                        // TODO: Navigate to product details
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { bottomNavController.popBackStack() },
                    onEditProfileClick = {
                        // TODO: Navigate to edit profile
                    },
                    onOrderHistoryClick = {
                        // TODO: Navigate to order history
                    },
                    onAddressesClick = {
                        // TODO: Navigate to addresses
                    },
                    onSettingsClick = {
                        // TODO: Navigate to settings
                    },
                    onLoginClick = {
                        // TODO: Navigate to login
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$title Screen - Coming Soon!",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
