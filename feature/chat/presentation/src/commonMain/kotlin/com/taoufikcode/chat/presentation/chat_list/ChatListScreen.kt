package com.taoufikcode.chat.presentation.chat_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatListRoot(
    viewModel: ChatListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
) {

}
@Serializable
data object ChatListRoute
@Preview
@Composable
private fun Preview() {
    KrossChatTheme {
        ChatListScreen(
            state = ChatListState(),
            onAction = {}
        )
    }
}