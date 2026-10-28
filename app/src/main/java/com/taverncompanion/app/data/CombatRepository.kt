package com.taverncompanion.app.data

import com.taverncompanion.app.model.Combatant
import kotlinx.coroutines.flow.Flow

/**
 * Repositório responsável por acessar os dados do personagem.
 * Isola a fonte de dados da camada de interface de usuário.
 *
 * @property combatantDao Objeto de acesso aos dados do personagem.
 */
class CombatRepository(
    private val combatantDao: CombatantDao
) {

    /**
     * Retorna todos os personagens em ordem de iniciativa de forma decrescente.
     * Toda modificação no banco de dados será notificada automaticamente..
     */
    val allCombatants: Flow<List<Combatant>> = combatantDao.getAllCombatants()

    /**
     * Insere um novo personagem ou atualiza um personagem existente.
     *
     * @param combatant Personagem a ser inserido ou atualizado.
     * @return Identificador único do personagem.
     */
    suspend fun insertCombatant(combatant: Combatant): Long {
        return combatantDao.insertCombatant(combatant)
    }

    /**
     * Atualiza os dados de um personagem já existente.
     *
     * @param combatant Personagem a ser atualizado.
     */
    suspend fun updateCombatant(combatant: Combatant) {
        combatantDao.updateCombatant(combatant)
    }

    /**
     * Remove um personagem do banco de dados quando ele é derrotado.
     *
     * @param combatant Personagem a ser removido.
     */
    suspend fun deleteCombatant(combatant: Combatant) {
        combatantDao.deleteCombatant((combatant))
    }

    /**
     * Remove todos os personagens do banco de dados quando o jogo é encerrado.
     */
    suspend fun clearAllCombatants() {
        combatantDao.clearAllCombatants()
    }
}
