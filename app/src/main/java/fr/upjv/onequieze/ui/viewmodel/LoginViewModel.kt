package fr.upjv.onequieze.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import fr.upjv.onequieze.firebase.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _loginResult = MutableStateFlow<AuthResult?>(null)
    val loginResult: StateFlow<AuthResult?> get() = _loginResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginResult.value = task.result
                } else {
                    _errorMessage.value = task.exception?.message
                }
            }
        }
    }
}