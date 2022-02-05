package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData : LiveData<FirebaseUser>() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Get current FirebaseUser
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }

    // when object has active observer, start observing FirebaseAuth to check for logged in user
    override fun onActive() {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    // if object is inactive, stop observing FirebaseAuth to prevent memory leaks
    override fun onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}