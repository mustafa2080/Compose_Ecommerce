package com.company.npw.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.company.npw.core.util.formatPrice
import com.company.npw.core.util.formatRating
import com.company.npw.presentation.components.ErrorMessage
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.PrimaryButton
import com.company.npw.presentation.components.SecondaryButton
import com.company.npw.presentation.components.responsive.ResponsivePadding
import com.company.npw.presentation.theme.cardBackground
import com.company.npw.presentation.theme.discountRed
import com.company.npw.presentation.theme.priceGreen
import com.company.npw.presentation.theme.ratingStar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    productDetailsViewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val uiState by productDetailsViewModel.uiState.collectAsState()
    var isInWishlist by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        productDetailsViewModel.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share product */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            uiState.product?.let { product ->
                ProductDetailsBottomBar(
                    product = product,
                    isInWishlist = isInWishlist,
                    onToggleWishlist = { 
                        isInWishlist = !isInWishlist
                        // TODO: Implement wishlist toggle
                    },
                    onAddToCart = { 
                        // TODO: Implement add to cart
                    },
                    onBuyNow = { 
                        // TODO: Implement buy now
                    }
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error ?: "Unknown error",
                    modifier = Modifier.padding(paddingValues),
                    onRetry = { productDetailsViewModel.loadProduct(productId) }
                )
            }
            uiState.product != null -> {
                val product = uiState.product
                if (product != null) {
                    ProductDetailsContent(
                        product = product,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailsContent(
    product: com.company.npw.domain.model.Product,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp) // Space for bottom bar
    ) {
        item {
            // Product Images
            ProductImageCarousel(
                images = product.images,
                hasDiscount = product.hasDiscount,
                discountPercentage = product.discountPercentage,
                isFlashSale = product.isFlashSale
            )
        }

        item {
            // Product Info
            ProductInfoSection(product = product)
        }

        item {
            // Product Description
            ProductDescriptionSection(description = product.description)
        }

        item {
            // Specifications
            if (product.specifications.isNotEmpty()) {
                ProductSpecificationsSection(specifications = product.specifications)
            }
        }

        item {
            // Return Policy
            if (product.returnPolicy.isNotEmpty()) {
                ProductPolicySection(
                    title = "Return Policy",
                    content = product.returnPolicy
                )
            }
        }

        item {
            // Shipping Info
            if (product.shippingInfo.isNotEmpty()) {
                ProductPolicySection(
                    title = "Shipping Information",
                    content = product.shippingInfo
                )
            }
        }
    }
}

@Composable
private fun ProductImageCarousel(
    images: List<String>,
    hasDiscount: Boolean,
    discountPercentage: Int,
    isFlashSale: Boolean
) {
    Box {
        if (images.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { images.size })
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Page Indicators
            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(images.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }
        
        // Badges
        if (hasDiscount) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.discountRed,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "-$discountPercentage%",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        if (isFlashSale) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        Color.Red,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "FLASH SALE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProductInfoSection(
    product: com.company.npw.domain.model.Product
) {
    Column(
        modifier = Modifier.padding(ResponsivePadding.horizontal())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product Name
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Brand
        if (product.brand.isNotEmpty()) {
            Text(
                text = "by ${product.brand}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Rating
        if (product.rating > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = MaterialTheme.colorScheme.ratingStar,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = product.rating.formatRating(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = " (${product.reviewCount} reviews)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Price
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.hasDiscount) {
                Text(
                    text = product.originalPrice.formatPrice(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = product.price.formatPrice(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.priceGreen
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stock Status
        Text(
            text = if (product.inStock) "In Stock (${product.stockQuantity} available)" else "Out of Stock",
            style = MaterialTheme.typography.bodyMedium,
            color = if (product.inStock) MaterialTheme.colorScheme.priceGreen else MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium
        )
        
        // Size and Color options
        if (product.sizes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Available Sizes:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(product.sizes) { size ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.cardBackground
                        )
                    ) {
                        Text(
                            text = size,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        if (product.colors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Available Colors:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(product.colors) { color ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.cardBackground
                        )
                    ) {
                        Text(
                            text = color,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDescriptionSection(description: String) {
    if (description.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ResponsivePadding.horizontal()),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.cardBackground
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProductSpecificationsSection(specifications: Map<String, String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ResponsivePadding.horizontal()),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Specifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            specifications.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ProductPolicySection(title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ResponsivePadding.horizontal()),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ProductDetailsBottomBar(
    product: com.company.npw.domain.model.Product,
    isInWishlist: Boolean,
    onToggleWishlist: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wishlist Button
            IconButton(
                onClick = onToggleWishlist,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = if (isInWishlist) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Add to Cart Button
            SecondaryButton(
                text = "Add to Cart",
                onClick = onAddToCart,
                modifier = Modifier.weight(1f),
                enabled = product.inStock
            )
            
            // Buy Now Button
            PrimaryButton(
                text = "Buy Now",
                onClick = onBuyNow,
                modifier = Modifier.weight(1f),
                enabled = product.inStock
            )
        }
    }
}
