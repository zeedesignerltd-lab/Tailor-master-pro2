package com.tailormaster.pro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.tailormaster.pro.data.Repository
import com.tailormaster.pro.ui.navigation.AppNavGraph
import com.tailormaster.pro.ui.screens.LoginScreen
import com.tailormaster.pro.ui.theme.TailorMasterProTheme
import com.tailormaster.pro.viewmodel.AuthViewModel
import com.tailormaster.pro.viewmodel.CustomerViewModel
import com.tailormaster.pro.viewmodel.OrderViewModel
import com.tailormaster.pro.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authVm: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TailorApp

        // Web client ID auto-generated from google-services.json
        val webClientId = getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                account.idToken?.let { authVm.signInWithGoogle(it) }
            } catch (e: Exception) {
                // Sign-in cancelled or failed; state stays Idle, user can retry.
            }
        }

        setContent {
            var darkMode by remember { mutableStateOf(false) }
            var loggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

            authVm = ViewModelProvider(
                this,
                ViewModelFactory(
                    repo = Repository(FirebaseAuth.getInstance().currentUser?.uid ?: "pending"),
                    authRepository = app.authRepository
                )
            )[AuthViewModel::class.java]

            TailorMasterProTheme(darkTheme = darkMode) {
                if (!loggedIn) {
                    LoginScreen(
                        vm = authVm,
                        onGoogleSignInClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
                        onLoggedIn = { loggedIn = true }
                    )
                } else {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@TailorMasterProTheme
                    val repo = remember(uid) { Repository(uid) }
                    val factory = remember(uid) { ViewModelFactory(repo, app.authRepository) }
                    val customerVm = ViewModelProvider(this, factory)[CustomerViewModel::class.java]
                    val orderVm = ViewModelProvider(this, factory)[OrderViewModel::class.java]

                    AppNavGraph(
                        customerVm = customerVm,
                        orderVm = orderVm,
                        darkMode = darkMode,
                        onDarkModeChange = { darkMode = it },
                        onSignOut = {
                            FirebaseAuth.getInstance().signOut()
                            googleSignInClient.signOut()
                            viewModelStore.clear()
                            loggedIn = false
                        }
                    )
                }
            }
        }
    }
}
