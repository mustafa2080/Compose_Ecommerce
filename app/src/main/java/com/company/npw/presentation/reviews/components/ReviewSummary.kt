package com.company.npw.presentation.reviews.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.company.npw.domain.model.Review
import com.company.npw.presentation.theme.ratingStar

@Composable
fun ReviewSummary(
    averageRating: Float,
    totalReviews: Int,
    reviews: List<Review>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Customer Reviews",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Average rating display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (averageRating > 0) String.format("%.1f", averageRating) else "0.0",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    RatingStars(
                        rating = averageRating,
                        starSize = 20.dp
                    )
                    
                    Text(
                        text = "$totalReviews reviews",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                // Rating breakdown
                if (reviews.isNotEmpty()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(5) { starIndex ->
                            val starRating = 5 - starIndex
                            val count = reviews.count { it.rating.toInt() == starRating }
                            val percentage = if (totalReviews > 0) (count.toFloat() / totalReviews) else 0f
                            
                            RatingBreakdownRow(
                                stars = starRating,
                                count = count,
                                percentage = percentage
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBreakdownRow(
    stars: Int,
    count: Int,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$stars",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(12.dp)
        )
        
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.ratingStar
        )
        
        LinearProgressIndicator(
            progress = percentage,
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.width(20.dp)
        )
    }
}

@Composable
private fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: androidx.compose.ui.unit.Dp = 16.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(starSize),
                tint = if (index < rating.toInt()) 
                    MaterialTheme.colorScheme.ratingStar 
                else 
                    MaterialTheme.colorScheme.outline
            )
        }
    }
}
