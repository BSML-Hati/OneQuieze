package fr.upjv.onequieze.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import fr.upjv.onequieze.firebase.FirebaseConfig
import fr.upjv.onequieze.firebase.repository.AuthRepository
import fr.upjv.onequieze.ui.navigation.HomeNavHost
import fr.upjv.onequieze.ui.navigation.NavigationPath
import fr.upjv.onequieze.ui.theme.OneQuiezeTheme
import fr.upjv.onequieze.ui.viewmodel.AuthViewModelFactory
import fr.upjv.onequieze.ui.viewmodel.LoginViewModel
import fr.upjv.onequieze.ui.viewmodel.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository()

        val authViewModelFactory = AuthViewModelFactory(authRepository)

        setContent {
            OneQuiezeTheme {
                val navController = rememberNavController()

                val loginViewModel = ViewModelProvider(
                    this,
                    authViewModelFactory
                )[LoginViewModel::class.java]
                val registerViewModel = ViewModelProvider(this, authViewModelFactory)[RegisterViewModel::class.java]

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        HomeNavHost(
                            navController = navController,
                            loginViewModel = loginViewModel,
                            registerViewModel = registerViewModel
                        )
                    }
                }
            }
        }
    }
}


