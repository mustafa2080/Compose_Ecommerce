package com.company.npw.core.util

object Constants {
    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val PRODUCTS_COLLECTION = "products"
    const val CATEGORIES_COLLECTION = "categories"
    const val ORDERS_COLLECTION = "orders"
    const val REVIEWS_COLLECTION = "reviews"
    const val CARTS_COLLECTION = "carts"
    const val WISHLISTS_COLLECTION = "wishlists"
    const val COUPONS_COLLECTION = "coupons"
    const val BANNERS_COLLECTION = "banners"
    const val NOTIFICATIONS_COLLECTION = "notifications"
    
    // Firebase Storage Paths
    const val PRODUCT_IMAGES_PATH = "product_images"
    const val USER_AVATARS_PATH = "user_avatars"
    const val REVIEW_IMAGES_PATH = "review_images"
    const val BANNER_IMAGES_PATH = "banner_images"
    
    // Shared Preferences Keys
    const val PREFS_NAME = "ecommerce_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_LANGUAGE = "language"
    const val KEY_FIRST_TIME = "first_time"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    
    // Network
    const val NETWORK_TIMEOUT = 30L
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    
    // Pagination
    const val PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5
    
    // Image Compression
    const val MAX_IMAGE_SIZE = 1024 * 1024 // 1MB
    const val IMAGE_QUALITY = 80
    
    // Animation Durations
    const val ANIMATION_DURATION_SHORT = 300
    const val ANIMATION_DURATION_MEDIUM = 500
    const val ANIMATION_DURATION_LONG = 800
    
    // Delays
    const val SPLASH_DELAY = 2000L
    const val SEARCH_DELAY = 500L
    const val DEBOUNCE_DELAY = 300L
    
    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_PASSWORD_LENGTH = 128
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 50
    
    // Error Messages
    const val ERROR_NETWORK = "Network error. Please check your connection."
    const val ERROR_GENERIC = "Something went wrong. Please try again."
    const val ERROR_INVALID_EMAIL = "Please enter a valid email address."
    const val ERROR_PASSWORD_TOO_SHORT = "Password must be at least 6 characters."
    const val ERROR_PASSWORDS_DONT_MATCH = "Passwords don't match."
    const val ERROR_USER_NOT_FOUND = "User not found."
    const val ERROR_INVALID_CREDENTIALS = "Invalid email or password."
    const val ERROR_EMAIL_ALREADY_EXISTS = "Email already exists."
    const val ERROR_WEAK_PASSWORD = "Password is too weak."
    
    // Success Messages
    const val SUCCESS_LOGIN = "Login successful!"
    const val SUCCESS_REGISTER = "Registration successful!"
    const val SUCCESS_PASSWORD_RESET = "Password reset email sent!"
    const val SUCCESS_ADDED_TO_CART = "Added to cart!"
    const val SUCCESS_ADDED_TO_WISHLIST = "Added to wishlist!"
    const val SUCCESS_REMOVED_FROM_WISHLIST = "Removed from wishlist!"
    const val SUCCESS_ORDER_PLACED = "Order placed successfully!"
    
    // Languages
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_ARABIC = "ar"
    
    // Product Filters
    const val FILTER_PRICE_LOW_TO_HIGH = "price_low_to_high"
    const val FILTER_PRICE_HIGH_TO_LOW = "price_high_to_low"
    const val FILTER_RATING_HIGH_TO_LOW = "rating_high_to_low"
    const val FILTER_NEWEST_FIRST = "newest_first"
    const val FILTER_POPULARITY = "popularity"
    
    // Order Status
    const val ORDER_STATUS_PENDING = "pending"
    const val ORDER_STATUS_CONFIRMED = "confirmed"
    const val ORDER_STATUS_PROCESSING = "processing"
    const val ORDER_STATUS_SHIPPED = "shipped"
    const val ORDER_STATUS_DELIVERED = "delivered"
    const val ORDER_STATUS_CANCELLED = "cancelled"
}
