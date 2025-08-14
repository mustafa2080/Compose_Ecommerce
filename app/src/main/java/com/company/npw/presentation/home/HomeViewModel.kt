package com.company.npw.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Category
import com.company.npw.domain.model.Product
import com.company.npw.domain.repository.CartRepository
import com.company.npw.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Load all data concurrently
                combine(
                    productRepository.getCategories(),
                    productRepository.getFeaturedProducts(),
                    productRepository.getFlashSaleProducts(),
                    productRepository.getRecommendedProducts("") // TODO: Get actual user ID
                ) { categoriesResource, featuredResource, flashSaleResource, recommendedResource ->
                    
                    val categories = when (categoriesResource) {
                        is Resource.Success -> categoriesResource.data ?: emptyList()
                        else -> emptyList()
                    }
                    
                    val featuredProducts = when (featuredResource) {
                        is Resource.Success -> featuredResource.data ?: emptyList()
                        else -> emptyList()
                    }
                    
                    val flashSaleProducts = when (flashSaleResource) {
                        is Resource.Success -> flashSaleResource.data ?: emptyList()
                        else -> emptyList()
                    }
                    
                    val recommendedProducts = when (recommendedResource) {
                        is Resource.Success -> recommendedResource.data ?: emptyList()
                        else -> emptyList()
                    }
                    
                    val error = when {
                        categoriesResource is Resource.Error -> categoriesResource.message
                        featuredResource is Resource.Error -> featuredResource.message
                        flashSaleResource is Resource.Error -> flashSaleResource.message
                        recommendedResource is Resource.Error -> recommendedResource.message
                        else -> null
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        categories = categories,
                        featuredProducts = featuredProducts,
                        flashSaleProducts = flashSaleProducts,
                        recommendedProducts = recommendedProducts,
                        error = error
                    )
                }.collect { }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun refreshData() {
        loadHomeData()
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val flashSaleProducts: List<Product> = emptyList(),
    val recommendedProducts: List<Product> = emptyList(),
    val cartItemCount: Int = 0,
    val notificationCount: Int = 0,
    val error: String? = null
)
