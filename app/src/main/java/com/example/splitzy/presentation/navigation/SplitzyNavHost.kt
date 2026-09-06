package com.example.splitzy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitzy.domain.model.GroupType
import com.example.splitzy.presentation.auth.LoginScreen
import com.example.splitzy.presentation.expenses.AddExpenseScreen
import com.example.splitzy.presentation.expenses.ManageCategoriesScreen
import com.example.splitzy.presentation.groups.AddMembersScreen
import com.example.splitzy.presentation.groups.CreateGroupScreen
import com.example.splitzy.presentation.home.HomeScreen
import com.example.splitzy.presentation.profile.ProfileScreen
import com.example.splitzy.presentation.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth
import java.net.URLDecoder
import java.net.URLEncoder

// Route definitions in one place so screens never hardcode each other's paths.
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val CREATE_GROUP = "create_group"
    const val ADD_MEMBERS = "add_members/{groupName}/{groupType}"
    const val ADD_EXPENSE = "add_expense/{groupId}"
    const val MANAGE_CATEGORIES = "manage_categories/{groupId}"

    fun addMembers(groupName: String, type: GroupType) =
        "add_members/${encode(groupName)}/${type.name}"

    fun addExpense(groupId: String) = "add_expense/$groupId"

    fun manageCategories(groupId: String) = "manage_categories/$groupId"

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}

private fun String?.decoded(): String = URLDecoder.decode(this.orEmpty(), "UTF-8")

// FirebaseAuth.getInstance() throws IllegalStateException if google-services.json
// is missing, so guard every call site — a not-yet-configured Firebase should
// never take down the rest of the app; treat it as "no signed-in user".
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
                    // Login needs Firebase to work (its ViewModel injects
                    // FirebaseAuth), so only route there when it's configured.
                    val auth = firebaseAuthOrNull()
                    val destination =
                        if (auth == null || auth.currentUser != null) Routes.HOME else Routes.LOGIN
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        // Home is the active group itself, not a list of groups.
        composable(Routes.HOME) {
            HomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onCreateGroup = { navController.navigate(Routes.CREATE_GROUP) },
                onAddExpense = { groupId -> navController.navigate(Routes.addExpense(groupId)) }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onCreateGroup = { navController.navigate(Routes.CREATE_GROUP) },
                onGroupOpened = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                onBack = { navController.popBackStack() },
                onNext = { name, type -> navController.navigate(Routes.addMembers(name, type)) }
            )
        }
        composable(
            route = Routes.ADD_MEMBERS,
            arguments = listOf(
                navArgument("groupName") { type = NavType.StringType },
                navArgument("groupType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddMembersScreen(
                groupName = backStackEntry.arguments?.getString("groupName").decoded(),
                groupType = GroupType.valueOf(
                    backStackEntry.arguments?.getString("groupType") ?: GroupType.HOME.name
                ),
                onBack = { navController.popBackStack() },
                // The new group becomes the active one, so home lands on it.
                onCreated = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
        composable(
            route = Routes.ADD_EXPENSE,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddExpenseScreen(
                groupId = backStackEntry.arguments?.getString("groupId").orEmpty(),
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
