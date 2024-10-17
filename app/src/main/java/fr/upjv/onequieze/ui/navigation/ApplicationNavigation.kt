package fr.upjv.onequieze.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.upjv.onequieze.ui.screen.MainScreen


object NavigationPath {
    const val MAIN_SCREEN = "main_screen"
}


fun NavGraphBuilder.addMainScreenNav(
    onButtonClick: NavHostController,
) {
    composable(
        route = NavigationPath.MAIN_SCREEN
    ) {
        MainScreen(
         )
    }
}






@Composable
fun HomeNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavigationPath.MAIN_SCREEN,
    ) {
        //ajout des fonctions pour naviguer
        addMainScreenNav(navController)
    }


}
