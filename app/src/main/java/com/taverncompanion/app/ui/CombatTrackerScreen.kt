package com.taverncompanion.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.taverncompanion.app.model.Combatant
import com.taverncompanion.app.viewmodel.CombatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatTrackerScreen(
    viewModel: CombatViewModel,
    onNavigateToDice: () -> Unit
) {

    val combatants by viewModel.combatants.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
    topBar =
    {
        TopAppBar(
            title = { Text("Tracker de Combate") },
            actions = {

                IconButton(onClick = { viewModel.clearAll() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Limpar combate"
                    )
                }

                IconButton(onClick = onNavigateToDice) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Ir para a mesa de dados"
                    )
                }
            }
        )
    },
    floatingActionButton =
    {
        FloatingActionButton(onClick = { showAddDialog = true }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar combate"
            )
        }
    }
    )
    {
        innerPadding ->
        if (combatants.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nenhum combate registrado",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Adicione um combate para começar",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = combatants,
                    key = { it.id }
                ) { combatant ->
                    CombatantCard(
                        combatant = combatant,
                        onDamage = { viewModel.modifyHp(combatant, -1) },
                        onHeal = { viewModel.modifyHp(combatant, 1) },
                        onDelete = { viewModel.removeCombatant(combatant) }
                    )
                }
            }
        }
        if (showAddDialog) {
            AddCombatantDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, maxHp, initiative, isPlayer ->
                    viewModel.addCombatant(name, maxHp, initiative, isPlayer)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun CombatantCard(
    combatant: Combatant,
    onDamage: () -> Unit,
    onHeal: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (combatant.isPlayer) Color(0xFF2196F3) else Color(0xFFE53935)
    val hpTextColor = if (combatant.isFainted()) Color.Gray else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = if (combatant.isFainted()) Color(0x1F000000) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = combatant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Iniciativa: ${combatant.initiative}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (combatant.isFainted()) "INCONSCIENTE" else "Vida: ${combatant.currentHp}/${combatant.maxHp}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = hpTextColor
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDamage) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Aplicar dano")
                }

                IconButton(onClick = onHeal) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Aplicar cura")
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover combate",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCombatantDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, maxHp: Int, initiative: Int, isPlayer: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var maxHpText by remember { mutableStateOf("") }
    var initiativeText by remember { mutableStateOf("") }
    var isPlayer by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Combate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = maxHpText,
                    onValueChange = { maxHpText = it },
                    label = { Text("HP máximo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = initiativeText,
                    onValueChange = { initiativeText = it },
                    label = { Text("Valor de início") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isPlayer,
                        onCheckedChange = { isPlayer = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("É um jogador/aliado?")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hp = maxHpText.toIntOrNull() ?: 10
                    val init = initiativeText.toIntOrNull() ?: 10
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), hp, init, isPlayer)
                    }
                }
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}