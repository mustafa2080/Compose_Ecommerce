package com.company.npw.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.GENERAL,
    val isRead: Boolean = false,
    val actionData: String = "", // JSON string for action data
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L // 0 means no expiration
) : Parcelable {
    val isExpired: Boolean
        get() = expiresAt > 0 && System.currentTimeMillis() > expiresAt
}

@Parcelize
enum class NotificationType : Parcelable {
    GENERAL,
    ORDER_UPDATE,
    PROMOTION,
    FLASH_SALE,
    PRODUCT_BACK_IN_STOCK,
    PRICE_DROP,
    REVIEW_REMINDER,
    DELIVERY_UPDATE,
    PAYMENT_CONFIRMATION,
    ACCOUNT_SECURITY,
    NEW_FEATURE,
    SYSTEM_MAINTENANCE
}

@Parcelize
data class NotificationAction(
    val type: NotificationActionType = NotificationActionType.NONE,
    val productId: String = "",
    val orderId: String = "",
    val categoryId: String = "",
    val url: String = ""
) : Parcelable

@Parcelize
enum class NotificationActionType : Parcelable {
    NONE,
    OPEN_PRODUCT,
    OPEN_ORDER,
    OPEN_CATEGORY,
    OPEN_URL,
    OPEN_CART,
    OPEN_PROFILE
}

// Sample notification data for testing
object NotificationSamples {
    fun getSampleNotifications(userId: String): List<Notification> {
        return listOf(
            Notification(
                id = "notif_001",
                userId = userId,
                title = "🎉 Flash Sale Alert!",
                message = "Samsung Galaxy S24 Ultra is now 20% off! Limited time offer.",
                type = NotificationType.FLASH_SALE,
                isRead = false,
                actionData = """{"type":"OPEN_PRODUCT","productId":"samsung_galaxy_s24"}""",
                imageUrl = "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=100",
                createdAt = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 hours ago
                expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours from now
            ),
            Notification(
                id = "notif_002",
                userId = userId,
                title = "📦 Order Shipped",
                message = "Your order #ORD-12345 has been shipped and is on its way!",
                type = NotificationType.ORDER_UPDATE,
                isRead = false,
                actionData = """{"type":"OPEN_ORDER","orderId":"ORD-12345"}""",
                imageUrl = "",
                createdAt = System.currentTimeMillis() - (4 * 60 * 60 * 1000) // 4 hours ago
            ),
            Notification(
                id = "notif_003",
                userId = userId,
                title = "💰 Price Drop Alert",
                message = "iPhone 15 Pro price dropped by $100! Don't miss out.",
                type = NotificationType.PRICE_DROP,
                isRead = true,
                actionData = """{"type":"OPEN_PRODUCT","productId":"iphone_15_pro"}""",
                imageUrl = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=100",
                createdAt = System.currentTimeMillis() - (8 * 60 * 60 * 1000) // 8 hours ago
            ),
            Notification(
                id = "notif_004",
                userId = userId,
                title = "⭐ Review Reminder",
                message = "How was your Nike Air Max 270? Share your experience!",
                type = NotificationType.REVIEW_REMINDER,
                isRead = true,
                actionData = """{"type":"OPEN_PRODUCT","productId":"nike_air_max"}""",
                imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=100",
                createdAt = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 1 day ago
            ),
            Notification(
                id = "notif_005",
                userId = userId,
                title = "🔐 Security Alert",
                message = "New login detected from a different device. Was this you?",
                type = NotificationType.ACCOUNT_SECURITY,
                isRead = false,
                actionData = """{"type":"OPEN_PROFILE","productId":""}""",
                imageUrl = "",
                createdAt = System.currentTimeMillis() - (12 * 60 * 60 * 1000) // 12 hours ago
            ),
            Notification(
                id = "notif_006",
                userId = userId,
                title = "🛍️ Back in Stock",
                message = "Dyson V15 Detect is back in stock! Get yours before it's gone.",
                type = NotificationType.PRODUCT_BACK_IN_STOCK,
                isRead = true,
                actionData = """{"type":"OPEN_PRODUCT","productId":"dyson_v15_vacuum"}""",
                imageUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=100",
                createdAt = System.currentTimeMillis() - (36 * 60 * 60 * 1000) // 1.5 days ago
            ),
            Notification(
                id = "notif_007",
                userId = userId,
                title = "🎁 Special Offer",
                message = "Get 15% off on all Fashion items. Use code: FASHION15",
                type = NotificationType.PROMOTION,
                isRead = false,
                actionData = """{"type":"OPEN_CATEGORY","categoryId":"fashion"}""",
                imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=100",
                createdAt = System.currentTimeMillis() - (6 * 60 * 60 * 1000), // 6 hours ago
                expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 7 days from now
            ),
            Notification(
                id = "notif_008",
                userId = userId,
                title = "🚚 Delivery Update",
                message = "Your order will be delivered today between 2-4 PM.",
                type = NotificationType.DELIVERY_UPDATE,
                isRead = true,
                actionData = """{"type":"OPEN_ORDER","orderId":"ORD-12346"}""",
                imageUrl = "",
                createdAt = System.currentTimeMillis() - (1 * 60 * 60 * 1000) // 1 hour ago
            )
        )
    }
}
