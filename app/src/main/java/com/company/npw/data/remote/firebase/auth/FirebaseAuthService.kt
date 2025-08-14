package com.company.npw.data.remote.firebase.auth

import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isUserLoggedIn: Boolean
        get() = currentUser != null

    fun loginWithEmail(email: String, password: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                val user = getUserFromFirebase(firebaseUser.uid)
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error(Constants.ERROR_USER_NOT_FOUND))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    fun registerWithEmail(
        email: String, 
        password: String, 
        name: String
    ): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                // Update display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
                
                // Create user document in database
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    name = name,
                    isEmailVerified = firebaseUser.isEmailVerified
                )
                
                saveUserToDatabase(user)
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error(Constants.ERROR_GENERIC))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    fun loginWithGoogle(idToken: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: "",
                    profileImageUrl = firebaseUser.photoUrl?.toString() ?: "",
                    isEmailVerified = firebaseUser.isEmailVerified
                )
                
                saveUserToDatabase(user)
                emit(Resource.Success(user))
            } else {
                emit(Resource.Error(Constants.ERROR_GENERIC))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    fun resetPassword(email: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(Constants.SUCCESS_PASSWORD_RESET))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    fun logout(): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            firebaseAuth.signOut()
            emit(Resource.Success("Logged out successfully"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    private suspend fun getUserFromFirebase(userId: String): User {
        return try {
            val snapshot = firebaseDatabase.reference
                .child(Constants.USERS_COLLECTION)
                .child(userId)
                .get()
                .await()
            
            snapshot.getValue(User::class.java) ?: User(id = userId)
        } catch (e: Exception) {
            User(id = userId)
        }
    }

    private suspend fun saveUserToDatabase(user: User) {
        try {
            firebaseDatabase.reference
                .child(Constants.USERS_COLLECTION)
                .child(user.id)
                .setValue(user)
                .await()
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }
}
