package com.company.npw.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: String = "",
    val product: Product = Product(),
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val addedAt: Long = System.currentTimeMillis()
) : Parcelable {
    val totalPrice: Double
        get() = product.price * quantity
}

@Parcelize
data class Cart(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    val totalItems: Int
        get() = items.sumOf { it.quantity }
    
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }
    
    val isEmpty: Boolean
        get() = items.isEmpty()
}

@Parcelize
data class WishlistItem(
    val id: String = "",
    val product: Product = Product(),
    val addedAt: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class Wishlist(
    val id: String = "",
    val userId: String = "",
    val items: List<WishlistItem> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    val isEmpty: Boolean
        get() = items.isEmpty()
}
