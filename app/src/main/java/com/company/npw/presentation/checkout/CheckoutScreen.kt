package com.company.npw.presentation.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.npw.presentation.checkout.components.*
import com.company.npw.presentation.components.CustomButton
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit = {},
    onOrderPlaced: (String) -> Unit = {},
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val checkoutState by viewModel.checkoutState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is CheckoutUiEvent.ShowMessage -> {
                // Show snackbar or toast
            }
            is CheckoutUiEvent.ShowError -> {
                // Show error snackbar or toast
            }
            is CheckoutUiEvent.OrderPlaced -> {
                onOrderPlaced(event.orderId)
            }
            null -> { /* No event */ }
        }
        if (uiEvent != null) {
            viewModel.onEvent(CheckoutEvent.ClearUiEvent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Checkout",
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
                checkoutState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                checkoutState.cart.isEmpty -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your cart is empty",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBackClick) {
                            Text("Continue Shopping")
                        }
                    }
                }
                else -> {
                    CheckoutContent(
                        checkoutState = checkoutState,
                        onAddressSelected = { address ->
                            viewModel.onEvent(CheckoutEvent.SelectAddress(address))
                        },
                        onPaymentMethodSelected = { paymentMethod ->
                            viewModel.onEvent(CheckoutEvent.SelectPaymentMethod(paymentMethod))
                        },
                        onCouponApplied = { couponCode ->
                            viewModel.onEvent(CheckoutEvent.ApplyCoupon(couponCode))
                        },
                        onPlaceOrder = {
                            viewModel.onEvent(CheckoutEvent.PlaceOrder)
                        }
                    )
                }
            }

            // Loading overlay for order placement
            if (checkoutState.isPlacingOrder) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Placing your order...",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutContent(
    checkoutState: CheckoutState,
    onAddressSelected: (com.company.npw.domain.model.Address) -> Unit,
    onPaymentMethodSelected: (com.company.npw.domain.model.PaymentMethod) -> Unit,
    onCouponApplied: (String) -> Unit,
    onPlaceOrder: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Items
            item {
                OrderItemsSection(
                    cartItems = checkoutState.cart.items
                )
            }
            
            // Delivery Address
            item {
                AddressSection(
                    selectedAddress = checkoutState.selectedAddress,
                    onAddressSelected = onAddressSelected
                )
            }
            
            // Payment Method
            item {
                PaymentMethodSection(
                    selectedPaymentMethod = checkoutState.selectedPaymentMethod,
                    onPaymentMethodSelected = onPaymentMethodSelected
                )
            }
            
            // Coupon Code
            item {
                CouponSection(
                    appliedCoupon = checkoutState.appliedCoupon,
                    isApplying = checkoutState.isApplyingCoupon,
                    onCouponApplied = onCouponApplied
                )
            }
            
            // Order Summary
            item {
                OrderSummarySection(
                    subtotal = checkoutState.subtotal,
                    shippingCost = checkoutState.shippingCost,
                    tax = checkoutState.tax,
                    discount = checkoutState.discount,
                    total = checkoutState.total
                )
            }
        }
        
        // Place Order Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.2f", checkoutState.total)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    CustomButton(
                        text = "Place Order",
                        onClick = onPlaceOrder,
                        enabled = checkoutState.selectedAddress != null && 
                                checkoutState.selectedPaymentMethod != null,
                        leadingIcon = Icons.Default.Payment
                    )
                }
            }
        }
    }
}
