package fr.upjv.onequieze.firebase.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import fr.upjv.onequieze.firebase.FirebaseConfig
import java.io.InputStream

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseConfig.auth
    private val firestore: FirebaseFirestore = FirebaseConfig.firestore
    private val storage: FirebaseStorage = FirebaseConfig.storage

    fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun register(email: String, password: String, username: String, profileImageStream: InputStream?): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            val userId = authResult.user?.uid ?: return@addOnSuccessListener
            profileImageStream?.let {
                uploadProfileImage(userId, it) { downloadUrl ->
                    saveUserToFirestore(userId, email, username, downloadUrl)
                }
            } ?: saveUserToFirestore(userId, email, username, null)
        }
    }

    private fun uploadProfileImage(userId: String, imageStream: InputStream, onComplete: (String) -> Unit) {
        val storageRef = storage.reference.child("profile_images/$userId.jpg")
        storageRef.putStream(imageStream).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString())
            }
        }
    }

    private fun saveUserToFirestore(userId: String, email: String, username: String, profileImageUrl: String?) {
        val userMap = mapOf(
            "email" to email,
            "username" to username,
            "profileImageUrl" to profileImageUrl
        )
        firestore.collection("users").document(userId).set(userMap)
    }
}
