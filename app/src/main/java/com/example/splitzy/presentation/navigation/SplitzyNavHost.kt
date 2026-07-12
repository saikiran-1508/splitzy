package com.example.splitzy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitzy.presentation.expenses.ExpensesScreen
import com.example.splitzy.presentation.groups.GroupsScreen

// Route definitions in one place so screens never hardcode each other's paths.
object Routes {
    const val GROUPS = "groups"
    const val EXPENSES = "expenses/{groupId}"
    fun expenses(groupId: String) = "expenses/$groupId"
}

@Composable
fun SplitzyNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.GROUPS) {
        composable(Routes.GROUPS) {
            GroupsScreen(
                onGroupClick = { groupId -> navController.navigate(Routes.expenses(groupId)) }
            )
        }
        composable(
            route = Routes.EXPENSES,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            ExpensesScreen(
                groupId = backStackEntry.arguments?.getString("groupId").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
