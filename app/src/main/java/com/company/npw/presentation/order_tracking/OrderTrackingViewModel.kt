package com.company.npw.presentation.order_tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Order
import com.company.npw.domain.model.OrderStatus
import com.company.npw.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _trackingState = MutableStateFlow(OrderTrackingState())
    val trackingState: StateFlow<OrderTrackingState> = _trackingState.asStateFlow()

    private val _uiEvent = MutableStateFlow<OrderTrackingUiEvent?>(null)
    val uiEvent: StateFlow<OrderTrackingUiEvent?> = _uiEvent.asStateFlow()

    fun onEvent(event: OrderTrackingEvent) {
        when (event) {
            is OrderTrackingEvent.LoadOrder -> loadOrder(event.orderId)
            is OrderTrackingEvent.RefreshOrder -> refreshOrder()
            is OrderTrackingEvent.ClearUiEvent -> clearUiEvent()
        }
    }

    private fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _trackingState.value = _trackingState.value.copy(isLoading = true, orderId = orderId)
            
            orderRepository.getOrderById(orderId).collect { result ->
                when (result) {
                    is Resource.Loading<*> -> {
                        _trackingState.value = _trackingState.value.copy(isLoading = true)
                    }
                    is Resource.Success<*> -> {
                        val order = result.data
                        if (order != null) {
                            _trackingState.value = _trackingState.value.copy(
                                order = order,
                                trackingSteps = generateTrackingSteps(order),
                                isLoading = false,
                                error = null
                            )
                        } else {
                            _trackingState.value = _trackingState.value.copy(
                                isLoading = false,
                                error = "Order not found"
                            )
                        }
                    }
                    is Resource.Error<*> -> {
                        _trackingState.value = _trackingState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun refreshOrder() {
        val orderId = _trackingState.value.orderId
        if (orderId.isNotEmpty()) {
            loadOrder(orderId)
        }
    }

    private fun generateTrackingSteps(order: Order): List<TrackingStep> {
        val steps = mutableListOf<TrackingStep>()
        
        // Order Placed
        steps.add(
            TrackingStep(
                status = OrderStatus.PENDING,
                title = "Order Placed",
                description = "Your order has been placed successfully",
                timestamp = order.createdAt,
                isCompleted = true,
                isActive = order.status == OrderStatus.PENDING
            )
        )
        
        // Order Confirmed
        steps.add(
            TrackingStep(
                status = OrderStatus.CONFIRMED,
                title = "Order Confirmed",
                description = "Your order has been confirmed and is being prepared",
                timestamp = if (order.status.ordinal >= OrderStatus.CONFIRMED.ordinal) order.updatedAt else 0L,
                isCompleted = order.status.ordinal >= OrderStatus.CONFIRMED.ordinal,
                isActive = order.status == OrderStatus.CONFIRMED
            )
        )
        
        // Processing
        steps.add(
            TrackingStep(
                status = OrderStatus.PROCESSING,
                title = "Processing",
                description = "Your order is being processed and packed",
                timestamp = if (order.status.ordinal >= OrderStatus.PROCESSING.ordinal) order.updatedAt else 0L,
                isCompleted = order.status.ordinal >= OrderStatus.PROCESSING.ordinal,
                isActive = order.status == OrderStatus.PROCESSING
            )
        )
        
        // Shipped
        steps.add(
            TrackingStep(
                status = OrderStatus.SHIPPED,
                title = "Shipped",
                description = "Your order has been shipped",
                timestamp = if (order.status.ordinal >= OrderStatus.SHIPPED.ordinal) order.updatedAt else 0L,
                isCompleted = order.status.ordinal >= OrderStatus.SHIPPED.ordinal,
                isActive = order.status == OrderStatus.SHIPPED
            )
        )
        
        // Out for Delivery
        steps.add(
            TrackingStep(
                status = OrderStatus.OUT_FOR_DELIVERY,
                title = "Out for Delivery",
                description = "Your order is out for delivery",
                timestamp = if (order.status.ordinal >= OrderStatus.OUT_FOR_DELIVERY.ordinal) order.updatedAt else 0L,
                isCompleted = order.status.ordinal >= OrderStatus.OUT_FOR_DELIVERY.ordinal,
                isActive = order.status == OrderStatus.OUT_FOR_DELIVERY
            )
        )
        
        // Delivered
        steps.add(
            TrackingStep(
                status = OrderStatus.DELIVERED,
                title = "Delivered",
                description = "Your order has been delivered successfully",
                timestamp = if (order.status == OrderStatus.DELIVERED) order.updatedAt else 0L,
                isCompleted = order.status == OrderStatus.DELIVERED,
                isActive = order.status == OrderStatus.DELIVERED
            )
        )
        
        return steps
    }

    private fun clearUiEvent() {
        _uiEvent.value = null
    }
}

data class OrderTrackingState(
    val orderId: String = "",
    val order: Order? = null,
    val trackingSteps: List<TrackingStep> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class TrackingStep(
    val status: OrderStatus,
    val title: String,
    val description: String,
    val timestamp: Long,
    val isCompleted: Boolean,
    val isActive: Boolean
)

sealed class OrderTrackingEvent {
    data class LoadOrder(val orderId: String) : OrderTrackingEvent()
    object RefreshOrder : OrderTrackingEvent()
    object ClearUiEvent : OrderTrackingEvent()
}

sealed class OrderTrackingUiEvent {
    data class ShowError(val message: String) : OrderTrackingUiEvent()
}
