package com.dydrian.mob22.core.service

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthService {
    suspend fun login(context: Context): Boolean
    fun logout()
    fun getLoggedInUser(): FirebaseUser?
    fun getUid(): String?
}