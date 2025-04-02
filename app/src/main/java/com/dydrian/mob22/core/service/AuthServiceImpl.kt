package com.dydrian.mob22.core.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.dydrian.mob22.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AuthServiceImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthService {

    override suspend fun login(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val idToken = getGoogleCredential(context)
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                authResult.user != null
            } catch (e: GetCredentialException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.google_signIn_failed, Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            }
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getLoggedInUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun getUid(): String? {
        return firebaseAuth.uid
    }

    private suspend fun getGoogleCredential(context: Context): String? {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption
            .Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("7872994357-98ddpk4ngcs5i05l43h2e3jdti9bla2v.apps.googleusercontent.com")
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.auth_credentials_error, Toast.LENGTH_SHORT).show()
                null
            }
        }
    }
}