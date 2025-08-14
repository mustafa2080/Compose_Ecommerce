package com.company.npw.domain.repository

import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Cart
import com.company.npw.domain.model.CartItem
import com.company.npw.domain.model.Wishlist
import com.company.npw.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(userId: String): Flow<Resource<Cart>>
    fun addToCart(userId: String, cartItem: CartItem): Flow<Resource<String>>
    fun updateCartItem(userId: String, cartItem: CartItem): Flow<Resource<String>>
    fun removeFromCart(userId: String, cartItemId: String): Flow<Resource<String>>
    fun clearCart(userId: String): Flow<Resource<String>>
    
    fun getWishlist(userId: String): Flow<Resource<Wishlist>>
    fun addToWishlist(userId: String, wishlistItem: WishlistItem): Flow<Resource<String>>
    fun removeFromWishlist(userId: String, wishlistItemId: String): Flow<Resource<String>>
    fun isInWishlist(userId: String, productId: String): Flow<Resource<Boolean>>
}
