package com.dydrian.mob22.ui.auth

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.dydrian.mob22.R
import com.dydrian.mob22.core.service.AuthService
import com.dydrian.mob22.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : BaseViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    fun handleIntent(intent: AuthIntent, context: Context) {
        when (intent) {
            is AuthIntent.LoginWithGoogle -> loginWithGoogle(context)
        }
    }

    private fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            errorHandler {
                authService.login(context)
            }?.let { result ->
                if (result) {
                    _state.emit(AuthState.Success)
                } else {
                    _state.emit(AuthState.Error(R.string.login_failed.toString()))
                }
            }
        }
    }
}

sealed class AuthIntent {
    object LoginWithGoogle : AuthIntent()
}

sealed class AuthState {
    object Idle : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}