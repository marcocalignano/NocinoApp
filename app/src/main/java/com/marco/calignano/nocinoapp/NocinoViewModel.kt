package com.marco.calignano.nocinoapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

class NocinoViewModel : ViewModel() {
    private val _title = MutableStateFlow("Nocino")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _isTitleInEditMode = MutableStateFlow(false)
    val isTitleInEditMode: StateFlow<Boolean> = _isTitleInEditMode.asStateFlow()

    private val _spirits = MutableStateFlow<List<Spirit>>(emptyList())
    val spirits: StateFlow<List<Spirit>> = _spirits.asStateFlow()

    private val _spices = MutableStateFlow<List<Spice>>(emptyList())
    val spices: StateFlow<List<Spice>> = _spices.asStateFlow()

    private val _totalAlcoholPercentage = MutableStateFlow(0.0)
    val totalAlcoholPercentage: StateFlow<Double> = _totalAlcoholPercentage.asStateFlow()

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun onTitleClick() {
        _isTitleInEditMode.value = true
    }

    fun onTitleDone() {
        _isTitleInEditMode.value = false
    }

    fun addSpirit(spirit: Spirit) {
        _spirits.update { it + spirit }
        calculateTotalAlcohol()
    }

    fun updateSpirit(index: Int, spirit: Spirit) {
        _spirits.update {
            it.toMutableList().apply {
                this[index] = spirit
            }
        }
        calculateTotalAlcohol()
    }

    fun removeSpirit(index: Int) {
        _spirits.update {
            it.toMutableList().apply {
                removeAt(index)
            }
        }
        calculateTotalAlcohol()
    }

    fun addSpice(spice: Spice) {
        _spices.update { it + spice }
    }

    fun updateSpice(index: Int, spice: Spice) {
        _spices.update {
            it.toMutableList().apply {
                this[index] = spice
            }
        }
    }

    fun removeSpice(index: Int) {
        _spices.update {
            it.toMutableList().apply {
                removeAt(index)
            }
        }
    }

    private fun calculateTotalAlcohol() {
        var totalAlcoholVolume = 0.0
        var totalVolume = 0.0
        _spirits.value.forEach { spirit ->
            val quantity = spirit.quantity.toDoubleOrNull() ?: 0.0
            val percentage = spirit.percentage.toDoubleOrNull() ?: 0.0
            totalVolume += quantity
            totalAlcoholVolume += quantity * (percentage / 100.0)
        }
        _totalAlcoholPercentage.value = if (totalVolume > 0) {
            ((totalAlcoholVolume / totalVolume * 1000.0).roundToInt() / 10.0)
        } else {
            0.0
        }
    }
}