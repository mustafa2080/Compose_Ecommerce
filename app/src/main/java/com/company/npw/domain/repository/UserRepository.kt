package com.company.npw.domain.repository

import com.company.npw.core.util.Resource
import com.company.npw.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserById(userId: String): Flow<Resource<User>>
    fun updateUser(user: User): Flow<Resource<String>>
    fun updateUserProfile(userId: String, name: String, phoneNumber: String): Flow<Resource<String>>
    fun uploadProfileImage(userId: String, imageUri: String): Flow<Resource<String>>
    fun deleteUser(userId: String): Flow<Resource<String>>
}
