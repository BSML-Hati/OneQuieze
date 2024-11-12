package fr.upjv.onequieze.data.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import com.apollographql.apollo.api.ApolloResponse
import fr.upjv.onequieze.CharacterQuery
import fr.upjv.onequieze.TotalCharactersCountQuery
import fr.upjv.onequieze.ui.apollo.apolloClient
import fr.upjv.onequieze.ui.screen.totalCharactersResponse


suspend fun RetrieveRandomCharacter(
    mediaId: Int,
    previouslyFetchedCharacters: MutableSet<Int>,
): CharacterQuery.Node? {


    if (totalCharactersResponse == null) {
        val queryTotalPages = TotalCharactersCountQuery(mediaId)
        val response = apolloClient.query(queryTotalPages).execute()

        // Si la requête pour obtenir le nombre total de personnages échoue, retourne null
        if (response.hasErrors() || response.data == null) {
            Log.e(
                "RetrieveRandomCharacter",
                "Erreur lors de la récupération du nombre total de personnages: ${response.errors}"
            )
            return null
        }

        totalCharactersResponse = response
    }

    val totalCharacters = totalCharactersResponse?.data?.Media?.characters?.pageInfo?.total ?: 0
    if (totalCharacters == 0) {
        Log.e("RetrieveRandomCharacter", "Aucun personnage trouvé ou les données sont nulles")
        return null
    }

    val pageSize = 25
    val totalFullPages = totalCharacters / pageSize
    val maxValidCharacterIndex = totalFullPages * pageSize

    var character: CharacterQuery.Node? = null

    // Continue à chercher un personnage valide tant qu'il a déjà été récupéré
    while (character == null) {
        val randomIndex = (1..maxValidCharacterIndex).random()
        val page = (randomIndex / pageSize) + 1
        val characterIndexOnPage = randomIndex % pageSize

        // Récupération des personnages à la page choisie
        val randomCharacterQuery = CharacterQuery(mediaId, page)
        val response: ApolloResponse<CharacterQuery.Data> =
            apolloClient.query(randomCharacterQuery).execute()

        // Si la requête échoue ou que les données sont nulles, on réessaye
        if (response.hasErrors() || response.data?.Media?.characters?.nodes.isNullOrEmpty()) {
            Log.e(
                "RetrieveRandomCharacter",
                "Erreur ou données vides lors de la récupération du personnage: ${response.errors}"
            )
            continue  // Réessaye si une erreur est rencontrée
        }

        // Vérifie les personnages sur la page
        val nodes = response.data?.Media?.characters?.nodes ?: continue
        val potentialCharacter = nodes.getOrNull(characterIndexOnPage)

        // Si le personnage est valide et qu'il n'a pas encore été récupéré
        if (potentialCharacter != null && !previouslyFetchedCharacters.contains(potentialCharacter.id)) {
            if (!potentialCharacter.name?.full.isNullOrEmpty() && !potentialCharacter.image?.medium.isNullOrEmpty()) {
                // Personnage valide trouvé, ajout à la liste des récupérés
                character = potentialCharacter
                previouslyFetchedCharacters.add(potentialCharacter.id)
            } else {
                Log.d(
                    "RetrieveRandomCharacter", "Personnage sans nom ou image, tentative de nouveau"
                )
            }
        } else {
            Log.d(
                "RetrieveRandomCharacter",
                "Personnage déjà récupéré ou non valide, tentative de nouveau"
            )
        }
    }

    return character
}


suspend fun matchCharacter(
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
        val isGameOver = isGameOver(life, onLifeUpdate)
        if (isGameOver) return false
        else {
            refreshCharacter(mediaId, character, previouslyFetchedCharacters)
            return true
        }
    }
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

/*
suspend fun RetrieveMediaName(mediaId: Int): ApolloResponse<MediaNameQuery.Data>? {

    // Crée la requête avec le paramètre
    val query = MediaNameQuery(mediaId)

    // Exécute la requête Apollo
    val response = apolloClient.query(query).execute()
    if (response.hasErrors()) {
        Log.e("RetrieveMediaName", "Erreur: ${response.errors}")
    } else if (response.data == null) {
        Log.e("RetrieveMediaName", "Les données sont nulles")
    } else {
        Log.d("RetrieveMediaName", "Données: ${response.data}")
    }

    return response
}*/