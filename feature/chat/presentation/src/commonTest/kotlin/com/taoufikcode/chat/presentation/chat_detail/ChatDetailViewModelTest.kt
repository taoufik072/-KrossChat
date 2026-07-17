@file:OptIn(ExperimentalCoroutinesApi::class)

package com.taoufikcode.chat.presentation.chat_detail

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.taoufikcode.chat.domain.models.ChatInfo
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.domain.models.ConnectionState
import com.taoufikcode.chat.domain.models.MessageWithSender
import com.taoufikcode.chat.presentation.FakeChatConnectionClient
import com.taoufikcode.chat.presentation.FakeChatMessageRepository
import com.taoufikcode.chat.presentation.FakeChatRepository
import com.taoufikcode.chat.presentation.FakeSessionStorage
import com.taoufikcode.chat.presentation.awaitUntil
import com.taoufikcode.chat.presentation.model.MessageUi
import com.taoufikcode.chat.presentation.otherParticipant
import com.taoufikcode.chat.presentation.testAuthInfo
import com.taoufikcode.chat.presentation.testChat
import com.taoufikcode.chat.presentation.testMessage
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.presentation.utils.UiText
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

class ChatDetailViewModelTest {

    private lateinit var chatRepository: FakeChatRepository
    private lateinit var sessionStorage: FakeSessionStorage
    private lateinit var messageRepository: FakeChatMessageRepository
    private lateinit var connectionClient: FakeChatConnectionClient
    private lateinit var viewModel: ChatDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        chatRepository = FakeChatRepository()
        sessionStorage = FakeSessionStorage()
        messageRepository = FakeChatMessageRepository()
        connectionClient = FakeChatConnectionClient()

        sessionStorage.emit(testAuthInfo)
        chatRepository.chatInfoFlow.value = ChatInfo(
            chat = testChat(),
            messages = listOf(
                MessageWithSender(
                    message = testMessage(
                        id = "m1",
                        senderId = otherParticipant.userId,
                        createdAt = Instant.parse("2025-06-10T10:30:00Z")
                    ),
                    sender = otherParticipant,
                    deliveryStatus = ChatMessageDeliveryStatus.SENT
                )
            )
        )

        viewModel = ChatDetailViewModel(
            chatRepository = chatRepository,
            sessionStorage = sessionStorage,
            messageRepository = messageRepository,
            connectionClient = connectionClient
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selecting a chat loads chat info and messages into state`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))

            val state = awaitUntil { it.chatUi != null }
            assertThat(state.chatUi?.id).isEqualTo("chat-1")
            assertThat(state.messages.isNotEmpty()).isTrue()
            assertThat(state.messages.first()).isInstanceOf(MessageUi.OtherUserMessage::class)
            cancelAndIgnoreRemainingEvents()
        }

        assertThat(messageRepository.fetchMessagesCalls).containsExactly("chat-1" to null)
        assertThat(chatRepository.markedAsRead).contains("chat-1")
    }

    @Test
    fun `empty first page marks the end of pagination`() = runTest {
        messageRepository.fetchMessagesResult = Result.Success(emptyList())

        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))

            assertThat(awaitUntil { it.endReached }.endReached).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `failed pagination surfaces a pagination error`() = runTest {
        messageRepository.fetchMessagesResult = Result.Failure(DataError.Remote.SERVER_ERROR)

        viewModel.state.test {
            awaitItem()

            viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))

            assertThat(awaitUntil { it.paginationError != null }.paginationError).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sending a message trims content then delegates to repository and clears the input`() =
        runTest {
            viewModel.state.test {
                awaitItem()
                viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))
                awaitUntil { it.chatUi != null }

                viewModel.state.value.messageTextFieldState
                    .setTextAndPlaceCursorAtEnd("  Hello there  ")
                viewModel.onAction(ChatDetailAction.OnSendMessageClick)

                val sent = messageRepository.sentMessages.single()
                assertThat(sent.chatId).isEqualTo("chat-1")
                assertThat(sent.content).isEqualTo("Hello there")
                assertThat(viewModel.state.value.messageTextFieldState.text.toString())
                    .isEqualTo("")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `blank message is not sent`() = runTest {
        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))
            awaitUntil { it.chatUi != null }

            viewModel.state.value.messageTextFieldState.setTextAndPlaceCursorAtEnd("   ")
            viewModel.onAction(ChatDetailAction.OnSendMessageClick)

            assertThat(messageRepository.sentMessages).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `failed send emits an error event`() = runTest {
        messageRepository.sendMessageResult =
            Result.Failure(DataError.Connection.MESSAGE_SEND_FAILED)

        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))
            awaitUntil { it.chatUi != null }
            viewModel.state.value.messageTextFieldState.setTextAndPlaceCursorAtEnd("Hello")

            viewModel.events.test {
                viewModel.onAction(ChatDetailAction.OnSendMessageClick)

                assertThat(awaitItem()).isInstanceOf(ChatDetailEvent.OnError::class)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connection state changes are reflected in state`() = runTest {
        viewModel.state.test {
            awaitItem()

            connectionClient.connectionState.value = ConnectionState.ERROR_NETWORK

            assertThat(awaitUntil { it.connectionState == ConnectionState.ERROR_NETWORK })
                .isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `leaving the chat clears state and emits OnChatLeft`() = runTest {
        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))
            awaitUntil { it.chatUi != null }

            viewModel.events.test {
                viewModel.onAction(ChatDetailAction.OnLeaveChatClick)

                assertThat(awaitItem()).isEqualTo(ChatDetailEvent.OnChatLeft)
            }

            val cleared = awaitUntil { it.chatUi == null }
            assertThat(cleared.messages).isEmpty()
            assertThat(chatRepository.leaveChatCalls).containsExactly("chat-1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleting a message delegates to the repository`() = runTest {
        val message = MessageUi.CurrentUserMessage(
            id = "m1",
            content = "Hello",
            deliveryStatus = ChatMessageDeliveryStatus.SENT,
            formattedSentTime = UiText.DynamicString("10:30am")
        )

        viewModel.state.test {
            awaitItem()
            viewModel.onAction(ChatDetailAction.OnSelectChat("chat-1"))
            awaitUntil { it.chatUi != null }

            viewModel.onAction(ChatDetailAction.OnDeleteMessageClick(message))

            assertThat(messageRepository.deletedMessageIds).containsExactly("m1")
            cancelAndIgnoreRemainingEvents()
        }
    }
}
