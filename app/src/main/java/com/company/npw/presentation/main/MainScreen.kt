package com.company.npw.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
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
import com.company.npw.presentation.components.NavigationDrawerContent
import com.company.npw.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // TODO: Get current user from auth state
    val currentUser = remember {
        User(
            id = "sample_user",
            name = "John Doe",
            email = "john.doe@example.com",
            profileImageUrl = "",
            isEmailVerified = true
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                currentUser = currentUser,
                selectedRoute = currentRoute ?: "",
                onMenuItemClick = { route ->
                    scope.launch {
                        drawerState.close()
                        when (route) {
                            "home" -> bottomNavController.navigate(Screen.Home.route)
                            "categories" -> bottomNavController.navigate(Screen.Categories.route)
                            "cart" -> bottomNavController.navigate(Screen.Cart.route)
                            "wishlist" -> bottomNavController.navigate(Screen.Wishlist.route)
                            "profile" -> bottomNavController.navigate(Screen.Profile.route)
                            else -> navController.navigate(route)
                        }
                    }
                },
                onProfileClick = {
                    scope.launch {
                        drawerState.close()
                        bottomNavController.navigate(Screen.Profile.route)
                    }
                },
                onLogoutClick = {
                    scope.launch {
                        drawerState.close()
                        // TODO: Implement logout
                    }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = bottomNavController,
                    cartItemCount = 0, // TODO: Get from cart state
                    wishlistItemCount = 0 // TODO: Get from wishlist state
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToCart = { bottomNavController.navigate(Screen.Cart.route) },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToCategory = { categoryId ->
                        navController.navigate(Screen.ProductList.createRoute(categoryId))
                    },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetails.createRoute(productId))
                    },
                    onNavigateToAllProducts = {
                        navController.navigate(Screen.ProductList.createRoute("all"))
                    },
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesScreen(
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    },
                    onCategoryClick = { categoryId, categoryName ->
                        navController.navigate(Screen.ProductList.createRoute(categoryId))
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
                        navController.navigate(Screen.ProductDetails.createRoute(productId))
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
