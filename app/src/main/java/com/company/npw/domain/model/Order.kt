package com.company.npw.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val status: OrderStatus = OrderStatus.PENDING,
    val shippingAddress: Address = Address(),
    val billingAddress: Address = Address(),
    val paymentMethod: PaymentMethod = PaymentMethod(),
    val subtotal: Double = 0.0,
    val shippingCost: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val couponCode: String = "",
    val trackingNumber: String = "",
    val estimatedDelivery: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class OrderItem(
    val id: String = "",
    val product: Product = Product(),
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val price: Double = 0.0
) : Parcelable {
    val totalPrice: Double
        get() = price * quantity
}

@Parcelize
enum class OrderStatus : Parcelable {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    RETURNED,
    REFUNDED
}

@Parcelize
data class PaymentMethod(
    val id: String = "",
    val type: PaymentType = PaymentType.CREDIT_CARD,
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expiryMonth: Int = 0,
    val expiryYear: Int = 0,
    val isDefault: Boolean = false
) : Parcelable

@Parcelize
enum class PaymentType : Parcelable {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    GOOGLE_PAY,
    APPLE_PAY,
    CASH_ON_DELIVERY
}
