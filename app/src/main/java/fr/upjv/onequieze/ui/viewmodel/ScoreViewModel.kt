package fr.upjv.onequieze.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.upjv.onequieze.data.model.ScoreDetails
import fr.upjv.onequieze.data.model.ScoreEntity
import fr.upjv.onequieze.data.model.UserScoreEntity
import fr.upjv.onequieze.firebase.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScoreViewModel(private val scoreRepository: ScoreRepository) : ViewModel(){

    private val _saveSuccess = MutableStateFlow(false)

    private val _scoreRecords = MutableStateFlow<List<ScoreEntity>>(emptyList())

    private val _userScores = MutableStateFlow<List<UserScoreEntity>>(emptyList())
    val userScores: StateFlow<List<UserScoreEntity>> = _userScores


    fun fetchUserScores() {
        viewModelScope.launch {
            val scores = scoreRepository.getAllScores()

            val userScoreList = scores.groupBy { it.userId }.mapNotNull { (userId, scores) ->
                val userData = scoreRepository.getUserData(userId)
                userData?.let {
                    UserScoreEntity(
                        userId = userId,
                        username = it.username,
                        profileImageUrl = it.profileImageUrl,
                        scores = scores.map { score ->
                            ScoreDetails(score = score.score, date = score.date)
                        }
                    )
                }
            }
            // Mise à jour de l'état avec la liste des entités regroupées
            _userScores.value = userScoreList
        }
    }

    fun saveGame(userId: String, score: Int) {
        viewModelScope.launch {
            try {
                scoreRepository.saveGameToFirestore(userId, score)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveSuccess.value = false
            }
        }
    }


}