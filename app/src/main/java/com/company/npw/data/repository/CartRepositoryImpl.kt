package com.company.npw.data.repository

import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.company.npw.data.remote.firebase.database.FirebaseDatabaseService
import com.company.npw.domain.model.Cart
import com.company.npw.domain.model.CartItem
import com.company.npw.domain.model.Wishlist
import com.company.npw.domain.model.WishlistItem
import com.company.npw.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val databaseService: FirebaseDatabaseService
) : CartRepository {

    override fun getCart(userId: String): Flow<Resource<Cart>> {
        return databaseService.getDocument(
            collection = Constants.CARTS_COLLECTION,
            documentId = userId,
            clazz = Cart::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val cart = resource.data ?: Cart(id = userId, userId = userId)
                            emit(Resource.Success(cart))
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun addToCart(userId: String, cartItem: CartItem): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            // Get current cart
            getCart(userId).collect { cartResource ->
                when (cartResource) {
                    is Resource.Success -> {
                        val currentCart = cartResource.data ?: Cart(id = userId, userId = userId)
                        val existingItems = currentCart.items.toMutableList()

                        // Check if item already exists in cart
                        val existingItemIndex = existingItems.indexOfFirst {
                            it.product.id == cartItem.product.id &&
                            it.selectedSize == cartItem.selectedSize &&
                            it.selectedColor == cartItem.selectedColor
                        }

                        if (existingItemIndex != -1) {
                            // Update quantity of existing item
                            val existingItem = existingItems[existingItemIndex]
                            existingItems[existingItemIndex] = existingItem.copy(
                                quantity = existingItem.quantity + cartItem.quantity
                            )
                        } else {
                            // Add new item to cart
                            existingItems.add(cartItem.copy(id = generateCartItemId()))
                        }

                        val updatedCart = currentCart.copy(
                            items = existingItems,
                            updatedAt = System.currentTimeMillis()
                        )

                        // Save updated cart
                        val result = databaseService.setDocument(
                            collection = Constants.CARTS_COLLECTION,
                            documentId = userId,
                            data = updatedCart
                        )

                        when (result) {
                            is Resource.Success -> emit(Resource.Success(Constants.SUCCESS_ADDED_TO_CART))
                            is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                            is Resource.Loading -> { /* Already emitted loading */ }
                        }
                    }
                    is Resource.Error -> emit(Resource.Error(cartResource.message ?: Constants.ERROR_GENERIC))
                    is Resource.Loading -> { /* Already emitted loading */ }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun updateCartItem(userId: String, cartItem: CartItem): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            getCart(userId).collect { cartResource ->
                when (cartResource) {
                    is Resource.Success -> {
                        val currentCart = cartResource.data ?: Cart(id = userId, userId = userId)
                        val existingItems = currentCart.items.toMutableList()

                        val itemIndex = existingItems.indexOfFirst { it.id == cartItem.id }

                        if (itemIndex != -1) {
                            if (cartItem.quantity <= 0) {
                                // Remove item if quantity is 0 or less
                                existingItems.removeAt(itemIndex)
                            } else {
                                // Update item
                                existingItems[itemIndex] = cartItem.copy(
                                    addedAt = existingItems[itemIndex].addedAt // Keep original added time
                                )
                            }

                            val updatedCart = currentCart.copy(
                                items = existingItems,
                                updatedAt = System.currentTimeMillis()
                            )

                            val result = databaseService.setDocument(
                                collection = Constants.CARTS_COLLECTION,
                                documentId = userId,
                                data = updatedCart
                            )

                            when (result) {
                                is Resource.Success -> emit(Resource.Success("Cart item updated"))
                                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                                is Resource.Loading -> { /* Already emitted loading */ }
                            }
                        } else {
                            emit(Resource.Error("Cart item not found"))
                        }
                    }
                    is Resource.Error -> emit(Resource.Error(cartResource.message ?: Constants.ERROR_GENERIC))
                    is Resource.Loading -> { /* Already emitted loading */ }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun removeFromCart(userId: String, cartItemId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            getCart(userId).collect { cartResource ->
                when (cartResource) {
                    is Resource.Success -> {
                        val currentCart = cartResource.data ?: Cart(id = userId, userId = userId)
                        val existingItems = currentCart.items.toMutableList()

                        val itemRemoved = existingItems.removeAll { it.id == cartItemId }

                        if (itemRemoved) {
                            val updatedCart = currentCart.copy(
                                items = existingItems,
                                updatedAt = System.currentTimeMillis()
                            )

                            val result = databaseService.setDocument(
                                collection = Constants.CARTS_COLLECTION,
                                documentId = userId,
                                data = updatedCart
                            )

                            when (result) {
                                is Resource.Success -> emit(Resource.Success("Item removed from cart"))
                                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                                is Resource.Loading -> { /* Already emitted loading */ }
                            }
                        } else {
                            emit(Resource.Error("Cart item not found"))
                        }
                    }
                    is Resource.Error -> emit(Resource.Error(cartResource.message ?: Constants.ERROR_GENERIC))
                    is Resource.Loading -> { /* Already emitted loading */ }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun clearCart(userId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val emptyCart = Cart(id = userId, userId = userId)
            val result = databaseService.setDocument(
                collection = Constants.CARTS_COLLECTION,
                documentId = userId,
                data = emptyCart
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success("Cart cleared"))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun getWishlist(userId: String): Flow<Resource<Wishlist>> {
        return databaseService.getDocument(
            collection = Constants.WISHLISTS_COLLECTION,
            documentId = userId,
            clazz = Wishlist::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val wishlist = resource.data ?: Wishlist(id = userId, userId = userId)
                            emit(Resource.Success(wishlist))
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun addToWishlist(userId: String, wishlistItem: WishlistItem): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            getWishlist(userId).collect { wishlistResource ->
                when (wishlistResource) {
                    is Resource.Success -> {
                        val currentWishlist = wishlistResource.data ?: Wishlist(id = userId, userId = userId)
                        val existingItems = currentWishlist.items.toMutableList()

                        // Check if item already exists in wishlist
                        val itemExists = existingItems.any { it.product.id == wishlistItem.product.id }

                        if (!itemExists) {
                            existingItems.add(wishlistItem.copy(id = generateWishlistItemId()))

                            val updatedWishlist = currentWishlist.copy(
                                items = existingItems,
                                updatedAt = System.currentTimeMillis()
                            )

                            val result = databaseService.setDocument(
                                collection = Constants.WISHLISTS_COLLECTION,
                                documentId = userId,
                                data = updatedWishlist
                            )

                            when (result) {
                                is Resource.Success -> emit(Resource.Success(Constants.SUCCESS_ADDED_TO_WISHLIST))
                                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                                is Resource.Loading -> { /* Already emitted loading */ }
                            }
                        } else {
                            emit(Resource.Error("Item already in wishlist"))
                        }
                    }
                    is Resource.Error -> emit(Resource.Error(wishlistResource.message ?: Constants.ERROR_GENERIC))
                    is Resource.Loading -> { /* Already emitted loading */ }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun removeFromWishlist(userId: String, wishlistItemId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())

            getWishlist(userId).collect { wishlistResource ->
                when (wishlistResource) {
                    is Resource.Success -> {
                        val currentWishlist = wishlistResource.data ?: Wishlist(id = userId, userId = userId)
                        val existingItems = currentWishlist.items.toMutableList()

                        val itemRemoved = existingItems.removeAll { it.id == wishlistItemId }

                        if (itemRemoved) {
                            val updatedWishlist = currentWishlist.copy(
                                items = existingItems,
                                updatedAt = System.currentTimeMillis()
                            )

                            val result = databaseService.setDocument(
                                collection = Constants.WISHLISTS_COLLECTION,
                                documentId = userId,
                                data = updatedWishlist
                            )

                            when (result) {
                                is Resource.Success -> emit(Resource.Success(Constants.SUCCESS_REMOVED_FROM_WISHLIST))
                                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                                is Resource.Loading -> { /* Already emitted loading */ }
                            }
                        } else {
                            emit(Resource.Error("Wishlist item not found"))
                        }
                    }
                    is Resource.Error -> emit(Resource.Error(wishlistResource.message ?: Constants.ERROR_GENERIC))
                    is Resource.Loading -> { /* Already emitted loading */ }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun isInWishlist(userId: String, productId: String): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())

            getWishlist(userId).collect { wishlistResource ->
                when (wishlistResource) {
                    is Resource.Success -> {
                        val wishlist = wishlistResource.data ?: Wishlist(id = userId, userId = userId)
                        val isInWishlist = wishlist.items.any { it.product.id == productId }
                        emit(Resource.Success(isInWishlist))
                    }
                    is Resource.Error -> emit(Resource.Error(wishlistResource.message ?: Constants.ERROR_GENERIC))
                    is Resource.Loading -> { /* Already emitted loading */ }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    private fun generateCartItemId(): String {
        return "cart_item_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private fun generateWishlistItemId(): String {
        return "wishlist_item_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}
