package fr.upjv.onequieze.firebase.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.toObject
import fr.upjv.onequieze.data.model.ScoreEntity
import fr.upjv.onequieze.data.model.UserEntity
import fr.upjv.onequieze.firebase.FirebaseConfig.firestore
import kotlinx.coroutines.tasks.await

class ScoreRepository {

    fun saveGameToFirestore(userId: String, score: Int) {

        val newDocRef = firestore.collection("games").document()
        val gameId = newDocRef.id

        val gameMap = mapOf(
            "gameId" to gameId,
            "userId" to userId,
            "score" to score,
            "date" to Timestamp.now()
        )

        newDocRef.set(gameMap)
            .addOnSuccessListener {
                println("Game saved with gameId: $gameId")
            }
            .addOnFailureListener { e ->
                println("Error saving game: $e")
            }
    }


    suspend fun getAllScores(): List<ScoreEntity> {
        val snapshot = firestore.collection("games").get().await()
        return snapshot.documents.mapNotNull { it.toObject<ScoreEntity>() }
    }

    suspend fun getUserData(userId: String): UserEntity? {
        return firestore.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject(UserEntity::class.java)
    }

}