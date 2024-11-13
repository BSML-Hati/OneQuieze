package fr.upjv.onequieze.data.model

import android.net.Uri
import com.google.firebase.Timestamp


data class UserScoreEntity(
    val userId: String,
    val username: String,
    val profileImageUrl: Uri,
    val scores: List<ScoreDetails>
)

data class ScoreDetails(
    val score: Int,
    val date: Timestamp
)