package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import android.content.Context
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import androidx.preference.PreferenceManager

class AuthViewModel: ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authState = FirebaseUserLiveData().map { user ->
        if (user != null){
            AuthenticationState.AUTHENTICATED
        }else{
            AuthenticationState.UNAUTHENTICATED
        }
    }


}