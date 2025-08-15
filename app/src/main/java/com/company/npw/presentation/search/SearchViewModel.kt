package com.company.npw.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.data.local.preferences.PreferencesManager
import com.company.npw.domain.model.Product
import com.company.npw.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        // Set up search query debouncing
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Wait 300ms after user stops typing
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            clearSearchResults()
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Add to search history
            addToSearchHistory(query)
            
            productRepository.searchProducts(query).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            searchResults = resource.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message ?: "Search failed"
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun performSearch(query: String) {
        searchProducts(query)
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            // TODO: Load search history from local storage
            // For now, using mock data
            val mockHistory = listOf(
                "iPhone",
                "Samsung Galaxy",
                "Laptop",
                "Headphones",
                "Smart Watch"
            )
            
            val mockSuggestions = listOf(
                "Electronics",
                "Fashion",
                "Home & Garden",
                "Sports",
                "Books",
                "Beauty"
            )
            
            _uiState.value = _uiState.value.copy(
                searchHistory = mockHistory,
                searchSuggestions = mockSuggestions
            )
        }
    }

    private fun addToSearchHistory(query: String) {
        viewModelScope.launch {
            val currentHistory = _uiState.value.searchHistory.toMutableList()
            
            // Remove if already exists
            currentHistory.remove(query)
            
            // Add to beginning
            currentHistory.add(0, query)
            
            // Keep only last 10 searches
            if (currentHistory.size > 10) {
                currentHistory.removeAt(currentHistory.size - 1)
            }
            
            _uiState.value = _uiState.value.copy(searchHistory = currentHistory)
            
            // TODO: Save to local storage
        }
    }

    fun clearSearchHistory() {
        _uiState.value = _uiState.value.copy(searchHistory = emptyList())
        // TODO: Clear from local storage
    }

    fun clearSearch() {
        _searchQuery.value = ""
        clearSearchResults()
    }

    private fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            error = null
        )
    }

    fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(
            isGridView = !_uiState.value.isGridView
        )
    }

    fun filterSearchResults(
        categoryId: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        brand: String? = null,
        rating: Float? = null,
        sortBy: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            productRepository.filterProducts(
                categoryId = categoryId,
                minPrice = minPrice,
                maxPrice = maxPrice,
                brand = brand,
                rating = rating,
                sortBy = sortBy
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            searchResults = resource.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message ?: "Filter failed"
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun sortSearchResults(sortBy: String) {
        val currentResults = _uiState.value.searchResults
        val sortedResults = when (sortBy) {
            "price_low_to_high" -> currentResults.sortedBy { it.price }
            "price_high_to_low" -> currentResults.sortedByDescending { it.price }
            "rating_high_to_low" -> currentResults.sortedByDescending { it.rating }
            "newest_first" -> currentResults.sortedByDescending { it.createdAt }
            "popularity" -> currentResults.sortedByDescending { it.reviewCount }
            else -> currentResults
        }
        
        _uiState.value = _uiState.value.copy(searchResults = sortedResults)
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchResults: List<Product> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val searchSuggestions: List<String> = emptyList(),
    val isGridView: Boolean = true,
    val error: String? = null
)
