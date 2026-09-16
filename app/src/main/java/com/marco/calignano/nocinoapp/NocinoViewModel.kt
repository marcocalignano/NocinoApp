package com.marco.calignano.nocinoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marco.calignano.nocinoapp.data.Mix
import com.marco.calignano.nocinoapp.data.MixSummary
import com.marco.calignano.nocinoapp.data.NocinoRepository
import com.marco.calignano.nocinoapp.data.Spirit
import com.marco.calignano.nocinoapp.data.Spice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NocinoViewModel(private val repository: NocinoRepository) : ViewModel() {

    val allMixes: StateFlow<List<Mix>> = repository.collectAllNocinos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedMixId = MutableStateFlow<Long?>(null)
    val selectedMixId: StateFlow<Long?> = _selectedMixId.asStateFlow()

    val selectedMixDetails: StateFlow<List<MixSummary>> = _selectedMixId.flatMapLatest { mixId ->
        if (mixId == null) {
            MutableStateFlow(emptyList())
        } else {
            repository.collectNocinoById(mixId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val alcoholPercentageBeforeTasting: StateFlow<Double> = selectedMixDetails.map { details ->
        if (details.isEmpty()) return@map 0.0

        val mixInfo = details.first()
        val water = mixInfo.waterContent ?: 0
        val sugar = mixInfo.sugarSyrupContent ?: 0

        // Deduplicate spirits, since the JOIN creates a cartesian product with spices
        val uniqueSpirits = details
            .filter { it.spiritName != null && it.spiritQuantity != null && it.spiritAlcoholPercentage != null }
            .distinctBy { Triple(it.spiritName, it.spiritQuantity, it.spiritAlcoholPercentage) }

        val totalPureAlcohol = uniqueSpirits.sumOf {
            (it.spiritQuantity!! * it.spiritAlcoholPercentage!!) / 100.0
        }

        val totalSpiritVolume = uniqueSpirits.sumOf { it.spiritQuantity!! }

        val totalVolume = totalSpiritVolume + water + sugar

        if (totalVolume == 0) {
            0.0
        } else {
            (totalPureAlcohol / totalVolume) * 100.0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )


    private val _isCreatingNewNocino = MutableStateFlow(false)
    val isCreatingNewNocino: StateFlow<Boolean> = _isCreatingNewNocino.asStateFlow()

    private val _newNocinoName = MutableStateFlow("")
    val newNocinoName: StateFlow<String> = _newNocinoName.asStateFlow()

    // State for the 'Add Spirit' dialog
    private val _showAddSpiritDialog = MutableStateFlow(false)
    val showAddSpiritDialog: StateFlow<Boolean> = _showAddSpiritDialog.asStateFlow()

    private val _newSpiritName = MutableStateFlow("")
    val newSpiritName: StateFlow<String> = _newSpiritName.asStateFlow()

    private val _newSpiritAlcoholPercentage = MutableStateFlow(0.0)
    val newSpiritAlcoholPercentage: StateFlow<Double> = _newSpiritAlcoholPercentage.asStateFlow()

    private val _newSpiritQuantity = MutableStateFlow(0)
    val newSpiritQuantity: StateFlow<Int> = _newSpiritQuantity.asStateFlow()

    // State for the 'Add Spice' dialog
    private val _showAddSpiceDialog = MutableStateFlow(false)
    val showAddSpiceDialog: StateFlow<Boolean> = _showAddSpiceDialog.asStateFlow()

    private val _newSpiceName = MutableStateFlow("")
    val newSpiceName: StateFlow<String> = _newSpiceName.asStateFlow()

    private val _newSpiceQuantity = MutableStateFlow(0)
    val newSpiceQuantity: StateFlow<Int> = _newSpiceQuantity.asStateFlow()

    fun onMixSelected(mixId: Long) {
        _selectedMixId.value = mixId
        _isCreatingNewNocino.value = false
    }

    fun startCreatingNewNocino() {
        _isCreatingNewNocino.value = true
    }

    fun cancelCreatingNewNocino() {
        _isCreatingNewNocino.value = false
        _newNocinoName.value = ""
    }

    fun onNewNocinoNameChange(name: String) {
        _newNocinoName.value = name
    }

    fun saveNewNocino() {
        viewModelScope.launch {
            val name = _newNocinoName.value.trim()
            if (name.isNotBlank()) {
                val newMix = Mix(
                    name = name,
                    waterContent = 0,
                    sugarSyrupContent = 0
                )
                val newId = repository.insertNewNocino(newMix, emptyList(), emptyList())
                _selectedMixId.value = newId
                cancelCreatingNewNocino()
            }
        }
    }

    // Functions for 'Add Spirit' dialog
    fun onShowAddSpiritDialog() { _showAddSpiritDialog.value = true }
    fun onDismissAddSpiritDialog() {
        _showAddSpiritDialog.value = false
        _newSpiritName.value = ""
        _newSpiritAlcoholPercentage.value = 0.0
        _newSpiritQuantity.value = 0
    }
    fun onNewSpiritNameChange(name: String) { _newSpiritName.value = name }
    fun onNewSpiritAlcoholPercentageChange(percentage: Double) { _newSpiritAlcoholPercentage.value = percentage }
    fun onNewSpiritQuantityChange(quantity: Int) { _newSpiritQuantity.value = quantity }

    fun saveNewSpirit() {
        viewModelScope.launch {
            val mixId = _selectedMixId.value ?: return@launch
            val name = _newSpiritName.value.trim()
            val alcoholPercentage = _newSpiritAlcoholPercentage.value
            val quantity = _newSpiritQuantity.value

            if (name.isNotBlank()) {
                repository.addSpirit(Spirit(mixId = mixId, name = name, alcoholPercentage = alcoholPercentage, quantity = quantity))
                onDismissAddSpiritDialog()
            }
        }
    }

    // Functions for 'Add Spice' dialog
    fun onShowAddSpiceDialog() { _showAddSpiceDialog.value = true }
    fun onDismissAddSpiceDialog() {
        _showAddSpiceDialog.value = false
        _newSpiceName.value = ""
        _newSpiceQuantity.value = 0
    }
    fun onNewSpiceNameChange(name: String) { _newSpiceName.value = name }
    fun onNewSpiceQuantityChange(quantity: Int) { _newSpiceQuantity.value = quantity }

    fun saveNewSpice() {
        viewModelScope.launch {
            val mixId = _selectedMixId.value ?: return@launch
            val name = _newSpiceName.value.trim()
            val quantity = _newSpiceQuantity.value

            if (name.isNotBlank()) {
                repository.addSpice(Spice(mixId = mixId, name = name, quantity = quantity))
                onDismissAddSpiceDialog()
            }
        }
    }
}
