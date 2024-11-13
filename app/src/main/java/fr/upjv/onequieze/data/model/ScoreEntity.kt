package fr.upjv.onequieze.data.model

import com.google.firebase.Timestamp

data class ScoreEntity(
    val userId: String,
    val score: Int,
    val date: Timestamp
)