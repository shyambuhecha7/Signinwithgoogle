package com.app.signinwithgoogle

import android.content.Intent
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var mCredentialManager: CredentialManager
    private lateinit var signIn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.activity_main)

        signIn = findViewById<Button>(R.id.btnSignIn)

        mCredentialManager = CredentialManager.create(this)

        signIn.setOnClickListener {
            signInWithGmail()
        }

    }

    fun signInWithGmail() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("646142665206-lgrdosvqdktjejc1alkm29g55ehpguat.apps.googleusercontent.com")
            .setNonce(generateNonce(32))
            .setAutoSelectEnabled(false)
            .build()

        val request: androidx.credentials.GetCredentialRequest =
            androidx.credentials.GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption).build()

        val cancellationSignal = CancellationSignal()

        if (mCredentialManager != null) {
            mCredentialManager.getCredentialAsync(
                this,
                request,
                cancellationSignal,
                Executors.newSingleThreadExecutor(),
                object : CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {


                    override fun onResult(result: GetCredentialResponse) {

                        result.credential.let { credential ->
                            if (credential is GoogleIdTokenCredential) {
                                try {
                                    handleSignIn(credential)
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            HomeActivity::class.java
                                        )
                                    )

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                // Handle non-GoogleIdTokenCredential case
                            }
                        } ?: run {
                            // Handle the null result case

                        }
                    }

                    override fun onError(e: GetCredentialException) {
                        when (e) {
                            is GetCredentialCancellationException -> {
                                Log.e("SignInWithGmail", "Sign-In was canceled by the user")
                            }

                            is NoCredentialException -> {
                                Log.e("SignInWithGmail", "No credentials available")
                            }

                            is GetCredentialException -> {
                                if (e.message?.contains("User disabled the feature") == true) {
                                    Log.e(
                                        "SignInWithGmail",
                                        "User has disabled the sign-in feature"
                                    )
                                } else {
                                    Log.e("SignInWithGmail", "Error occurred: ${e.message}")
                                    handleFailure(e)
                                }
                            }

                            else -> {
                                Log.e("SignInWithGmail", "Error occurred: ${e.message}")
                                handleFailure(e)
                            }
                        }
                    }

                }
            )
        } else {
            Log.d("SHYAM", "signInWithGmail: ELSE")
        }


    }


    private fun handleSignIn(credential: GoogleIdTokenCredential) {
        // Extract user information from the credential

        val email = "" + credential.id
        val id = "" + credential.id
        val fname = "" + credential.givenName
        val lname = "" + credential.familyName
        val auth_token = "" + credential.idToken
        val image = "" + credential.profilePictureUri


        val tokenId = credential.idToken
        Log.d("MAIN", tokenId.toString())

    }

    private fun handleFailure(e: GetCredentialException) {
        Log.d("MAIN_", e.toString())
    }

    fun generateNonce(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

//    private fun checkUserSignedIn() {
//        lifecycleScope.launch {
//            try {
//                // Create a request to get the saved credentials
//                val getRequest = GetCredentialRequest.Builder().addCredentialOption(GetPasswordOption())
//                    .build()
//
//                // Attempt to retrieve credentials
//                val credentialResponse = mCredentialManager.getCredential(this@MainActivity,getRequest)
//
//                // Check if the credential is retrieved, which indicates the user is signed in
//                if (credentialResponse != null) {
//                    startActivity(Intent(this@MainActivity,HomeActivity::class.java))
//                    println("User is already signed in")
//                } else {
//                    println("User is not signed in")
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}