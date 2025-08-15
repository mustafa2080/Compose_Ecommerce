package com.company.npw.presentation.order_tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.TopAppBar
import com.company.npw.presentation.components.CustomTopAppBar
import com.company.npw.presentation.order_tracking.components.OrderInfoCard
import com.company.npw.presentation.order_tracking.components.TrackingStepCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    onBackClick: () -> Unit = {},
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val trackingState by viewModel.trackingState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    // Load order when screen opens
    LaunchedEffect(orderId) {
        viewModel.onEvent(OrderTrackingEvent.LoadOrder(orderId))
    }

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is OrderTrackingUiEvent.ShowError -> {
                // Show error snackbar or toast
            }
            null -> { /* No event */ }
        }
        if (uiEvent != null) {
            viewModel.onEvent(OrderTrackingEvent.ClearUiEvent)
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Track Order",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(OrderTrackingEvent.RefreshOrder) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
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
                trackingState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                trackingState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = trackingState.error ?: "Unknown error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onEvent(OrderTrackingEvent.RefreshOrder) }
                        ) {
                            Text("Retry")
                        }
                    }
                }
                trackingState.order != null -> {
                    OrderTrackingContent(
                        trackingState = trackingState
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderTrackingContent(
    trackingState: OrderTrackingState
) {
    val order = trackingState.order ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order Information
        item {
            OrderInfoCard(order = order)
        }
        
        // Tracking Progress Header
        item {
            Text(
                text = "Order Progress",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Tracking Steps
        items(
            items = trackingState.trackingSteps,
            key = { it.status }
        ) { step ->
            TrackingStepCard(
                step = step,
                isLast = step == trackingState.trackingSteps.last()
            )
        }
        
        // Estimated Delivery (if available)
        if (order.estimatedDelivery > 0 && order.status.ordinal < com.company.npw.domain.model.OrderStatus.DELIVERED.ordinal) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Estimated Delivery",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Text(
                            text = formatEstimatedDelivery(order.estimatedDelivery),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
        
        // Tracking Number (if available)
        if (order.trackingNumber.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tracking Number",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = order.trackingNumber,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun formatEstimatedDelivery(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("EEEE, MMM dd, yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}
