package com.taverncompanion.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taverncompanion.app.model.Combatant
import kotlinx.coroutines.flow.Flow

/**
 * Interface de acesso aos dados do personagem.
 */
@Dao
interface CombatantDao {

    /**
     * Retorna todos os personagens em ordem de iniciativa de forma decrescente.
     * Quem tiver a maior iniciativa terá o turno primeiro.
     *
     * O retorno em [Flow] permite que os dados sejam atualizados automaticamente.
     */
    @Query("SELECT * FROM combatants ORDER BY initiative DESC")
    fun getAllCombatants(): Flow<List<Combatant>>

    /**
     * Insere um novo personagem ou atualiza um personagem existente.
     *
     * A palavra suspend permite que a função seja executada via Coroutines.
     */
    @Insert(onConflict = onConflictStrategy.REPLACE)
    suspend fun insertCombatant(combatant: Combatant): Long

    /**
     * Atualiza os dados de um personagem já existente.
     */
    @Update
    suspend fun updateCombatant(combatant: Combatant)

    /**
     * Remove um personagem do banco de dados quando ele é derrotado.
     */
    @Delete
    suspend fun deleteCombatant(Combatant: Combatant)

    /**
     * Remove todos os personagens do banco de dados quando o jogo é encerrado.
     */
    @Query("DELETE FROM combatants")
    suspend fun clearAllCombatants()
}