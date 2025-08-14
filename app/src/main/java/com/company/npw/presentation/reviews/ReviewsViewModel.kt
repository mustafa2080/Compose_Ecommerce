package com.company.npw.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Review
import com.company.npw.domain.repository.AuthRepository
import com.company.npw.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _reviewsState = MutableStateFlow(ReviewsState())
    val reviewsState: StateFlow<ReviewsState> = _reviewsState.asStateFlow()

    private val _uiEvent = MutableStateFlow<ReviewsUiEvent?>(null)
    val uiEvent: StateFlow<ReviewsUiEvent?> = _uiEvent.asStateFlow()

    fun onEvent(event: ReviewsEvent) {
        when (event) {
            is ReviewsEvent.LoadReviews -> loadReviews(event.productId)
            is ReviewsEvent.SubmitReview -> submitReview(event.productId, event.review)
            is ReviewsEvent.UpdateRating -> updateRating(event.rating)
            is ReviewsEvent.UpdateComment -> updateComment(event.comment)
            is ReviewsEvent.ClearUiEvent -> clearUiEvent()
        }
    }

    private fun loadReviews(productId: String) {
        viewModelScope.launch {
            _reviewsState.value = _reviewsState.value.copy(isLoading = true)
            
            productRepository.getProductReviews(productId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _reviewsState.value = _reviewsState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        val reviews = result.data ?: emptyList()
                        val averageRating = if (reviews.isNotEmpty()) {
                            reviews.map { it.rating }.average().toFloat()
                        } else {
                            0f
                        }
                        
                        _reviewsState.value = _reviewsState.value.copy(
                            reviews = reviews,
                            averageRating = averageRating,
                            totalReviews = reviews.size,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _reviewsState.value = _reviewsState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun submitReview(productId: String, review: Review) {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { userResource ->
                when (userResource) {
                    is Resource.Success -> {
                        val userId = userResource.data?.id
                        val userName = userResource.data?.name
                        
                        if (userId != null && userName != null) {
                            _reviewsState.value = _reviewsState.value.copy(isSubmitting = true)
                            
                            val reviewToSubmit = review.copy(
                                id = "review_${System.currentTimeMillis()}_${(1000..9999).random()}",
                                userId = userId,
                                userName = userName,
                                productId = productId,
                                createdAt = System.currentTimeMillis()
                            )
                            
                            productRepository.submitReview(reviewToSubmit).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        _reviewsState.value = _reviewsState.value.copy(isSubmitting = true)
                                    }
                                    is Resource.Success -> {
                                        _reviewsState.value = _reviewsState.value.copy(
                                            isSubmitting = false,
                                            newReviewRating = 0f,
                                            newReviewComment = ""
                                        )
                                        _uiEvent.value = ReviewsUiEvent.ReviewSubmitted
                                        loadReviews(productId) // Reload reviews
                                    }
                                    is Resource.Error -> {
                                        _reviewsState.value = _reviewsState.value.copy(isSubmitting = false)
                                        _uiEvent.value = ReviewsUiEvent.ShowError(result.message ?: "Failed to submit review")
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        _uiEvent.value = ReviewsUiEvent.ShowError("Please login to submit a review")
                    }
                }
            }
        }
    }

    private fun updateRating(rating: Float) {
        _reviewsState.value = _reviewsState.value.copy(newReviewRating = rating)
    }

    private fun updateComment(comment: String) {
        _reviewsState.value = _reviewsState.value.copy(newReviewComment = comment)
    }

    private fun clearUiEvent() {
        _uiEvent.value = null
    }
}

data class ReviewsState(
    val reviews: List<Review> = emptyList(),
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val newReviewRating: Float = 0f,
    val newReviewComment: String = "",
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null
)

sealed class ReviewsEvent {
    data class LoadReviews(val productId: String) : ReviewsEvent()
    data class SubmitReview(val productId: String, val review: Review) : ReviewsEvent()
    data class UpdateRating(val rating: Float) : ReviewsEvent()
    data class UpdateComment(val comment: String) : ReviewsEvent()
    object ClearUiEvent : ReviewsEvent()
}

sealed class ReviewsUiEvent {
    data class ShowError(val message: String) : ReviewsUiEvent()
    object ReviewSubmitted : ReviewsUiEvent()
}
