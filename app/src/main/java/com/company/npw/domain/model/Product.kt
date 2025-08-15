package com.company.npw.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val discountPercentage: Int = 0,
    val images: List<String> = emptyList(),
    val category: Category = Category(),
    val brand: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val inStock: Boolean = true,
    val stockQuantity: Int = 0,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val specifications: Map<String, String> = emptyMap(),
    val returnPolicy: String = "",
    val shippingInfo: String = "",
    val isFeatured: Boolean = false,
    val isFlashSale: Boolean = false,
    val flashSaleEndTime: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    val hasDiscount: Boolean
        get() = discountPercentage > 0 && originalPrice > price
    
    val isOnSale: Boolean
        get() = hasDiscount || isFlashSale
}

@Parcelize
data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val parentCategoryId: String? = null,
    val isActive: Boolean = true
) : Parcelable
