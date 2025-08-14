package com.company.npw.presentation.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.TopAppBar
import com.company.npw.presentation.wishlist.components.WishlistItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val wishlistState by viewModel.wishlistState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is WishlistUiEvent.ShowMessage -> {
                // Show snackbar or toast
            }
            is WishlistUiEvent.ShowError -> {
                // Show error snackbar or toast
            }
            null -> { /* No event */ }
        }
        if (uiEvent != null) {
            viewModel.onEvent(WishlistEvent.ClearUiEvent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Wishlist",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                wishlistState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                wishlistState.wishlist.isEmpty -> {
                    EmptyWishlistContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    WishlistContent(
                        wishlistState = wishlistState,
                        onProductClick = onProductClick,
                        onRemoveFromWishlist = { wishlistItemId ->
                            viewModel.onEvent(WishlistEvent.RemoveItem(wishlistItemId))
                        },
                        onAddToCart = { wishlistItem ->
                            viewModel.onEvent(WishlistEvent.AddToCart(wishlistItem))
                        }
                    )
                }
            }

            // Loading overlay for updates
            if (wishlistState.isUpdating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun EmptyWishlistContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your wishlist is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Save items you love for later",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WishlistContent(
    wishlistState: WishlistState,
    onProductClick: (String) -> Unit,
    onRemoveFromWishlist: (String) -> Unit,
    onAddToCart: (com.company.npw.domain.model.WishlistItem) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${wishlistState.wishlist.items.size} items in your wishlist",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(
            items = wishlistState.wishlist.items,
            key = { it.id }
        ) { wishlistItem ->
            WishlistItemCard(
                wishlistItem = wishlistItem,
                onProductClick = { onProductClick(wishlistItem.product.id) },
                onRemoveClick = { onRemoveFromWishlist(wishlistItem.id) },
                onAddToCartClick = { onAddToCart(wishlistItem) }
            )
        }
    }
}
