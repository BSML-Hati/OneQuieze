package fr.upjv.onequieze.data.firebase.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import fr.upjv.onequieze.data.firebase.FirebaseConfig
import fr.upjv.onequieze.data.model.UserInfo
import kotlinx.coroutines.tasks.await
import java.io.InputStream

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseConfig.auth
    private val firestore: FirebaseFirestore = FirebaseConfig.firestore
    private val storage: FirebaseStorage = FirebaseConfig.storage

    fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun register(
        email: String, password: String, username: String, profileImageStream: InputStream?
    ): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener
                profileImageStream?.let {
                    uploadProfileImage(userId, it) { downloadUrl ->
                        saveUserToFirestore(userId, email, username, downloadUrl)
                    }
                } ?: saveUserToFirestore(userId, email, username, null)
            }
    }

    private fun uploadProfileImage(
        userId: String, imageStream: InputStream, onComplete: (String) -> Unit
    ) {
        val storageRef = storage.reference.child("profile_images/$userId.jpg")
        storageRef.putStream(imageStream).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString())
            }
        }
    }

    private fun saveUserToFirestore(
        userId: String, email: String, username: String, profileImageUrl: String?
    ) {
        val userMap = mapOf(
            "email" to email, "username" to username, "profileImageUrl" to profileImageUrl
        )
        firestore.collection("users").document(userId).set(userMap)
    }

    suspend fun getUserInfo(userId: String): UserInfo? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                val username = document.getString("username") ?: "Player"
                val profileImageUrl = document.getString("profileImageUrl") ?: ""
                UserInfo(username, profileImageUrl)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
