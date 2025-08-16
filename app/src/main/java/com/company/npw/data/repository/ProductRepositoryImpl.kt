package com.company.npw.data.repository

import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.company.npw.data.remote.firebase.database.FirebaseDatabaseService
import com.company.npw.domain.model.Category
import com.company.npw.domain.model.Product
import com.company.npw.domain.model.Review
import com.company.npw.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val databaseService: FirebaseDatabaseService
) : ProductRepository {

    override fun getFeaturedProducts(): Flow<Resource<List<Product>>> {
        return flow {
            emit(Resource.Loading())
            try {
                // Return sample data for now
                val sampleProducts = getSampleProducts().filter { it.isFeatured }
                emit(Resource.Success(sampleProducts))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    override fun getProductsByCategory(categoryId: String): Flow<Resource<List<Product>>> {
        return flow {
            emit(Resource.Loading())
            try {
                // Return sample data for now
                val sampleProducts = getSampleProducts().filter {
                    it.category.id == categoryId || categoryId == "all"
                }
                emit(Resource.Success(sampleProducts))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    override fun getProductById(productId: String): Flow<Resource<Product>> {
        return databaseService.getDocument(
            collection = Constants.PRODUCTS_COLLECTION,
            documentId = productId,
            clazz = Product::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val product = resource.data
                            if (product != null) {
                                emit(Resource.Success(product))
                            } else {
                                emit(Resource.Error("Product not found"))
                            }
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun searchProducts(query: String): Flow<Resource<List<Product>>> = flow {
        try {
            emit(Resource.Loading())

            // Return sample data for now
            val allProducts = getSampleProducts()
            val filteredProducts = allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true) ||
                product.category.name.contains(query, ignoreCase = true)
            }
            emit(Resource.Success(filteredProducts))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun getCategories(): Flow<Resource<List<Category>>> {
        return flow {
            emit(Resource.Loading())
            try {
                // Return sample data for now
                val sampleCategories = getSampleCategories()
                emit(Resource.Success(sampleCategories))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    override fun getProductReviews(productId: String): Flow<Resource<List<Review>>> {
        return databaseService.getCollection(
            collection = Constants.REVIEWS_COLLECTION,
            clazz = Review::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val productReviews = resource.data?.filter { it.productId == productId } ?: emptyList()
                            emit(Resource.Success(productReviews))
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun addReview(review: Review): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val result = databaseService.addDocument(
                collection = Constants.REVIEWS_COLLECTION,
                data = review
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success("Review added successfully"))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun getFlashSaleProducts(): Flow<Resource<List<Product>>> {
        return flow {
            emit(Resource.Loading())
            try {
                // Return sample data for now
                val sampleProducts = getSampleProducts().filter {
                    it.isFlashSale && it.flashSaleEndTime > System.currentTimeMillis()
                }
                emit(Resource.Success(sampleProducts))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
            }
        }
    }

    override fun getRecommendedProducts(userId: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            // Return sample data for now
            val sampleProducts = getSampleProducts().take(8)
            emit(Resource.Success(sampleProducts))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun filterProducts(
        categoryId: String?,
        minPrice: Double?,
        maxPrice: Double?,
        brand: String?,
        rating: Float?,
        sortBy: String?
    ): Flow<Resource<List<Product>>> = flow {
        // TODO: Implement filtering logic
        emit(Resource.Loading())
        emit(Resource.Success(emptyList()))
    }

    // Sample data functions for demonstration
    private fun getSampleCategories(): List<Category> {
        return listOf(
            Category(
                id = "electronics",
                name = "Electronics",
                description = "Latest gadgets and devices",
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
                description = "Everything for your home",
                imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400",
                isActive = true
            ),
            Category(
                id = "sports",
                name = "Sports",
                description = "Sports and fitness equipment",
                imageUrl = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400",
                isActive = true
            ),
            Category(
                id = "books",
                name = "Books",
                description = "Books and educational materials",
                imageUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400",
                isActive = true
            ),
            Category(
                id = "beauty",
                name = "Beauty",
                description = "Beauty and health products",
                imageUrl = "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400",
                isActive = true
            )
        )
    }

    private fun getSampleProducts(): List<Product> {
        return listOf(
            Product(
                id = "iphone_15_pro",
                name = "iPhone 15 Pro",
                description = "Latest iPhone with advanced camera system and A17 Pro chip",
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
                    "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400"
                ),
                category = Category(id = "electronics", name = "Electronics"),
                brand = "Samsung",
                rating = 4.7f,
                reviewCount = 890,
                inStock = true,
                stockQuantity = 35,
                isFeatured = true,
                isFlashSale = true,
                flashSaleEndTime = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
            ),
            Product(
                id = "nike_air_max",
                name = "Nike Air Max 270",
                description = "Comfortable running shoes with Air Max technology",
                price = 129.99,
                originalPrice = 149.99,
                discountPercentage = 13,
                images = listOf(
                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400"
                ),
                category = Category(id = "fashion", name = "Fashion"),
                brand = "Nike",
                rating = 4.5f,
                reviewCount = 2340,
                inStock = true,
                stockQuantity = 120,
                isFeatured = false,
                isFlashSale = true,
                flashSaleEndTime = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000)
            ),
            Product(
                id = "levi_jeans_501",
                name = "Levi's 501 Original Jeans",
                description = "Classic straight-leg jeans with authentic fit and vintage appeal",
                price = 89.99,
                originalPrice = 109.99,
                discountPercentage = 18,
                images = listOf(
                    "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400"
                ),
                category = Category(id = "fashion", name = "Fashion"),
                brand = "Levi's",
                rating = 4.6f,
                reviewCount = 1890,
                inStock = true,
                stockQuantity = 85,
                isFeatured = true,
                isFlashSale = false
            ),
            Product(
                id = "dyson_v15_vacuum",
                name = "Dyson V15 Detect Vacuum",
                description = "Advanced cordless vacuum with laser dust detection",
                price = 649.99,
                originalPrice = 749.99,
                discountPercentage = 13,
                images = listOf(
                    "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400"
                ),
                category = Category(id = "home", name = "Home & Garden"),
                brand = "Dyson",
                rating = 4.9f,
                reviewCount = 567,
                inStock = true,
                stockQuantity = 25,
                isFeatured = true,
                isFlashSale = false
            ),
            Product(
                id = "macbook_pro_m3",
                name = "MacBook Pro M3",
                description = "Powerful laptop with M3 chip for professionals",
                price = 1599.0,
                originalPrice = 1799.0,
                discountPercentage = 11,
                images = listOf(
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400"
                ),
                category = Category(id = "electronics", name = "Electronics"),
                brand = "Apple",
                rating = 4.9f,
                reviewCount = 892,
                inStock = true,
                stockQuantity = 15,
                isFeatured = true,
                isFlashSale = false
            ),
            Product(
                id = "adidas_ultraboost",
                name = "Adidas Ultraboost 22",
                description = "Premium running shoes with Boost technology",
                price = 179.99,
                originalPrice = 199.99,
                discountPercentage = 10,
                images = listOf(
                    "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400"
                ),
                category = Category(id = "sports", name = "Sports"),
                brand = "Adidas",
                rating = 4.4f,
                reviewCount = 1567,
                inStock = true,
                stockQuantity = 78,
                isFeatured = false,
                isFlashSale = true,
                flashSaleEndTime = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000)
            ),
            Product(
                id = "kindle_paperwhite",
                name = "Kindle Paperwhite",
                description = "Waterproof e-reader with adjustable warm light",
                price = 139.99,
                originalPrice = 159.99,
                discountPercentage = 13,
                images = listOf(
                    "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400"
                ),
                category = Category(id = "books", name = "Books"),
                brand = "Amazon",
                rating = 4.6f,
                reviewCount = 3421,
                inStock = true,
                stockQuantity = 95,
                isFeatured = true,
                isFlashSale = false
            )
        )
    }
}
