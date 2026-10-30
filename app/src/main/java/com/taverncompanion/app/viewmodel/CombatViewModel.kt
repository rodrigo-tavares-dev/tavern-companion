package com.taverncompanion.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taverncompanion.app.data.CombatantRepository
import com.taverncompanion.app.model.Combatant
import com.taverncompanion.app.model.DiceType
import com.taverncompanion.app.model.RollResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por acessar os dados do personagem, lógica de combate e dados.
 * Faz a ponte entre a camada de interface de usuário e a camada de dados.
 *
 *@property repository Repositório responsável por acessar os dados do personagem.
 */
class CombatViewModel(
    private val repository: CombatantRepository
) : ViewModel() {

    /**
     * Lista de personagens em ordem de iniciativa.
     * Converte o Flow em StateFlow para notificação automática.
     */
    val combatants: StateFlow<List<Combatant>> = repository.allCombatants.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    //Armazena o dado selecionado para rolagem.
    private val _selectedDice = MutableStateFlow(DiceType.D20)
    val selectedDice: StateFlow<DiceType> = _selectedDice.asStateFlow()

    //Armazena o resultado da última rolagem.
    private val _lastRollResult = MutableStateFlow<RollResult>(RollResult.Empty)
    val lastRollResult: StateFlow<RollResult> = _lastRollResult.asStateFlow()


    /**
     * Atualiza o dado selecionado para rolagem.
     *
     * @param dice O novo dado a ser selecionado.
     */
    fun selectedDice(dice: DiceType) {
        _selectedDice.value = dice
    }

    /**
     * Realiza uma rolagem de dado e atualiza o resultado.
     * Calcula o resultado com base no tipo de dado selecionado.
     * Pode ser acionado por um botão ou ação de usuário.
     */
    fun rollSelectedDice() {
        val dice = _selectedDice.value
        val rolledValue = dice.roll()

        //Determina o resultado da rolagem com base no tipo de dado.
        val result = when {
            dice == DiceType.D20 && rolledValue == 20 -> RollResult.CriticalSuccess()
            dice == DiceType.D20 && rolledValue == 1 -> RollResult.CriticalFailure()
            else -> RollResult.StandardSuccess(rolledValue, dice = dice)
        }

        _lastRollResult.value = result
    }

    /**
     * Reseta o resultado da última rolagem.
     */
    fun resetRoll() {
        _lastRollResult.value = RollResult.Empty
    }

    /**
     * Adiciona um novo personagem ao banco de dados.
     */
    fun addCombatant(name: String, maxHp: Int, initiative: Int, isPlayer: Boolean) {
        viewModelScope.launch {
            val newCombatant = Combatant(
                name = name,
                currentHp = maxHp,
                maxHp = maxHp,
                initiative = initiative,
                isPlayer = isPlayer
            )
            repository.insertCombatant(newCombatant)
        }
    }

    /**
     * Atualiza a vida de um personagem.
     *
     * @param combatant A personagem a ser atualizada.
     * @param amount A quantidade de vida a ser modificada.
     */
    fun modifyHp(combatant: Combatant, amount:Int) {
        viewModelScope.launch {
            val update = combatant.applyChange(amount)
            repository.updateCombatant(update)
        }
    }

    /**
     * Remove um personagem do banco de dados.
     */
    fun removeCombatant(combatant: Combatant) {
        viewModelScope.launch {
            repository.deleteCombatant(combatant)
        }
    }

    /**
     * Remove todos os personagens do banco de dados.
     */
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAllCombatants()
        }
    }
    }
}