package com.company.npw.presentation.order_tracking.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.company.npw.domain.model.OrderStatus
import com.company.npw.presentation.order_tracking.TrackingStep
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackingStepCard(
    step: TrackingStep,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status icon
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        step.isActive -> MaterialTheme.colorScheme.primary
                        step.isCompleted -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    }
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getStatusIcon(step.status),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = when {
                            step.isActive -> MaterialTheme.colorScheme.onPrimary
                            step.isCompleted -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }
            
            // Connecting line
            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                ) {
                    val color = if (step.isCompleted) 
                        Color(0xFF4CAF50) 
                    else 
                        Color(0xFFE0E0E0)
                    
                    drawLine(
                        color = color,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = size.width
                    )
                }
            }
        }
        
        // Step content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!isLast) 40.dp else 0.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (step.isActive || step.isCompleted) 
                    MaterialTheme.colorScheme.onSurface 
                else 
                    MaterialTheme.colorScheme.outline
            )
            
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            
            if (step.timestamp > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(step.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            // Additional info for active step
            if (step.isActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "Current Status",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun getStatusIcon(status: OrderStatus): ImageVector {
    return when (status) {
        OrderStatus.PENDING -> Icons.Default.Schedule
        OrderStatus.CONFIRMED -> Icons.Default.CheckCircle
        OrderStatus.PROCESSING -> Icons.Default.Build
        OrderStatus.SHIPPED -> Icons.Default.LocalShipping
        OrderStatus.OUT_FOR_DELIVERY -> Icons.Default.DeliveryDining
        OrderStatus.DELIVERED -> Icons.Default.Home
        OrderStatus.CANCELLED -> Icons.Default.Cancel
        OrderStatus.RETURNED -> Icons.Default.Undo
        OrderStatus.REFUNDED -> Icons.Default.AttachMoney
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return formatter.format(date)
}
