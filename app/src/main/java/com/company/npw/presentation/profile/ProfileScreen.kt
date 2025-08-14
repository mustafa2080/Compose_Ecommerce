package com.company.npw.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.company.npw.presentation.components.LoadingIndicator
import com.company.npw.presentation.components.TopAppBar
import com.company.npw.presentation.profile.components.ProfileMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onOrderHistoryClick: () -> Unit = {},
    onAddressesClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is ProfileUiEvent.ShowMessage -> {
                // Show snackbar or toast
            }
            is ProfileUiEvent.ShowError -> {
                // Show error snackbar or toast
            }
            is ProfileUiEvent.NavigateToLogin -> {
                onLoginClick()
            }
            is ProfileUiEvent.NavigateToEditProfile -> {
                onEditProfileClick()
            }
            is ProfileUiEvent.NavigateToOrderHistory -> {
                onOrderHistoryClick()
            }
            is ProfileUiEvent.NavigateToAddresses -> {
                onAddressesClick()
            }
            is ProfileUiEvent.NavigateToSettings -> {
                onSettingsClick()
            }
            null -> { /* No event */ }
        }
        if (uiEvent != null) {
            viewModel.onEvent(ProfileEvent.ClearUiEvent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Profile",
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
                profileState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                profileState.user != null -> {
                    ProfileContent(
                        profileState = profileState,
                        onEditProfileClick = { viewModel.onEvent(ProfileEvent.NavigateToEditProfile) },
                        onOrderHistoryClick = { viewModel.onEvent(ProfileEvent.NavigateToOrderHistory) },
                        onAddressesClick = { viewModel.onEvent(ProfileEvent.NavigateToAddresses) },
                        onSettingsClick = { viewModel.onEvent(ProfileEvent.NavigateToSettings) },
                        onLogoutClick = { viewModel.onEvent(ProfileEvent.Logout) }
                    )
                }
                else -> {
                    // Error state or user not logged in
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Please log in to view your profile",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onLoginClick) {
                            Text("Log In")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profileState: ProfileState,
    onEditProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val user = profileState.user ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // User Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image
                    AsyncImage(
                        model = user.profileImageUrl.ifEmpty { null },
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    
                    if (user.phoneNumber.isNotEmpty()) {
                        Text(
                            text = user.phoneNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = onEditProfileClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Profile")
                    }
                }
            }
        }
        
        item {
            // Menu Items
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.ShoppingBag,
                        title = "Order History",
                        subtitle = "View your past orders",
                        onClick = onOrderHistoryClick
                    )
                    
                    Divider()
                    
                    ProfileMenuItem(
                        icon = Icons.Default.LocationOn,
                        title = "Addresses",
                        subtitle = "Manage delivery addresses",
                        onClick = onAddressesClick
                    )
                    
                    Divider()
                    
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        subtitle = "App preferences and notifications",
                        onClick = onSettingsClick
                    )
                    
                    Divider()
                    
                    ProfileMenuItem(
                        icon = Icons.Default.ExitToApp,
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        onClick = onLogoutClick,
                        isDestructive = true
                    )
                }
            }
        }
    }
}
