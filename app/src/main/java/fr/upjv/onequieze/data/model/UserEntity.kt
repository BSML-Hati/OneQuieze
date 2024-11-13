package fr.upjv.onequieze.data.model

import android.net.Uri
import com.google.firebase.Timestamp

data class UserEntity(
    val email: String,
    val profileImageUrl: Uri,
    val username: String
)