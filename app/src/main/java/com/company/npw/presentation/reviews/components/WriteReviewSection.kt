package com.company.npw.presentation.reviews.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.company.npw.presentation.components.CustomButton
import com.company.npw.presentation.theme.ratingStar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewSection(
    rating: Float,
    comment: String,
    isSubmitting: Boolean,
    onRatingChanged: (Float) -> Unit,
    onCommentChanged: (String) -> Unit,
    onSubmitReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Write a Review",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (!isExpanded) {
                    TextButton(
                        onClick = { isExpanded = true }
                    ) {
                        Text("Write Review")
                    }
                }
            }
            
            if (isExpanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Rating selection
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Rating",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(5) { index ->
                                val starRating = index + 1
                                Icon(
                                    imageVector = if (starRating <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Rate $starRating stars",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { onRatingChanged(starRating.toFloat()) },
                                    tint = if (starRating <= rating) 
                                        MaterialTheme.colorScheme.ratingStar 
                                    else 
                                        MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        
                        if (rating > 0) {
                            Text(
                                text = getRatingText(rating.toInt()),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Comment input
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Review (Optional)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        
                        OutlinedTextField(
                            value = comment,
                            onValueChange = onCommentChanged,
                            placeholder = { Text("Share your experience with this product...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                isExpanded = false
                                onRatingChanged(0f)
                                onCommentChanged("")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        CustomButton(
                            text = if (isSubmitting) "Submitting..." else "Submit Review",
                            onClick = onSubmitReview,
                            enabled = rating > 0 && !isSubmitting,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

private fun getRatingText(rating: Int): String {
    return when (rating) {
        1 -> "Poor"
        2 -> "Fair"
        3 -> "Good"
        4 -> "Very Good"
        5 -> "Excellent"
        else -> ""
    }
}
