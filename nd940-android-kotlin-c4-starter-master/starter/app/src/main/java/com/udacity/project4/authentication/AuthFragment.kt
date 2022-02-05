package com.udacity.project4.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding

class AuthFragment : Fragment() {

    companion object {
        const val TAG = "AuthFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private val viewModel by viewModels<AuthViewModel>()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<ActivityAuthenticationBinding>(
            inflater, R.layout.activity_authentication, container, false
        )

        binding.loginButton.setOnClickListener { launchSignInFlow() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // if user presses the back button, bring them back to home screen
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.mainActivity, false)
        }

        // observe the auth state. If logged in, send user to RemindActivity. Else display error message
        viewModel.authState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                AuthViewModel.AuthenticationState.AUTHENTICATED -> navController.navigate(R.id.action_authFragment_to_reminderListFragment)
                else -> Log.e(TAG, "Unauthenticated or Illegal Authentication user ($authenticationState)")
            }
        })

    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
            SIGN_IN_RESULT_CODE
        )
    }
}