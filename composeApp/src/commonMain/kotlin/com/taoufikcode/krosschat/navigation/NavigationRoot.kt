package com.taoufikcode.krosschat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.taoufikcode.chat.presentation.navigation.ChatGraphRoutes
import com.taoufikcode.chat.presentation.navigation.chatGraph
import com.taoufikcode.presentation.navigation.AuthGraphRoutes
import com.taoufikcode.presentation.navigation.authGraph

@Composable
fun NavigationRoot(navController: NavHostController,startDestination: Any) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(ChatGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            })
        chatGraph(
            navController = navController,
            onLogout = {
                navController.navigate(AuthGraphRoutes.Graph) {
                    popUpTo(ChatGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
    }
}