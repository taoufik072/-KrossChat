@file:OptIn(ExperimentalCoroutinesApi::class)

package com.taoufikcode.chat.presentation.chat_list_detail

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.taoufikcode.chat.presentation.FakeChatConnectionClient
import com.taoufikcode.chat.presentation.awaitUntil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ChatListDetailViewModelTest {

    private lateinit var viewModel: ChatListDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ChatListDetailViewModel(FakeChatConnectionClient())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selecting a chat updates selectedChatId`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatListDetailAction.OnSelectChat("chat-1"))

            assertThat(awaitUntil { it.selectedChatId != null }.selectedChatId)
                .isEqualTo("chat-1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `create chat click opens the create chat dialog`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatListDetailAction.OnCreateChatClick)

            assertThat(awaitUntil { it.dialogState != DialogState.Hidden }.dialogState)
                .isEqualTo(DialogState.CreateChat)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dismissing hides the current dialog`() = runTest {
        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ChatListDetailAction.OnCreateChatClick)
            awaitUntil { it.dialogState == DialogState.CreateChat }

            viewModel.onAction(ChatListDetailAction.OnDismissCurrentDialog)

            assertThat(awaitUntil { it.dialogState == DialogState.Hidden }.dialogState)
                .isEqualTo(DialogState.Hidden)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add participants opens dialog only with a selected chat`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatListDetailAction.OnAddParticipantsClick)
            expectNoEvents()

            viewModel.onAction(ChatListDetailAction.OnSelectChat("chat-1"))
            awaitUntil { it.selectedChatId == "chat-1" }
            viewModel.onAction(ChatListDetailAction.OnAddParticipantsClick)

            val state = awaitUntil { it.dialogState != DialogState.Hidden }
            assertThat(state.dialogState).isEqualTo(DialogState.AddParticipants("chat-1"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `profile settings click opens the profile dialog`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatListDetailAction.OnProfileSettingsClick)

            assertThat(awaitUntil { it.dialogState != DialogState.Hidden }.dialogState)
                .isEqualTo(DialogState.Profile)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
