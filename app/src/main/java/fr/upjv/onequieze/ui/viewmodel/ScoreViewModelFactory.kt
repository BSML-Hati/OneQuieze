package fr.upjv.onequieze.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.upjv.onequieze.firebase.repository.ScoreRepository


class ScoreViewModelFactory(
    private val scoreRepository: ScoreRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScoreViewModel(scoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}