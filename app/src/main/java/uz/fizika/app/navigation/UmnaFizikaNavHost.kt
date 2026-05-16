package uz.fizika.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uz.fizika.core.ui.theme.NeonColors
import uz.fizika.formulas.FormulaListScreen
import uz.fizika.formulas.FormulaDetailScreen
import uz.fizika.games.GamesScreen
import uz.fizika.tests.TestsScreen
import uz.fizika.tests.TestSessionScreen
import uz.fizika.solver.SolverScreen
import uz.fizika.aichat.AIChatScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Formulas : Screen("formulas", "Формулы", Icons.Default.AutoStories)
    object Games    : Screen("games",    "Игры",    Icons.Default.SportsEsports)
    object Solver   : Screen("solver",   "Решатель",Icons.Default.Psychology)
    object Tests    : Screen("tests",    "Тесты",   Icons.Default.Quiz)
    object AI       : Screen("ai_chat",  "ИИ",      Icons.Default.SmartToy)
}

private val bottomNavItems = listOf(
    Screen.Formulas, Screen.Games, Screen.Solver, Screen.Tests, Screen.AI
)

@Composable
fun UmnaFizikaNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = NeonColors.Surface,
                tonalElevation = androidx.compose.ui.unit.Dp(0f)
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy
                        ?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonColors.Primary,
                            selectedTextColor = NeonColors.Primary,
                            indicatorColor = NeonColors.Primary.copy(alpha = 0.15f),
                            unselectedIconColor = NeonColors.OnSurfaceVariant,
                            unselectedTextColor = NeonColors.OnSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Formulas.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(tween(300)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(300)
                )
            },
            exitTransition = {
                fadeOut(tween(200)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                )
            },
            popEnterTransition = {
                fadeIn(tween(300)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(300)
                )
            },
            popExitTransition = {
                fadeOut(tween(200)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(200)
                )
            }
        ) {
            composable(Screen.Formulas.route) { FormulaListScreen(navController) }
            composable(Screen.Games.route)    { GamesScreen(navController) }
            composable(Screen.Solver.route)   { SolverScreen(navController) }
            composable(Screen.Tests.route)    { TestsScreen(navController) }
            composable(Screen.AI.route)       { AIChatScreen(navController) }

            // Test screens
            composable("test_session") { TestSessionScreen(navController) }

            // Detail screens
            composable(
                route = "formula_detail/{formulaId}",
                arguments = listOf(navArgument("formulaId") { type = NavType.StringType })
            ) { back ->
                val id = back.arguments?.getString("formulaId") ?: return@composable
                FormulaDetailScreen(formulaId = id, navController = navController)
            }
        }
    }
}
