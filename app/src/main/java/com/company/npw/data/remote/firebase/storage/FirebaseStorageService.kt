package com.company.npw.data.remote.firebase.storage

import android.net.Uri
import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageService @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {
    
    suspend fun uploadImage(
        imageUri: Uri,
        path: String,
        fileName: String? = null
    ): Resource<String> {
        return try {
            val finalFileName = fileName ?: "${UUID.randomUUID()}.jpg"
            val storageRef = firebaseStorage.reference
                .child(path)
                .child(finalFileName)
            
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            
            Resource.Success(downloadUrl.toString())
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_GENERIC)
        }
    }
    
    suspend fun uploadProductImage(imageUri: Uri, productId: String): Resource<String> {
        val fileName = "${productId}_${System.currentTimeMillis()}.jpg"
        return uploadImage(imageUri, Constants.PRODUCT_IMAGES_PATH, fileName)
    }
    
    suspend fun uploadUserAvatar(imageUri: Uri, userId: String): Resource<String> {
        val fileName = "${userId}_avatar.jpg"
        return uploadImage(imageUri, Constants.USER_AVATARS_PATH, fileName)
    }
    
    suspend fun uploadReviewImage(imageUri: Uri, reviewId: String): Resource<String> {
        val fileName = "${reviewId}_${System.currentTimeMillis()}.jpg"
        return uploadImage(imageUri, Constants.REVIEW_IMAGES_PATH, fileName)
    }
    
    suspend fun uploadBannerImage(imageUri: Uri, bannerId: String): Resource<String> {
        val fileName = "${bannerId}_banner.jpg"
        return uploadImage(imageUri, Constants.BANNER_IMAGES_PATH, fileName)
    }
    
    suspend fun deleteImage(imageUrl: String): Resource<String> {
        return try {
            val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
            Resource.Success("Image deleted successfully")
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_GENERIC)
        }
    }
    
    suspend fun uploadMultipleImages(
        imageUris: List<Uri>,
        path: String
    ): Resource<List<String>> {
        return try {
            val downloadUrls = mutableListOf<String>()
            
            for (imageUri in imageUris) {
                val fileName = "${UUID.randomUUID()}.jpg"
                val result = uploadImage(imageUri, path, fileName)
                
                when (result) {
                    is Resource.Success -> downloadUrls.add(result.data ?: "")
                    is Resource.Error -> return Resource.Error(result.message ?: Constants.ERROR_GENERIC)
                    is Resource.Loading -> { /* Continue */ }
                }
            }
            
            Resource.Success(downloadUrls)
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_GENERIC)
        }
    }
}
