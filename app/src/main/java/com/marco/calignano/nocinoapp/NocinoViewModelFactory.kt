package com.marco.calignano.nocinoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating a [NocinoViewModel].
 */
class NocinoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NocinoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NocinoViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
