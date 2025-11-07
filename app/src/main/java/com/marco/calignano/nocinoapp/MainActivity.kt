package com.marco.calignano.nocinoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marco.calignano.nocinoapp.ui.theme.NocinoAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NocinoViewModel by viewModels { NocinoViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NocinoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NocinoEditor(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun NocinoEditor(
    modifier: Modifier = Modifier,
    viewModel: NocinoViewModel
) {
    val title by viewModel.title.collectAsState()
    val isTitleInEditMode by viewModel.isTitleInEditMode.collectAsState()
    val spirits by viewModel.spirits.collectAsState()
    val spices by viewModel.spices.collectAsState()
    val totalAlcoholPercentage by viewModel.totalAlcoholPercentage.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {
        if (isTitleInEditMode) {
            val focusRequester = remember { FocusRequester() }
            OutlinedTextField(
                value = title,
                onValueChange = { newTitle -> viewModel.updateTitle(newTitle) },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onTitleClick() }
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Spirits", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            itemsIndexed(spirits) { index, spirit ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = spirit.name,
                        onValueChange = { newName ->
                            viewModel.updateSpirit(index, spirit.copy(name = newName))
                        },
                        label = { Text("Spirit Name") },
                        modifier = Modifier
                            .weight(0.5f)
                            .onFocusChanged { if (it.isFocused) viewModel.onTitleDone() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = spirit.quantity,
                        onValueChange = { newQuantity ->
                            viewModel.updateSpirit(index, spirit.copy(quantity = newQuantity))
                        },
                        label = { Text("Quantity (ml)") },
                        modifier = Modifier
                            .weight(0.25f)
                            .onFocusChanged { if (it.isFocused) viewModel.onTitleDone() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = spirit.percentage,
                        onValueChange = { newPercentage ->
                            viewModel.updateSpirit(index, spirit.copy(percentage = newPercentage))
                        },
                        label = { Text("Alcohol %") },
                        modifier = Modifier
                            .weight(0.25f)
                            .onFocusChanged { if (it.isFocused) viewModel.onTitleDone() }
                    )
                    IconButton(onClick = { viewModel.removeSpirit(index) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Spirit"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.addSpirit(Spirit("", "", "")) },
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Add Spirit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total Alcohol:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$totalAlcoholPercentage%",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Spices", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            itemsIndexed(spices) { index, spice ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = spice.name,
                        onValueChange = { newName ->
                            viewModel.updateSpice(index, spice.copy(name = newName))
                        },
                        label = { Text("Spice Name") },
                        modifier = Modifier
                            .weight(0.6f)
                            .onFocusChanged { if (it.isFocused) viewModel.onTitleDone() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = spice.quantity,
                        onValueChange = { newQuantity ->
                            viewModel.updateSpice(index, spice.copy(quantity = newQuantity))
                        },
                        label = { Text("Quantity") },
                        modifier = Modifier
                            .weight(0.4f)
                            .onFocusChanged { if (it.isFocused) viewModel.onTitleDone() }
                    )
                    IconButton(onClick = { viewModel.removeSpice(index) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Spice"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.addSpice(Spice("", "")) },
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Add Spice")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NocinoEditorPreview() {
    NocinoAppTheme {
        NocinoEditor(viewModel = NocinoViewModel())
    }
}
