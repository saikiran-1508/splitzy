package com.example.splitzy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitzy.presentation.expenses.ExpensesScreen
import com.example.splitzy.presentation.groups.GroupsScreen
import java.net.URLDecoder
import java.net.URLEncoder

// Route definitions in one place so screens never hardcode each other's paths.
object Routes {
    const val GROUPS = "groups"
    const val EXPENSES = "expenses/{groupId}/{groupName}"
    fun expenses(groupId: String, groupName: String): String {
        val encodedName = URLEncoder.encode(groupName, "UTF-8")
        return "expenses/$groupId/$encodedName"
    }
}

@Composable
fun SplitzyNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.GROUPS) {
        composable(Routes.GROUPS) {
            GroupsScreen(
                onGroupClick = { group ->
                    navController.navigate(Routes.expenses(group.id, group.name))
                }
            )
        }
        composable(
            route = Routes.EXPENSES,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
                navArgument("groupName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedName = backStackEntry.arguments?.getString("groupName").orEmpty()
            ExpensesScreen(
                groupId = backStackEntry.arguments?.getString("groupId").orEmpty(),
                groupName = URLDecoder.decode(encodedName, "UTF-8"),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
