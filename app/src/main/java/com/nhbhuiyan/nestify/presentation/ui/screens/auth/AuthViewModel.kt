package com.nhbhuiyan.nestify.presentation.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import com.nhbhuiyan.nestify.domain.model.StudentIdentity
import com.nhbhuiyan.nestify.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val currentUser = authRepository.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser)
            sessionManager.bind(currentUser.uid)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signInWithEmail(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.localizedMessage ?: "Sign In Failed")
                }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        // Client-side validation: must belong to @student.cuet.ac.bd domain (except for hardcoded super admins)
        val isCUETEmail = StudentIdentity.parse(email) != null
        val isSuperAdminEmail = email.trim().equals("nehal.cuet@gmail.com", ignoreCase = true) ||
                email.trim().equals("nehal.ahmmed.cuet@gmail.com", ignoreCase = true)

        if (!isCUETEmail && !isSuperAdminEmail) {
            _authState.value = AuthState.Error("Only @student.cuet.ac.bd emails are allowed for sign-up.")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            authRepository.signUpWithEmail(email, password)
                .onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.localizedMessage ?: "Registration Failed")
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    // Make sure Google user is also validated client side
                    val email = user.email ?: ""
                    val isCUETEmail = StudentIdentity.parse(email) != null
                    val isSuperAdminEmail = email.trim().equals("nehal.cuet@gmail.com", ignoreCase = true) ||
                            email.trim().equals("nehal.ahmmed.cuet@gmail.com", ignoreCase = true)

                    if (isCUETEmail || isSuperAdminEmail) {
                        _authState.value = AuthState.Authenticated(user)
                    } else {
                        // Sign out user if not authorized
                        authRepository.signOut()
                        _authState.value = AuthState.Error("Only CUET student accounts are authorized.")
                    }
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.localizedMessage ?: "Google Sign In Failed")
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
