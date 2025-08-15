package com.company.npw.domain.repository

import com.company.npw.core.util.Resource
import com.company.npw.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginWithEmail(email: String, password: String): Flow<Resource<User>>
    fun registerWithEmail(email: String, password: String, name: String): Flow<Resource<User>>
    fun loginWithGoogle(idToken: String): Flow<Resource<User>>
    fun loginWithFacebook(accessToken: String): Flow<Resource<User>>
    fun resetPassword(email: String): Flow<Resource<String>>
    fun logout(): Flow<Resource<String>>
    fun getCurrentUser(): Flow<Resource<User?>>
    val isUserLoggedIn: Boolean
}
