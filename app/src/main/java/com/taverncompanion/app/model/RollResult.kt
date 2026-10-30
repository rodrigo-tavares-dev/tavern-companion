package com.taverncompanion.app.model

/**
 * Representa o resultado de um dado lançado.
 * O uso do 'sealed class' força o tratamento de todas as possibilidades.
 */

sealed class RollResult {

    /**
     * Estadodo inicial do dado.
     */
    data object Empty: RollResult()

    /**
     * Rolagem comum, dentro dos limites de sucesso e fracasso.
     *
     * @property value O valor obtido na rolagem.
     * @property dice O tipo de dado usado na rolagem.
     */
    data class StandardSuccess(
        val value: Int,
        val dice: DiceType
    ) : RollResult()

    /**
     * Sucesso crítico, quando o valor obtido é igual a 20.
     */

    data class CriticalSuccess(
        val value: Int = 20,
        val dice: DiceType = DiceType.D20
    ) : RollResult()

    /**
     * Falha crônica, quando o valor obtido é igual a 1.
     */

    data class CriticalFailure(
        val value: Int = 1,
        val dice: DiceType = DiceType.D20
    ) : RollResult()
}