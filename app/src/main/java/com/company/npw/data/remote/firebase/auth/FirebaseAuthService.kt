package com.company.npw.data.remote.firebase.auth

import android.util.Log
import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.company.npw.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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
            Log.d("FirebaseAuth", "Starting login for email: $email")
            emit(Resource.Loading())

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                Log.d("FirebaseAuth", "Login successful for user: ${firebaseUser.uid}")
                val user = getUserFromFirebase(firebaseUser.uid)
                Log.d("FirebaseAuth", "User data retrieved: $user")
                emit(Resource.Success(user))
            } else {
                Log.e("FirebaseAuth", "Login failed: No user returned")
                emit(Resource.Error(Constants.ERROR_USER_NOT_FOUND))
            }
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuth", "Firebase Auth Error: ${e.errorCode} - ${e.message}")
            val errorMessage = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Invalid email address"
                "ERROR_WRONG_PASSWORD" -> "Wrong password"
                "ERROR_USER_NOT_FOUND" -> "No account found with this email"
                "ERROR_USER_DISABLED" -> "This account has been disabled"
                "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Try again later"
                "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Check your connection"
                else -> e.message ?: "Login failed"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "General Error: ${e.message}", e)
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        name: String
    ): Flow<Resource<User>> = flow {
        try {
            Log.d("FirebaseAuth", "Starting registration for email: $email")
            emit(Resource.Loading())

            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                Log.d("FirebaseAuth", "Registration successful for user: ${firebaseUser.uid}")

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

                Log.d("FirebaseAuth", "Saving user to database: $user")
                saveUserToDatabase(user)
                emit(Resource.Success(user))
            } else {
                Log.e("FirebaseAuth", "Registration failed: No user returned")
                emit(Resource.Error(Constants.ERROR_GENERIC))
            }
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuth", "Firebase Auth Error: ${e.errorCode} - ${e.message}")
            val errorMessage = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Invalid email address"
                "ERROR_WEAK_PASSWORD" -> "Password is too weak"
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Email is already registered"
                "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Check your connection"
                else -> e.message ?: "Registration failed"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "General Error: ${e.message}", e)
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
            Log.d("FirebaseAuth", "Getting user data from database for: $userId")
            val snapshot = firebaseDatabase.reference
                .child(Constants.USERS_COLLECTION)
                .child(userId)
                .get()
                .await()

            val user = snapshot.getValue(User::class.java) ?: User(id = userId)
            Log.d("FirebaseAuth", "Retrieved user from database: $user")
            user
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error getting user from database: ${e.message}", e)
            // Return basic user info from Firebase Auth
            val firebaseUser = firebaseAuth.currentUser
            User(
                id = userId,
                email = firebaseUser?.email ?: "",
                name = firebaseUser?.displayName ?: "",
                profileImageUrl = firebaseUser?.photoUrl?.toString() ?: "",
                isEmailVerified = firebaseUser?.isEmailVerified ?: false
            )
        }
    }

    private suspend fun saveUserToDatabase(user: User) {
        try {
            Log.d("FirebaseAuth", "Saving user to database: $user")
            firebaseDatabase.reference
                .child(Constants.USERS_COLLECTION)
                .child(user.id)
                .setValue(user)
                .await()
            Log.d("FirebaseAuth", "User saved to database successfully")
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error saving user to database: ${e.message}", e)
            // Don't throw error, just log it
        }
    }
}
