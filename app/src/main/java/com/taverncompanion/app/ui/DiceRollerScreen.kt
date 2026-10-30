package com.taverncompanion.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taverncompanion.app.model.DiceType
import com.taverncompanion.app.model.RollResult
import com.taverncompanion.app.sensor.ShakeDetector
import com.taverncompanion.app.viewmodel.CombatViewModel

/**
 * Tela de rolagem de dados.
 * @param viewModel para acessar os dados do jogo.
 * @param onNavigateBack para voltar para a tela anterior.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiceRollerScreen(
    viewModel: CombatViewModel,
    onNavigateBack: () -> Unit
) {
    //Obtém o contexto da aplicação para acessar o sensor de shake.
    val context = LocalContext.current

    //Observa reativamente o estado do dado selecionado e do resultado da última rolagem.
    val selectDice by viewModel.selectedDice.collectAsState()
    val rollResult by viewModel.lastRollResult.collectAsState()

    //Instaciação memorizada do detector de shake.
    val shakeDetector = remember {
        ShakeDetector(context) {
            viewModel.rollSelectedDice()
        }
    }

    //Gestão do ciclo de vida do detector de shake.
    DisposableEffect(Unit) {
        shakeDetector.stopListening()
        onDispose {
            shakeDetector.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mesa de Dados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
       Column(
           modifier = Modifier
               .fillMaxSize()
               .padding(innerPadding)
               .padding(24.dp),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.SpaceBetween
       ) {
           //Seção 1: Escolha do dado e rolagem do dado.
           Column(horizontalAlignment = Alignment.CenterHorizontally) {
               Text(
                   text = "Escolha o dado",
                   style = MaterialTheme.typography.titleMedium,
               )
               Spacer(modifier = Modifier.height(12.dp))

               //Distribui os dados quebrando a linha conforme a configuração.
               FlowRow(
                   horizontalArrangement = Arrangement.spacedBy(8.dp),
                   verticalArrangement = Arrangement.spacedBy(8.dp),
                   modifier = Modifier.fillMaxWidth()
               ) {
                   DiceType.values().forEach { dice ->
                       FilterChip(
                           selected = dice == selectDice,
                           onClick = { viewModel.selectedDice(dice) },
                           label = { Text(dice.label.uppercase()) }
                       )
                   }
               }
           }

           //Seção 2: Resultado da última rolagem.
           Box(
               contentAlignment = Alignment.Center,
               modifier = Modifier
                   .size(200.dp)
                   .clip(CircleShape)
                   .background(getResultBackgroundColor(rollResult))
           ) {
               Column(horizontalAlignment = Alignment.CenterHorizontally) {
                   when(val result = rollResult) {
                       is RollResult.Empty -> {
                           Text(
                               text = selectDice.label.uppercase(),
                               fontSize = 32.sp,
                               fontWeight = FontWeight.Bold,
                               color = Color.White
                           )
                           Text(
                               text = "Pronto",
                               style = MaterialTheme.typography.bodySmall,
                               color = Color.LightGray
                           )
                       }
                       is RollResult.StandardSuccess -> {
                           Text(
                               text = result.value.toString(),
                               fontSize = 64.sp,
                               fontWeight = FontWeight.ExtraBold,
                               color = Color.White
                           )
                           Text(
                               text = result.dice.label.uppercase(),
                               style = MaterialTheme.typography.bodyMedium,
                               color = Color.LightGray
                           )
                       }
                       is RollResult.CriticalSuccess -> {
                           Text(
                               text = "20",
                               fontSize = 64.sp,
                               fontWeight = FontWeight.ExtraBold,
                               color = Color(0xFFFFD700)
                           )
                           Text(
                               text = "SUCESSO CRÍTICO!",
                               fontWeight = FontWeight.Bold,
                               color = Color(0xFFFFD700)
                           )
                       }
                       is RollResult.CriticalFailure -> {
                           Text(
                               text = "1",
                               fontSize = 64.sp,
                               fontWeight = FontWeight.ExtraBold,
                               color = Color(0xFFFF0000)
                           )
                           Text(
                               text = "FRACASSO CRÍTICO!",
                               fontWeight = FontWeight.Bold,
                               color = Color.White
                           )
                       }
                   }
               }
           }

           //Seção 3: Botão de rolagem e mensagem de instrução.
           Column(
               horizontalAlignment = Alignment.CenterHorizontally,
               modifier = Modifier.fillMaxWidth()
           ) {
               Text(
                   text = "Agite o celular para rolar o dado ou aperte o botão",
                   style = MaterialTheme.typography.bodySmall,
                   color = Color.Gray
               )
               Spacer(modifier = Modifier.height(8.dp))

               Button(
                   onClick = {viewModel.rollSelectedDice()},
                   modifier = Modifier.fillMaxWidth(),
                   colors = ButtonDefaults.buttonColors(
                       containerColor = MaterialTheme.colorScheme.primary
                   )
               ) {
                   Icon(imageVector = Icons.Default.Casino, contentDescription = null)
                   Spacer(modifier = Modifier.size(8.dp))
                   Text("Rolar ${selectDice.label.uppercase()}")
               }
           }
       }
    }
}

/**
 * Retorna a cor de fundo com base no resultado da última rolagem.
 *
 * @param result O resultado da última rolagem.
 * @return A cor de fundo correspondente.
 */
@Composable
private fun getResultBackgroundColor(result: RollResult): Color {
    return when (result) {
        is RollResult.Empty -> MaterialTheme.colorScheme.surfaceVariant
        is RollResult.StandardSuccess -> Color(0xFF2E3B4E)
        is RollResult.CriticalSuccess -> Color(0xFF1B5E20) //Verde escuro para 20 natural
        is RollResult.CriticalFailure -> Color(0xFFB71C1C) //Vermelho escurto para 1 natural
    }
}