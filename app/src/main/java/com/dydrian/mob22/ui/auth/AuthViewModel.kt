package com.dydrian.mob22.ui.auth

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.dydrian.mob22.core.service.AuthService
import com.dydrian.mob22.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : BaseViewModel() {
    protected val _success = MutableSharedFlow<Unit>()
    val success = _success.asSharedFlow()

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            errorHandler { authService.login(context) }?.let {
                if (it) _success.emit(Unit)
            }
        }
    }
}