package com.company.npw.presentation.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.npw.presentation.cart.components.CartItemCard
import com.company.npw.presentation.cart.components.CartSummary
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {},
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartState by viewModel.cartState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()
    val context = LocalContext.current

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is CartUiEvent.ShowMessage -> {
                // Show snackbar or toast
            }
            is CartUiEvent.ShowError -> {
                // Show error snackbar or toast
            }
            is CartUiEvent.NavigateToCheckout -> {
                onCheckoutClick()
            }
            null -> { /* No event */ }
        }
        if (uiEvent != null) {
            viewModel.onEvent(CartEvent.ClearUiEvent)
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Shopping Cart",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onBackClick,
                actions = {
                    if (cartState.cart.items.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onEvent(CartEvent.ClearCart) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Cart"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                cartState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                cartState.cart.isEmpty -> {
                    EmptyCartContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    CartContent(
                        cartState = cartState,
                        onQuantityChange = { cartItem, quantity ->
                            viewModel.onEvent(CartEvent.UpdateQuantity(cartItem, quantity))
                        },
                        onRemoveItem = { cartItemId ->
                            viewModel.onEvent(CartEvent.RemoveItem(cartItemId))
                        },
                        onCheckoutClick = {
                            viewModel.onEvent(CartEvent.ProceedToCheckout)
                        }
                    )
                }
            }

            // Loading overlay for updates
            if (cartState.isUpdating) {
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
private fun EmptyCartContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Add some items to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CartContent(
    cartState: CartState,
    onQuantityChange: (cartItem: com.company.npw.domain.model.CartItem, quantity: Int) -> Unit,
    onRemoveItem: (cartItemId: String) -> Unit,
    onCheckoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cart items list
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = cartState.cart.items,
                key = { it.id }
            ) { cartItem ->
                CartItemCard(
                    cartItem = cartItem,
                    onQuantityChange = { quantity ->
                        onQuantityChange(cartItem, quantity)
                    },
                    onRemoveClick = {
                        onRemoveItem(cartItem.id)
                    }
                )
            }
        }
        
        // Cart summary and checkout button
        CartSummary(
            cart = cartState.cart,
            onCheckoutClick = onCheckoutClick,
            modifier = Modifier.padding(16.dp)
        )
    }
}
