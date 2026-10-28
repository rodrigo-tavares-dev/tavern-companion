package com.taverncompanion.app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa um personagem do jogo.
 *
 * @param id Identificador único do personagem.
 * @param name Nome do personagem.
 * @param currentHp Vida atual do personagem.
 * @param maxHp Vida máxima do personagem.
 * @param initiative Pontuação de iniciativa do personagem.
 * @param isPlayer Indica se o personagem é um jogador ou NPC.
 */

@Entity(tableName = "combatants")
data class Combatant (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "current_hp")
    val currentHp: Int,

    @ColumnInfo(name = "max_hp")
    val maxHp: Int,

    @ColumnInfo (name = "initiative")
    val initiative: Int,

    @ColumnInfo (name = "is_player")
    val isPlayer: Boolean = false
) {
    /**
     * Verifica se o personagem está com vida.
     */
    fun isFainted(): Boolean = currentHp <= 0

    /**
     * Aplica uma mudança na vida do personagem.
     */

    fun applyChange(amount: Int): Combatant {
        val updateHp = (currentHp + amount).coerceIn(0, maxHp)
        return this.copy(currentHp = updateHp)
    }
}