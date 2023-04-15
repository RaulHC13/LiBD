package com.proyecto.libd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.libd.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                if (cuenta != null) {
                    val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credenciales).addOnCompleteListener {
                        if (it.isSuccessful) {
                            prefs.setEmail(cuenta.email?:"")
                            irHome()
                        } else {
                            Toast.makeText(this, "Error login", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    lateinit var binding: ActivityLoginBinding
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = Prefs(this)
        comprobarSesion()
        setListeners()
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("174343878081-9o233u6st1l677sh9o91u8tfojfs6pj9.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConfig)

        googleClient.signOut()
        responseLauncher.launch(googleClient.signInIntent)
    }

    private fun comprobarSesion() {
        val email = prefs.getEmail()
        if (email != null) {
            irHome()
        }
    }

    private fun irHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }
}