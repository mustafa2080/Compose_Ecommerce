package com.company.npw.presentation.checkout.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OrderSummarySection(
    subtotal: Double,
    shippingCost: Double,
    tax: Double,
    discount: Double,
    total: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            SummaryRow(
                label = "Subtotal",
                value = "$${String.format("%.2f", subtotal)}"
            )
            
            SummaryRow(
                label = "Shipping",
                value = if (shippingCost == 0.0) "FREE" else "$${String.format("%.2f", shippingCost)}",
                valueColor = if (shippingCost == 0.0) MaterialTheme.colorScheme.primary else null
            )
            
            if (shippingCost == 0.0 && subtotal >= 50.0) {
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
            
            if (discount > 0) {
                SummaryRow(
                    label = "Discount",
                    value = "-$${String.format("%.2f", discount)}",
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
            
            Divider()
            
            SummaryRow(
                label = "Total",
                value = "$${String.format("%.2f", total)}",
                labelStyle = MaterialTheme.typography.titleMedium,
                valueStyle = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
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
