package com.company.npw.presentation.home

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.company.npw.R
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Category
import com.company.npw.domain.model.Product
import com.company.npw.presentation.components.ErrorMessage
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.HomeTopAppBar
import com.company.npw.presentation.components.responsive.ResponsiveCardSize
import com.company.npw.presentation.components.responsive.ResponsivePadding
import com.company.npw.presentation.components.responsive.getGridColumns
import com.company.npw.presentation.theme.cardBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToAllProducts: () -> Unit,
    onMenuClick: () -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopAppBar(
                onMenuClick = onMenuClick,
                onSearchClick = onNavigateToSearch,
                onCartClick = onNavigateToCart,
                onNotificationsClick = onNavigateToNotifications,
                cartItemCount = uiState.cartItemCount,
                notificationCount = uiState.notificationCount
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error ?: "Unknown error",
                    modifier = Modifier.padding(paddingValues),
                    onRetry = { homeViewModel.loadHomeData() }
                )
            }
            else -> {
                HomeContent(
                    uiState = uiState,
                    onNavigateToCategory = onNavigateToCategory,
                    onNavigateToProduct = onNavigateToProduct,
                    onNavigateToAllProducts = onNavigateToAllProducts,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToAllProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = ResponsivePadding.medium()),
        verticalArrangement = Arrangement.spacedBy(ResponsivePadding.medium())
    ) {
        // Welcome Banner
        item {
            WelcomeBanner(
                modifier = Modifier.padding(horizontal = ResponsivePadding.horizontal())
            )
        }

        // Categories Section
        if (uiState.categories.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.categories),
                    onViewAllClick = { /* TODO: Navigate to categories */ },
                    modifier = Modifier.padding(horizontal = ResponsivePadding.horizontal())
                )
            }
            item {
                CategoriesRow(
                    categories = uiState.categories,
                    onCategoryClick = onNavigateToCategory
                )
            }
        }

        // Flash Sales Section
        if (uiState.flashSaleProducts.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.flash_sales),
                    onViewAllClick = onNavigateToAllProducts,
                    modifier = Modifier.padding(horizontal = ResponsivePadding.horizontal())
                )
            }
            item {
                ProductsRow(
                    products = uiState.flashSaleProducts,
                    onProductClick = onNavigateToProduct
                )
            }
        }

        // Featured Products Section
        if (uiState.featuredProducts.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.featured_products),
                    onViewAllClick = onNavigateToAllProducts,
                    modifier = Modifier.padding(horizontal = ResponsivePadding.horizontal())
                )
            }
            item {
                ProductsGrid(
                    products = uiState.featuredProducts.take(6),
                    onProductClick = onNavigateToProduct,
                    modifier = Modifier.padding(horizontal = ResponsivePadding.horizontal())
                )
            }
        }

        // Recommended Products Section
        if (uiState.recommendedProducts.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.recommended_for_you),
                    onViewAllClick = onNavigateToAllProducts,
                    modifier = Modifier.padding(horizontal = ResponsivePadding.horizontal())
                )
            }
            item {
                ProductsRow(
                    products = uiState.recommendedProducts,
                    onProductClick = onNavigateToProduct
                )
            }
        }
    }
}

@Composable
private fun WelcomeBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(ResponsiveCardSize.bannerHeight()),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to E-Commerce!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Discover amazing products at great prices",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        TextButton(onClick = onViewAllClick) {
            Text(text = stringResource(R.string.view_all))
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CategoriesRow(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = ResponsivePadding.horizontal()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
private fun ProductsRow(
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = ResponsivePadding.horizontal()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
private fun ProductsGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(getGridColumns()),
        modifier = modifier.height(600.dp), // Fixed height for home screen
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}

// Placeholder composables - will be implemented in Product Management task
@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .height(ResponsiveCardSize.categoryCardHeight()),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.cardBackground
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.cardBackground
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
