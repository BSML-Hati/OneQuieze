package fr.upjv.onequieze.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import fr.upjv.onequieze.data.firebase.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registerResult = MutableStateFlow<AuthResult?>(null)
    val registerResult: StateFlow<AuthResult?> get() = _registerResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun register(
        email: String, password: String, username: String, profileImageStream: InputStream?
    ) {
        viewModelScope.launch {
            authRepository.register(email, password, username, profileImageStream)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _registerResult.value = task.result
                    } else {
                        _errorMessage.value = task.exception?.message
                    }
                }
        }
    }
}