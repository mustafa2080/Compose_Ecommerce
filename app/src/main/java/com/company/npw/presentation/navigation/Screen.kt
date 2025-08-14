package com.company.npw.presentation.navigation

sealed class Screen(val route: String) {
    // Authentication
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    
    // Main Navigation
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Cart : Screen("cart")
    object Wishlist : Screen("wishlist")
    object Profile : Screen("profile")
    
    // Product Related
    object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: String) = "product_details/$productId"
    }
    object ProductList : Screen("product_list/{categoryId}") {
        fun createRoute(categoryId: String) = "product_list/$categoryId"
    }
    object Search : Screen("search")
    object SearchResults : Screen("search_results/{query}") {
        fun createRoute(query: String) = "search_results/$query"
    }
    
    // Shopping
    object Checkout : Screen("checkout")
    object Payment : Screen("payment")
    object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: String) = "order_confirmation/$orderId"
    }
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }
    object OrderHistory : Screen("order_history")
    
    // User Profile
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object Addresses : Screen("addresses")
    object AddAddress : Screen("add_address")
    object EditAddress : Screen("edit_address/{addressId}") {
        fun createRoute(addressId: String) = "edit_address/$addressId"
    }
    object PaymentMethods : Screen("payment_methods")
    object AddPaymentMethod : Screen("add_payment_method")
    
    // Reviews
    object Reviews : Screen("reviews/{productId}") {
        fun createRoute(productId: String) = "reviews/$productId"
    }
    object WriteReview : Screen("write_review/{productId}") {
        fun createRoute(productId: String) = "write_review/$productId"
    }
    
    // Support
    object ContactUs : Screen("contact_us")
    object Chat : Screen("chat")
    object Help : Screen("help")
    object About : Screen("about")
    
    // Admin (if user is admin)
    object AdminDashboard : Screen("admin_dashboard")
    object AdminProducts : Screen("admin_products")
    object AdminOrders : Screen("admin_orders")
    object AdminUsers : Screen("admin_users")
    object AdminAnalytics : Screen("admin_analytics")
    
    // Notifications
    object Notifications : Screen("notifications")
}
