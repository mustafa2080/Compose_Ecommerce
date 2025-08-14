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
        return databaseService.getCollection(
            collection = Constants.PRODUCTS_COLLECTION,
            clazz = Product::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val featuredProducts = resource.data?.filter { it.isFeatured } ?: emptyList()
                            emit(Resource.Success(featuredProducts))
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun getProductsByCategory(categoryId: String): Flow<Resource<List<Product>>> {
        return databaseService.getCollection(
            collection = Constants.PRODUCTS_COLLECTION,
            clazz = Product::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val categoryProducts = resource.data?.filter { it.category.id == categoryId } ?: emptyList()
                            emit(Resource.Success(categoryProducts))
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
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

            // Get all products and filter by query
            databaseService.getCollection(
                collection = Constants.PRODUCTS_COLLECTION,
                clazz = Product::class.java
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val allProducts = resource.data ?: emptyList()
                        val filteredProducts = allProducts.filter { product ->
                            product.name.contains(query, ignoreCase = true) ||
                            product.description.contains(query, ignoreCase = true) ||
                            product.brand.contains(query, ignoreCase = true) ||
                            product.category.name.contains(query, ignoreCase = true)
                        }
                        emit(Resource.Success(filteredProducts))
                    }
                    is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                    is Resource.Loading -> emit(Resource.Loading())
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun getCategories(): Flow<Resource<List<Category>>> {
        return databaseService.getCollection(
            collection = Constants.CATEGORIES_COLLECTION,
            clazz = Category::class.java
        )
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
        return databaseService.getCollection(
            collection = Constants.PRODUCTS_COLLECTION,
            clazz = Product::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val flashSaleProducts = resource.data?.filter { 
                                it.isFlashSale && it.flashSaleEndTime > System.currentTimeMillis() 
                            } ?: emptyList()
                            emit(Resource.Success(flashSaleProducts))
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun getRecommendedProducts(userId: String): Flow<Resource<List<Product>>> = flow {
        // TODO: Implement recommendation algorithm
        emit(Resource.Loading())
        emit(Resource.Success(emptyList()))
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
}
