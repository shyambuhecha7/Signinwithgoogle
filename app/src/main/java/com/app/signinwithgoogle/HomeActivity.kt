package com.app.signinwithgoogle

import android.content.Intent
import android.credentials.GetCredentialException
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var mCredentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mCredentialManager = CredentialManager.create(this)

        findViewById<Button>(R.id.signout).setOnClickListener {
            signOut()
        }
    }
    private  fun signOut() {
        lifecycleScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()

                mCredentialManager.clearCredentialState(clearRequest)

                 startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                 finish()

            } catch (e: Exception) {
                // Handle the exception if clearing the credential state fails
                e.printStackTrace()
            }
        }
    }

}