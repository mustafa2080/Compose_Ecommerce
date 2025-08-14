package com.company.npw.domain.repository

import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Category
import com.company.npw.domain.model.Product
import com.company.npw.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getFeaturedProducts(): Flow<Resource<List<Product>>>
    fun getProductsByCategory(categoryId: String): Flow<Resource<List<Product>>>
    fun getProductById(productId: String): Flow<Resource<Product>>
    fun searchProducts(query: String): Flow<Resource<List<Product>>>
    fun getCategories(): Flow<Resource<List<Category>>>
    fun getProductReviews(productId: String): Flow<Resource<List<Review>>>
    fun addReview(review: Review): Flow<Resource<String>>
    fun getFlashSaleProducts(): Flow<Resource<List<Product>>>
    fun getRecommendedProducts(userId: String): Flow<Resource<List<Product>>>
    fun filterProducts(
        categoryId: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        brand: String? = null,
        rating: Float? = null,
        sortBy: String? = null
    ): Flow<Resource<List<Product>>>
}
