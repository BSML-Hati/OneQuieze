package fr.upjv.onequieze.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.upjv.onequieze.firebase.FirebaseConfig
import fr.upjv.onequieze.ui.screen.GameScreen
import fr.upjv.onequieze.ui.screen.LoginScreen
import fr.upjv.onequieze.ui.screen.MainScreen
import fr.upjv.onequieze.ui.screen.RegisterScreen
import fr.upjv.onequieze.ui.screen.ScoreboardScreen
import fr.upjv.onequieze.ui.screen.UserScreen
import fr.upjv.onequieze.ui.viewmodel.LoginViewModel
import fr.upjv.onequieze.ui.viewmodel.RegisterViewModel


object NavigationPath {
    const val MAIN_SCREEN = "main_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val REGISTER_SCREEN = "register_screen"
    const val GAME_SCREEN = "game_screen"
    const val SCOREBOARD_SCREEN = "scoreboard_screen"
    const val USER_SCREEN = "user_screen"
}


fun NavGraphBuilder.addMainScreenNav(
    onGameButtonClick: () -> Unit,
    onScoreboardButtonClick: () -> Unit,
    onUserButtonClick: () -> Unit,
) {
    composable(
        route = NavigationPath.MAIN_SCREEN
    ) {
        MainScreen(onGameButtonClick = { onGameButtonClick() },
            onScoreboardButtonClick = { onScoreboardButtonClick() },
            onUserButtonClick = { onUserButtonClick() })
    }
}

fun NavGraphBuilder.addLoginScreenNavigation(navController: NavController, loginViewModel: LoginViewModel) {
    composable(route = NavigationPath.LOGIN_SCREEN) {
        LoginScreen(navController, loginViewModel)
    }
}

fun NavGraphBuilder.addRegisterScreenNavigation(navController: NavController, registerViewModel: RegisterViewModel) {
    composable(route = NavigationPath.REGISTER_SCREEN) {
        RegisterScreen(navController, registerViewModel)
    }
}

fun NavGraphBuilder.addUserScreenNavigation(navController: NavController) {
    composable(
        route = NavigationPath.USER_SCREEN,
    ) {
        UserScreen(navController)
    }
}

fun NavGraphBuilder.addScoreBoardScreenNavigation(
    navController: NavController,
    onMainScreenButtonClick: () -> Unit,
) {
    composable(
        route = NavigationPath.SCOREBOARD_SCREEN,
    ) {
        ScoreboardScreen(navController, onMainScreenButtonClick)
    }
}

fun NavGraphBuilder.addGameScreenNavigation(
    navController: NavController, onScoreboardButtonClick: () -> Unit
) {
    composable(
        route = NavigationPath.GAME_SCREEN,
    ) {
        GameScreen(navController, onScoreboardButtonClick)
    }
}


@Composable
fun HomeNavHost(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel
) {
    NavHost(
        navController = navController,
        startDestination = if (FirebaseConfig.auth.currentUser != null) NavigationPath.MAIN_SCREEN else NavigationPath.LOGIN_SCREEN,
    ) {

        addMainScreenNav(onGameButtonClick = { navController.navigate(NavigationPath.GAME_SCREEN) },
            onScoreboardButtonClick = { navController.navigate(NavigationPath.SCOREBOARD_SCREEN) },
            onUserButtonClick = { navController.navigate(NavigationPath.USER_SCREEN) })
        addLoginScreenNavigation(navController = navController, loginViewModel)
        addRegisterScreenNavigation(navController = navController, registerViewModel)
        addGameScreenNavigation(navController = navController,
            onScoreboardButtonClick = { navController.navigate(NavigationPath.SCOREBOARD_SCREEN) })
        addScoreBoardScreenNavigation(
            navController = navController,
            onMainScreenButtonClick = { navController.navigate(NavigationPath.MAIN_SCREEN) })
        addUserScreenNavigation(navController = navController)
    }


}
