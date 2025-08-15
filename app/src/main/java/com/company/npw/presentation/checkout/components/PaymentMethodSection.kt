package com.company.npw.presentation.checkout.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.company.npw.domain.model.PaymentMethod
import com.company.npw.domain.model.PaymentType

@Composable
fun PaymentMethodSection(
    selectedPaymentMethod: PaymentMethod?,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPaymentDialog by remember { mutableStateOf(false) }
    
    // Sample payment methods - in real app, these would come from user's saved payment methods
    val samplePaymentMethods = remember {
        listOf(
            PaymentMethod(
                id = "1",
                type = PaymentType.CREDIT_CARD,
                cardNumber = "**** **** **** 1234",
                cardHolderName = "John Doe",
                expiryMonth = 12,
                expiryYear = 2025,
                isDefault = true
            ),
            PaymentMethod(
                id = "2",
                type = PaymentType.DEBIT_CARD,
                cardNumber = "**** **** **** 5678",
                cardHolderName = "John Doe",
                expiryMonth = 8,
                expiryYear = 2026,
                isDefault = false
            ),
            PaymentMethod(
                id = "3",
                type = PaymentType.GOOGLE_PAY,
                cardNumber = "",
                cardHolderName = "Google Pay",
                isDefault = false
            ),
            PaymentMethod(
                id = "4",
                type = PaymentType.CASH_ON_DELIVERY,
                cardNumber = "",
                cardHolderName = "Cash on Delivery",
                isDefault = false
            )
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(
                    onClick = { showPaymentDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (selectedPaymentMethod != null) {
                PaymentMethodCard(
                    paymentMethod = selectedPaymentMethod,
                    isSelected = true,
                    onClick = { showPaymentDialog = true }
                )
            } else {
                OutlinedButton(
                    onClick = { showPaymentDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Payment Method")
                }
            }
        }
    }
    
    // Payment Method Selection Dialog
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = {
                Text("Select Payment Method")
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(samplePaymentMethods) { paymentMethod ->
                        PaymentMethodCard(
                            paymentMethod = paymentMethod,
                            isSelected = paymentMethod == selectedPaymentMethod,
                            onClick = {
                                onPaymentMethodSelected(paymentMethod)
                                showPaymentDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPaymentDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CreditCard,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (paymentMethod.type) {
                        PaymentType.CREDIT_CARD -> "Credit Card"
                        PaymentType.DEBIT_CARD -> "Debit Card"
                        PaymentType.GOOGLE_PAY -> "Google Pay"
                        PaymentType.APPLE_PAY -> "Apple Pay"
                        PaymentType.PAYPAL -> "PayPal"
                        PaymentType.CASH_ON_DELIVERY -> "Cash on Delivery"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (paymentMethod.cardNumber.isNotEmpty()) {
                    Text(
                        text = paymentMethod.cardNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                if (paymentMethod.isDefault) {
                    Text(
                        text = "Default",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
