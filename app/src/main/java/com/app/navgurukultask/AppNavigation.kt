package com.app.navgurukultask

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.navgurukultask.presentation.home_screen.StudentScreen
import com.app.navgurukultask.presentation.student_detail_screen.StudentDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { StudentScreen(navController) }
        composable(
            route = "details/{studentId}"
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            StudentDetailScreen(navController, studentId)
        }    }
}
