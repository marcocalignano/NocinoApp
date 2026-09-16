package com.marco.calignano.nocinoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marco.calignano.nocinoapp.data.NocinoRepository

/**
 * Factory for creating a [NocinoViewModel] with a constructor that takes a [NocinoRepository].
 */
class NocinoViewModelFactory(private val repository: NocinoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NocinoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NocinoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
