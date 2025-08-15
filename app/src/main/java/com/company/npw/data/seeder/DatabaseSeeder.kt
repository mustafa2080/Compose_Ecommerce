package com.company.npw.data.seeder

import com.company.npw.core.util.Constants
import com.company.npw.domain.model.Category
import com.company.npw.domain.model.Product
import com.company.npw.domain.model.Review
import com.company.npw.domain.model.User
import com.company.npw.domain.model.UserRole
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    
    suspend fun seedDatabase() {
        try {
            seedCategories()
            seedProducts()
            seedUsers()
            seedReviews()
        } catch (e: Exception) {
            throw Exception("Failed to seed database: ${e.message}")
        }
    }
    
    private suspend fun seedCategories() {
        val categories = listOf(
            Category(
                id = "electronics",
                name = "Electronics",
                description = "Latest gadgets and electronic devices",
                imageUrl = "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=400",
                isActive = true
            ),
            Category(
                id = "fashion",
                name = "Fashion",
                description = "Trendy clothing and accessories",
                imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400",
                isActive = true
            ),
            Category(
                id = "home",
                name = "Home & Garden",
                description = "Everything for your home and garden",
                imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400",
                isActive = true
            ),
            Category(
                id = "sports",
                name = "Sports & Fitness",
                description = "Sports equipment and fitness gear",
                imageUrl = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400",
                isActive = true
            ),
            Category(
                id = "books",
                name = "Books",
                description = "Books, magazines, and educational materials",
                imageUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400",
                isActive = true
            ),
            Category(
                id = "beauty",
                name = "Beauty & Health",
                description = "Beauty products and health supplements",
                imageUrl = "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400",
                isActive = true
            )
        )
        
        val categoriesRef = firebaseDatabase.reference.child(Constants.CATEGORIES_COLLECTION)
        categories.forEach { category ->
            categoriesRef.child(category.id).setValue(category).await()
        }
    }
    
    private suspend fun seedProducts() {
        val products = listOf(
            // Electronics
            Product(
                id = "iphone_15_pro",
                name = "iPhone 15 Pro",
                description = "The latest iPhone with advanced camera system and A17 Pro chip",
                price = 999.0,
                originalPrice = 1099.0,
                discountPercentage = 9,
                images = listOf(
                    "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400",
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400"
                ),
                category = Category(id = "electronics", name = "Electronics"),
                brand = "Apple",
                rating = 4.8f,
                reviewCount = 1250,
                inStock = true,
                stockQuantity = 50,
                sizes = listOf("128GB", "256GB", "512GB", "1TB"),
                colors = listOf("Natural Titanium", "Blue Titanium", "White Titanium", "Black Titanium"),
                tags = listOf("smartphone", "apple", "premium", "5g"),
                specifications = mapOf(
                    "Display" to "6.1-inch Super Retina XDR",
                    "Chip" to "A17 Pro",
                    "Camera" to "48MP Main + 12MP Ultra Wide + 12MP Telephoto",
                    "Battery" to "Up to 23 hours video playback"
                ),
                returnPolicy = "30-day return policy",
                shippingInfo = "Free shipping on orders over $50",
                isFeatured = true,
                isFlashSale = false
            ),
            Product(
                id = "samsung_galaxy_s24",
                name = "Samsung Galaxy S24 Ultra",
                description = "Premium Android smartphone with S Pen and advanced AI features",
                price = 899.0,
                originalPrice = 999.0,
                discountPercentage = 10,
                images = listOf(
                    "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400",
                    "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=400"
                ),
                category = Category(id = "electronics", name = "Electronics"),
                brand = "Samsung",
                rating = 4.7f,
                reviewCount = 890,
                inStock = true,
                stockQuantity = 35,
                sizes = listOf("256GB", "512GB", "1TB"),
                colors = listOf("Titanium Black", "Titanium Gray", "Titanium Violet", "Titanium Yellow"),
                tags = listOf("smartphone", "samsung", "android", "s-pen"),
                specifications = mapOf(
                    "Display" to "6.8-inch Dynamic AMOLED 2X",
                    "Processor" to "Snapdragon 8 Gen 3",
                    "Camera" to "200MP Main + 50MP Periscope + 10MP Telephoto + 12MP Ultra Wide",
                    "Battery" to "5000mAh with 45W fast charging"
                ),
                returnPolicy = "30-day return policy",
                shippingInfo = "Free shipping on orders over $50",
                isFeatured = true,
                isFlashSale = true,
                flashSaleEndTime = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 7 days
            ),
            
            // Fashion
            Product(
                id = "nike_air_max",
                name = "Nike Air Max 270",
                description = "Comfortable running shoes with Air Max technology",
                price = 129.99,
                originalPrice = 149.99,
                discountPercentage = 13,
                images = listOf(
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
                    "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400"
                ),
                category = Category(id = "fashion", name = "Fashion"),
                brand = "Nike",
                rating = 4.5f,
                reviewCount = 2340,
                inStock = true,
                stockQuantity = 120,
                sizes = listOf("7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "11.5", "12"),
                colors = listOf("White/Black", "Black/White", "Red/White", "Blue/White"),
                tags = listOf("shoes", "running", "nike", "air-max"),
                specifications = mapOf(
                    "Material" to "Synthetic and textile upper",
                    "Sole" to "Rubber outsole",
                    "Technology" to "Air Max 270 unit",
                    "Weight" to "Approximately 300g"
                ),
                returnPolicy = "60-day return policy",
                shippingInfo = "Free shipping on orders over $50",
                isFeatured = false,
                isFlashSale = false
            ),
            
            Product(
                id = "levi_jeans_501",
                name = "Levi's 501 Original Jeans",
                description = "Classic straight-leg jeans with authentic fit and vintage appeal",
                price = 89.99,
                originalPrice = 109.99,
                discountPercentage = 18,
                images = listOf(
                    "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400",
                    "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=400"
                ),
                category = Category(id = "fashion", name = "Fashion"),
                brand = "Levi's",
                rating = 4.6f,
                reviewCount = 1890,
                inStock = true,
                stockQuantity = 85,
                sizes = listOf("28", "30", "32", "34", "36", "38", "40"),
                colors = listOf("Dark Blue", "Light Blue", "Black", "Gray"),
                tags = listOf("jeans", "denim", "classic", "levis"),
                specifications = mapOf(
                    "Material" to "100% Cotton",
                    "Fit" to "Straight leg",
                    "Rise" to "Mid-rise",
                    "Care" to "Machine wash cold"
                ),
                returnPolicy = "30-day return policy",
                shippingInfo = "Free shipping on orders over $50",
                isFeatured = false,
                isFlashSale = true,
                flashSaleEndTime = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000) // 3 days
            ),
            
            // Home & Garden
            Product(
                id = "dyson_v15_vacuum",
                name = "Dyson V15 Detect Cordless Vacuum",
                description = "Advanced cordless vacuum with laser dust detection",
                price = 649.99,
                originalPrice = 749.99,
                discountPercentage = 13,
                images = listOf(
                    "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400",
                    "https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=400"
                ),
                category = Category(id = "home", name = "Home & Garden"),
                brand = "Dyson",
                rating = 4.9f,
                reviewCount = 567,
                inStock = true,
                stockQuantity = 25,
                sizes = listOf("Standard"),
                colors = listOf("Yellow/Nickel", "Blue/Red"),
                tags = listOf("vacuum", "cordless", "dyson", "cleaning"),
                specifications = mapOf(
                    "Runtime" to "Up to 60 minutes",
                    "Bin Capacity" to "0.77 liters",
                    "Weight" to "3.1 kg",
                    "Filtration" to "Advanced whole-machine filtration"
                ),
                returnPolicy = "30-day return policy",
                shippingInfo = "Free shipping",
                isFeatured = true,
                isFlashSale = false
            )
        )
        
        val productsRef = firebaseDatabase.reference.child(Constants.PRODUCTS_COLLECTION)
        products.forEach { product ->
            productsRef.child(product.id).setValue(product).await()
        }
    }
    
    private suspend fun seedUsers() {
        val adminUser = User(
            id = "admin_user_001",
            email = "admin@ecommerce.com",
            name = "Admin User",
            phoneNumber = "+1234567890",
            profileImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400",
            isEmailVerified = true,
            role = UserRole.ADMIN,
            isActive = true
        )
        
        val superAdminUser = User(
            id = "super_admin_001",
            email = "superadmin@ecommerce.com",
            name = "Super Admin",
            phoneNumber = "+1234567891",
            profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            isEmailVerified = true,
            role = UserRole.SUPER_ADMIN,
            isActive = true
        )
        
        val usersRef = firebaseDatabase.reference.child(Constants.USERS_COLLECTION)
        usersRef.child(adminUser.id).setValue(adminUser).await()
        usersRef.child(superAdminUser.id).setValue(superAdminUser).await()
    }
    
    private suspend fun seedReviews() {
        val reviews = listOf(
            Review(
                id = "review_001",
                productId = "iphone_15_pro",
                userId = "user_001",
                userName = "John Doe",
                userAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100",
                rating = 5.0f,
                title = "Amazing phone!",
                comment = "The camera quality is incredible and the performance is top-notch. Highly recommended!",
                images = listOf(),
                isVerifiedPurchase = true,
                helpfulCount = 45
            ),
            Review(
                id = "review_002",
                productId = "samsung_galaxy_s24",
                userId = "user_002",
                userName = "Jane Smith",
                userAvatarUrl = "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=100",
                rating = 4.5f,
                title = "Great Android phone",
                comment = "Love the S Pen functionality and the display is gorgeous. Battery life could be better.",
                images = listOf(),
                isVerifiedPurchase = true,
                helpfulCount = 32
            ),
            Review(
                id = "review_003",
                productId = "nike_air_max",
                userId = "user_003",
                userName = "Mike Johnson",
                userAvatarUrl = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=100",
                rating = 4.0f,
                title = "Comfortable for running",
                comment = "Very comfortable shoes for daily running. Good cushioning and support.",
                images = listOf(),
                isVerifiedPurchase = true,
                helpfulCount = 28
            )
        )
        
        val reviewsRef = firebaseDatabase.reference.child(Constants.REVIEWS_COLLECTION)
        reviews.forEach { review ->
            reviewsRef.child(review.id).setValue(review).await()
        }
    }
}
