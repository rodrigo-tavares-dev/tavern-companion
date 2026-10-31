package com.taverncompanion.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taverncompanion.app.data.CombatantRepository
import com.taverncompanion.app.data.TavernDatabase
import com.taverncompanion.app.ui.CombatTrackerScreen
import com.taverncompanion.app.ui.DiceRollerScreen
import com.taverncompanion.app.ui.theme.TavernCompanionTheme
import com.taverncompanion.app.viewmodel.CombatViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TavernDatabase.getDatabase(applicationContext)

        val repository = CombatantRepository(database.combatantDao())

        val viewModeFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CombatViewModel::class.java)) {
                    return CombatViewModel(repository) as T
                }
                throw IllegalArgumentException("Classe ViewModel desconhecida")
            }
        }

        val viewModelFactory = viewModeFactory

        val combatViewModel = ViewModelProvider(this, viewModelFactory)[CombatViewModel::class.java]

        setContent {
            TavernCompanionTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "combat_tracker"
                ) {
                    composable(route = "combat_tracker") {
                        CombatTrackerScreen(
                            viewModel = combatViewModel,
                            onNavigateToDice = {
                                navController.navigate("dice_roller")
                            }
                        )
                    }
                    composable(route = "dice_roller") {
                        DiceRollerScreen(
                            viewModel = combatViewModel,
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
}