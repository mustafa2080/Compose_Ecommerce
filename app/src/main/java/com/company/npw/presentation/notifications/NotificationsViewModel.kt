package com.company.npw.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.domain.model.Notification
import com.company.npw.domain.model.NotificationSamples
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    // TODO: Inject notification repository when implemented
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var allNotifications = mutableListOf<Notification>()

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // TODO: Replace with actual repository call
                // For now, using sample data
                val sampleNotifications = NotificationSamples.getSampleNotifications("current_user_id")
                allNotifications.clear()
                allNotifications.addAll(sampleNotifications)
                
                updateUiState()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load notifications"
                )
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val index = allNotifications.indexOfFirst { it.id == notificationId }
                if (index != -1) {
                    allNotifications[index] = allNotifications[index].copy(isRead = true)
                    updateUiState()
                    
                    // TODO: Update in repository/database
                }
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                allNotifications.replaceAll { it.copy(isRead = true) }
                updateUiState()
                
                // TODO: Update in repository/database
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to mark all as read"
                )
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                allNotifications.removeAll { it.id == notificationId }
                updateUiState()
                
                // TODO: Delete from repository/database
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete notification"
                )
            }
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                allNotifications.clear()
                updateUiState()
                
                // TODO: Clear from repository/database
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to clear notifications"
                )
            }
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }

    private fun updateUiState() {
        val sortedNotifications = allNotifications
            .filter { !it.isExpired }
            .sortedByDescending { it.createdAt }
        
        val unreadCount = sortedNotifications.count { !it.isRead }
        
        _uiState.value = _uiState.value.copy(
            notifications = sortedNotifications,
            isLoading = false,
            error = null,
            unreadCount = unreadCount
        )
    }

    fun getUnreadCount(): Int {
        return _uiState.value.unreadCount
    }

    // Method to handle notification actions
    fun handleNotificationAction(notification: Notification): NotificationAction? {
        return try {
            if (notification.actionData.isNotEmpty()) {
                // Parse action data JSON and return appropriate action
                // For now, return a simple action based on notification type
                when (notification.type) {
                    com.company.npw.domain.model.NotificationType.ORDER_UPDATE,
                    com.company.npw.domain.model.NotificationType.DELIVERY_UPDATE,
                    com.company.npw.domain.model.NotificationType.PAYMENT_CONFIRMATION -> {
                        NotificationAction.OpenOrder("sample_order_id")
                    }
                    com.company.npw.domain.model.NotificationType.PRODUCT_BACK_IN_STOCK,
                    com.company.npw.domain.model.NotificationType.PRICE_DROP,
                    com.company.npw.domain.model.NotificationType.REVIEW_REMINDER -> {
                        NotificationAction.OpenProduct("sample_product_id")
                    }
                    com.company.npw.domain.model.NotificationType.PROMOTION,
                    com.company.npw.domain.model.NotificationType.FLASH_SALE -> {
                        NotificationAction.OpenCategory("sample_category_id")
                    }
                    com.company.npw.domain.model.NotificationType.ACCOUNT_SECURITY -> {
                        NotificationAction.OpenProfile
                    }
                    else -> null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

// Sealed class for notification actions
sealed class NotificationAction {
    object OpenProfile : NotificationAction()
    object OpenCart : NotificationAction()
    data class OpenProduct(val productId: String) : NotificationAction()
    data class OpenOrder(val orderId: String) : NotificationAction()
    data class OpenCategory(val categoryId: String) : NotificationAction()
    data class OpenUrl(val url: String) : NotificationAction()
}
