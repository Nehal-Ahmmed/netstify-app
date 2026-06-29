package com.nhbhuiyan.nestify.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser>
    
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser>
    
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    
    suspend fun linkGoogleAccount(idToken: String): Result<Unit>
    
    suspend fun unlinkGoogleAccount(): Result<Unit>
    
    suspend fun signOut()
}
