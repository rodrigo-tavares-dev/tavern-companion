package com.taverncompanion.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.taverncompanion.app.model.Combatant

/**
 * Base de dados da aplicação.
 * Define as entidades e a versão do banco de dados.
 */
@Database(
    entities = [Combatant::class],
    version = 1,
    exportSchema = false
)
abstract class TavernDatabase : RoomDatabase() {

    /**
     * Retorna o DAO para acessar os dados do personagem.
     * O Room gera o código para implementar esta função [CombatantDao].
     */
    abstract fun combatantDao(): CombatantDao

    companion object {
        /**
         * Referência em cache de uma instância do banco de dados.
         * @Volatile assegura que gravações neste campo sejam visíveis imediatamente a outras threads.
         */
        @Volatile
        private var INSTANCE: TavernDatabase? = null

        /**
         * Retorna uma instância do banco de dados ou cria uma nova se não existir..
         *
         * @param context Contexto da aplicação.
         * @return Instância do banco de dados.
         */
        fun getDatabase(context: Context): TavernDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TavernDatabase::class.java,
                    "tavern_companion_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}