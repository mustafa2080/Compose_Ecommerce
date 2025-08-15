package com.company.npw.data.repository

import android.net.Uri
import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.company.npw.data.remote.firebase.database.FirebaseDatabaseService
import com.company.npw.data.remote.firebase.storage.FirebaseStorageService
import com.company.npw.domain.model.User
import com.company.npw.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val databaseService: FirebaseDatabaseService,
    private val storageService: FirebaseStorageService
) : UserRepository {

    override fun getUserById(userId: String): Flow<Resource<User>> {
        return databaseService.getDocument(
            collection = Constants.USERS_COLLECTION,
            documentId = userId,
            clazz = User::class.java
        ).let { flow ->
            flow { 
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val user = resource.data
                            if (user != null) {
                                emit(Resource.Success(user))
                            } else {
                                emit(Resource.Error("User not found"))
                            }
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun updateUser(user: User): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val updatedUser = user.copy(updatedAt = System.currentTimeMillis())
            val result = databaseService.setDocument(
                collection = Constants.USERS_COLLECTION,
                documentId = user.id,
                data = updatedUser
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success("User updated successfully"))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun updateUserProfile(
        userId: String, 
        name: String, 
        phoneNumber: String
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val updates = mapOf(
                "name" to name,
                "phoneNumber" to phoneNumber,
                "updatedAt" to System.currentTimeMillis()
            )
            
            val result = databaseService.updateDocument(
                collection = Constants.USERS_COLLECTION,
                documentId = userId,
                updates = updates
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success("Profile updated successfully"))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun uploadProfileImage(userId: String, imageUri: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val uri = Uri.parse(imageUri)
            val uploadResult = storageService.uploadUserAvatar(uri, userId)
            
            when (uploadResult) {
                is Resource.Success -> {
                    val imageUrl = uploadResult.data ?: ""
                    
                    // Update user document with new profile image URL
                    val updates = mapOf(
                        "profileImageUrl" to imageUrl,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    
                    val updateResult = databaseService.updateDocument(
                        collection = Constants.USERS_COLLECTION,
                        documentId = userId,
                        updates = updates
                    )
                    
                    when (updateResult) {
                        is Resource.Success -> emit(Resource.Success(imageUrl))
                        is Resource.Error -> emit(Resource.Error(updateResult.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> { /* Already emitted loading */ }
                    }
                }
                is Resource.Error -> emit(Resource.Error(uploadResult.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun deleteUser(userId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val result = databaseService.deleteDocument(
                collection = Constants.USERS_COLLECTION,
                documentId = userId
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success("User deleted successfully"))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }
}
