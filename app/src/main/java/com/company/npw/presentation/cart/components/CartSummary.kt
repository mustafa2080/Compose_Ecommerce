package com.company.npw.presentation.cart.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.company.npw.domain.model.Cart
import com.company.npw.presentation.components.CustomButton

@Composable
fun CartSummary(
    cart: Cart,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = cart.subtotal
    val shipping = if (subtotal > 50.0) 0.0 else 5.99 // Free shipping over $50
    val tax = subtotal * 0.08 // 8% tax
    val total = subtotal + shipping + tax

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            // Summary rows
            SummaryRow(
                label = "Subtotal (${cart.totalItems} items)",
                value = "$${String.format("%.2f", subtotal)}"
            )
            
            SummaryRow(
                label = "Shipping",
                value = if (shipping == 0.0) "FREE" else "$${String.format("%.2f", shipping)}",
                valueColor = if (shipping == 0.0) MaterialTheme.colorScheme.primary else null
            )
            
            if (shipping == 0.0 && subtotal < 50.0) {
                Text(
                    text = "Free shipping on orders over $50",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            SummaryRow(
                label = "Tax",
                value = "$${String.format("%.2f", tax)}"
            )
            
            Divider()
            
            SummaryRow(
                label = "Total",
                value = "$${String.format("%.2f", total)}",
                labelStyle = MaterialTheme.typography.titleMedium,
                valueStyle = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CustomButton(
                text = "Proceed to Checkout",
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight? = null,
    valueColor: androidx.compose.ui.graphics.Color? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = labelStyle,
            fontWeight = fontWeight
        )
        
        Text(
            text = value,
            style = valueStyle,
            fontWeight = fontWeight,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}
