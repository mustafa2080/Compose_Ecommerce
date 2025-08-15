package com.company.npw.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String = "",
    val rating: Float = 0f,
    val title: String = "",
    val comment: String = "",
    val images: List<String> = emptyList(),
    val isVerifiedPurchase: Boolean = false,
    val helpfulCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class Coupon(
    val id: String = "",
    val code: String = "",
    val title: String = "",
    val description: String = "",
    val discountType: DiscountType = DiscountType.PERCENTAGE,
    val discountValue: Double = 0.0,
    val minimumOrderAmount: Double = 0.0,
    val maximumDiscountAmount: Double = 0.0,
    val usageLimit: Int = 0,
    val usedCount: Int = 0,
    val isActive: Boolean = true,
    val validFrom: Long = System.currentTimeMillis(),
    val validUntil: Long = System.currentTimeMillis(),
    val applicableCategories: List<String> = emptyList(),
    val applicableProducts: List<String> = emptyList()
) : Parcelable {
    val isValid: Boolean
        get() {
            val currentTime = System.currentTimeMillis()
            return isActive && 
                   currentTime >= validFrom && 
                   currentTime <= validUntil &&
                   (usageLimit == 0 || usedCount < usageLimit)
        }
}

@Parcelize
enum class DiscountType : Parcelable {
    PERCENTAGE,
    FIXED_AMOUNT,
    FREE_SHIPPING
}
