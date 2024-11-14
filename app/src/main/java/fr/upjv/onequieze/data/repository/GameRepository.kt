package fr.upjv.onequieze.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.apollographql.apollo.api.ApolloResponse
import com.google.firebase.firestore.Query
import fr.upjv.onequieze.CharacterQuery
import fr.upjv.onequieze.R
import fr.upjv.onequieze.TotalCharactersCountQuery
import fr.upjv.onequieze.data.firebase.FirebaseConfig
import fr.upjv.onequieze.data.firebase.repository.AuthRepository
import fr.upjv.onequieze.data.model.GameScore
import fr.upjv.onequieze.ui.apollo.apolloClient
import fr.upjv.onequieze.ui.screen.totalCharactersResponse
import kotlinx.coroutines.tasks.await
import java.util.Date

class GameRepository(private val authRepository: AuthRepository) {

    private val firestore = FirebaseConfig.firestore
    private val auth = FirebaseConfig.auth

    fun saveGameScore(score: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val gameData = hashMapOf(
            "userId" to userId, "score" to score, "date" to Date()
        )

        firestore.collection("games").add(gameData).addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    suspend fun getCompleteScores(): List<GameScore> {
        val scores = mutableListOf<GameScore>()

        try {

            val games = firestore.collection("games").orderBy("score", Query.Direction.DESCENDING)
                .orderBy("date", Query.Direction.DESCENDING).get().await().documents

            for (game in games) {
                val userId = game.getString("userId") ?: continue
                val score = game.getLong("score")?.toInt() ?: 0
                val date = game.getDate("date") ?: continue

                val userInfo = authRepository.getUserInfo(userId)

                val username = userInfo?.username ?: "Joueur"
                val profileImageUrl = userInfo?.profileImageUrl ?: ""

                scores.add(
                    GameScore(
                        userId = userId,
                        username = username,
                        profileImageUrl = profileImageUrl,
                        score = score,
                        date = date
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return scores
    }

}

suspend fun RetrieveRandomCharacter(
    mediaId: Int,
    previouslyFetchedCharacters: MutableSet<Int>,
): CharacterQuery.Node? {


    if (totalCharactersResponse == null) {
        val queryTotalPages = TotalCharactersCountQuery(mediaId)
        val response = apolloClient.query(queryTotalPages).execute()

        if (response.hasErrors() || response.data == null) {
            return null
        }

        totalCharactersResponse = response
    }

    val totalCharacters = totalCharactersResponse?.data?.Media?.characters?.pageInfo?.total ?: 0
    if (totalCharacters == 0) {
        return null
    }

    val pageSize = 25
    val totalFullPages = totalCharacters / pageSize
    val maxValidCharacterIndex = totalFullPages * pageSize

    var character: CharacterQuery.Node? = null

    while (character == null) {
        val randomIndex = (1..maxValidCharacterIndex).random()
        val page = (randomIndex / pageSize) + 1
        val characterIndexOnPage = randomIndex % pageSize

        val randomCharacterQuery = CharacterQuery(mediaId, page)
        val response: ApolloResponse<CharacterQuery.Data> =
            apolloClient.query(randomCharacterQuery).execute()

        if (response.hasErrors() || response.data?.Media?.characters?.nodes.isNullOrEmpty()) {
            continue
        }

        val nodes = response.data?.Media?.characters?.nodes ?: continue
        val potentialCharacter = nodes.getOrNull(characterIndexOnPage)

        if (potentialCharacter != null && !previouslyFetchedCharacters.contains(potentialCharacter.id)) {
            if (!potentialCharacter.name?.full.isNullOrEmpty() && !potentialCharacter.image?.medium.isNullOrEmpty()) {
                character = potentialCharacter
                previouslyFetchedCharacters.add(potentialCharacter.id)
            }
        }
    }
    return character
}


suspend fun matchCharacter(
    context: Context,
    score: Int,
    charName: String,
    nameInput: String,
    life: Int,
    mediaId: Int,
    character: MutableState<CharacterQuery.Node?>,
    previouslyFetchedCharacters: MutableSet<Int>,
    onScoreUpdate: (Int) -> Unit,
    onLifeUpdate: (Int) -> Unit
): Boolean {
    if (nameInput != charName) {
        Toast.makeText(context, context.getString(R.string.incorrect_name), Toast.LENGTH_SHORT)
            .show()
        val isGameOver = isGameOver(life, onLifeUpdate)
        if (isGameOver) return false
        else {
            refreshCharacter(mediaId, character, previouslyFetchedCharacters)
            return true
        }
    }
    Toast.makeText(context, context.getString(R.string.you_got_it), Toast.LENGTH_SHORT).show()
    onScoreUpdate(score + 1)
    refreshCharacter(mediaId, character, previouslyFetchedCharacters)
    return true
}

fun isGameOver(life: Int, onLifeUpdate: (Int) -> Unit): Boolean {
    onLifeUpdate(life - 1)
    if (life == 1) {
        return true
    } else {
        return false
    }
}

suspend fun refreshCharacter(
    mediaId: Int,
    character: MutableState<CharacterQuery.Node?>,
    previouslyFetchedCharacters: MutableSet<Int>
) {

    val responseCharacter = RetrieveRandomCharacter(
        mediaId,
        previouslyFetchedCharacters,
    )
    character.value = responseCharacter


}