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

/**
 * Tela principal da aplicação para monitorar os combates.
 *
 * *param viewModel contém o estado da batalha.
 * @param onNavigateToDice Navega para a tela de dados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatTrackerScreen(
    viewModel: CombatViewModel,
    onNavigateToDice: () -> Unit
) {
    //Coleta a lista de combates do ViewModel como um estado.
    val combatants by viewModel.combatants.collectAsState()

    //Armazena o estado do diálogo de adição de combate.
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
    topBar =
    {
        TopAppBar(
            title = { Text("Tracker de Combate") },
            actions = {
                //Botão para limpar a lista de combates.
                IconButton(onClick = { viewModel.clearAll() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Limpar combate"
                    )
                }
                //Botão para navegar para a mesa de rolagem de dados.
                IconButton(onClick = onNavigateToDice) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Ir para a mesa de dados"
                    )
                }
            }
        )
    },
    floatingActionButton = {
        //Botão flutuante para adicionar um novo participante.
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
        //Exibe a lista de combates ou uma mensagem se não houver nenhum.
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
            //Exibe a lista de combates se houver algum.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = combatants,
                    key = { it.id } //Chave única para preservar o estado de cada item.
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

        //Redenriza o diálogo de adição de combate se necessário.
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

/**
 * Representa um card de combate com detalhes do personagem.
 *
 * @param combatant Dados do personagem.
 * @param onDamage Função para aplicar dano ao personagem.
 * @param onHeal Função para aplicar cura ao personagem.
 * @param onDelete Função para remover o personagem do combate.
 */
@Composable
private fun CombatantCard(
    combatant: Combatant,
    onDamage: () -> Unit,
    onHeal: () -> Unit,
    onDelete: () -> Unit
) {
    //Aplica a distinção: jogador (azul) ou NPC (vermelho).
    val borderColor = if (combatant.isPlayer) Color(0xFF2196F3) else Color(0xFFE53935)
    val hpTextColor = if (combatant.isFainted()) Color.Gray else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(
            //Define a cor de fundo com base na vida do personagem.
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
            //Coluna contendo detalhes do personagem.
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

            //Botões para aplicar dano, cura e remover o personagem.
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Aplica 1 ponto de dano
                IconButton(onClick = onDamage) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Aplicar dano")
                }

                //Aplica 1 ponto de cura
                IconButton(onClick = onHeal) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Aplicar cura")
                }

                Spacer(modifier = Modifier.width(8.dp))

                //Exclui o combatente
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

/**
 * Caixa de diálogo suspensa com formulário para adicionar um nova criatura/aliado.
 *
 * @param onDismiss Função para fechar o diálogo.
 * @param onConfirm Função para adicionar o novo combatente.
 */
@Composable
private fun AddCombatantDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, maxHp: Int, initiative: Int, isPlayer: Boolean) -> Unit
) {
    //Estados locais para armazenar os dados do novo combatente.
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
                    //Converte os valores de HP e iniciativa para inteiros.
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