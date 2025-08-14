package com.company.npw.presentation.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.CartItem
import com.company.npw.domain.model.Wishlist
import com.company.npw.domain.model.WishlistItem
import com.company.npw.domain.repository.AuthRepository
import com.company.npw.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _wishlistState = MutableStateFlow(WishlistState())
    val wishlistState: StateFlow<WishlistState> = _wishlistState.asStateFlow()

    private val _uiEvent = MutableStateFlow<WishlistUiEvent?>(null)
    val uiEvent: StateFlow<WishlistUiEvent?> = _uiEvent.asStateFlow()

    init {
        loadWishlist()
    }

    fun onEvent(event: WishlistEvent) {
        when (event) {
            is WishlistEvent.LoadWishlist -> loadWishlist()
            is WishlistEvent.RemoveItem -> removeFromWishlist(event.wishlistItemId)
            is WishlistEvent.AddToCart -> addToCart(event.wishlistItem)
            is WishlistEvent.ClearUiEvent -> clearUiEvent()
        }
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { userResource ->
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            cartRepository.getWishlist(userId).collect { wishlistResource ->
                                when (wishlistResource) {
                                    is Resource.Loading -> {
                                        _wishlistState.value = _wishlistState.value.copy(isLoading = true)
                                    }
                                    is Resource.Success -> {
                                        val wishlist = wishlistResource.data ?: Wishlist(id = userId, userId = userId)
                                        _wishlistState.value = _wishlistState.value.copy(
                                            wishlist = wishlist,
                                            isLoading = false,
                                            error = null
                                        )
                                    }
                                    is Resource.Error -> {
                                        _wishlistState.value = _wishlistState.value.copy(
                                            isLoading = false,
                                            error = wishlistResource.message
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        _wishlistState.value = _wishlistState.value.copy(
                            isLoading = false,
                            error = userResource.message
                        )
                    }
                    is Resource.Loading -> {
                        _wishlistState.value = _wishlistState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun removeFromWishlist(wishlistItemId: String) {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { userResource ->
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            cartRepository.removeFromWishlist(userId, wishlistItemId).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        _wishlistState.value = _wishlistState.value.copy(isUpdating = true)
                                    }
                                    is Resource.Success -> {
                                        _wishlistState.value = _wishlistState.value.copy(isUpdating = false)
                                        _uiEvent.value = WishlistUiEvent.ShowMessage(result.data ?: "Item removed from wishlist")
                                        loadWishlist() // Reload wishlist to get updated data
                                    }
                                    is Resource.Error -> {
                                        _wishlistState.value = _wishlistState.value.copy(isUpdating = false)
                                        _uiEvent.value = WishlistUiEvent.ShowError(result.message ?: "Failed to remove item")
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        _uiEvent.value = WishlistUiEvent.ShowError("User not authenticated")
                    }
                }
            }
        }
    }

    private fun addToCart(wishlistItem: WishlistItem) {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { userResource ->
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        if (userId != null) {
                            val cartItem = CartItem(
                                product = wishlistItem.product,
                                quantity = 1,
                                selectedSize = "",
                                selectedColor = ""
                            )
                            
                            cartRepository.addToCart(userId, cartItem).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        _wishlistState.value = _wishlistState.value.copy(isUpdating = true)
                                    }
                                    is Resource.Success -> {
                                        _wishlistState.value = _wishlistState.value.copy(isUpdating = false)
                                        _uiEvent.value = WishlistUiEvent.ShowMessage(result.data ?: "Item added to cart")
                                    }
                                    is Resource.Error -> {
                                        _wishlistState.value = _wishlistState.value.copy(isUpdating = false)
                                        _uiEvent.value = WishlistUiEvent.ShowError(result.message ?: "Failed to add to cart")
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        _uiEvent.value = WishlistUiEvent.ShowError("User not authenticated")
                    }
                }
            }
        }
    }

    private fun clearUiEvent() {
        _uiEvent.value = null
    }
}

data class WishlistState(
    val wishlist: Wishlist = Wishlist(),
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val error: String? = null
)

sealed class WishlistEvent {
    object LoadWishlist : WishlistEvent()
    data class RemoveItem(val wishlistItemId: String) : WishlistEvent()
    data class AddToCart(val wishlistItem: WishlistItem) : WishlistEvent()
    object ClearUiEvent : WishlistEvent()
}

sealed class WishlistUiEvent {
    data class ShowMessage(val message: String) : WishlistUiEvent()
    data class ShowError(val message: String) : WishlistUiEvent()
}
