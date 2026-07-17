@file:OptIn(ExperimentalCoroutinesApi::class)

package com.taoufikcode.chat.presentation.chat_list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.taoufikcode.chat.presentation.FakeAuthService
import com.taoufikcode.chat.presentation.FakeChatRepository
import com.taoufikcode.chat.presentation.FakeDeviceTokenService
import com.taoufikcode.chat.presentation.FakeProfileRepository
import com.taoufikcode.chat.presentation.FakeSessionStorage
import com.taoufikcode.chat.presentation.awaitUntil
import com.taoufikcode.chat.presentation.otherParticipant
import com.taoufikcode.chat.presentation.testAuthInfo
import com.taoufikcode.chat.presentation.testChat
import com.taoufikcode.chat.presentation.testLocalUser
import com.taoufikcode.chat.presentation.testMessage
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

class ChatListViewModelTest {

    private lateinit var repository: FakeChatRepository
    private lateinit var sessionStorage: FakeSessionStorage
    private lateinit var deviceTokenService: FakeDeviceTokenService
    private lateinit var authService: FakeAuthService
    private lateinit var profileRepository: FakeProfileRepository
    private lateinit var viewModel: ChatListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeChatRepository()
        sessionStorage = FakeSessionStorage()
        deviceTokenService = FakeDeviceTokenService()
        authService = FakeAuthService()
        profileRepository = FakeProfileRepository()
        sessionStorage.emit(testAuthInfo)
        viewModel = ChatListViewModel(
            repository = repository,
            sessionStorage = sessionStorage,
            deviceTokenService = deviceTokenService,
            authService = authService,
            profileRepository = profileRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starting the screen fetches chats and current user profile`() = runTest {
        viewModel.state.test {
            awaitItem()

            assertThat(repository.fetchChatsCalls).isEqualTo(1)
            assertThat(profileRepository.fetchCurrentUserCalls).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `chats are mapped to ui with the logged-in user id`() = runTest {
        repository.chatsFlow.value = listOf(
            testChat(
                lastMessage = testMessage(
                    id = "m1",
                    senderId = otherParticipant.userId,
                    createdAt = Instant.parse("2025-06-15T10:30:00Z")
                ),
                unreadCount = 3
            )
        )

        viewModel.state.test {
            val state = awaitUntil { it.chats.isNotEmpty() }

            val chatUi = state.chats.single()
            assertThat(chatUi.currentUser.id).isEqualTo(testLocalUser.id)
            assertThat(chatUi.otherParticipants.map { it.id })
                .isEqualTo(listOf(otherParticipant.userId))
            assertThat(chatUi.lastMessageSenderUsername).isEqualTo(otherParticipant.username)
            assertThat(chatUi.unreadCount).isEqualTo(3)
            assertThat(state.currentUser?.id).isEqualTo(testLocalUser.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state resets when the session is cleared`() = runTest {
        repository.chatsFlow.value = listOf(testChat())

        viewModel.state.test {
            awaitUntil { it.chats.isNotEmpty() }

            sessionStorage.emit(null)

            val state = awaitUntil { it.chats.isEmpty() }
            assertThat(state.currentUser).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selecting a chat updates selectedChatId`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatListAction.OnSelectChat("chat-42"))

            assertThat(awaitUntil { it.selectedChatId != null }.selectedChatId)
                .isEqualTo("chat-42")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout click opens the confirmation dialog and closes the menu`() = runTest {
        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ChatListAction.OnUserAvatarClick)
            awaitUntil { it.isUserMenuOpen }

            viewModel.onAction(ChatListAction.OnLogoutClick)

            val state = awaitUntil { it.showLogoutConfirmation }
            assertThat(state.isUserMenuOpen).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirmed logout unregisters token then logs out and clears local data`() = runTest {
        viewModel.events.test {
            viewModel.onAction(ChatListAction.OnConfirmLogout)

            assertThat(awaitItem()).isEqualTo(ChatListEvent.OnLogoutSuccess)
        }

        assertThat(deviceTokenService.unregisterCalls)
            .containsExactly(testAuthInfo.refreshToken)
        assertThat(authService.logoutCalls).containsExactly(testAuthInfo.refreshToken)
        assertThat(sessionStorage.storedAuthInfo).isNull()
        assertThat(repository.deleteAllChatsCalls).isEqualTo(1)
    }

    @Test
    fun `failed token unregistration emits logout error and keeps the session`() = runTest {
        deviceTokenService.unregisterResult = Result.Failure(DataError.Remote.SERVER_ERROR)

        viewModel.events.test {
            viewModel.onAction(ChatListAction.OnConfirmLogout)

            assertThat(awaitItem()).isInstanceOf(ChatListEvent.OnLogoutError::class)
        }

        assertThat(authService.logoutCalls).isEmpty()
        assertThat(sessionStorage.storedAuthInfo).isEqualTo(testAuthInfo)
    }

    @Test
    fun `failed logout call emits logout error and keeps the session`() = runTest {
        authService.logoutResult = Result.Failure(DataError.Remote.SERVER_ERROR)

        viewModel.events.test {
            viewModel.onAction(ChatListAction.OnConfirmLogout)

            assertThat(awaitItem()).isInstanceOf(ChatListEvent.OnLogoutError::class)
        }

        assertThat(sessionStorage.storedAuthInfo).isEqualTo(testAuthInfo)
        assertThat(repository.deleteAllChatsCalls).isEqualTo(0)
    }
}
