package com.company.npw.presentation.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.company.npw.domain.model.Review
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.TopAppBar
import com.company.npw.presentation.reviews.components.ReviewCard
import com.company.npw.presentation.reviews.components.ReviewSummary
import com.company.npw.presentation.reviews.components.WriteReviewSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    productId: String,
    onBackClick: () -> Unit = {},
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val reviewsState by viewModel.reviewsState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    // Load reviews when screen opens
    LaunchedEffect(productId) {
        viewModel.onEvent(ReviewsEvent.LoadReviews(productId))
    }

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is ReviewsUiEvent.ShowError -> {
                // Show error snackbar or toast
            }
            is ReviewsUiEvent.ReviewSubmitted -> {
                // Show success message
            }
            null -> { /* No event */ }
        }
        if (uiEvent != null) {
            viewModel.onEvent(ReviewsEvent.ClearUiEvent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Reviews & Ratings",
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
                reviewsState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    ReviewsContent(
                        reviewsState = reviewsState,
                        productId = productId,
                        onRatingChanged = { rating ->
                            viewModel.onEvent(ReviewsEvent.UpdateRating(rating))
                        },
                        onCommentChanged = { comment ->
                            viewModel.onEvent(ReviewsEvent.UpdateComment(comment))
                        },
                        onSubmitReview = {
                            val review = Review(
                                rating = reviewsState.newReviewRating,
                                comment = reviewsState.newReviewComment
                            )
                            viewModel.onEvent(ReviewsEvent.SubmitReview(productId, review))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewsContent(
    reviewsState: ReviewsState,
    productId: String,
    onRatingChanged: (Float) -> Unit,
    onCommentChanged: (String) -> Unit,
    onSubmitReview: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Review Summary
        item {
            ReviewSummary(
                averageRating = reviewsState.averageRating,
                totalReviews = reviewsState.totalReviews,
                reviews = reviewsState.reviews
            )
        }
        
        // Write Review Section
        item {
            WriteReviewSection(
                rating = reviewsState.newReviewRating,
                comment = reviewsState.newReviewComment,
                isSubmitting = reviewsState.isSubmitting,
                onRatingChanged = onRatingChanged,
                onCommentChanged = onCommentChanged,
                onSubmitReview = onSubmitReview
            )
        }
        
        // Reviews List Header
        if (reviewsState.reviews.isNotEmpty()) {
            item {
                Text(
                    text = "Customer Reviews",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Individual Reviews
            items(
                items = reviewsState.reviews,
                key = { it.id }
            ) { review ->
                ReviewCard(review = review)
            }
        } else if (!reviewsState.isLoading) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "No reviews yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Be the first to review this product",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}
