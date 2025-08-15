package com.company.npw.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val address: Address? = null,
    val isEmailVerified: Boolean = false,
    val role: UserRole = UserRole.USER,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences()
) : Parcelable {
    val isAdmin: Boolean
        get() = role == UserRole.ADMIN

    val isSuperAdmin: Boolean
        get() = role == UserRole.SUPER_ADMIN
}

@Parcelize
data class Address(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false
) : Parcelable

@Parcelize
data class UserPreferences(
    val isDarkMode: Boolean = false,
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true
) : Parcelable

enum class UserRole {
    USER,           // عضو عادي
    ADMIN,          // أدمن
    SUPER_ADMIN     // سوبر أدمن
}
