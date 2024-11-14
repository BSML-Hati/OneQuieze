package fr.upjv.onequieze.data.model

import java.util.Date

data class GameScore(
    val userId: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val score: Int = 0,
    val date: Date = Date()
)