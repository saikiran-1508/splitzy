package com.example.splitzy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitzy.presentation.auth.LoginScreen
import com.example.splitzy.presentation.expenses.ExpensesScreen
import com.example.splitzy.presentation.expenses.ManageCategoriesScreen
import com.example.splitzy.presentation.groups.GroupsScreen
import com.example.splitzy.presentation.profile.ProfileScreen
import com.example.splitzy.presentation.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth

import java.net.URLDecoder
import java.net.URLEncoder

// Route definitions in one place so screens never hardcode each other's paths.
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val GROUPS = "groups"
    const val PROFILE = "profile"
    const val EXPENSES = "expenses/{groupId}/{groupName}"
    const val MANAGE_CATEGORIES = "manage_categories/{groupId}"
    fun expenses(groupId: String, groupName: String): String {
        val encodedName = URLEncoder.encode(groupName, "UTF-8")
        return "expenses/$groupId/$encodedName"
    }
    fun manageCategories(groupId: String) = "manage_categories/$groupId"
}

// FirebaseAuth.getInstance() throws IllegalStateException until a real
// google-services.json is added and the google-services plugin is applied —
// there's no real Firebase project behind this yet. Guard every call site so
// a not-yet-configured Firebase never takes down the rest of the app; treat
// it the same as "no signed-in user".
private fun firebaseAuthOrNull(): FirebaseAuth? = try {
    FirebaseAuth.getInstance()
} catch (e: IllegalStateException) {
    null
}

@Composable
fun SplitzyNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    // Login itself needs Firebase to work (its ViewModel injects
                    // FirebaseAuth), so only send anyone there when Firebase is
                    // actually configured. Otherwise go straight to Groups — the
                    // rest of the app doesn't depend on Firebase.
                    val auth = firebaseAuthOrNull()
                    val destination =
                        if (auth == null || auth.currentUser != null) Routes.GROUPS else Routes.LOGIN
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Routes.GROUPS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.GROUPS) {
            GroupsScreen(
                onGroupClick = { group ->
                    navController.navigate(Routes.expenses(group.id, group.name))
                },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
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
                onBack = { navController.popBackStack() },
                onManageCategories = { groupId ->
                    navController.navigate(Routes.manageCategories(groupId))
                }
            )
        }
        composable(
            route = Routes.MANAGE_CATEGORIES,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            ManageCategoriesScreen(
                groupId = backStackEntry.arguments?.getString("groupId").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
