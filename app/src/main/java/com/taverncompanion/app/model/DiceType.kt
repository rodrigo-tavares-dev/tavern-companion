package com.taverncompanion.app.model

import kotlin.random.Random

/**
 * Representa os dados poliédricos utilizados no jogo.
 *
 * @aram sides Quantidade de faces do dado.
 * @param label Identificação do dado.
 */
enum class DiceType(val sides: Int, val label: String) {
    D4(sides = 4, label = "D4"),
    D6(sides = 6, label = "D6"),
    D8(sides = 8, label = "D8"),
    D10(sides = 10, label = "D10"),
    D12(sides = 12, label = "D12"),
    D20(sides = 20, label = "D20");

    /**
     *Simula a rolagem do dado e retorna o valor obtido.
     *Retorna o valor entre 1 e o número de faces do dado.
     */

    fun roll(): Int {
        // Random.nextInt(de, ate inclusive) -> 1 até side + 1 garante o intervalo fechado [1, sides].
        return Random.nextInt(1, sides + 1)
    }
}