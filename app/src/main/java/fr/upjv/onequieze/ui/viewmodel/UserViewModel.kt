package fr.upjv.onequieze.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import fr.upjv.onequieze.data.firebase.FirebaseConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val auth = FirebaseConfig.auth
    private val firestore = FirebaseConfig.firestore
    private val storage = FirebaseConfig.storage

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> get() = _username

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> get() = _profileImageUrl

    private val _errorMessage = MutableStateFlow<String?>(null)

    val userId: String? = auth.currentUser?.uid

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
                _username.value = document.getString("username")
                _profileImageUrl.value = document.getString("profileImageUrl")
            }.addOnFailureListener { exception ->
                _errorMessage.value = exception.message
            }
    }

    fun updateProfileImage(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        storageRef.putFile(imageUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                _profileImageUrl.value = uri.toString()
                saveProfileImageUrl(uri.toString())
            }
        }.addOnFailureListener {
            _errorMessage.value = it.message
        }
    }

    private fun saveProfileImageUrl(profileImageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).update("profileImageUrl", profileImageUrl)
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
            }
    }

    fun logout() {
        FirebaseConfig.auth.signOut()
    }
}