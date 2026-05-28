package com.taoufikcode.krosschat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.taoufikcode.chat.presentation.chat_list.ChatListRoute
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.krosschat.navigation.DeepLinkListener
import com.taoufikcode.krosschat.navigation.NavigationRoot
import com.taoufikcode.presentation.navigation.AuthGraphRoutes
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    onAuthenticationChecked: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    DeepLinkListener(navController)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCheckingAuth) {
        if(!state.isCheckingAuth) {
            onAuthenticationChecked()
        }
    }

    KrossChatTheme {
        if(!state.isCheckingAuth) {
            NavigationRoot(
                navController = navController,
                startDestination = if(state.isLoggedIn) {
                    ChatListRoute
                } else {
                    AuthGraphRoutes.Graph
                }
            )
        }
    }
}