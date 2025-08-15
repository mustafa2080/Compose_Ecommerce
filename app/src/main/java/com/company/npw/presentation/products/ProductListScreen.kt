package com.company.npw.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.npw.core.util.Resource
import com.company.npw.presentation.components.ErrorMessage
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.responsive.ResponsivePadding
import com.company.npw.presentation.components.responsive.getGridColumns
import com.company.npw.presentation.products.components.ProductGridItem
import com.company.npw.presentation.products.components.ProductListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    categoryId: String? = null,
    categoryName: String = "Products",
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onFilterClick: () -> Unit,
    productListViewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by productListViewModel.uiState.collectAsState()

    // Load products when screen opens
    androidx.compose.runtime.LaunchedEffect(categoryId) {
        if (categoryId != null) {
            productListViewModel.loadProductsByCategory(categoryId)
        } else {
            productListViewModel.loadAllProducts()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // View toggle button
                    IconButton(
                        onClick = { productListViewModel.toggleViewMode() }
                    ) {
                        Icon(
                            imageVector = if (uiState.isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = if (uiState.isGridView) "List View" else "Grid View"
                        )
                    }
                    
                    // Filter button
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error ?: "Unknown error",
                        onRetry = {
                            if (categoryId != null) {
                                productListViewModel.loadProductsByCategory(categoryId)
                            } else {
                                productListViewModel.loadAllProducts()
                            }
                        }
                    )
                }
                uiState.products.isEmpty() -> {
                    EmptyProductsMessage()
                }
                else -> {
                    ProductContent(
                        uiState = uiState,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductContent(
    uiState: ProductListUiState,
    onProductClick: (String) -> Unit
) {
    if (uiState.isGridView) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(getGridColumns()),
            contentPadding = PaddingValues(ResponsivePadding.medium()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.products) { product ->
                ProductGridItem(
                    product = product,
                    onProductClick = { onProductClick(product.id) },
                    onAddToCart = { /* TODO: Implement add to cart */ },
                    onToggleWishlist = { /* TODO: Implement wishlist toggle */ }
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(ResponsivePadding.medium()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.products) { product ->
                ProductListItem(
                    product = product,
                    onProductClick = { onProductClick(product.id) },
                    onAddToCart = { /* TODO: Implement add to cart */ },
                    onToggleWishlist = { /* TODO: Implement wishlist toggle */ }
                )
            }
        }
    }
}

@Composable
private fun EmptyProductsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No Products Found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your filters or check back later",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
