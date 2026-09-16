package com.marco.calignano.nocinoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.marco.calignano.nocinoapp.data.NocinoDatabase
import com.marco.calignano.nocinoapp.data.NocinoRepository
import com.marco.calignano.nocinoapp.ui.theme.NocinoAppTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val database by lazy { NocinoDatabase.getDatabase(context) }
            val repository by lazy { NocinoRepository(database.nocinoDao()) }
            val viewModel: NocinoViewModel by viewModels { NocinoViewModelFactory(repository) }

            NocinoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NocinoScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NocinoScreen(
    modifier: Modifier = Modifier,
    viewModel: NocinoViewModel
) {
    val allMixes by viewModel.allMixes.collectAsState()
    val selectedMixDetails by viewModel.selectedMixDetails.collectAsState()
    val selectedMixId by viewModel.selectedMixId.collectAsState()
    val isCreatingNewNocino by viewModel.isCreatingNewNocino.collectAsState()
    val newNocinoName by viewModel.newNocinoName.collectAsState()
    val alcoholPercentage by viewModel.alcoholPercentageBeforeTasting.collectAsState()

    val selectedMix = allMixes.find { it.mixId == selectedMixId }

    var expanded by remember { mutableStateOf(false) }

    // Dialog States
    val showAddSpiritDialog by viewModel.showAddSpiritDialog.collectAsState()
    val showAddSpiceDialog by viewModel.showAddSpiceDialog.collectAsState()

    if (showAddSpiritDialog) {
        AddSpiritDialog(viewModel = viewModel)
    }

    if (showAddSpiceDialog) {
        AddSpiceDialog(viewModel = viewModel)
    }

    Column(modifier = modifier.padding(16.dp)) {
        if (isCreatingNewNocino) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newNocinoName,
                    onValueChange = { viewModel.onNewNocinoNameChange(it) },
                    label = { Text("New Nocino Name") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.saveNewNocino() }) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
                IconButton(onClick = { viewModel.cancelCreatingNewNocino() }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                }
            }
        } else {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedMix?.name ?: "Select a mix",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nocino Recipe") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    allMixes.forEach { mix ->
                        DropdownMenuItem(
                            text = { Text(mix.name) },
                            onClick = {
                                viewModel.onMixSelected(mix.mixId)
                                expanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("New Nocino") },
                        onClick = {
                            viewModel.startCreatingNewNocino()
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedMixDetails.firstOrNull()?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recipe Details", style = MaterialTheme.typography.titleLarge)
                Row {
                    Button(onClick = { viewModel.onShowAddSpiritDialog() }) {
                        Text("Add Spirit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.onShowAddSpiceDialog() }) {
                        Text("Add Spice")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Water: ${it.waterContent} ml")
            Text("Sugar Syrup: ${it.sugarSyrupContent} ml")
            val formattedPercentage = remember(alcoholPercentage) {
                NumberFormat.getPercentInstance().apply { maximumFractionDigits = 2 }.format(alcoholPercentage / 100.0)
            }
            Text("Alcohol percentage before tasting: $formattedPercentage", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (selectedMixDetails.isNotEmpty()) {
                item {
                    Text("Ingredients", style = MaterialTheme.typography.titleLarge)
                }
            }
            items(selectedMixDetails.distinctBy { it.spiritName ?: it.spiceName }) { summary ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        summary.spiritName?.let {
                            Text("Spirit: $it", fontWeight = FontWeight.Bold)
                            Text("Alcohol: ${summary.spiritAlcoholPercentage}%")
                            summary.spiritQuantity?.let { quantity ->
                                Text("Quantity: $quantity ml")
                            }
                        }
                        summary.spiceName?.let {
                            Text("Spice: $it", fontWeight = FontWeight.Bold)
                            summary.spiceQuantity?.let { quantity ->
                                Text("Quantity: $quantity")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddSpiritDialog(viewModel: NocinoViewModel) {
    val newSpiritName by viewModel.newSpiritName.collectAsState()
    val newSpiritAlcoholPercentage by viewModel.newSpiritAlcoholPercentage.collectAsState()
    val newSpiritQuantity by viewModel.newSpiritQuantity.collectAsState()

    AlertDialog(
        onDismissRequest = { viewModel.onDismissAddSpiritDialog() },
        title = { Text("Add New Spirit") },
        text = {
            Column {
                OutlinedTextField(
                    value = newSpiritName,
                    onValueChange = { viewModel.onNewSpiritNameChange(it) },
                    label = { Text("Spirit Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newSpiritAlcoholPercentage.toString(),
                    onValueChange = { viewModel.onNewSpiritAlcoholPercentageChange(it.toDoubleOrNull() ?: 0.0) },
                    label = { Text("Alcohol Percentage") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newSpiritQuantity.toString(),
                    onValueChange = { viewModel.onNewSpiritQuantityChange(it.toIntOrNull() ?: 0) },
                    label = { Text("Quantity (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.saveNewSpirit() }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { viewModel.onDismissAddSpiritDialog() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddSpiceDialog(viewModel: NocinoViewModel) {
    val newSpiceName by viewModel.newSpiceName.collectAsState()
    val newSpiceQuantity by viewModel.newSpiceQuantity.collectAsState()

    AlertDialog(
        onDismissRequest = { viewModel.onDismissAddSpiceDialog() },
        title = { Text("Add New Spice") },
        text = {
            Column {
                OutlinedTextField(
                    value = newSpiceName,
                    onValueChange = { viewModel.onNewSpiceNameChange(it) },
                    label = { Text("Spice Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newSpiceQuantity.toString(),
                    onValueChange = { viewModel.onNewSpiceQuantityChange(it.toIntOrNull() ?: 0) },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.saveNewSpice() }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { viewModel.onDismissAddSpiceDialog() }) {
                Text("Cancel")
            }
        }
    )
}
