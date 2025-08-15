package com.company.npw.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.*
import com.company.npw.domain.repository.AuthRepository
import com.company.npw.domain.repository.CartRepository
import com.company.npw.domain.repository.OrderRepository
import com.company.npw.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _checkoutState = MutableStateFlow(CheckoutState())
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()

    private val _uiEvent = MutableStateFlow<CheckoutUiEvent?>(null)
    val uiEvent: StateFlow<CheckoutUiEvent?> = _uiEvent.asStateFlow()

    init {
        loadCheckoutData()
    }

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.LoadCheckoutData -> loadCheckoutData()
            is CheckoutEvent.SelectAddress -> selectAddress(event.address)
            is CheckoutEvent.SelectPaymentMethod -> selectPaymentMethod(event.paymentMethod)
            is CheckoutEvent.ApplyCoupon -> applyCoupon(event.couponCode)
            is CheckoutEvent.PlaceOrder -> placeOrder()
            is CheckoutEvent.ClearUiEvent -> clearUiEvent()
        }
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            try {
                _checkoutState.value = _checkoutState.value.copy(isLoading = true, error = null)

                val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            // Load cart
                            cartRepository.getCart(userId).collect { cartResource ->
                                when (cartResource) {
                                    is Resource.Success -> {
                                        val cart = cartResource.data ?: Cart()
                                        if (cart.isEmpty) {
                                            _checkoutState.value = _checkoutState.value.copy(isLoading = false)
                                            _uiEvent.value = CheckoutUiEvent.ShowError("Cart is empty")
                                            return@collect
                                        }

                                        _checkoutState.value = _checkoutState.value.copy(
                                            cart = cart,
                                            isLoading = false
                                        )
                                        calculateTotals()
                                    }
                                    is Resource.Error -> {
                                        _checkoutState.value = _checkoutState.value.copy(
                                            isLoading = false,
                                            error = cartResource.message
                                        )
                                    }
                                    is Resource.Loading -> { /* Already loading */ }
                                }
                            }
                        } else {
                            _checkoutState.value = _checkoutState.value.copy(
                                isLoading = false,
                                error = "User not found"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _checkoutState.value = _checkoutState.value.copy(
                            isLoading = false,
                            error = userResource.message
                        )
                    }
                    is Resource.Loading -> {
                        // This shouldn't happen since we filter out loading states
                        _checkoutState.value = _checkoutState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                _checkoutState.value = _checkoutState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load checkout data"
                )
            }
        }
    }

    private fun selectAddress(address: Address) {
        _checkoutState.value = _checkoutState.value.copy(selectedAddress = address)
        calculateTotals()
    }

    private fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        _checkoutState.value = _checkoutState.value.copy(selectedPaymentMethod = paymentMethod)
    }

    private fun applyCoupon(couponCode: String) {
        viewModelScope.launch {
            _checkoutState.value = _checkoutState.value.copy(isApplyingCoupon = true)
            
            // TODO: Implement coupon validation
            // For now, simulate a discount
            val discount = if (couponCode.uppercase() == "SAVE10") {
                _checkoutState.value.subtotal * 0.1
            } else {
                0.0
            }
            
            if (discount > 0) {
                _checkoutState.value = _checkoutState.value.copy(
                    appliedCoupon = couponCode,
                    discount = discount,
                    isApplyingCoupon = false
                )
                _uiEvent.value = CheckoutUiEvent.ShowMessage("Coupon applied successfully!")
            } else {
                _checkoutState.value = _checkoutState.value.copy(isApplyingCoupon = false)
                _uiEvent.value = CheckoutUiEvent.ShowError("Invalid coupon code")
            }
            
            calculateTotals()
        }
    }

    private fun placeOrder() {
        val state = _checkoutState.value
        
        if (state.selectedAddress == null) {
            _uiEvent.value = CheckoutUiEvent.ShowError("Please select a delivery address")
            return
        }
        
        if (state.selectedPaymentMethod == null) {
            _uiEvent.value = CheckoutUiEvent.ShowError("Please select a payment method")
            return
        }

        viewModelScope.launch {
            try {
                _checkoutState.value = _checkoutState.value.copy(isPlacingOrder = true)

                val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            val order = createOrderFromCheckout(userId, state)

                            orderRepository.createOrder(order).collect { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        _checkoutState.value = _checkoutState.value.copy(isPlacingOrder = false)

                                        // Clear cart after successful order
                                        cartRepository.clearCart(userId)

                                        _uiEvent.value = CheckoutUiEvent.OrderPlaced(result.data ?: "")
                                    }
                                    is Resource.Error -> {
                                        _checkoutState.value = _checkoutState.value.copy(isPlacingOrder = false)
                                        _uiEvent.value = CheckoutUiEvent.ShowError(result.message ?: "Failed to place order")
                                    }
                                    is Resource.Loading -> { /* Already loading */ }
                                }
                            }
                        } else {
                            _checkoutState.value = _checkoutState.value.copy(isPlacingOrder = false)
                            _uiEvent.value = CheckoutUiEvent.ShowError("User not found")
                        }
                    }
                    else -> {
                        _checkoutState.value = _checkoutState.value.copy(isPlacingOrder = false)
                        _uiEvent.value = CheckoutUiEvent.ShowError("User not authenticated")
                    }
                }
            } catch (e: Exception) {
                _checkoutState.value = _checkoutState.value.copy(isPlacingOrder = false)
                _uiEvent.value = CheckoutUiEvent.ShowError(e.message ?: "Failed to place order")
            }
        }
    }

    private fun createOrderFromCheckout(userId: String, state: CheckoutState): Order {
        val orderItems = state.cart.items.map { cartItem ->
            OrderItem(
                id = "order_item_${System.currentTimeMillis()}_${(1000..9999).random()}",
                product = cartItem.product,
                quantity = cartItem.quantity,
                selectedSize = cartItem.selectedSize,
                selectedColor = cartItem.selectedColor,
                price = cartItem.product.price
            )
        }

        return Order(
            id = "order_${System.currentTimeMillis()}_${(1000..9999).random()}",
            userId = userId,
            items = orderItems,
            status = OrderStatus.PENDING,
            shippingAddress = state.selectedAddress ?: Address(),
            billingAddress = state.selectedAddress ?: Address(),
            paymentMethod = state.selectedPaymentMethod ?: PaymentMethod(),
            subtotal = state.subtotal,
            shippingCost = state.shippingCost,
            tax = state.tax,
            discount = state.discount,
            total = state.total,
            couponCode = state.appliedCoupon
        )
    }

    private fun calculateTotals() {
        val state = _checkoutState.value
        val subtotal = state.cart.subtotal
        val shippingCost = if (subtotal > 50.0) 0.0 else 5.99
        val tax = subtotal * 0.08 // 8% tax
        val total = subtotal + shippingCost + tax - state.discount

        _checkoutState.value = state.copy(
            subtotal = subtotal,
            shippingCost = shippingCost,
            tax = tax,
            total = total
        )
    }

    private fun clearUiEvent() {
        _uiEvent.value = null
    }
}

data class CheckoutState(
    val cart: Cart = Cart(),
    val selectedAddress: Address? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val appliedCoupon: String = "",
    val subtotal: Double = 0.0,
    val shippingCost: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val isLoading: Boolean = false,
    val isApplyingCoupon: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val error: String? = null
)

sealed class CheckoutEvent {
    object LoadCheckoutData : CheckoutEvent()
    data class SelectAddress(val address: Address) : CheckoutEvent()
    data class SelectPaymentMethod(val paymentMethod: PaymentMethod) : CheckoutEvent()
    data class ApplyCoupon(val couponCode: String) : CheckoutEvent()
    object PlaceOrder : CheckoutEvent()
    object ClearUiEvent : CheckoutEvent()
}

sealed class CheckoutUiEvent {
    data class ShowMessage(val message: String) : CheckoutUiEvent()
    data class ShowError(val message: String) : CheckoutUiEvent()
    data class OrderPlaced(val orderId: String) : CheckoutUiEvent()
}
