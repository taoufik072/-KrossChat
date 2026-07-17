package com.taoufikcode.chat.presentation

import app.cash.turbine.ReceiveTurbine
import com.taoufikcode.chat.domain.models.Chat
import com.taoufikcode.chat.domain.models.ChatInfo
import com.taoufikcode.chat.domain.models.ChatMessage
import com.taoufikcode.chat.domain.models.ChatMessageDeliveryStatus
import com.taoufikcode.chat.domain.models.ChatParticipant
import com.taoufikcode.chat.domain.models.ConnectionState
import com.taoufikcode.chat.domain.models.MessageWithSender
import com.taoufikcode.chat.domain.models.OutgoingNewMessage
import com.taoufikcode.chat.domain.repository.ChatMessageRepository
import com.taoufikcode.chat.domain.repository.ChatRepository
import com.taoufikcode.chat.domain.repository.ProfileRepository
import com.taoufikcode.chat.domain.service.ChatConnectionClient
import com.taoufikcode.core.domain.auth.AuthInfo
import com.taoufikcode.core.domain.auth.AuthService
import com.taoufikcode.core.domain.auth.SessionStorage
import com.taoufikcode.core.domain.auth.User
import com.taoufikcode.core.domain.notification.DeviceTokenService
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult
import com.taoufikcode.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlin.time.Instant

// ---------- Test data ----------

val testLocalUser = User(
    id = "user-local",
    email = "local@example.com",
    userName = "local",
    hasVerifiedEmail = true,
    profilePictureUrl = null
)

val testAuthInfo = AuthInfo(
    accessToken = "access-token",
    refreshToken = "refresh-token",
    user = testLocalUser
)

val localParticipant = ChatParticipant(
    userId = testLocalUser.id,
    username = testLocalUser.userName,
    profilePictureUrl = null
)

val otherParticipant = ChatParticipant(
    userId = "user-other",
    username = "alice",
    profilePictureUrl = null
)

fun testMessage(
    id: String,
    senderId: String,
    createdAt: Instant,
    content: String = "Message $id",
    chatId: String = "chat-1"
) = ChatMessage(
    id = id,
    chatId = chatId,
    content = content,
    createdAt = createdAt,
    senderId = senderId,
    deliveryStatus = ChatMessageDeliveryStatus.SENT
)

fun testChat(
    id: String = "chat-1",
    participants: List<ChatParticipant> = listOf(localParticipant, otherParticipant),
    lastMessage: ChatMessage? = null,
    unreadCount: Int = 0
) = Chat(
    id = id,
    participants = participants,
    lastActivityAt = Instant.parse("2025-06-15T10:30:00Z"),
    lastMessage = lastMessage,
    unreadCount = unreadCount
)

/** Awaits items until one matches [predicate], skipping intermediate emissions. */
suspend fun <T> ReceiveTurbine<T>.awaitUntil(predicate: (T) -> Boolean): T {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}

// ---------- Fakes ----------

class FakeSessionStorage : SessionStorage {
    private val authInfoFlow = MutableStateFlow<AuthInfo?>(null)

    val storedAuthInfo: AuthInfo? get() = authInfoFlow.value

    fun emit(info: AuthInfo?) {
        authInfoFlow.value = info
    }

    override fun observeAuthInfo(): Flow<AuthInfo?> = authInfoFlow

    override suspend fun set(info: AuthInfo?) {
        authInfoFlow.value = info
    }
}

class FakeAuthService : AuthService {
    var logoutResult: EmptyResult<DataError.Remote> = Result.Success(Unit)
    val logoutCalls = mutableListOf<String>()

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote> =
        Result.Success(testAuthInfo)

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun resetPassword(
        newPassword: String,
        token: String
    ): EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun logout(refreshToken: String): EmptyResult<DataError.Remote> {
        logoutCalls += refreshToken
        return logoutResult
    }
}

class FakeDeviceTokenService : DeviceTokenService {
    var unregisterResult: EmptyResult<DataError.Remote> = Result.Success(Unit)
    val unregisterCalls = mutableListOf<String>()

    override suspend fun registerToken(
        token: String,
        platform: String
    ): EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun unregisterToken(token: String): EmptyResult<DataError.Remote> {
        unregisterCalls += token
        return unregisterResult
    }
}

class FakeChatRepository : ChatRepository {
    val chatsFlow = MutableStateFlow<List<Chat>>(emptyList())
    val chatInfoFlow = MutableStateFlow<ChatInfo?>(null)

    var fetchChatsCalls = 0
    var deleteAllChatsCalls = 0
    val markedAsRead = mutableListOf<String>()
    val leaveChatCalls = mutableListOf<String>()
    var leaveChatResult: EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote> =
        Result.Success(otherParticipant)

    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> =
        Result.Success(testChat())

    override suspend fun getChats(): Result<List<Chat>, DataError.Remote> =
        Result.Success(chatsFlow.value)

    override fun observeChats(): Flow<List<Chat>> = chatsFlow

    override fun observeChatById(chatId: String): Flow<ChatInfo> = chatInfoFlow.filterNotNull()

    override fun observeActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipant>> =
        MutableStateFlow(emptyList())

    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> {
        fetchChatsCalls++
        return Result.Success(chatsFlow.value)
    }

    override suspend fun getChatById(chatId: String): EmptyResult<DataError.Remote> =
        Result.Success(Unit)

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        leaveChatCalls += chatId
        return leaveChatResult
    }

    override suspend fun addParticipantsToChat(
        chatId: String,
        userIds: List<String>
    ): Result<Chat, DataError.Remote> = Result.Success(testChat(id = chatId))

    override suspend fun deleteAllChats() {
        deleteAllChatsCalls++
    }

    override suspend fun markChatAsRead(chatId: String) {
        markedAsRead += chatId
    }
}

class FakeChatMessageRepository : ChatMessageRepository {
    val messagesFlow = MutableStateFlow<List<MessageWithSender>>(emptyList())

    var fetchMessagesResult: Result<List<ChatMessage>, DataError> = Result.Success(emptyList())
    val fetchMessagesCalls = mutableListOf<Pair<String, String?>>()
    var sendMessageResult: EmptyResult<DataError> = Result.Success(Unit)
    val sentMessages = mutableListOf<OutgoingNewMessage>()
    val deletedMessageIds = mutableListOf<String>()
    val retriedMessageIds = mutableListOf<String>()

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError> {
        fetchMessagesCalls += chatId to before
        return fetchMessagesResult
    }

    override fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>> = messagesFlow

    override suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError> {
        sentMessages += message
        return sendMessageResult
    }

    override suspend fun retryMessage(messageId: String): EmptyResult<DataError> {
        retriedMessageIds += messageId
        return Result.Success(Unit)
    }

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        deletedMessageIds += messageId
        return Result.Success(Unit)
    }
}

class FakeProfileRepository : ProfileRepository {
    var fetchCurrentUserCalls = 0

    override suspend fun fetchCurrentUser(): Result<ChatParticipant, DataError> {
        fetchCurrentUserCalls++
        return Result.Success(localParticipant)
    }

    override suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote> = Result.Success(Unit)

    override suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> =
        Result.Success(Unit)
}

class FakeChatConnectionClient : ChatConnectionClient {
    override val connectionState = MutableStateFlow(ConnectionState.CONNECTED)
    override val chatMessages = MutableSharedFlow<ChatMessage>()

    val sentMessages = mutableListOf<ChatMessage>()

    override suspend fun sendMessage(message: ChatMessage): EmptyResult<DataError.Connection> {
        sentMessages += message
        return Result.Success(Unit)
    }

    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local> = Result.Success(Unit)
}
