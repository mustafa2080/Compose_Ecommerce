package com.company.npw.data.repository

import com.company.npw.core.util.Resource
import com.company.npw.data.remote.firebase.auth.FirebaseAuthService
import com.company.npw.domain.model.User
import com.company.npw.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService
) : AuthRepository {

    override fun loginWithEmail(email: String, password: String): Flow<Resource<User>> {
        return firebaseAuthService.loginWithEmail(email, password)
    }

    override fun registerWithEmail(email: String, password: String, name: String): Flow<Resource<User>> {
        return firebaseAuthService.registerWithEmail(email, password, name)
    }

    override fun loginWithGoogle(idToken: String): Flow<Resource<User>> {
        return firebaseAuthService.loginWithGoogle(idToken)
    }

    override fun loginWithFacebook(accessToken: String): Flow<Resource<User>> = flow {
        // TODO: Implement Facebook login
        emit(Resource.Error("Facebook login not implemented yet"))
    }

    override fun resetPassword(email: String): Flow<Resource<String>> {
        return firebaseAuthService.resetPassword(email)
    }

    override fun logout(): Flow<Resource<String>> {
        return firebaseAuthService.logout()
    }

    override fun getCurrentUser(): Flow<Resource<User?>> = flow {
        try {
            emit(Resource.Loading<User?>())

            val currentUser = firebaseAuthService.currentUser
            if (currentUser != null) {
                val user = User(
                    id = currentUser.uid,
                    email = currentUser.email ?: "",
                    name = currentUser.displayName ?: "",
                    profileImageUrl = currentUser.photoUrl?.toString() ?: "",
                    isEmailVerified = currentUser.isEmailVerified
                )
                emit(Resource.Success<User?>(user))
            } else {
                emit(Resource.Success<User?>(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error<User?>(e.message ?: "Failed to get current user"))
        }
    }

    override val isUserLoggedIn: Boolean
        get() = firebaseAuthService.isUserLoggedIn
}
