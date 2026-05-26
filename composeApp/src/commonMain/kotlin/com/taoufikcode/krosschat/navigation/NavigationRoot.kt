package com.taoufikcode.krosschat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.taoufikcode.chat.presentation.chat_list.ChatListRoot
import com.taoufikcode.chat.presentation.chat_list.ChatListRoute
import com.taoufikcode.presentation.navigation.AuthGraphRoutes
import com.taoufikcode.presentation.navigation.authGraph

@Composable
fun NavigationRoot(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AuthGraphRoutes.Graph
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(ChatListRoute) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            })
        composable<ChatListRoute> {
            ChatListRoot()
        }
    }
}