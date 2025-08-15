package com.company.npw.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.company.npw.R
import com.company.npw.domain.model.User
import com.company.npw.presentation.theme.GradientEnd
import com.company.npw.presentation.theme.GradientStart

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0,
    val isAdmin: Boolean = false
)

@Composable
fun NavigationDrawerContent(
    currentUser: User?,
    selectedRoute: String,
    onMenuItemClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = remember {
        listOf(
            DrawerMenuItem("Home", Icons.Default.Home, "home"),
            DrawerMenuItem("Categories", Icons.Default.Category, "categories"),
            DrawerMenuItem("My Orders", Icons.Default.ShoppingBag, "order_history"),
            DrawerMenuItem("Wishlist", Icons.Default.Favorite, "wishlist"),
            DrawerMenuItem("Cart", Icons.Default.ShoppingCart, "cart"),
            DrawerMenuItem("Notifications", Icons.Default.Notifications, "notifications"),
            DrawerMenuItem("Search", Icons.Default.Search, "search"),
            DrawerMenuItem("Settings", Icons.Default.Settings, "settings"),
            DrawerMenuItem("Help & Support", Icons.Default.Help, "help"),
            DrawerMenuItem("Contact Us", Icons.Default.ContactSupport, "contact_us"),
            DrawerMenuItem("About", Icons.Default.Info, "about"),
            // Admin items
            DrawerMenuItem("Admin Dashboard", Icons.Default.Dashboard, "admin_dashboard", isAdmin = true),
            DrawerMenuItem("Manage Products", Icons.Default.Inventory, "admin_products", isAdmin = true),
            DrawerMenuItem("Manage Orders", Icons.Default.Assignment, "admin_orders", isAdmin = true),
            DrawerMenuItem("Analytics", Icons.Default.Analytics, "admin_analytics", isAdmin = true),
        )
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header with user info
        DrawerHeader(
            user = currentUser,
            onProfileClick = onProfileClick
        )
        
        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        
        // Menu items
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Regular menu items
            items(menuItems.filter { !it.isAdmin }) { item ->
                DrawerMenuItem(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onMenuItemClick(item.route) }
                )
            }
            
            // Admin section
            if (currentUser?.isAdmin == true || currentUser?.isSuperAdmin == true) {
                item {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "Admin",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                items(menuItems.filter { it.isAdmin }) { item ->
                    DrawerMenuItem(
                        item = item,
                        isSelected = selectedRoute == item.route,
                        onClick = { onMenuItemClick(item.route) }
                    )
                }
            }
        }
        
        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        
        // Logout button
        DrawerMenuItem(
            item = DrawerMenuItem("Logout", Icons.Default.Logout, "logout"),
            isSelected = false,
            onClick = onLogoutClick,
            isLogout = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerHeader(
    user: User?,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .clickable { onProfileClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Profile image
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                if (user?.profileImageUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = user.profileImageUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default profile",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // User name
            Text(
                text = user?.name ?: "Guest User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            // User email
            Text(
                text = user?.email ?: "guest@example.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            
            // User role badge
            if (user?.isAdmin == true || user?.isSuperAdmin == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (user.isSuperAdmin) "Super Admin" else "Admin",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    item: DrawerMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    isLogout: Boolean = false
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        androidx.compose.ui.graphics.Color.Transparent
    }
    
    val contentColor = when {
        isLogout -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            
            // Badge for notifications, cart, etc.
            if (item.badgeCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
            
            // Arrow for selected item
            if (isSelected && !isLogout) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
