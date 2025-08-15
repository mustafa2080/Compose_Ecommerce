package com.company.npw.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Cart
import com.company.npw.domain.model.CartItem
import com.company.npw.domain.repository.AuthRepository
import com.company.npw.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    private val _uiEvent = MutableStateFlow<CartUiEvent?>(null)
    val uiEvent: StateFlow<CartUiEvent?> = _uiEvent.asStateFlow()

    init {
        loadCart()
    }

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.LoadCart -> loadCart()
            is CartEvent.UpdateQuantity -> updateCartItemQuantity(event.cartItem, event.quantity)
            is CartEvent.RemoveItem -> removeCartItem(event.cartItemId)
            is CartEvent.ClearCart -> clearCart()
            is CartEvent.ProceedToCheckout -> proceedToCheckout()
            is CartEvent.ClearUiEvent -> clearUiEvent()
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            try {
                _cartState.value = _cartState.value.copy(isLoading = true, error = null)

                val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            cartRepository.getCart(userId).collect { cartResource ->
                                when (cartResource) {
                                    is Resource.Loading -> {
                                        _cartState.value = _cartState.value.copy(isLoading = true)
                                    }
                                    is Resource.Success -> {
                                        val cart = cartResource.data ?: Cart(id = userId, userId = userId)
                                        _cartState.value = _cartState.value.copy(
                                            cart = cart,
                                            isLoading = false,
                                            error = null
                                        )
                                    }
                                    is Resource.Error -> {
                                        _cartState.value = _cartState.value.copy(
                                            isLoading = false,
                                            error = cartResource.message
                                        )
                                    }
                                }
                            }
                        } else {
                            _cartState.value = _cartState.value.copy(
                                isLoading = false,
                                error = "User not found"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _cartState.value = _cartState.value.copy(
                            isLoading = false,
                            error = userResource.message
                        )
                    }
                    is Resource.Loading -> {
                        // This shouldn't happen since we filter out loading states
                        _cartState.value = _cartState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                _cartState.value = _cartState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load cart"
                )
            }
        }
    }

    private fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            try {
                _cartState.value = _cartState.value.copy(isUpdating = true)

                val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            val updatedItem = cartItem.copy(quantity = newQuantity)
                            cartRepository.updateCartItem(userId, updatedItem).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = true)
                                    }
                                    is Resource.Success -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = false)
                                        _uiEvent.value = CartUiEvent.ShowMessage(result.data ?: "Cart updated")
                                        loadCart() // Reload cart to get updated data
                                    }
                                    is Resource.Error -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = false)
                                        _uiEvent.value = CartUiEvent.ShowError(result.message ?: "Failed to update cart")
                                    }
                                }
                            }
                        } else {
                            _cartState.value = _cartState.value.copy(isUpdating = false)
                            _uiEvent.value = CartUiEvent.ShowError("User not found")
                        }
                    }
                    else -> {
                        _cartState.value = _cartState.value.copy(isUpdating = false)
                        _uiEvent.value = CartUiEvent.ShowError("User not authenticated")
                    }
                }
            } catch (e: Exception) {
                _cartState.value = _cartState.value.copy(isUpdating = false)
                _uiEvent.value = CartUiEvent.ShowError(e.message ?: "Failed to update cart")
            }
        }
    }

    private fun removeCartItem(cartItemId: String) {
        viewModelScope.launch {
            try {
                _cartState.value = _cartState.value.copy(isUpdating = true)

                val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            cartRepository.removeFromCart(userId, cartItemId).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = true)
                                    }
                                    is Resource.Success -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = false)
                                        _uiEvent.value = CartUiEvent.ShowMessage(result.data ?: "Item removed")
                                        loadCart() // Reload cart to get updated data
                                    }
                                    is Resource.Error -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = false)
                                        _uiEvent.value = CartUiEvent.ShowError(result.message ?: "Failed to remove item")
                                    }
                                }
                            }
                        } else {
                            _cartState.value = _cartState.value.copy(isUpdating = false)
                            _uiEvent.value = CartUiEvent.ShowError("User not found")
                        }
                    }
                    else -> {
                        _cartState.value = _cartState.value.copy(isUpdating = false)
                        _uiEvent.value = CartUiEvent.ShowError("User not authenticated")
                    }
                }
            } catch (e: Exception) {
                _cartState.value = _cartState.value.copy(isUpdating = false)
                _uiEvent.value = CartUiEvent.ShowError(e.message ?: "Failed to remove item")
            }
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            try {
                _cartState.value = _cartState.value.copy(isUpdating = true)

                val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            cartRepository.clearCart(userId).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = true)
                                    }
                                    is Resource.Success -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = false)
                                        _uiEvent.value = CartUiEvent.ShowMessage(result.data ?: "Cart cleared")
                                        loadCart() // Reload cart to get updated data
                                    }
                                    is Resource.Error -> {
                                        _cartState.value = _cartState.value.copy(isUpdating = false)
                                        _uiEvent.value = CartUiEvent.ShowError(result.message ?: "Failed to clear cart")
                                    }
                                }
                            }
                        } else {
                            _cartState.value = _cartState.value.copy(isUpdating = false)
                            _uiEvent.value = CartUiEvent.ShowError("User not found")
                        }
                    }
                    else -> {
                        _cartState.value = _cartState.value.copy(isUpdating = false)
                        _uiEvent.value = CartUiEvent.ShowError("User not authenticated")
                    }
                }
            } catch (e: Exception) {
                _cartState.value = _cartState.value.copy(isUpdating = false)
                _uiEvent.value = CartUiEvent.ShowError(e.message ?: "Failed to clear cart")
            }
        }
    }

    private fun proceedToCheckout() {
        val cart = _cartState.value.cart
        if (cart.isEmpty) {
            _uiEvent.value = CartUiEvent.ShowError("Cart is empty")
            return
        }
        _uiEvent.value = CartUiEvent.NavigateToCheckout
    }

    private fun clearUiEvent() {
        _uiEvent.value = null
    }
}

data class CartState(
    val cart: Cart = Cart(),
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val error: String? = null
)

sealed class CartEvent {
    object LoadCart : CartEvent()
    data class UpdateQuantity(val cartItem: CartItem, val quantity: Int) : CartEvent()
    data class RemoveItem(val cartItemId: String) : CartEvent()
    object ClearCart : CartEvent()
    object ProceedToCheckout : CartEvent()
    object ClearUiEvent : CartEvent()
}

sealed class CartUiEvent {
    data class ShowMessage(val message: String) : CartUiEvent()
    data class ShowError(val message: String) : CartUiEvent()
    object NavigateToCheckout : CartUiEvent()
}
